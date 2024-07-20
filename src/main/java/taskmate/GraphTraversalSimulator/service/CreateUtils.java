package taskmate.GraphTraversalSimulator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import taskmate.GraphTraversalSimulator.domain.ClassHierarchyMapper;
import taskmate.GraphTraversalSimulator.domain.node.*;
import taskmate.GraphTraversalSimulator.helper.IriEncoding;
import taskmate.GraphTraversalSimulator.helper.IriEncodingFactory;
import taskmate.GraphTraversalSimulator.helper.ResourceMapView;

import java.lang.Object;

import static org.eclipse.rdf4j.model.util.Values.iri;

/* Node creation utility methods */

/**
 * Creates Node object from a Concept describing resource with literal value properties embedded
 * and no relations to other Concepts
 */
@Component
public class CreateUtils {
  private final ClassHierarchyMapper classMapper;

  @Autowired
  public CreateUtils(ClassHierarchyMapper classMapper) {
    this.classMapper = classMapper;
  }

  public Node makeNode(ResourceMapView resource) {
    IriEncoding typeIriEncoding = resource.getType();
    String typeLocalName = typeIriEncoding.getLocalName();
    if (typeLocalName.equals("Action")) {
      typeLocalName = getSpecificActionClassName(resource);
    }
    /* Create concept instance at runtime */
    Node node = (Node) makeEmptyObject(typeLocalName);
    /* Set generic node fields */
    node.setIriEncoding(resource.getIri());
    /* Set subclass specific fields */
    if (node instanceof State) {
      ((State) node).setText(resource.get(IriEncodingFactory.fromMaskedIri("ns0!Text")));
      String value = resource.get(IriEncodingFactory.fromMaskedIri("ns0!Process-level"));
      boolean processLevel = false;
      if (value.equals("yes")) {
        processLevel = true;
      }
      ((State) node).setProcessLevel(processLevel);
    } else if (node instanceof Activity) {
      ((Activity) node).setText(resource.get(IriEncodingFactory.fromMaskedIri("ns0!Text")));
    } else if (node instanceof Action) {
      ((Action) node).setStatement(resource.get(IriEncodingFactory.fromMaskedIri("ns0!Statement")));
      if (node instanceof See) {
        ((See) node).setXPath(resource.get(IriEncodingFactory.fromMaskedIri("ns0!XPath")));
        ((See) node).setCssSelector(resource.get(IriEncodingFactory.fromMaskedIri("ns0!CSS_Selector")));
      }
    } else if (node instanceof Property) {
      ((Property) node).setText(resource.get(IriEncodingFactory.fromMaskedIri("ns0!Text")));
    }
    return node;
  }

  /**
   * Create class instance at runtime.
   * Improved version does not use Reflection (more resource-efficient)
   *
   * @param iriLocalName prefix-free
   **/
  public Object makeEmptyObject(String iriLocalName) {
    Class cls = classMapper.getJavaClassFromRdfType(iriLocalName);
    try {
      return cls.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /* HELPERS */

  /**
   * Concrete MetaModel concept to Java subclass identification
   * (because ADOxx metamodel / modeling language uses fewer classes than Java and parameter-based individualisation)
   *
   * @param typeIriEncoding IriEncoding of metamodel concept type
   * @return String with Simple Class Name of deepest specialisation type
   **/
  private String getSpecificActionClassName(ResourceMapView resource) {
    String specificClassName;
    String actionTypeSimpleName = resource.get(IriEncodingFactory.fromMaskedIri("ns0!Action_type"));
    if (actionTypeSimpleName.equals("Input")) {
      String inputTypeSimpleName = resource.get(IriEncodingFactory.fromMaskedIri("ns0!Input_type"));
      return inputTypeSimpleName;
    } else {
      return actionTypeSimpleName;
    }
  }
}