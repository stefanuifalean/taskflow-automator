package taskmate.KnowledgeGraphAPI.helper;

import taskmate.KnowledgeGraphAPI.helper.IriUtils.Prefix;

public class IriEncodingFactory {
  /**
   * @param url String with form similar to: "cv!Instance_class"
   */
  public static IriEncoding fromMaskedIri(String url) {
    String[] parts = url.split("!");
    return new IriEncoding(Prefix.valueOf(parts[0]), parts[1]);
  }

  /**
   * @param url String with full IRI address (URL form)
   */
  public static IriEncoding fromFullIri(String url) {
    String[] parts = url.split("#");
    return new IriEncoding(Prefix.get(parts[0]+"#").get(), parts[1]);
  }
}
