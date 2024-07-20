package taskmate.GraphTraversalSimulator.service;

import taskmate.GraphTraversalSimulator.domain.node.Node;
import taskmate.GraphTraversalSimulator.domain.relation.Relation;
import taskmate.GraphTraversalSimulator.helper.IriEncoding;

import java.util.Collection;

public interface PseudoLPGViewService {
  Node getNode(IriEncoding nodeIriEncoding);

  Relation getRichRelation(IriEncoding relIriEncoding);

  Collection<Relation> getRelationsDirectOutgoing(IriEncoding fromNodeIriEncoding);

  Collection<Relation> getRelationsDirectIncoming(IriEncoding toNodeIriEncoding);
}