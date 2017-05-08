package server.resources;

import io.opentracing.Span;
import io.opentracing.Tracer;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import server.domain.GitHubStats;
import server.services.GitHubService;
import server.services.LanguageStatisticsService;

@RestController
@RequestMapping("/api/github-stats")
public class GitHubStatsResource {

  private LanguageStatisticsService langStats;

  private GitHubService gitHubService;

  private Tracer tracer;

  public GitHubStatsResource(
      LanguageStatisticsService langStats, GitHubService gitHubService, Tracer tracer) {
    this.langStats = langStats;
    this.gitHubService = gitHubService;
    this.tracer = tracer;
  }

  @RequestMapping(value = "/{userName}", method = RequestMethod.GET)
  public List<GitHubStats> get(@PathVariable("userName") String userName) {
    Span span = tracer.buildSpan("github-stats-api").start();
    List<GitHubStats> result =
        langStats.calcLangStats(gitHubService.getCommittedFilesByUser(userName));
    span.finish();
    return result;
  }
}
