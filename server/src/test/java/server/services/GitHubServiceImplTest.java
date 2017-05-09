package server.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import io.reactivex.subscribers.TestSubscriber;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import server.domain.*;
import server.gateways.GitHubGateway;

@RunWith(SpringJUnit4ClassRunner.class)
public class GitHubServiceImplTest {
  @Mock private GitHubGateway gitHubGateway;

  private GitHubService gitHubService;

  @Before
  public void setup() {
    initMocks(this);
    gitHubService = new GitHubServiceImpl(gitHubGateway);

    when(gitHubGateway.getRepos(anyString())).thenAnswer(m -> getReposForTest());
    when(gitHubGateway.getCommitsInWeek(anyString(), anyString()))
        .thenAnswer(m -> getCommitsInWeekForTest());
    when(gitHubGateway.getSingleCommit(anyString(), anyString(), anyString()))
        .thenAnswer(m -> getSingleCommitForTest());
    when(gitHubGateway.getSingleCommitByUrl(anyString())).thenAnswer(m -> getSingleCommitForTest());
  }

  @Test
  public void getReposInWeek() throws Exception {
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();

    gitHubService.getReposInWeek("test-user").subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    assertThat(testSubscriber.values())
        .as("getReposInWeek returns updated repos within one week")
        .containsExactly("repo2", "repo3");
  }

  @Test
  public void getCommitsInWeek() throws Exception {
    TestSubscriber<Commit> testSubscriber = new TestSubscriber<>();

    gitHubService.getCommitsInWeek("test-user", "test-repo").subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    assertThat(testSubscriber.values())
        .as("getCommitsInWeek returns commits updated by `user` within one week")
        .extracting(Commit::getSha)
        .containsExactly("sha1");
  }

  @Test
  public void fetchCommittedFilesByUserNormally() {
    TestSubscriber<CommittedFile> testSubscriber = new TestSubscriber<>();

    gitHubService.getCommittedFilesByUser("test-user").subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    assertThat(testSubscriber.values())
        .as("getCommittedFilesByUser returns CommittedFiles by `user`")
        .extracting(CommittedFile::getFilename)
        .containsOnly("filename1", "filename1", "filename2", "filename2");
  }

  private List<Repository> getReposForTest() {
    try {
      Thread.sleep(300);
    } catch (InterruptedException ignore) {
    }
    return Arrays.asList(
        new Repository("repo1", LocalDateTime.now().minusMonths(1)),
        new Repository("repo2", LocalDateTime.now()),
        new Repository("repo3", LocalDateTime.now().minusDays(3)));
  }

  private List<Commit> getCommitsInWeekForTest() {
    try {
      Thread.sleep(300);
    } catch (InterruptedException ignore) {
    }
    return Arrays.asList(
        Commit.builder()
            .sha("sha1")
            .committer(new Committer("test-user"))
            .commit(new CommitDetail("url1"))
            .build(),
        Commit.builder()
            .sha("sha2")
            .committer(new Committer("no-test-user"))
            .commit(new CommitDetail("url2"))
            .build());
  }

  private SingleCommit getSingleCommitForTest() {
    try {
      Thread.sleep(300);
    } catch (InterruptedException ignore) {
    }
    return new SingleCommit(
        Arrays.asList(new CommittedFile("filename1", 10), new CommittedFile("filename2", 20)));
  }
}
