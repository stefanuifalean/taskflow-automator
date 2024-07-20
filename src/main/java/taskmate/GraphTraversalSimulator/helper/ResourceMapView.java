package taskmate.GraphTraversalSimulator.helper;

import org.eclipse.rdf4j.query.BindingSet;

import java.util.*;

import static taskmate.GraphTraversalSimulator.helper.IriUtils.Prefix.*;

public class ResourceMapView {
  /**
   * @ When working with triples query results, we work with String values returned @
   * As objects can be either IRI or (Literal) Value, objects are of String type.
   * IRIs are still represented as String, against convention.
   */
  private final Map<IriEncoding, List<String>> predToObjs = new HashMap<>();

  public ResourceMapView(Collection<BindingSet> bindingSets) {
    bindingSets.forEach(this::addPredObjEntry); // for each triple
    String iri = bindingSets.stream().findFirst().get().getValue("iri").stringValue();
    // Entry artificially added for resource description completeness
    predToObjs.put(IriEncodingFactory.fromMaskedIri("ns0!iri"), new ArrayList<>(Collections.singletonList(iri)));
  }

  /**
   * @return String (IRI or Literal Value) for given predicate's object if predicate exists
   * in Resource Description or null otherwise
   */
  public String get(IriEncoding predicateIriEncoding) {
    if (predToObjs.containsKey(predicateIriEncoding)) {
      return (predToObjs.get(predicateIriEncoding).get(0));
    } else {
      return null;
    }
  }

  /**
   * @return IriEncoding of resource's IRI
   */
  public IriEncoding getIri() {
    String iriFullStr = get(IriEncodingFactory.fromMaskedIri("ns0!iri"));
    return IriEncodingFactory.fromFullIri(iriFullStr);
  }

  /**
   * @return IriEncoding of custom metamodel type
   */
  public IriEncoding getType() {
    String typeStrFullIri = predToObjs.get(IriEncodingFactory.fromMaskedIri("rdf!type")).stream().
        filter(strFullIri -> strFullIri.startsWith(ns0.getUrl())).findFirst().get();
    return IriEncodingFactory.fromFullIri(typeStrFullIri);
  }

  /* HELPERS */

  private void addPredObjEntry(BindingSet bindingSet) { // columnLabel(="p")-columnCell <=> name-value binding
    String pred = bindingSet.getValue("p").stringValue(); // get columnCell value
    String obj = bindingSet.getValue("o").stringValue();
    IriEncoding predIriEncoding = IriEncodingFactory.fromFullIri(pred);
    if (predToObjs.containsKey(predIriEncoding)) {
      predToObjs.get(predIriEncoding).add(obj); // update
    } else { // new is required since singletonList() alone returns a non-resizable List backed by the array
      predToObjs.put(predIriEncoding, new ArrayList<>(Collections.singletonList(obj))); // insert
    }
  }
}
