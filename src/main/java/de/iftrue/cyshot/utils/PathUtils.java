package de.iftrue.cyshot.utils;

import java.io.File;
import java.nio.file.Path;

public abstract class PathUtils {
  public static Path toAbsolute(Path path) {
    return new File(path.toString()).getAbsoluteFile().toPath();
  }

  public static Path toAbsolute(String path) {
    return new File(path).getAbsoluteFile().toPath();
  }
}
