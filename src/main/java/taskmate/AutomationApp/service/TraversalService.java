package taskmate.AutomationApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskmate.AutomationApp.helper.Container;
import taskmate.KnowledgeGraphAPI.domain.Action;
import taskmate.KnowledgeGraphAPI.domain.Activity;
import taskmate.KnowledgeGraphAPI.domain.EndState;
import taskmate.KnowledgeGraphAPI.domain.Node;
import taskmate.KnowledgeGraphAPI.service.KnowledgeService;

import java.util.*;

@Service
public class TraversalService {
    private final Stack<Container> traversedActivitiesHistory = new Stack<>();
    private final String classAttribute = "class";
    private final KnowledgeService knowledgeService;

    @Autowired
    public TraversalService(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    /* Passes over State and Activity elements (containers) on page loading.
    Only resource pages of instructional WorkflowElements are loaded */
    public Node getNextAction(Node step) {
        step = knowledgeService.getFollowingNode(step.getIriEncoding());
        while (!(step instanceof Action)) {
            if (step instanceof Activity) {
                Container container = new Container(step.getIriEncoding(), knowledgeService);
                traversedActivitiesHistory.push(container);
                step = container.jumpPastStart(step.getIriEncoding());
            } else if (step instanceof EndState) {
                // If reached an EndState encapsulated in an Activity
                if (!traversedActivitiesHistory.empty()) {
                    // Jump past end
                    Container directContainer = traversedActivitiesHistory.pop(); // get last container and remove from history
                    step = knowledgeService.getFollowingNode(directContainer.getActivityIriEncoding()); // IriEncoding
                }
                // Else if reached a process terminating EndState
                else {
                    return null;
                }
            }
        }
        return step;
    }
}