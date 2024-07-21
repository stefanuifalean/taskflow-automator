package taskmate.AutomationApp.controller;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import taskmate.AutomationApp.service.ExecutionService;
import taskmate.AutomationApp.service.TraversalService;
import taskmate.KnowledgeGraphAPI.domain.Action;
import taskmate.KnowledgeGraphAPI.domain.Node;
import taskmate.KnowledgeGraphAPI.service.KnowledgeService;

@Controller
public class AutomationController {
  private WebDriver applicationToAutomate;
  private final TraversalService traversalService;
  private final ExecutionService executionService;
  private final KnowledgeService knowledgeService;

  @Autowired
  public AutomationController(TraversalService traversalService,
                              ExecutionService executionService, KnowledgeService knowledgeService) {
    this.traversalService = traversalService;
    this.executionService = executionService;
    this.knowledgeService = knowledgeService;
  }

  /**
   * Use the Back-End (graph traversal API) to automate Front-End (target browser web site/app)
   */
  @GetMapping("/automate")
  public ResponseEntity runAutomationScript() {
    Node step = knowledgeService.getProcessStartingEvent();
    applicationToAutomate = initBrowserAutomation();
    boolean loop = true;
    while (loop) {
      executionService.execute(step, applicationToAutomate);
      step = traversalService.getNextAction(step);
      if (step == null) {
        loop = false;
      } else {
        System.out.println(((Action)step).getStatement());
      }
    }
    applicationToAutomate.close();
    return ResponseEntity.ok("Automation Task executed successfully");
  }

  private WebDriver initBrowserAutomation() {
    // Selenium by default starts up a browser with a clean, brand-new profile <=> you are actually already browsing privately
    WebDriver driver = new ChromeDriver();
    driver.manage().window().maximize();
    return driver;
  }
}