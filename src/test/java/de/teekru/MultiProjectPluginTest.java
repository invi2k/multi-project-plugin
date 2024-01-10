package de.teekru;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class MultiProjectPluginTest {
  @Test
  void apply_Success() {
    Project project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("de.teekru.multi-project");
  }
}
