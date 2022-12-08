package de.iftrue.cyshot.cypress.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class TestCase {
  private String name;
  private String content;
}
