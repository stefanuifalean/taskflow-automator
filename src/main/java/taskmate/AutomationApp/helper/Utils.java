package taskmate.AutomationApp.helper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.core.io.ClassPathResource;
import taskmate.AutomationApp.helper.context.Container;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
  /* Create Aggregation with all ungrouped properties required in flow
  Parse file and create data aggregation */
  public static Map<String, String> loadProperties() {
    Path filePath = null;
    try {
      filePath = new ClassPathResource("_properties_").getFile().toPath(); // root is resources/
    } catch (IOException e) {
      throw new RuntimeException("No _properties_ file found in resources/ directory");
    }
    return parsePairsFile(filePath);
  }

  private static Map<String, String> parsePairsFile(Path filePath) {
    List<String> lines = readFile(filePath);
    Map<String, String> bindings = new HashMap<>();
    lines.forEach(line -> {
      int separatorIndex = line.indexOf("=");
      String placeholder = line.substring(0, separatorIndex);
      String value = line.substring(separatorIndex + 1);
      bindings.put(placeholder.trim(), value.trim());
    });
    return bindings;
  }

  private static List<String> readFile(Path filePath) {
    List<String> lines;
    try {
      lines = Files.readAllLines(filePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return lines;
  }

  public static String extractPropertyTag(String conceptName) {
    Pattern pattern = Pattern.compile("_p_[a-zA-Z_0-9]+"); // file Regex "(^_p_[A-Za-z]{1}\s)(.)*(file)(.)*";
    Matcher matcher = pattern.matcher(conceptName);
    if (matcher.find()) {
      return matcher.group();
    } else {
      throw new RuntimeException("Invalid concept naming. Couldn't extract property name from: " + conceptName);
    }
  }

  public static String getStepName(WebDriver processTraversalSimulator) {
    return processTraversalSimulator.findElement(By.cssSelector(".field.name")).getText();
  }

  public static String getStepClasses(WebDriver processTraversalSimulator) {
    return processTraversalSimulator.findElement(By.id("nodeClassNames")).getText();
  }

  public static Container getStepContext(WebDriver processTraversalSimulator,
                                         Map<String, Container> urlToStepContextRegistry) {
    String stepUrl = processTraversalSimulator.getCurrentUrl();
    if (!urlToStepContextRegistry.containsKey(stepUrl))
      throw new RuntimeException("Error. Not previously initialized Step Context found for url: " + stepUrl);
    return urlToStepContextRegistry.get(stepUrl);
  }
}
