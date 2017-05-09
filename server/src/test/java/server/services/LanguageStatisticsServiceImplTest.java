package server.services;

import static org.assertj.core.api.Assertions.*;

import org.junit.Before;

public class LanguageStatisticsServiceImplTest {
  private LanguageStatisticsService languageStatisticsService;

  @Before
  public void setup() {
    languageStatisticsService = new LanguageStatisticsServiceImpl();
  }
}
