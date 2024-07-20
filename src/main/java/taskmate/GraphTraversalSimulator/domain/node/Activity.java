package taskmate.GraphTraversalSimulator.domain.node;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

public class Activity extends Task {
  @Nullable @Setter @Getter private String text;
}
