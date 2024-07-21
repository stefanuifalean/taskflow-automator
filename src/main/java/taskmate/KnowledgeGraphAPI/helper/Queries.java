package taskmate.KnowledgeGraphAPI.helper;

import static taskmate.KnowledgeGraphAPI.helper.IriUtils.Prefix;

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

    public static String iriOfFollowing(IriEncoding sourceIriEncoding) {
        StringBuilder query = new StringBuilder();
        query.append("PREFIX : <").append(Prefix.mdl.getUrl()).append(">\n").
                append("PREFIX ns0: <").append(Prefix.ns0.getUrl()).append(">\n").
                append("SELECT ?nodeIri WHERE {\n").
                append(":").append(sourceIriEncoding.getLocalName()).append(" ns0:followedBy ?nodeIri .\n").
                append("}\n");
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
}
