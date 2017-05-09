package server.config;

import com.uber.jaeger.Configuration.ReporterConfiguration;
import com.uber.jaeger.Configuration.SamplerConfiguration;
import com.uber.jaeger.samplers.ConstSampler;
import io.opentracing.Tracer;
import io.opentracing.contrib.spanmanager.DefaultSpanManager;
import io.opentracing.contrib.spanmanager.SpanManager;
import io.opentracing.contrib.spring.web.client.TracingRestTemplateInterceptor;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import server.gateways.GitHubGateway;
import server.services.GitHubService;
import server.services.GitHubServiceImpl;
import server.services.LanguageStatisticsService;
import server.services.LanguageStatisticsServiceImpl;
import server.services.TodoService;
import server.services.TodoServiceImpl;

@Configuration
public class AppConfig {

  @Bean
  public TodoService todoService() {
    return new TodoServiceImpl();
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setInterceptors(
        Collections.singletonList(new TracingRestTemplateInterceptor(tracer())));
    return restTemplate;
  }

  @Bean
  public GitHubGateway gitHubGateway() {
    return new GitHubGateway(restTemplate());
  }

  @Bean
  public LanguageStatisticsService languageStatisticsService() {
    return new LanguageStatisticsServiceImpl();
  }

  @Bean
  public GitHubService gitHubService() {
    return new GitHubServiceImpl(gitHubGateway());
  }

  @Bean
  public Tracer tracer() {
    com.uber.jaeger.Configuration conf =
        new com.uber.jaeger.Configuration(
            "rx-spring-boot-react-starter-server",
            new SamplerConfiguration(ConstSampler.TYPE, 1),
            new ReporterConfiguration());
    return conf.getTracer();
  }

  @Bean
  public SpanManager spanManager() {
    return DefaultSpanManager.getInstance();
  }
}
