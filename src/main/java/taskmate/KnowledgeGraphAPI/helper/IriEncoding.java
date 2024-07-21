package taskmate.KnowledgeGraphAPI.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

import static taskmate.KnowledgeGraphAPI.helper.IriUtils.Prefix;

@AllArgsConstructor
public class IriEncoding {
  @Getter
  private Prefix namespace;
  @Getter
  private String localName;

  /**
   * @return String with form similar to: "cv!Instance_class"
   */
  public String toStringMasked() {
    return namespace.name() + "!" + localName;
  }

	@Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if ((obj == null) || (obj.getClass() != this.getClass()))
      return false;
    // object must be Test at this point
    IriEncoding test = (IriEncoding) obj;
    return (Objects.equals(namespace, test.namespace) && Objects.equals(localName, test.localName));
  }

	@Override
  public int hashCode() {
    int hash = 7;
    hash = 31 * hash + (null == namespace ? 0 : namespace.hashCode());
    hash = 31 * hash + (null == localName ? 0 : localName.hashCode());
    return hash;
  }
}
