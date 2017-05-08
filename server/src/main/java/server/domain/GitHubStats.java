package server.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class GitHubStats {

  private String name;
  private String color;
  private Double ratio;
}
