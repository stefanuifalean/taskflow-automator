package taskmate.AutomationApp.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskmate.AutomationApp.helper.context.Container;
import taskmate.GraphTraversalSimulator.domain.node.Action;
import taskmate.GraphTraversalSimulator.domain.node.Activity;
import taskmate.GraphTraversalSimulator.domain.node.EndState;
import taskmate.GraphTraversalSimulator.domain.node.Node;
import taskmate.GraphTraversalSimulator.service.KnowledgeService;

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

//  public boolean proceedFurther(WebDriver processTraversalSimulator) {
//    String nextUrl = getNextAction(processTraversalSimulator);
//    if (nextUrl == null) return false;
//    processTraversalSimulator.get(nextUrl);
//    return true;
//  }

  /* HELPERS */

  /* Passes over State and Activity elements (containers) on page loading.
  Only resource pages of instructional WorkflowElements are loaded */
  public Node getNextAction(Node step) {
    step = knowledgeService.getNextFrom(step.getIriEncoding());
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
          step = knowledgeService.getNextFrom(directContainer.getActivityIriEncoding()); // IriEncoding
        }
        // Else if reached a process terminating EndState
        else {
          return null;
        }
      }
    }
    return step;

    /* *//* Identify the softNext or hardNext relation on which to proceed *//*
    WebElement row = processTraversalSimulator.findElements(By.cssSelector("#outgoing .relation")).stream().
        filter(webElement -> webElement.getAttribute(classAttribute).toLowerCase().contains("next")).findFirst().get();
    String classes = row.getAttribute(classAttribute);
    if (classes.contains("Activity")) {
      Container container = new Container(getLink(row), knowledgeService);
      traversedActivitiesHistory.push(container);
      return container.getInsideActionUrl();
    } else if (classes.contains("EndState")) {
      // Check if reached an EndState encapsulated in an Activity
      if (!processTraversalSimulator.findElements(By.cssSelector("#outgoing .relation.IsInside")).isEmpty()) {
        Container directContainer = getLastContainer(traversedActivitiesHistory);
        return directContainer.jumpPastEnd(traversedActivitiesHistory);
      } else { // Process-level ending state
        return null;
      }
    } else { // an Action
      return getLink(row);
    }
*/
  }
//
//  private Container getLastContainer(Stack<Container> traversedActivities) {
//    return traversedActivities.pop();
//  }

//  private String getLink(WebElement row) {
//    return row.findElement(By.tagName("a")).getAttribute("href");
//  }
}