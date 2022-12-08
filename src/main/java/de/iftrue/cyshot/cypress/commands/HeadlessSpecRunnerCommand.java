package de.iftrue.cyshot.cypress.commands;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class HeadlessSpecRunnerCommand extends Command {
  private final String workingDirectory;
  private final String specFileName;

  @Override
  protected String getCommand() {
    return "npm";
  }

  @Override
  protected List<String> getArguments() {
    return List.of("run", "cypress:headless", "--", specFileName);
  }

  @Override
  protected long getTimeoutInSeconds() {
    return 1800;
  }

  @Override
  protected String getWorkingDirectory() {
    return workingDirectory;
  }
}
