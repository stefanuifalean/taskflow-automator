package taskmate.KnowledgeGraphAPI.service;

import taskmate.KnowledgeGraphAPI.domain.Node;
import taskmate.KnowledgeGraphAPI.helper.IriEncoding;

public interface PseudoLPGViewService {
  Node getNode(IriEncoding nodeIriEncoding);
}