package de.iftrue.cyshot.cypress.commands;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class HeadedSpecRunnerCommand extends Command {
  private final String workingDirectory;

  @Override
  protected String getCommand() {
    return "npm";
  }

  @Override
  protected List<String> getArguments() {
    return List.of("run", "cypress:headed");
  }

  @Override
  protected long getTimeoutInSeconds() {
    return 36000;
  }

  @Override
  protected String getWorkingDirectory() {
    return workingDirectory;
  }
}
