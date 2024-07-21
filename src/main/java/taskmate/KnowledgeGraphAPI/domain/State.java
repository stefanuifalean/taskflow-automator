package taskmate.KnowledgeGraphAPI.domain;

import lombok.Getter;
import lombok.Setter;

public abstract class State extends WorkflowElement {
  @Getter @Setter boolean processLevel;
}
