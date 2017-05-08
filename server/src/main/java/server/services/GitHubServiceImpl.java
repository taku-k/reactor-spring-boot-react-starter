package server.services;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import java.time.LocalDateTime;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.domain.Commit;
import server.domain.CommittedFile;
import server.domain.Repository;
import server.domain.SingleCommit;
import server.gateways.GitHubGateway;

public class GitHubServiceImpl implements GitHubService {

  private static final Logger LOG = LoggerFactory.getLogger(GitHubServiceImpl.class);
  private final GitHubGateway gitHubGateway;

  public GitHubServiceImpl(GitHubGateway gitHubGateway) {
    this.gitHubGateway = gitHubGateway;
  }

  @Override
  public Flowable<String> getRepos(String user) {
    return Flowable.create(
        emitter -> {
          gitHubGateway.getRepos(user).stream().map(Repository::getName).forEach(emitter::onNext);
          emitter.onComplete();
        },
        BackpressureStrategy.BUFFER);
  }

  @Override
  public Flowable<String> getReposInWeek(String user) {
    return Flowable.create(
        emitter -> {
          gitHubGateway
              .getRepos(user)
              .stream()
              .filter(repo -> repo.getPushed().isAfter(LocalDateTime.now().minusWeeks(1)))
              .map(Repository::getName)
              .forEach(emitter::onNext);
          emitter.onComplete();
        },
        BackpressureStrategy.BUFFER);
  }

  @Override
  public Flowable<Commit> getCommitsInWeek(String user, String repo) {
    return Flowable.create(
        emitter -> {
          gitHubGateway
              .getCommitsInWeek(user, repo)
              .stream()
              .filter(commit -> commit.getCommitter().getLogin().equals(user))
              .forEach(emitter::onNext);
          emitter.onComplete();
        },
        BackpressureStrategy.BUFFER);
  }

  @Override
  public Flowable<CommittedFile> getCommittedFiles(String user, String repo, String sha) {
    return Flowable.create(
        emitter -> {
          gitHubGateway.getSingleCommit(user, repo, sha).getFiles().forEach(emitter::onNext);
          emitter.onComplete();
        },
        BackpressureStrategy.BUFFER);
  }

  @Override
  public Flowable<CommittedFile> getCommittedFilesByUrl(String url) {
    return Flowable.create(
        emitter -> {
          SingleCommit commit = gitHubGateway.getSingleCommitByUrl(url);
          LOG.info(commit.toString());
          commit
              .getFiles()
              .forEach(
                  files -> {
                    LOG.info(files.toString());
                    if (files != null) {
                      emitter.onNext(files);
                    }
                  });
          emitter.onComplete();
        },
        BackpressureStrategy.BUFFER);
  }

  @Override
  public Flowable<CommittedFile> getCommittedFilesByUser(String user) {
    return getReposInWeek(user)
        .flatMap(
            repo ->
                Flowable.combineLatest(
                    Flowable.just(repo), getCommitsInWeek(user, repo), (r, c) -> Pair.of(r, c)))
        .flatMap(
            pair -> {
              return getCommittedFiles(user, pair.getLeft(), pair.getRight().getSha());
            });
  }
}
