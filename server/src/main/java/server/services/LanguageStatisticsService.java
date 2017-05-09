package server.services;

import io.reactivex.Flowable;
import java.util.List;
import server.domain.CommittedFile;
import server.domain.GitHubStats;

public interface LanguageStatisticsService {

  List<GitHubStats> calcLangStats(Flowable<CommittedFile> files);
}
