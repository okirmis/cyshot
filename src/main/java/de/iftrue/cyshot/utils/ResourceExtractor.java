package de.iftrue.cyshot.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public abstract class ResourceExtractor {
  public static String readResource(String resource) throws IOException {
    try (final InputStream stream = ResourceExtractor.class
        .getResourceAsStream(resource)) {
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException exception) {
      log.error(
          "Failed to read resource {}: {}",
          resource, exception.getMessage()
      );
      throw exception;
    }
  }

  public static String forceReadResource(String resource) {
    try {
      return readResource(resource);
    } catch (IOException exception) {
      return null;
    }
  }

  public static void copyResourceTo(String resource, Path destination) throws IOException {
    try {
      Files.writeString(destination, readResource(resource));
    } catch (IOException exception) {
      log.error(
          "Failed to copy resource {} to {}: {}",
          resource, destination, exception.getMessage()
      );
      throw  exception;
    }
  }
}
