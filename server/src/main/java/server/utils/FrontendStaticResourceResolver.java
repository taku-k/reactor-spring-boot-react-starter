package server.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

public class FrontendStaticResourceResolver implements ResourceResolver {

  private static final Logger LOG = LoggerFactory.getLogger(FrontendStaticResourceResolver.class);
  private Resource index = new ClassPathResource("/static/index.html");
  private List<String> ignoredPaths = Arrays.asList("api");

  @Override
  public Resource resolveResource(
      HttpServletRequest request,
      String requestPath,
      List<? extends Resource> locations,
      ResourceResolverChain chain) {
    LOG.debug(String.format("Requested to [%s]", requestPath));
    return resolve(requestPath, locations);
  }

  @Override
  public String resolveUrlPath(
      String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {
    Resource resolvedResource = resolve(resourcePath, locations);
    if (resolvedResource == null) {
      return null;
    }
    try {
      return resolvedResource.getURL().toString();
    } catch (IOException e) {
      return resolvedResource.getFilename();
    }
  }

  private Resource resolve(String requestPath, List<? extends Resource> locations) {
    if (ignoredPaths.contains(requestPath)) {
      return null;
    }
    if (StringUtils.getFilenameExtension(requestPath) == null) {
      return index;
    }
    return locations
        .stream()
        .map(loc -> createRelative(loc, requestPath))
        .filter(resource -> resource != null && resource.exists())
        .findFirst()
        .orElse(index);
  }

  private Resource createRelative(Resource resource, String relativePath) {
    try {
      return resource.createRelative(relativePath);
    } catch (IOException e) {
      return null;
    }
  }
}
