package taskmate.GraphTraversalSimulator.domain;

import org.springframework.stereotype.Component;
import taskmate.GraphTraversalSimulator.domain.node.*;
import taskmate.GraphTraversalSimulator.domain.relation.HardNext;
import taskmate.GraphTraversalSimulator.domain.relation.IsInside;
import taskmate.GraphTraversalSimulator.domain.relation.RequiresInput;
import taskmate.GraphTraversalSimulator.domain.relation.SoftNext;

import java.util.HashMap;
import java.util.Map;

@Component
public class ClassHierarchyMapper {
  private final Map<String, Class> rdfTypeToJavaClass = new HashMap<>();

  public ClassHierarchyMapper() {
    rdfTypeToJavaClass.put("Is_inside", IsInside.class);
    rdfTypeToJavaClass.put("requiresInput", RequiresInput.class);
    rdfTypeToJavaClass.put("softNext", SoftNext.class);
    rdfTypeToJavaClass.put("hardNext", HardNext.class);

    rdfTypeToJavaClass.put("StartState", StartState.class);
    rdfTypeToJavaClass.put("EndState", EndState.class);
    rdfTypeToJavaClass.put("Activity", Activity.class);
    rdfTypeToJavaClass.put("See", See.class);
    rdfTypeToJavaClass.put("Click", Click.class);
    rdfTypeToJavaClass.put("Type", Type.class);
    rdfTypeToJavaClass.put("Invisible", Invisible.class);
    rdfTypeToJavaClass.put("Property", Property.class);
  }

  public Class getJavaClassFromRdfType(String iriLocalName) {
    if (!rdfTypeToJavaClass.containsKey(iriLocalName)) {
      throw new RuntimeException("Exception: rdf:type local name does not have a Java Hierarchy mapping: " + iriLocalName + "\n");
    }
    return rdfTypeToJavaClass.get(iriLocalName);
  }
}
