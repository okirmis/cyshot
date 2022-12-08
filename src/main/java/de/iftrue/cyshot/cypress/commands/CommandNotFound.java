package de.iftrue.cyshot.cypress.commands;

public class CommandNotFound extends RuntimeException {
  CommandNotFound(String commandName) {
    super("Command not found: " + commandName);
  }
}
