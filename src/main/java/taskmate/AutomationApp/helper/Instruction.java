package taskmate.AutomationApp.helper;

import lombok.Getter;

public class Instruction {
  private enum Action {
    SEE, CLICK, TYPE, INVISIBLE, CONVERSATION
  }
  @Getter Action action;
  @Getter String object = null;
  @Getter String value;
  public Instruction(String nodeName, String mostDerivedClass) {
    String[] stems = nodeName.split(" ");
    if (!stems[0].matches("\\(w+\\)")) {
      throw new RuntimeException("Malformed instruction predicate in modeled Action element with name: " + nodeName);
    }
    action = Action.valueOf(mostDerivedClass.toUpperCase());
    if (stems[1].matches("_\\w{3,}_") && !stems[1].contains("key-")) {
      object = stems[1];
    }
    StringBuilder stringBuilder = new StringBuilder();
    for (int index = (object == null ? 1 : 2); index < stems.length; index++) {
      stringBuilder.append(stems[index]).append(" ");
    }
    value = stringBuilder.toString().trim();
  }
}