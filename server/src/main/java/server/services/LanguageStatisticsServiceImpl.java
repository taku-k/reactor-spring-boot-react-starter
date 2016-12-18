package server.services;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import server.domain.Language;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class LanguageStatisticsServiceImpl implements LanguageStatisticsService {
    private class LanguageConstructor extends Constructor {
        @Override
        @SuppressWarnings("unchecked")
        protected Object constructObject(Node node) {
            if (node.getTag() == Tag.MAP) {
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super
                        .constructObject(node);
                if (Language.isValid(map)) {
                    return Language.createFromMap(map);
                }
            }
            return super.constructObject(node);
        }
    }

    private Map<String, Language> languages;

    private Map<String, Language> extensionLanguage;

    private Map<String, Language> filenameLanguage;

    @SuppressWarnings("unchecked")
    public LanguageStatisticsServiceImpl() {
        // Load languages.yml and construct Language instances
        Yaml yaml = new Yaml(new LanguageConstructor());
        languages = (Map<String, Language>) yaml.load(
                getClass().getClassLoader().getResourceAsStream("languages.yml"));
        languages.entrySet().forEach(stringLanguageEntry ->
                stringLanguageEntry.getValue().setName(stringLanguageEntry.getKey())
        );

        // Generate hash map which key is extension or filename and value is Language instance
        extensionLanguage = new HashMap<>();
        filenameLanguage = new HashMap<>();
        languages.values().forEach(language -> {
            Optional.ofNullable(language.getExtensions())
                    .ifPresent(extensions ->
                            extensions.forEach(ext ->
                                    extensionLanguage.put(ext, language)
                            )
                    );
            Optional.ofNullable(language.getFilenames())
                    .ifPresent(filenames ->
                            filenames.forEach(filename ->
                                    filenameLanguage.put(filename, language)
                            )
                    );
        });
    }

    @Override
    public Language getLanguageByName(String name) {
        return languages.get(name);
    }
}
