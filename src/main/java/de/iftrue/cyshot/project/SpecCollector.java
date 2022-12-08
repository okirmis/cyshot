package de.iftrue.cyshot.project;

import de.iftrue.cyshot.cypress.models.Spec;
import de.iftrue.cyshot.cypress.models.TestCase;
import de.iftrue.cyshot.utils.PathUtils;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Dependent
public class SpecCollector {
  @Inject
  Project project;

  public List<Path> getFiles() {
    final List<Path> paths = new ArrayList<>();

    this.findFiles(
        paths,
        project
            .getConfiguration()
            .getInputs()
            .stream()
            .map(input -> FileSystems.getDefault().getPathMatcher(input.getPattern()))
            .toList()
    );

    return paths;
  }

  public List<Spec> getSpecs() {
    return getFiles().stream().map(
        path -> {
          try {
            return new Spec(
                PathUtils.toAbsolute(path).toString(),
                List.of(new TestCase("main", Files.readString(path)))
            );
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
    ).toList();
  }

  private void findFiles(List<Path> paths, List<PathMatcher> matchers) {
    try {
      Files.walkFileTree(Path.of(project.getPath()), new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          if (matchers.stream().anyMatch(matcher -> matcher.matches(file))) {
            paths.add(file.toRealPath(LinkOption.NOFOLLOW_LINKS));
          }

          return FileVisitResult.CONTINUE;
        }
      });
    } catch (Exception e) {
      log.warn("Failed to list input files");
    }
  }
}
