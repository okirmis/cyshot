package de.iftrue.cyshot.cypress;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.iftrue.cyshot.project.Project;
import de.iftrue.cyshot.cypress.models.Spec;
import de.iftrue.cyshot.cypress.output.SpecOutput;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Slf4j
@Dependent
public class SpecResourceProvider {
  @Inject
  Project project;

  public boolean copyImages(SpecOutput output) {
    if (output.images().isEmpty()) {
      log.info("{} did not produce any images", output.spec().name());
      return true;
    }

    return output
        .images()
        .values()
        .stream()
        .allMatch(
            imagePath -> this.copyImage(output.spec(), imagePath)
        );
  }

  public boolean copyImages(List<SpecOutput> outputs) {
    return outputs.stream().allMatch(this::copyImages);
  }

  @SneakyThrows
  public boolean storeProperties(List<SpecOutput> outputs) {
    final JsonObject object = new JsonObject();
    outputs.forEach(output -> output.properties().forEach(object::add));

    final Path propertiesFilePath = Path.of(
        project
            .getConfiguration()
            .getImagePath()
    )
        .toRealPath(LinkOption.NOFOLLOW_LINKS)
        .resolve(FileNames.PROPERTIES_FILE_NAME);

    try {
      Files.writeString(
          propertiesFilePath,
          new GsonBuilder()
              .setPrettyPrinting()
              .create()
              .toJson(object)
      );

      log.info("Stored properties to {}", propertiesFilePath);

      return true;
    } catch (IOException exception) {
      log.error("Failed to store properties to {}: {}", propertiesFilePath, exception.getMessage());

      return false;
    }
  }

  protected boolean copyImage(Spec spec, Path imagePath) {
    try {
      Files.copy(
          imagePath,
          Path.of(project.getConfiguration().getImagePath(), imagePath.getFileName().toString()),
          StandardCopyOption.REPLACE_EXISTING
      );
      log.info("Found image {} ({})", imagePath.getFileName(), spec.name());

      return true;
    } catch (IOException exception) {
      log.error(
          "Failed to copy {} ({}): {}",
          imagePath.getFileName(),
          spec.name(),
          exception.getMessage()
      );

      return false;
    }
  }
}
