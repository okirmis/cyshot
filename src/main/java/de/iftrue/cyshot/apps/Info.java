package de.iftrue.cyshot.apps;

import com.google.gson.GsonBuilder;
import de.iftrue.cyshot.project.Project;
import de.iftrue.cyshot.project.ProjectConfiguration;
import de.iftrue.cyshot.project.SpecCollector;
import de.iftrue.cyshot.utils.PathUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@CommandLine.Command(name = "info", description = "Show the configuration of a project")
public class Info implements Runnable {
  @Data
  @NoArgsConstructor
  static class ProjectInfo {
    private String projectRoot;
    private String projectName;
    private String imageOutputPath;
    private String temporaryOutputPath;
    private List<String> inputFileFilters;
    private List<String> matchingFiles;
  }

  @CommandLine.Option(names = {"-p", "--path"}, defaultValue = ".", description = "Path to your application source and configuration")
  String path;

  @Inject
  Project project;

  @Inject
  SpecCollector specCollector;

  @Override
  public void run() {
    if (!project.load(path)) {
      return;
    }

    System.out.println(
      new GsonBuilder()
          .setPrettyPrinting()
          .create()
          .toJson(getInfo(), ProjectInfo.class)
    );
  }

  private ProjectInfo getInfo() {
    ProjectInfo info = new ProjectInfo();
    info.setProjectRoot(PathUtils.toAbsolute(project.getPath()).toString());
    info.setProjectName(project.getConfiguration().getName());
    info.setImageOutputPath(
        PathUtils.toAbsolute(
            project.getConfiguration().getImagePath()
        ).toString()
    );
    info.setTemporaryOutputPath(
        PathUtils.toAbsolute(
            project.getConfiguration().getTemporaryOutputPath()
        ).toString()
    );
    info.setInputFileFilters(
        project
            .getConfiguration()
            .getInputs()
            .stream()
            .map(ProjectConfiguration.Input::getPattern)
            .toList()
    );
    info.setMatchingFiles(
        specCollector
            .getFiles()
            .stream().map(Path::toString)
            .toList()
    );

    return info;
  }
}
