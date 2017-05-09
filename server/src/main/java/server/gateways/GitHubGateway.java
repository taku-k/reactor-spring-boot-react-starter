package server.gateways;

import static java.lang.String.format;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.web.client.RestTemplate;
import server.domain.Commit;
import server.domain.Repository;
import server.domain.SingleCommit;

@Slf4j
public class GitHubGateway {

  private static final Logger LOG = Logger.getLogger(GitHubGateway.class);

  private RestTemplate restTemplate;

  private static final String BASE = "https://api.github.com";
  static final String REPOS = BASE + "/users/%s/repos";
  static final String COMMITS = BASE + "/repos/%s/%s/commits?since=%s";
  static final String SINGLE_COMMIT = BASE + "/repos/%s/%s/commits/%s";

  public GitHubGateway(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<Repository> getRepos(String user) {
    LOG.info(format("Get repos by user(%s)", user));
    Repository[] repos = restTemplate.getForObject(format(REPOS, user), Repository[].class);
    return Arrays.asList(repos);
  }

  public List<Commit> getCommitsInWeek(String user, String repo) {
    String aWeekAgo =
        ZonedDateTime.now(ZoneOffset.UTC)
            .minusWeeks(1)
            .minusDays(1)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00'Z'"));
    String url = format(COMMITS, user, repo, aWeekAgo);
    Commit[] commits = restTemplate.getForObject(url, Commit[].class);
    LOG.info(format("Get commits by repo(%s): %d commits are found", url, commits.length));
    return Arrays.asList(commits);
  }

  public SingleCommit getSingleCommit(String user, String repo, String sha) {
    LOG.info(format("Get a single commit by sha(%s)", sha));
    return restTemplate.getForObject(format(SINGLE_COMMIT, user, repo, sha), SingleCommit.class);
  }

  public SingleCommit getSingleCommitByUrl(String url) {
    LOG.info(format("Get a single commit by url(%s)", url));
    return restTemplate.getForObject(url, SingleCommit.class);
  }
}
