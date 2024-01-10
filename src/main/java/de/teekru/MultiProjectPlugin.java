package de.teekru;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.initialization.Settings;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * MultiProjectPlugin automatically create Gradle Projects for directories in a configured
 * root directory.
 */
@SuppressWarnings("unused")
public class MultiProjectPlugin implements Plugin<Settings> {
  private MultiProjectExtension extension;
  private Directory rootBuildDir;

  /**
   * Create a new MultiProjectPlugin.
   */
  public MultiProjectPlugin() {
  }

  /**
   * Plugin entry point.
   *
   * @param settings the gradle settings
   */
  @Override
  public void apply(Settings settings) {
    this.extension = settings.getExtensions().create(
        "multiproject",
        MultiProjectExtension.class,
        settings
    );

    settings.getGradle().settingsEvaluated(this::settingsEvaluated);
    settings.getGradle().rootProject(this::initRootProject);
  }

  private void settingsEvaluated(Settings settings) {
    if (extension.getDirectory().exists()) {
      includeSubprojects(settings, extension.getDirectory().toPath());
    }
  }

  private void includeSubprojects(Settings settings, Path src) {
    try (var stream = Files.newDirectoryStream(src)) {
      stream.forEach(path -> {
        var name = path.getName(path.getNameCount() - 1).toString();
        settings.include(name);
        var project = settings.project(":" + name);
        project.setProjectDir(path.toFile());
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void initRootProject(Project rootProject) {
    rootBuildDir = rootProject.getLayout().getBuildDirectory().get();
    rootProject.getSubprojects().forEach(this::initSubProject);
  }

  private void initSubProject(Project subproject) {
    var buildDir = rootBuildDir.dir(subproject.getName());
    subproject.getLayout().getBuildDirectory().set(buildDir);
    subproject.afterEvaluate(this::initSourceSets);
  }

  private void initSourceSets(Project subproject) {
    if (!(subproject.findProperty("sourceSets") instanceof SourceSetContainer sourceSets)) {
      return;
    }
    sourceSets.forEach(sourceSet -> initSourceSet(subproject, sourceSet));
  }

  private void initSourceSet(Project subproject, SourceSet sourceSet) {
    changeSourceDirs(subproject, sourceSet.getJava());
    changeSourceDirs(subproject, sourceSet.getResources());
    changeSourceDirs(subproject, sourceSet.getAllSource());
  }

  private void changeSourceDirs(Project subproject, SourceDirectorySet set) {
    var root = extension.getDirectory().toPath();
    set.setSrcDirs(set.getSrcDirs().stream()
                      .map(file -> {
                        var path = file.toPath();
                        var relpath = root.relativize(path);
                        if (relpath.startsWith(Path.of(subproject.getName(), "src"))) {
                          var newPath = root.resolve(relpath.getName(0))
                                            .resolve(relpath.subpath(2, relpath.getNameCount()));
                          return newPath.toFile();
                        }
                        return file;
                      })
                      .collect(Collectors.toSet())
    );
  }
}
