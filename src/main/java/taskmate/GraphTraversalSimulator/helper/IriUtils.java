package taskmate.GraphTraversalSimulator.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

public class IriUtils {
  /**
   * IRI Domain Name substitutions
   **/
  @AllArgsConstructor
  public enum Prefix {
    mdl("http://www.taskmate.eu/outlook#"),
    rdf("http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
    cv("http://www.comvantage.eu/mm#"),
    ns0("http://www.taskmate.eu/mm#");

    @Getter private String url;

    /**
     * Reverse Lookup
     */
    public static Optional<Prefix> get(String url) {
      return Arrays.stream(Prefix.values()).filter(prefix -> prefix.url.equals(url)).findFirst();
    }
  }
}
