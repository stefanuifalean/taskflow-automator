package taskmate.AutomationApp.helper;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtils {
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
}
