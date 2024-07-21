package taskmate.KnowledgeGraphAPI.domain;

import lombok.Getter;
import lombok.Setter;

public abstract class Action extends WorkflowElement {
  @Setter @Getter private String statement;
}
