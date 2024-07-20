package taskmate.GraphTraversalSimulator.domain.node;

import lombok.Getter;
import lombok.Setter;
import taskmate.GraphTraversalSimulator.helper.IriEncoding;

public abstract class Node {
  @Setter @Getter private IriEncoding iriEncoding;
}
