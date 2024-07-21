package taskmate.AutomationApp.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import taskmate.AutomationApp.helper.FileUtils;
import taskmate.KnowledgeGraphAPI.domain.*;

import java.time.Duration;
import java.util.Map;

@Service
public class ExecutionService {
    private WebElement target;
    private final Map<String, String> properties;

    { // Initialization block: executes single time when Service bean is created
        properties = FileUtils.loadProperties();
    }

    public void execute(Node step, WebDriver applicationToAutomate) {
        if (!(step instanceof Action)) {
            return;
        }
        String statement = ((Action) step).getStatement();
        if (step instanceof Invisible && statement.contains("(Access Web Address)")) {
            String input = getDependency(statement);
            applicationToAutomate.get(input);
        } else if (step instanceof See) {
            WebDriverWait wait = new WebDriverWait(applicationToAutomate, Duration.ofSeconds(20));
            String xPath = ((See) step).getXPath();
            if (xPath != null) {
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
                target = applicationToAutomate.findElement(By.xpath(xPath)); // throws Exception if not found
            } else {
                String cssSelector = ((See) step).getCssSelector();
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
                target = applicationToAutomate.findElement(By.cssSelector(cssSelector));
            }
        } else if (step instanceof Click) {
            target.click();
        } else if (step instanceof Type) {
            String input = getDependency(statement);
            target.sendKeys(input);
        }
    }

    private String getDependency(String statement) {
        String[] words = statement.split(" ");
        String propertyIdentifier = words[words.length - 1]; // last word
        propertyIdentifier = propertyIdentifier.substring(0, propertyIdentifier.length() - 1); // cut trailing "
        if (!properties.containsKey(propertyIdentifier))
            throw new RuntimeException("Property not found: " + propertyIdentifier);
        String input = properties.get(propertyIdentifier);
        System.out.println("property[" + propertyIdentifier + "] = " + input);
        return input;
    }
}