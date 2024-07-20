package taskmate.GraphTraversalSimulator.service;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.rdf4j.query.BindingSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskmate.GraphTraversalSimulator.domain.node.Node;
import taskmate.GraphTraversalSimulator.domain.relation.Relation;
import taskmate.GraphTraversalSimulator.helper.*;
import taskmate.GraphTraversalSimulator.repository.Rdf4JRepository;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KnowledgeService {
  private final List<String> ALLOWED_OUT_RELATIONS = Arrays.asList(
      "triggers", "requiresApp", "requiresInput", "hasProp",
      "informs", "sets", "hardNext", "softNext");
  private final List<String> ALLOWED_IN_RELATIONS = Arrays.asList(
      "Is_inside"
  );
  private final Rdf4JRepository rdfRepo;
  private final PseudoLPGViewService pseudoLPGViewService;

  @Autowired
  public KnowledgeService(Rdf4JRepository rdfRepo, PseudoLPGViewService pseudoLPGViewService) {
    this.rdfRepo = rdfRepo;
    this.pseudoLPGViewService = pseudoLPGViewService;
  }

  public void openFileFromSystemCall(Path filePath) {
    try {
      Process fileExecutionProcess = Runtime.getRuntime().exec(new String[]
          {"cmd", "/c", "\"", filePath.toString(), "\""});
      if (fileExecutionProcess.waitFor() == 0) { // process terminated normally
        fileExecutionProcess.destroy();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public URI getPictureURIForActionTargetUIElement(IriEncoding actionNodeIriEncoding) throws URISyntaxException {
    Collection<BindingSet> bindingSets = rdfRepo.runSelect(Queries.actionTargetPictureURI(actionNodeIriEncoding));
    Optional<BindingSet> record = bindingSets.stream().findFirst();
    if (!record.isPresent()) {
      return null;
    }
      String extractedUri = record.get().getValue("PictureURI").stringValue();
      URI pictureUri = new URI(extractedUri); // respects file:// or https:// sources of identifying an image resource
      return pictureUri;
  }

  public Collection<Map<String, String>> retrieveSubGraph(IriEncoding rootNodeIriEncoding, int depth) {
    List<Map<String, String>> tripleList = new ArrayList<>();
    Map<String, String> tripleReference = new HashMap<>();
    Map<IriEncoding, Object> visited = new HashMap<>();
    Node rootNode = pseudoLPGViewService.getNode(rootNodeIriEncoding);
    int currentLevel = 0;
    depthFirstSearch(tripleList, depth, rootNode, tripleReference, currentLevel, visited);
    return tripleList;
  }

  public Collection<Node> getDirectChildrenOfActivity(IriEncoding activityIriEncoding) {
    Collection<BindingSet> iriBindings = rdfRepo.runSelect(Queries.immediatelyNestedChildrenOf(activityIriEncoding));
    Collection<Node> children = new ArrayList<>();
    iriBindings.forEach(bindingSet -> {
      IriEncoding childIriEnc = IriEncodingFactory.fromFullIri(bindingSet.getValue("elm").stringValue());
      Node child = pseudoLPGViewService.getNode(childIriEnc);
      children.add(child);
    });
    return children;
  }

  public Node getProcessStartingEvent() {
    Collection<BindingSet> iriBindings = rdfRepo.runSelect(Queries.iriOfProcessStartState());
    Optional<BindingSet> record = iriBindings.stream().findFirst();
    if (!record.isPresent()) {
      throw new RuntimeException("Exception: StartState with Process-level='yes' missing in model serialization.");
    }
    String processStartStateIriString = record.get().getValue("iri").stringValue();
    IriEncoding iriEncoding = IriEncodingFactory.fromFullIri(processStartStateIriString);
    return pseudoLPGViewService.getNode(iriEncoding);
  }

  public Node getNextFrom(IriEncoding sourceIriEncoding) {
    Collection<BindingSet> bindingSets = rdfRepo.runSelect(Queries.iriOfHardSoftNextNode(sourceIriEncoding));
    Optional<BindingSet> record = bindingSets.stream().findFirst();
    if (!record.isPresent()) {
      throw new RuntimeException("Exception: No outgoing hard/softNext element in model serialization.");
    }
    String nextElementIriString = record.get().getValue("nodeIri").stringValue();
    IriEncoding iriEncoding = IriEncodingFactory.fromFullIri(nextElementIriString);
    return pseudoLPGViewService.getNode(iriEncoding);
  }

  /* HELPERS */ 

  private void depthFirstSearch(final List<Map<String, String>> tripleList, final int outwardsDepth,
                                Node rootNode, Map<String, String> tripleReference, int currentLevel,
                                Map<IriEncoding, Object> visited) {
    if (currentLevel > outwardsDepth) return;
    // Mark rootNode as visited
    visited.put(rootNode.getIriEncoding(), null);

    /* Overhaul to reduce LONG node label (which is its identifier) in JS GraphVis */
    String subject = shortenIri(rootNode.getIriEncoding());
    // Treat properties
    Field[] nodeFields = FieldUtils.getAllFields(rootNode.getClass());
    for (Field nodeField : nodeFields) {
      if (nodeField.getName().equals("iriEncoding")) { // skip making connection with IRI val property
        continue;
      }
      Object fieldValue = null;
      try {
        fieldValue = FieldUtils.readField(nodeField, rootNode, true);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
      tripleReference.put("o", String.valueOf(fieldValue));
      tripleReference.put("p", "ns0:" + nodeField.getName());
      tripleReference.put("s", subject);
      postTriple(tripleReference, tripleList);
    } /* Excluded rdf:type triples on the same reason */
    // Treat relations (to other Nodes since all object rdf:type = IRI)
    Collection<Relation> allOutRels = pseudoLPGViewService.getRelationsDirectOutgoing(rootNode.getIriEncoding());
    Collection<Relation> allIncRels = pseudoLPGViewService.getRelationsDirectIncoming(rootNode.getIriEncoding());
    /* Treat only simple (with non-embedded attributes) relations for the sake of visual demonstration brevity */
    Collection<Relation> simpleOuts = allOutRels.stream().filter(relation -> {
      String relClass = relation.getClass().getSimpleName();
      return ALLOWED_OUT_RELATIONS.contains(relClass);
    }).collect(Collectors.toList());
    Collection<Relation> simpleIns = allIncRels.stream().filter(rel ->
        ALLOWED_IN_RELATIONS.contains(rel.getClass().getSimpleName())). /* only isInside by our diagram use-case */
        collect(Collectors.toList());
    //Resolve one layer of incoming relations first (in our case just the isInside from contained children to root)
    for (Relation simpleInRel : simpleIns) {
      /* If container, move root to each of its children first and go outwards from there */
      Node relSourceNode = simpleInRel.getSource();
      tripleReference.put("o", subject);
      tripleReference.put("p", "ns0:" + simpleInRel.getClass().getSimpleName());
      tripleReference.put("s", shortenIri(relSourceNode.getIriEncoding()));
      postTriple(tripleReference, tripleList);
      // create child-Is_inside-parent but don't navigate child if visited previously (on another route)
      if (!visited.containsKey(relSourceNode.getIriEncoding())) {
        depthFirstSearch(tripleList, outwardsDepth, relSourceNode, tripleReference, currentLevel + 1, visited);
      }
    }
    for (Relation simpleOutRel : simpleOuts) {
      Node relTargetNode = simpleOutRel.getTarget();
      tripleReference.put("s", subject);
      /* Prop rels are predicates and not dedicated resource so are guaranteed to be in ns0 */
      tripleReference.put("p", "ns0:" + simpleOutRel.getClass().getSimpleName());
      tripleReference.put("o", shortenIri(relTargetNode.getIriEncoding()));
      postTriple(tripleReference, tripleList);
      if (!visited.containsKey(relTargetNode.getIriEncoding())) {
        depthFirstSearch(tripleList, outwardsDepth, relTargetNode, tripleReference, currentLevel + 1, visited);
      }
    }
  }

  private void postTriple(Map<String, String> tripleReference, List<Map<String, String>> tripleList) {
    // Create new map with cloned triple information from tripleReference (else each triple will be lost at Garbage Collection)
    tripleList.add(new HashMap<>(tripleReference));
    System.out.println(tripleReference);
    // Clear local tripleReference by reinitializing it for a new statement
    tripleReference.clear();
  }

  private String shortenIri(IriEncoding iriEncoding) {
    String[] splits = iriEncoding.toStringMasked().split("-");
    return ":" + splits[0].split("!")[1] + "-" + splits[1];
  }
}
