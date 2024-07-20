package taskmate.AutomationApp.helper.context.dependency;

import lombok.Setter;
import taskmate.AutomationApp.helper.context.dependency.aggregation.SingleLayerAggregation;

public class DataResourceOfNonLoop extends DataResource {
   @Setter SingleLayerAggregation singleLayerAggregation;

   @Override
   public String getValue(String placeholder) {
      String value = singleLayerAggregation.getValue(placeholder);
      if (value == null) throw new RuntimeException("Exception: Dependent value in singleLayerAggregation not found");
      return value;
   }
}
