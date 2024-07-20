package taskmate.GraphTraversalSimulator.domain.node;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

public abstract class State extends WorkflowElement {
  @Nullable @Setter @Getter private String text;
  @Getter @Setter boolean processLevel;
}
