package taskmate.KnowledgeGraphAPI.domain;

import lombok.Getter;
import lombok.Setter;
import taskmate.KnowledgeGraphAPI.helper.IriEncoding;

public abstract class Node {
  @Setter @Getter private IriEncoding iriEncoding;
}
