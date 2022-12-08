package de.iftrue.cyshot.cypress.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Result {
  private boolean completed;
  private String output;
  private String error;
  private int exitCode;

  public static Result INTERRUPTED = new Result(false, "", "", 0);

  Result(Process process, boolean completed) throws IOException {
    this.completed = completed;
    this.exitCode = process.exitValue();
    this.error = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
    this.output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
  }

  public boolean isSuccess() {
    return this.completed && this.exitCode == 0;
  }
}
