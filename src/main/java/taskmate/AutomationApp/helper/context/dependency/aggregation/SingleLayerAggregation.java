package taskmate.AutomationApp.helper.context.dependency.aggregation;

import lombok.AllArgsConstructor;

import java.util.Map;
@AllArgsConstructor
public class SingleLayerAggregation implements Aggregation {
  private Map<String, String> valBindings;


  @Override
  public String getValue(String placeholder) {
      return valBindings.get(placeholder);
  }
}
