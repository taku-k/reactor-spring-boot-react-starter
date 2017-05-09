package server.gateways;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import server.domain.Commit;
import server.domain.CommittedFile;
import server.domain.Repository;
import server.domain.SingleCommit;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GitHubGatewayTest {
  @Autowired private RestTemplate restTemplate;

  @Autowired private GitHubGateway gitHubGateway;

  private static final String user = "taku-k";
  private static final String repo = "rx-spring-boot-react-starter";
  private static final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db5e";

  @Test
  public void getRepos() {
    MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    mockServer
        .expect(requestTo(String.format(GitHubGateway.REPOS, user)))
        .andRespond(
            withSuccess(
                "[{\"name\":\"repo1\",\"pushed_at\":\"2016-11-28T14:12:46Z\"}]",
                MediaType.APPLICATION_JSON));

    List<Repository> repos = gitHubGateway.getRepos(user);

    assertThat(repos.size()).isEqualTo(1);
    assertThat(repos.get(0).getPushed().getHour()).isEqualTo(14);
    mockServer.verify();
  }

  @Test
  public void getCommits() {
    String aWeekAgo =
        ZonedDateTime.now(ZoneOffset.UTC)
            .minusWeeks(1)
            .minusDays(1)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00'Z'"));
    ;
    MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    mockServer
        .expect(requestTo(String.format(GitHubGateway.COMMITS, user, repo, aWeekAgo)))
        .andRespond(
            withSuccess(
                "[{\"sha\":\""
                    + sha
                    + "\",\"url\":\"https://example.com\",\"committer\":{\"login\":\""
                    + user
                    + "\"}}]",
                MediaType.APPLICATION_JSON));

    List<Commit> commits = gitHubGateway.getCommitsInWeek(user, repo);

    assertThat(commits.size()).isEqualTo(1);
    assertThat(commits.get(0).getCommitter().getLogin()).isEqualTo("taku-k");
    mockServer.verify();
  }

  @Test
  public void getSingleCommit() {
    MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    mockServer
        .expect(requestTo(String.format(GitHubGateway.SINGLE_COMMIT, user, repo, sha)))
        .andRespond(
            withSuccess(
                "{\"sha\":\"" + sha + "\",\"files\":[{\"filename\":\"file1.txt\",\"changes\":12}]}",
                MediaType.APPLICATION_JSON));

    SingleCommit singleCommit = gitHubGateway.getSingleCommit(user, repo, sha);

    assertThat(singleCommit.getFiles())
        .extracting(CommittedFile::getFilename, CommittedFile::getChanges)
        .containsExactly(tuple("file1.txt", 12L));
  }
}
