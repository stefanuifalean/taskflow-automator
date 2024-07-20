package taskmate.AutomationApp.helper.context.dependency;

public abstract class DataResource {
  /**
   * @param placeholder String tag of form _val_nameOfVal
   * @return String result for _val_ lookup or null if key does not exist
   */
  public abstract String getValue(String placeholder);
}
