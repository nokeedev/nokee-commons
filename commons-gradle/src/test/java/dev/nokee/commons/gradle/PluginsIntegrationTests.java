package dev.nokee.commons.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.Arrays;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class PluginsIntegrationTests {
	@TempDir Path testDirectory;
	Project project;
	Plugins<Project> plugins;

	@BeforeEach
	void setup() {
		project = ProjectBuilder.builder().withProjectDir(testDirectory.toFile()).build();
		plugins = Plugins.forProject(project);
	}

	@Test
	void callsBackOnlyWhenAllPluginsApplied() {
		Runnable action = Mockito.mock();
		plugins.whenAllPluginsApplied(Arrays.asList(PluginA.class, PluginB.class, PluginC.class), action);

		project.getPluginManager().apply(PluginA.class);
		verify(action, never()).run();

		project.getPlugins().apply(PluginB.class);
		verify(action, never()).run();

		plugins.apply(PluginC.class);
		verify(action).run();
	}


	static class PluginA implements Plugin<Project> {
		@Override
		public void apply(Project target) {

		}
	}

	static class PluginB implements Plugin<Project> {

		@Override
		public void apply(Project target) {

		}
	}

	static class PluginC implements Plugin<Project> {

		@Override
		public void apply(Project target) {

		}
	}
}
