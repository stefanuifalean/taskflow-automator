package taskmate.KnowledgeGraphAPI.service;

import org.eclipse.rdf4j.query.BindingSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskmate.KnowledgeGraphAPI.domain.Node;
import taskmate.KnowledgeGraphAPI.helper.*;
import taskmate.KnowledgeGraphAPI.repository.Rdf4JRepository;

import java.util.*;

import static org.eclipse.rdf4j.model.util.Values.iri;

@Service
public class PseudoLPGViewServiceImpl implements PseudoLPGViewService {
  private final CreateUtils createUtils;

  private final Rdf4JRepository repo;

  @Autowired
  public PseudoLPGViewServiceImpl(CreateUtils createUtils, Rdf4JRepository repo) {
    this.createUtils = createUtils;
    this.repo = repo;
  }

  /**
   * @param nodeIriEncoding IriEncoding of Concept describing resource
   * @return Node object with literal value properties embedded and no relations to other Concepts
   */
  @Override
  public Node getNode(IriEncoding nodeIriEncoding) {
    Collection<BindingSet> bindingSets = repo.runSelect(Queries.triplesForIri(nodeIriEncoding));
    return createUtils.makeNode(new ResourceMapView(bindingSets));
  }
}