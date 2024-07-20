package taskmate.AutomationApp.helper.context.dependency;

import lombok.Getter;
import lombok.Setter;

/**
 * Requirements of a Task element (either Action or Activity)
 */
public class Dependency {
  @Getter @Setter private String softwareResource;
  @Getter @Setter private DataResource dataResource;

  public boolean hasSoftwareResource() {
    return softwareResource != null;
  }

  public boolean hasDataResource() {
    return dataResource != null;
  }
}
