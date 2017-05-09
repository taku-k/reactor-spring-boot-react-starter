package server.resources;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.spanmanager.SpanManager;
import io.opentracing.contrib.spanmanager.SpanManager.ManagedSpan;
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

  private SpanManager spanManager;

  public GitHubStatsResource(
      LanguageStatisticsService langStats,
      GitHubService gitHubService,
      Tracer tracer,
      SpanManager spanManager) {
    this.langStats = langStats;
    this.gitHubService = gitHubService;
    this.tracer = tracer;
    this.spanManager = spanManager;
  }

  @RequestMapping(value = "/{userName}", method = RequestMethod.GET)
  public List<GitHubStats> get(@PathVariable("userName") String userName) {
    Span span = tracer.buildSpan("github-stats-api").start();
    ManagedSpan managed = spanManager.activate(span);
    List<GitHubStats> result =
        langStats.calcLangStats(gitHubService.getCommittedFilesByUser(userName));
    span.finish();
    managed.close();
    return result;
  }
}
