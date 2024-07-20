package taskmate.GraphTraversalSimulator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
import taskmate.GraphTraversalSimulator.domain.node.StartState;
import taskmate.GraphTraversalSimulator.service.KnowledgeService;

@Controller
public class ProcessTraversalController {
  private final KnowledgeService knowledgeService;

  @Autowired
  public ProcessTraversalController(KnowledgeService knowledgeService) {
    this.knowledgeService = knowledgeService;
  }

  @GetMapping("/start")
  public RedirectView getProcessStartingEvent() {
    StartState startState = (StartState) knowledgeService.getProcessStartingEvent();
    return new RedirectView("/node/" + startState.getIriEncoding().toStringMasked(), true);
  }
}