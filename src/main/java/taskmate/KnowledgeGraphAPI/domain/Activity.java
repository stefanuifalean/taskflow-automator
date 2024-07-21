package taskmate.KnowledgeGraphAPI.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

public class Activity extends WorkflowElement {
  @Nullable @Setter @Getter private String text;
}
