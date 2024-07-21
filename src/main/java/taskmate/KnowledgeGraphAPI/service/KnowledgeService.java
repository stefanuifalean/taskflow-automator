package taskmate.KnowledgeGraphAPI.service;

import org.eclipse.rdf4j.query.BindingSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskmate.KnowledgeGraphAPI.domain.Node;
import taskmate.KnowledgeGraphAPI.helper.*;
import taskmate.KnowledgeGraphAPI.repository.Rdf4JRepository;

import java.util.*;

@Service
public class KnowledgeService {
  private final Rdf4JRepository rdfRepo;
  private final PseudoLPGViewService pseudoLPGViewService;

  @Autowired
  public KnowledgeService(Rdf4JRepository rdfRepo, PseudoLPGViewService pseudoLPGViewService) {
    this.rdfRepo = rdfRepo;
    this.pseudoLPGViewService = pseudoLPGViewService;
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

  public Node getFollowingNode(IriEncoding sourceIriEncoding) {
    Collection<BindingSet> bindingSets = rdfRepo.runSelect(Queries.iriOfFollowing(sourceIriEncoding));
    Optional<BindingSet> record = bindingSets.stream().findFirst();
    if (!record.isPresent()) {
      throw new RuntimeException("Exception: " + sourceIriEncoding.toStringMasked() + " has no outgoing followedBy node");
    }
    String nextElementIriString = record.get().getValue("nodeIri").stringValue();
    IriEncoding iriEncoding = IriEncodingFactory.fromFullIri(nextElementIriString);
    return pseudoLPGViewService.getNode(iriEncoding);
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
}
