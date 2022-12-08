package de.iftrue.cyshot.cypress;

import de.iftrue.cyshot.project.Project;
import de.iftrue.cyshot.cypress.commands.NpmInstallationCommand;
import de.iftrue.cyshot.cypress.commands.Result;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.nio.file.Path;

@Slf4j
@Dependent
public class DependencyInstaller {
  @Inject
  Project project;

  public boolean install() {
    log.info("Installing dependencies using npm ...");

    final Result processResult = new NpmInstallationCommand(
        Path.of(
            project
                .getConfiguration()
                .getTemporaryOutputPath(),
            FileNames.SPEC_DIRECTORY_NAME
        )
    ).execute();

    if (!processResult.isSuccess()) {
      log.error("Failed to install dependencies");
    }

    return processResult.isSuccess();
  }
}
