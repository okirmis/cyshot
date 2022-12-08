package de.iftrue.cyshot.project;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.util.List;

@Getter
public class ProjectConfiguration {
  @Getter
  @NoArgsConstructor
  public static class Input {
    String pattern = "glob:**.cyshot.js";
  }

  String name = "Unknown Project";
  String imagePath = "./cyshot/images";
  String temporaryOutputPath = "./cyshot/temp";
  List<Input> inputs = List.of(new Input());

  @SneakyThrows
  public void resolvePaths(Path path) {
    this.imagePath = path
        .resolve(this.imagePath)
        .toFile()
        .getCanonicalPath();

    this.temporaryOutputPath = path
        .resolve(this.temporaryOutputPath)
        .toFile()
        .getCanonicalPath();
  }
}
