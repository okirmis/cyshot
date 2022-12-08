package de.iftrue.cyshot.cypress.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.nio.file.Path;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
public class Spec {
  private final String name;
  private final List<TestCase> testCases;

  @Setter
  private Path filePath;
}
