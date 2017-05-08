package server.services;

import io.reactivex.Flowable;
import server.domain.Commit;
import server.domain.CommittedFile;

public interface GitHubService {

  Flowable<String> getRepos(String user);

  Flowable<String> getReposInWeek(String user);

  Flowable<Commit> getCommitsInWeek(String user, String repo);

  Flowable<CommittedFile> getCommittedFiles(String user, String repo, String sha);

  Flowable<CommittedFile> getCommittedFilesByUrl(String url);

  Flowable<CommittedFile> getCommittedFilesByUser(String user);
}
