package taskmate.AutomationApp.helper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
public class InputConstruct {
  @Getter @Setter private String constructName;
  @Getter @Setter private boolean isCollection = false;
  @Getter @Setter private List<InputConstruct> container = null;
  @Getter @Setter private String value;

  /** Starting from a root input dependency (file) traverse linked chain and retrieve
   * Collection (List of InputConstructs) with given name
   */
  public List<InputConstruct> getCollectionNamed(String innerCollectionName) {
    return goInsideRecursion(this, innerCollectionName);
  }

  private List<InputConstruct> goInsideRecursion(InputConstruct chainElement, String collectionName) {
    if (constructName.equals(collectionName)) {
      return container;
    }
    // We know from graph models that it's a chain of single children until the last collection (with multi leaf nodes)
    return goInsideRecursion(chainElement.container.get(0), collectionName);
  }
}

