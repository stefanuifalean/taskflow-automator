package taskmate.AutomationApp.helper.context.dependency.aggregation;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DualLayerAggregation implements Aggregation {
  // TODO:
//  private Map<String, String> valBindings;

  /**
   * @param placeholder String tag of form _val_nameOfVal
   * @return String result for _val_ lookup or null if key does not exist
   */
  @Override
  public String getValue(String placeholder) {
      /*return valBindings.get(placeholder);*/
    return null;
  }
}
