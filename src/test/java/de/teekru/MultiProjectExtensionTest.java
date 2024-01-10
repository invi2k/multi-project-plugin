package de.teekru;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class MultiProjectExtensionTest {
  private Project project;
  private MultiProjectExtension extension;

  @BeforeEach
  @Inject
  void setup() {
    project = ProjectBuilder.builder().build();
    extension = project.getExtensions().create(
        MultiProjectExtension.CONFIG_NAME,
        MultiProjectExtension.class
    );
  }

  @Test
  void create_Success() {
    var sourceDir = new File(project.getProjectDir(), "src");
    var buildDir = new File(project.getProjectDir(), "build");
    assertThat(extension.getSourceDirectory().getAsFile().get()).isEqualTo(sourceDir);
    assertThat(extension.getBuildDirectory().getAsFile().get()).isEqualTo(buildDir);
    assertThat(extension.getModules().get()).isEmpty();
  }

  @Test
  void setSourceDir_Success_String() {
    var expected = new File(project.getProjectDir(), "src2");

    extension.setSourceDirectory(expected.toString());

    assertThat(extension.getSourceDirectory().isPresent()).isTrue();
    assertThat(extension.getSourceDirectory().getAsFile().get()).isEqualTo(expected);
  }

  @Test
  void setSourceDir_Success_Directory() {
    var expected = project.getLayout().getProjectDirectory().dir("src2");

    extension.setSourceDirectory(expected);

    assertThat(extension.getSourceDirectory().isPresent()).isTrue();
    assertThat(extension.getSourceDirectory().get()).isEqualTo(expected);
  }

  @Test
  void setBuildDir_Success_String() {
    var expected = new File(project.getProjectDir(), "build2");

    extension.setBuildDirectory(expected.toString());

    assertThat(extension.getBuildDirectory().isPresent()).isTrue();
    assertThat(extension.getBuildDirectory().getAsFile().get()).isEqualTo(expected);
  }

  @Test
  void setBuildDir_Success_Directory() {
    var expected = project.getLayout().getProjectDirectory().dir("build2");

    extension.setBuildDirectory(expected);

    assertThat(extension.getBuildDirectory().isPresent()).isTrue();
    assertThat(extension.getBuildDirectory().get()).isEqualTo(expected);
  }
}
