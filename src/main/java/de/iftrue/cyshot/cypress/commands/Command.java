package de.iftrue.cyshot.cypress.commands;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class Command {
  private static final Map<String, String> COMMAND_LOCATIONS = new HashMap<>();

  protected abstract String getCommand();
  protected abstract List<String> getArguments();
  protected long getTimeoutInSeconds() { return 10; }
  protected String getWorkingDirectory() { return "."; }
  protected final String getDisplayCommandName() {
    return String.join(" ", this.getCommand());
  }

  public Result execute() {
    return Command.execute(getCommand(), getArguments(), getWorkingDirectory(), getTimeoutInSeconds());
  }

  protected static String resolveFromPath(String commandName) {
    if (commandName.startsWith("/")) {
      return commandName;
    }

    if (!COMMAND_LOCATIONS.containsKey(commandName)) {
      final Result result = Command.execute(
          "/bin/sh",
          List.of("-c", "which " + commandName), ".", 10
      );

      if (result.isSuccess()) {
        final String path = result.getOutput().trim();
        COMMAND_LOCATIONS.put(commandName, path);
        return path;
      }

      throw new CommandNotFound(commandName);
    }

    return COMMAND_LOCATIONS.get(commandName);
  }

  protected static Result execute(
      String command, List<String> arguments,
      String workingDir, long timeoutInSeconds
  ) {
    log.debug("Executing command {} {}", command, String.join(" ", arguments));

    try {
      final List<String> commandWithArguments = new ArrayList<>();
      commandWithArguments.add(resolveFromPath(command));
      commandWithArguments.addAll(arguments);

      final Process process = new ProcessBuilder()
          .command(commandWithArguments)
          .directory(new File(workingDir))
          .start();

      return new Result(process, process.waitFor(timeoutInSeconds, TimeUnit.SECONDS));
    } catch (IOException | InterruptedException exception) {
      return Result.INTERRUPTED;
    }
  }
}
