package taskmate.GraphTraversalSimulator.domain.node;

import lombok.Getter;
import lombok.Setter;

public class See extends Action {
  @Setter @Getter private String xPath;
  @Setter @Getter private String cssSelector;
}
