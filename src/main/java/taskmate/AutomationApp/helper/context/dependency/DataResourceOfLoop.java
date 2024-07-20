package taskmate.AutomationApp.helper.context.dependency;

import lombok.Setter;
import taskmate.AutomationApp.helper.context.dependency.aggregation.DualLayerAggregation;

public class DataResourceOfLoop extends DataResource {
  @Setter DualLayerAggregation dualLayerAggregation;

  public String getValue(String placeholder) {
    return dualLayerAggregation.getValue(placeholder);
  }
}
