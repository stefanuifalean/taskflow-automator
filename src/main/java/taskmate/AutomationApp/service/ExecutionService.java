package taskmate.AutomationApp.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import taskmate.AutomationApp.helper.Utils;
import taskmate.GraphTraversalSimulator.domain.node.*;

import java.time.Duration;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExecutionService {
  private WebElement target;
  private final Map<String, String> properties;

  { // Initialization block: executes single time when Service bean is created
    properties = Utils.loadProperties();
  }

  public void execute(Node step, WebDriver applicationToAutomate) {
    if (!(step instanceof Action)) {
      return;
    }
    String statement = ((Action) step).getStatement();
    String input = getDependency(statement);
    if (step instanceof Invisible && statement.contains("(Access Web Address)")) {
      if (input == null) throw new RuntimeException("Unspecified property on statement: " + statement);
      applicationToAutomate.get(input);
    } else if (step instanceof See) {
//      Wait wait = new FluentWait(applicationToAutomate)
//          .withTimeout(Duration.ofSeconds(10))
//          .pollingEvery(Duration.ofMillis(250));
      WebDriverWait wait = new WebDriverWait(applicationToAutomate, 20);
      String xPath = ((See) step).getXPath();
      if (xPath != null) {
//        target = (WebElement) wait.until((Function<WebDriver, WebElement>) app-> app.findElement(By.xpath(xPath)));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
        target = applicationToAutomate.findElement(By.xpath(xPath)); // throws Exception if not found
      } else {
        String cssSelector = ((See) step).getCssSelector();
//        target = (WebElement) wait.until((Function<WebDriver, WebElement>) app -> app.findElement(By.id(id)));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
        target = applicationToAutomate.findElement(By.cssSelector(cssSelector));
      }
    } else if (step instanceof Click) {
      target.click();
    } else if (step instanceof Type) {
      if (input == null) throw new RuntimeException("Unspecified property on statement: " + statement);
      target.sendKeys(input);
    }
  }

  private String getDependency(String statement) {
    Pattern pattern = Pattern.compile("_val_(\\w+)");
    Matcher matcher = pattern.matcher(statement);
    if (matcher.find()) {
      String property = matcher.group(1);
      if (property == null || !properties.containsKey(property))
        throw new RuntimeException("Property not found: " + property);
      String input = properties.get(property);
      System.out.println("property[" + property + "] = " + input);
      return input;
    }
    return null;
  }
}