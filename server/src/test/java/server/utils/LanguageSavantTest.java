package server.utils;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import server.domain.Language;

public class LanguageSavantTest {
  private LanguageSavant savant;

  @Before
  public void setup() {
    savant = new LanguageSavant();
  }

  @Test
  public void languagesHasRubyLanguage() throws Exception {
    Optional<Language> ruby = savant.getLangByExtension(".rb");
    assertThat(ruby).isNotNull();
  }
}
