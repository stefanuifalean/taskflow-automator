package taskmate.GraphTraversalSimulator.controller;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import taskmate.GraphTraversalSimulator.domain.node.Node;
import taskmate.GraphTraversalSimulator.domain.relation.Relation;
import taskmate.GraphTraversalSimulator.domain.relation.RequiresInput;
import taskmate.GraphTraversalSimulator.helper.IriEncoding;
import taskmate.GraphTraversalSimulator.helper.IriEncodingFactory;
import taskmate.GraphTraversalSimulator.service.KnowledgeService;
import taskmate.GraphTraversalSimulator.service.PseudoLPGViewService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/node")
public class NodeController {
  private final PseudoLPGViewService service;
  private final KnowledgeService kserv;

  @Autowired
  public NodeController(PseudoLPGViewService service, KnowledgeService kserv) {
    this.service = service;
    this.kserv = kserv;
  }

  /*
   * In HTTP requests, as per RFC specifications, after an endpoint address it is illegal to chain any other URLs.
   * Thus we encode the domain name from the resource Node IRI into a prefix name followed by "!" character
   * and the Local Name part of the IRI which remains unaltered.
   * (This approach removes the drawback of problems in multi-namespace RDF queries)
   */
  @GetMapping("/{iriEnc}")
  public String getNodeByIriEncodingReflective(Model model, @PathVariable("iriEnc") String iriEncodingStr) {
    IriEncoding iriEncoding = IriEncodingFactory.fromMaskedIri(iriEncodingStr);
    Node node = service.getNode(iriEncoding);
    Class nodeClass = node.getClass(); // Returns the runtime (most derived) class of this object
    StringBuffer classHierarchyNames = new StringBuffer();
    while (nodeClass != Node.class) {
      classHierarchyNames.append(nodeClass.getSimpleName()).append(" ");
      nodeClass = nodeClass.getSuperclass();
    }
    model.addAttribute("node", node);
    model.addAttribute("nodeClassnames", classHierarchyNames.toString().trim());
    model.addAttribute("fieldUtils", new FieldUtils());

    /**
     *  !!! EAGER loading of neighbor nodes which are created for each created relation on every node accessing !!!
     */

    Collection<Relation> outgoingRelations = service.getRelationsDirectOutgoing(iriEncoding);
    model.addAttribute("outRels", outgoingRelations);
    Collection<Relation> incomingRelations = service.getRelationsDirectIncoming(iriEncoding);
    model.addAttribute("incRels", incomingRelations);

//    model.addAttribute("hasRequiresInput", hasRequiresInput(model, outgoingRelations));
//    model.addAttribute("hasRequiresApp", hasRequiresApp(model, outgoingRelations));
    model.addAttribute("hasPictureURI", hasIndirectGraphicalRepresentation(model, iriEncoding));
    return "default";
  }

//  private boolean hasRequiresInput(Model model, Collection<Relation> outgoingRelations) {
//    List<Relation> relations = outgoingRelations.stream().filter(relation -> {
//      if (relation instanceof RequiresInput) {
//        Node target = relation.getTarget();
//        return target instanceof DataEntity && target.getName().contains("file");
//      } // filter files only and exclude properties
//      return false;
//    }).collect(Collectors.toList());
//    if (relations.isEmpty()) {
//      return false;
//    } else {
//      model.addAttribute("requiresInput", relations);
//      return true;
//    }
//  }

//  private boolean hasRequiresApp(Model model, Collection<Relation> outgoingRelations) {
//    Optional<Relation> appRelation = outgoingRelations.stream().filter(relation -> relation instanceof requiresApp).
//        findFirst();
//    if (!appRelation.isPresent()) {
//      return false;
//    } else {
//      model.addAttribute("requiresApp", appRelation.get());
//      return true;
//    }
//  }

  private boolean hasIndirectGraphicalRepresentation(Model model, IriEncoding actionTargetIriEncoding) {
    URI pictureURI = null;
    try {
      pictureURI = kserv.getPictureURIForActionTargetUIElement(actionTargetIriEncoding);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    if (pictureURI == null) {
      return false;
    } else {
      model.addAttribute("pictureURI", pictureURI);
      return true;
    }
  }
  //     TODO: SPARQL Query that goes on actsUpon and if finds hasGraphicalRepresentation offers option to show it on page of
  // TODO: the action that has that UIElement association
}
