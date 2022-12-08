package de.iftrue.cyshot.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@ApplicationScoped
public class Project {
  public static String PROJECT_FILE_NAME = "cyshot.json";

  @Getter
  private ProjectConfiguration configuration = new ProjectConfiguration();

  @Getter
  private String path;

  public boolean load(String path) {
    final Path projectFileName = Path.of(path, PROJECT_FILE_NAME);

    if (Files.exists(projectFileName)) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        this.configuration = mapper.readValue(new File(projectFileName.toString()), ProjectConfiguration.class);
      } catch (IOException e) {
        log.error("Failed to load project configuration file ({}): {}", projectFileName,  e.getMessage());
        return false;
      }
    }

    this.configuration.resolvePaths(Path.of(path));
    this.path = path;
    return true;
  }
}
