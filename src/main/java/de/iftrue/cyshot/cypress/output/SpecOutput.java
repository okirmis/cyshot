package de.iftrue.cyshot.cypress.output;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.iftrue.cyshot.cypress.models.Spec;
import de.iftrue.cyshot.utils.PathUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Getter
@Accessors(fluent = true)
public class SpecOutput {
  private static final String IMAGES = "images";
  private static final String PROPERTIES = "properties";

  private final Spec spec;
  private final Map<String, Path> images = new HashMap<>();
  private final Map<String, JsonElement> properties = new HashMap<>();

  @SneakyThrows
  public SpecOutput(Spec spec) {
    this.spec = spec;

    final Path imageOutputDirectory = getImageOutputDirectory(spec);
    if (Files.exists(imageOutputDirectory)) {
      Files
          .list(imageOutputDirectory)
          .forEach(this::storeImage);
    }

    JsonObject properties = JsonParser
        .parseString(
            Files.readString(getPropertiesOutputFile(spec))
        )
        .getAsJsonObject();

    properties.keySet().forEach(
        key -> this.properties.put(key, properties.get(key))
    );
  }

  private void storeImage(Path path) {
    this.images.put(
        path.getFileName().toString(),
        PathUtils.toAbsolute(path)
    );
  }

  private Path getImageOutputDirectory(Spec spec) {
    return spec
        .filePath()
        .getParent()
        .resolve(IMAGES)
        .resolve(spec.filePath().getFileName());
  }

  private Path getPropertiesOutputFile(Spec spec) {
    return spec
        .filePath()
        .getParent()
        .resolve(PROPERTIES)
        .resolve(
            "%s.json"
                .formatted(
                    spec.filePath().getFileName()
                )
        );
  }
}
