package taskmate.AutomationApp.helper;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import taskmate.KnowledgeGraphAPI.domain.Node;
import taskmate.KnowledgeGraphAPI.domain.StartState;
import taskmate.KnowledgeGraphAPI.helper.IriEncoding;
import taskmate.KnowledgeGraphAPI.service.KnowledgeService;

import java.util.Collection;
import java.util.Optional;

public class Container {
  @Getter
  private final IriEncoding activityIriEncoding;
  private final KnowledgeService knowledgeService;

  @Autowired
  public Container(IriEncoding activityIriEncoding, KnowledgeService knowledgeService) {
    this.activityIriEncoding = activityIriEncoding;
    this.knowledgeService = knowledgeService;
  }

  public Node jumpPastStart(IriEncoding activityIriEncoding) {
    // Give me all elements that are inside current step Activity that
    // do not appear inside one or more Activities nested inside current one
    Collection<Node> directChildrenOfActivity = knowledgeService.getDirectChildrenOfActivity(activityIriEncoding);
    Optional<Node> optStartNode = directChildrenOfActivity.stream().filter(child -> child instanceof StartState).findFirst();
    if (!optStartNode.isPresent()) {
      throw new RuntimeException("Activity with id:" + activityIriEncoding.toStringMasked() + "does not have a start state!");
    }
    Node startState = optStartNode.get();
    Node afterStart = knowledgeService.getFollowingNode(startState.getIriEncoding());
    return afterStart;
  }
}