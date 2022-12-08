package de.iftrue.cyshot.cypress;

import de.iftrue.cyshot.project.Project;
import de.iftrue.cyshot.cypress.models.Spec;
import de.iftrue.cyshot.utils.PathUtils;
import de.iftrue.cyshot.utils.ResourceExtractor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Dependent
@RequiredArgsConstructor
public class SpecWriter {

  @Inject
  SpecCodeGenerator builder;

  @Inject
  Project project;

  @SneakyThrows
  public boolean write(List<Spec> specs) {
    final String targetDirectory = project.getConfiguration().getTemporaryOutputPath();

    createDirectory(targetDirectory);
    ResourceExtractor.copyResourceTo(
        "/cypress/package.json",
        Path.of(targetDirectory, FileNames.SPEC_DIRECTORY_NAME, "package.json")
    );
    ResourceExtractor.copyResourceTo(
        "/cypress/cypress.config.js",
        Path.of(targetDirectory, FileNames.SPEC_DIRECTORY_NAME, "cypress.config.js")
    );
    ResourceExtractor.copyResourceTo(
        "/cypress/commands.js",
        Path.of(targetDirectory, FileNames.SPEC_DIRECTORY_NAME, "commands.js")
    );

    for (Spec spec : specs) {
      final Path path = getSpecFileName(targetDirectory, spec);

      try {
        spec.filePath(path);

        Files.writeString(path, builder.buildSpec(spec));
        log.info("Writing spec for {}", spec.name());
      } catch (IOException e) {
        log.error("Failed to write spec {}: {}", path, e.getMessage());

        return false;
      }
    }

    return true;
  }

  private Path getSpecFileName(String targetDirectory, Spec spec) {
    return Path.of(
        targetDirectory,
        FileNames.SPEC_DIRECTORY_NAME,
        "%s-%s.spec.js"
            .formatted(
                DigestUtils.md5Hex(spec.name()),
                Path.of(spec.name())
                  .getFileName()
                  .toString()
                  .replaceAll(FileNames.DESCRIPTION_FILE_EXTENSION, "")
            )
    );
  }

  private void createDirectory(String targetDirectory) {
    final Path directory = Path.of(targetDirectory, FileNames.SPEC_DIRECTORY_NAME);

    if (Files.exists(directory)) {
      try {
        Files
            .walk(directory)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
      } catch (IOException e) {
        log.warn("Failed to remove existing directory {}", PathUtils.toAbsolute(directory));
      }
    }

    createMissingDirectory(directory);
    createMissingDirectory(directory.resolve(FileNames.PROPERTIES_DIRECTORY_NAME));
  }

  private void createMissingDirectory(Path directory) {
    try {
      Files.createDirectories(directory);
    } catch (IOException e) {
      log.error("Failed to create directory {}", PathUtils.toAbsolute(directory));
    }
  }
}
