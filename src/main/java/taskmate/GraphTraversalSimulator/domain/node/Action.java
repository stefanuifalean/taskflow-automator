package taskmate.GraphTraversalSimulator.domain.node;

import lombok.Getter;
import lombok.Setter;

public abstract class Action extends Task {
  @Setter @Getter private String statement;
}
