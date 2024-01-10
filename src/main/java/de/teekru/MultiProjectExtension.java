package de.teekru;

import org.gradle.api.initialization.Settings;

import java.io.File;
import java.nio.file.NotDirectoryException;

/**
 * MultiProjectExtension adds a configuration to the MultiProjectPlugin.
 * <br>
 * <code>
 * multiproject.directory = "path to the root directory for the subprojects"
 * </code>
 */
@SuppressWarnings("unused")
public class MultiProjectExtension {
  private final Settings settings;
  private File directory;

  /**
   * Creates a new MultiProjectExtension.
   *
   * @param settings the gradle settings
   */
  public MultiProjectExtension(Settings settings) {
    this.settings = settings;
    this.directory = new File(settings.getRootDir(), "src");
  }

  /**
   * Sets the root directory for the subprojects.
   *
   * @param file the root directory for the subprojects
   * @throws NotDirectoryException if the given file is not a directory
   */
  public void setDirectory(File file) throws NotDirectoryException {
    if (file.exists() && file.isFile()) {
      var msg = String.format("The multiproject directory is not a directory: %s", file);
      throw new NotDirectoryException(msg);
    }
    if (file.isAbsolute()) {
      this.directory = file;
    }
    else {
      this.directory = new File(this.settings.getRootDir(), file.toString());
    }
  }

  /**
   * Sets the root directory for the subprojects.
   *
   * @param path the root directory for the subprojects
   * @throws NotDirectoryException if the given path is not a directory
   */
  public void setDirectory(String path) throws NotDirectoryException {
    setDirectory(new File(path));
  }

  /**
   * Gets the root directory for the subprojects.
   *
   * @return the root directory for the subprojects
   */
  public File getDirectory() {
    return directory;
  }

}
