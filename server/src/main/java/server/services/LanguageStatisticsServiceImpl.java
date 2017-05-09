package server.services;

import io.reactivex.Flowable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.domain.CommittedFile;
import server.domain.GitHubStats;
import server.domain.Language;
import server.utils.LanguageSavant;

public class LanguageStatisticsServiceImpl implements LanguageStatisticsService {

  private static final Logger LOG = LoggerFactory.getLogger(LanguageStatisticsServiceImpl.class);

  private static final LanguageSavant savant = new LanguageSavant();

  @Override
  public List<GitHubStats> calcLangStats(Flowable<CommittedFile> files) {
    List<GitHubStats> stats = new ArrayList<>();
    Map<Language, List<CommittedFile>> filesByLang = new HashMap<>();
    AtomicLong sum = new AtomicLong(0);
    files.subscribe(
        file -> {
          savant
              .getLangByExtension(file.getFilename())
              .ifPresent(
                  lang -> {
                    filesByLang.putIfAbsent(lang, new ArrayList<>());
                    filesByLang.get(lang).add(file);
                    sum.addAndGet(file.getChanges());
                  });
        });
    for (Map.Entry<Language, List<CommittedFile>> e : filesByLang.entrySet()) {
      long sumLang = e.getValue().stream().mapToLong(CommittedFile::getChanges).sum();
      double rate = (double) sumLang / (double) sum.get() * 100.0;
      Language lang = e.getKey();
      stats.add(
          GitHubStats.builder().name(lang.getName()).color(lang.getColor()).ratio(rate).build());
    }
    stats.sort(Comparator.comparing(GitHubStats::getRatio).reversed());
    return stats;
  }
}
