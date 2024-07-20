package taskmate.GraphTraversalSimulator.domain.relation;

import lombok.Getter;
import lombok.Setter;
import taskmate.GraphTraversalSimulator.domain.node.Node;

public abstract class Relation {
  @Getter @Setter private Node source;
  @Getter @Setter private Node target;
}
