package de.iftrue.cyshot.apps;

import de.iftrue.cyshot.project.SpecCollector;
import de.iftrue.cyshot.project.Project;
import de.iftrue.cyshot.cypress.DependencyInstaller;
import de.iftrue.cyshot.cypress.SpecResourceProvider;
import de.iftrue.cyshot.cypress.SpecRunner;
import de.iftrue.cyshot.cypress.SpecWriter;
import de.iftrue.cyshot.cypress.models.Spec;
import de.iftrue.cyshot.cypress.output.SpecOutput;
import io.quarkus.runtime.Quarkus;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@CommandLine.Command(name = "build", description = "Build the documentation from source")
public class Build implements Runnable {

  @CommandLine.Option(names = {"-p", "--path"}, defaultValue = ".", description = "Path to your application source and configuration")
  String path;

  @CommandLine.Option(names = "--build", negatable = true, defaultValue = "true", description = "Automatically run the specs")
  boolean build = true;

  @CommandLine.Option(names = "--ui", defaultValue = "false", description = "Run specs with UI")
  boolean runWithUi = false;

  @CommandLine.Option(names = {"--parallel", "--jobs", "-j"}, defaultValue = "1", description = "Number of specs executed in parallel")
  int maxParallelProcesses = 1;

  @Inject
  Project project;

  @Inject
  SpecCollector specCollector;

  @Inject
  SpecWriter specWriter;

  @Inject
  SpecRunner specRunner;

  @Inject
  SpecResourceProvider resourceProvider;

  @Inject
  DependencyInstaller dependencyInstaller;

  @SneakyThrows
  @Override
  public void run() {
    if (!project.load(path)) {
      return;
    }

    log.info("Project name: {}", project.getConfiguration().getName());

    final List<Spec> specs = specCollector.getSpecs();
    if (!specWriter.write(specs) || !dependencyInstaller.install()) {
      return;
    }

    if (build) {
      final Path imageOutputDirectory = Path.of(project.getConfiguration().getImagePath());
      try {
        Files.createDirectories(imageOutputDirectory);
      } catch (IOException exception) {
        log.error("Failed to create image output directory {}", imageOutputDirectory);
        return;
      }

      List<SpecOutput> specOutputs;
      if (runWithUi) {
        specOutputs = specRunner.runAllSpecsWithUi(specs);
      } else {
        ForkJoinPool threadPool = new ForkJoinPool(maxParallelProcesses);
        specOutputs = threadPool.submit(
            () -> specs
                .parallelStream()
                .map(specRunner::runSingleSpecHeadless)
                .toList()
        ).get();
        threadPool.shutdown();
      }

      resourceProvider.copyImages(specOutputs);
      resourceProvider.storeProperties(specOutputs);
    }

    Quarkus.asyncExit(0);
    Quarkus.waitForExit();
  }
}
