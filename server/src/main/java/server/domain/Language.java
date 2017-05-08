package server.domain;

import java.util.List;
import java.util.Map;
import lombok.*;

@Getter
@Builder
@ToString
public class Language {

  @Setter private String name;
  private String color;
  private List<String> extensions;
  private int languageId;
  private String group;
  private List<String> filenames;

  @SuppressWarnings("unchecked")
  public static Language createFromMap(Map<String, Object> map) {
    return Language.builder()
        .color((String) map.get("color"))
        .extensions((List<String>) map.get("extensions"))
        .languageId((Integer) map.get("language_id"))
        .group((String) map.get("group"))
        .filenames((List<String>) map.get("filenames"))
        .build();
  }

  public static boolean isValid(Map<String, Object> map) {
    return map.containsKey("language_id");
  }
}
