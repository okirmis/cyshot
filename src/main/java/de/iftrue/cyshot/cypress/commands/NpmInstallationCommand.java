package de.iftrue.cyshot.cypress.commands;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class NpmInstallationCommand extends Command {
  private final Path workingDirectory;

  @Override
  protected String getCommand() {
    return "npm";
  }

  @Override
  protected List<String> getArguments() {
    return List.of("install", "--no-audit", "--no-fund");
  }

  @Override
  protected long getTimeoutInSeconds() {
    return 1800;
  }

  @Override
  protected String getWorkingDirectory() {
    return workingDirectory.toString();
  }
}
