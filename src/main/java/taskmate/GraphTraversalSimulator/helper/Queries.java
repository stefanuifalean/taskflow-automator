package taskmate.GraphTraversalSimulator.helper;

import static taskmate.GraphTraversalSimulator.helper.IriUtils.Prefix;

public class Queries {
  public static String iriOfProcessStartState() {
    StringBuilder query = new StringBuilder();
    query.append("PREFIX rdf: <").append(Prefix.rdf.getUrl()).append(">\n").
        append("PREFIX ns0: <").append(Prefix.ns0.getUrl()).append(">\n").
        append("SELECT ?iri WHERE {\n").
        append("?iri rdf:type ns0:StartState .\n").
        append("?iri ns0:Process-level \"yes\" .\n}");
    return query.toString();
  }

  public static String triplesForIri(IriEncoding iriEncoding) {
    StringBuilder query = new StringBuilder();
    query.append("PREFIX : <").append(Prefix.mdl.getUrl()).append(">\n").
        append("SELECT * WHERE {\n").
        append("?iri ?p ?o .\n").
        append("FILTER (?iri = :").append(iriEncoding.getLocalName()).append(") .\n}");
    return query.toString();
  }

  /**
   * Find parameterized relations that either go out of the refNode or come into refNode
   *
   * @param direction "outgoing" or "incoming"
   */
  public static String irisForRichRelations(String direction, IriEncoding refNodeIriEncoding) {
    StringBuilder query = new StringBuilder();
    query.append("PREFIX cv: <").append(Prefix.cv.getUrl()).append(">\n").
        append("PREFIX : <").append(Prefix.mdl.getUrl()).append(">\n").
        append("SELECT ?relIri WHERE {\n").
        append("?relIri ").append(direction.equals("outgoing") ? "cv:from_instance" : "cv:to_instance").
        append(" :").append(refNodeIriEncoding.getLocalName()).append(" .\n}");
    return query.toString();
  }

  /**
   * Find (concept property)-abstracted relations that either go out of the refNode or come into refNode
   *
   * @param direction "outgoing" or "incoming"
   */
  public static String triplesForSimpleRelations(String direction, IriEncoding refNodeIriEncoding) {
    StringBuilder query = new StringBuilder();
    query.append("PREFIX : <").append(Prefix.mdl.getUrl()).append(">\n").
        append("PREFIX rdf: <").append(Prefix.rdf.getUrl()).append(">\n").
        append("PREFIX cv: <").append(Prefix.cv.getUrl()).append(">\n").
        append("SELECT * WHERE {\n").
        append("?outgoingNodeIri ?propRelIri ?incomingNodeIri .\n").
        append("FILTER (?").append(direction.equals("outgoing") ? "outgoingNodeIri" : "incomingNodeIri").
        append(" = :").append(refNodeIriEncoding.getLocalName()).append(") .\n");
    if (direction.equals("outgoing")) {
      /* We need some semantic filtering to keep only prop outgoing relations
       Subject is guaranteed to be an IRI in an RDF statement. Only object needs check
       For semantic filtering, following line double asserts that object is an IRI and an instance of a concept
     */
      query.append("FILTER (EXISTS {?incomingNodeIri rdf:type cv:Instance_class}) .\n");
    }
    query.append("}");
    return query.toString();
  }

  public static String actionTargetPictureURI(IriEncoding actionNodeIriEncoding) {
    StringBuilder query = new StringBuilder();
    query.append("PREFIX : <").append(Prefix.mdl.getUrl()).append(">\n").
        append("PREFIX ns0: <").append(Prefix.ns0.getUrl()).append(">\n").
        append("SELECT ?PictureURI WHERE {\n").
        append("?actionIri ns0:actsUpon/ns0:hasGraphicalAppearance/ns0:PictureURI ?PictureURI .\n").
        append("FILTER (?actionIri = :").append(actionNodeIriEncoding.getLocalName()).append(") .\n}");
    return query.toString();
  }

  public static String immediatelyNestedChildrenOf(IriEncoding activityNodeIriEncoding) {
    StringBuilder query = new StringBuilder();
    query.append("PREFIX : <").append(Prefix.mdl.getUrl()).append(">\n").
        append("PREFIX ns0: <").append(Prefix.ns0.getUrl()).append(">\n").
        append("SELECT ?elm WHERE {\n").
        append("?elm ns0:Is_inside :").append(activityNodeIriEncoding.getLocalName()).append(" .\n").
        append("FILTER NOT EXISTS {\n").
        append("\t?elm ns0:Is_inside ?parent .\n").
        append("\t?parent ns0:Is_inside+ :").append(activityNodeIriEncoding.getLocalName()).append(" .\n").
        append("} .\n}");
    return query.toString();
  }

  public static String iriOfHardSoftNextNode(IriEncoding sourceIriEncoding) {
    StringBuilder query = new StringBuilder();
    query.append("PREFIX cv: <").append(Prefix.cv.getUrl()).append(">\n").
        append("PREFIX : <").append(Prefix.mdl.getUrl()).append(">\n").
        append("PREFIX ns0: <").append(Prefix.ns0.getUrl()).append(">\n").
        append("PREFIX rdf: <").append(Prefix.rdf.getUrl()).append(">\n").
        append("SELECT ?nodeIri WHERE {\n").
        append("?nextRelIri cv:from_instance :").append(sourceIriEncoding.getLocalName()).append(" .\n").
        append("?nextRelIri cv:to_instance ?nodeIri .\n").
        append("FILTER (EXISTS {?nextRelIri rdf:type ns0:hardNext} || EXISTS {?nextRelIri rdf:type ns0:softNext}) .\n").
        append("}\n");
    return query.toString();
  }
}
