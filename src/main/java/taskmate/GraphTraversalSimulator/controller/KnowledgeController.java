package taskmate.GraphTraversalSimulator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import taskmate.GraphTraversalSimulator.domain.node.Node;
import taskmate.GraphTraversalSimulator.helper.IriEncoding;
import taskmate.GraphTraversalSimulator.helper.IriEncodingFactory;
import taskmate.GraphTraversalSimulator.service.KnowledgeService;
import taskmate.GraphTraversalSimulator.service.PseudoLPGViewService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

@Controller
@RequestMapping("/knowledge")
public class KnowledgeController {
  private final KnowledgeService knowledgeService;
  private final PseudoLPGViewService pseudoLPGViewService;

  @Autowired
  public KnowledgeController(KnowledgeService knowledgeService, PseudoLPGViewService pseudoLPGViewService) {
    this.knowledgeService = knowledgeService;
    this.pseudoLPGViewService = pseudoLPGViewService;
  }

//  @GetMapping("/input/{maskedIri}")
//  public ResponseEntity<Void> openLocalFile(@PathVariable("maskedIri") String dataEntityIriEncodingString) {
//    return runtimeLaunch(dataEntityIriEncodingString);
//  }
//
//  @GetMapping("/app/{maskedIri}")
//  public ResponseEntity<Void> openLocalProgram(@PathVariable("maskedIri") String swResourceIriEncoding) {
//    return runtimeLaunch(swResourceIriEncoding);
//  }

  /* Call: http://localhost:8080/knowledge/subgraph/mdl!Activity-55260-Get_to_Projects/2 */
  @GetMapping("/subgraph/{root}/{depth}")
  public String getTriples(Model model, @PathVariable("root") String rootIriEncString,
                           @PathVariable("depth") String depthString) {
    IriEncoding rootNodeIriEncoding = IriEncodingFactory.fromMaskedIri(rootIriEncString);
    int depth = Integer.parseInt(depthString);
    Collection<Map<String, String>> tripleList = knowledgeService.retrieveSubGraph(rootNodeIriEncoding, depth);
    // Rendering feature: Derived Semantic graph of intermediary subgraph view
    String dotString = getDotString(tripleList);
    model.addAttribute("dotString", dotString);
    return "network";
  }

  /* HELPERS */
//  private ResponseEntity<Void> runtimeLaunch(String nodeIriEncodingString) {
//    IriEncoding nodeIriEncoding = IriEncodingFactory.fromMaskedIri(nodeIriEncodingString);
//    Node node = pseudoLPGViewService.getNode(nodeIriEncoding);
//    String fileLocalPathString = null;
//    if (node instanceof DataEntity) {
//      fileLocalPathString = ((DataEntity) node).getFileLocalPathString();
//    } else if (node instanceof SoftwareResource) {
//      fileLocalPathString = ((SoftwareResource) node).getProgramLocalPathString();
//    }
//    Path filePath = Paths.get(fileLocalPathString);
//    knowledgeService.openFileFromSystemCall(filePath);
//    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//  }

  /**
   * Algorithm that transforms the filtered extraction of SPO triples from the middleware
   * into a valid DOT representation that can be rendered visually in network form.
   *
   * @return Dot language representation of the graph parseable by VisJS library
   */
  private String getDotString(Collection<Map<String, String>> tripleList) {
    StringBuffer dotString = new StringBuffer();
    dotString.append("digraph derivedSemanticGraph {\n");
    tripleList.forEach(triple -> {
      String subject = triple.get("s");
      String predicate = triple.get("p");
      String object = triple.get("o");
      // strip default "" added by rdf standard to a literal value
//      object = object.startsWith("\"") ? object.substring(1, object.length() - 1) : object;
      dotString.append(String.format("\"%s\" -> \"%s\" [label=\"%s\"];\n", subject, object, predicate));
    });
    dotString.append("}");
    return dotString.toString();
  }
}
