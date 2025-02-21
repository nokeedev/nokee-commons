package dev.nokee.commons.gradle.provider;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static dev.nokee.commons.gradle.provider.ProviderUtils.flatten;
import static dev.nokee.commons.hamcrest.gradle.BuildDependenciesMatcher.buildDependencies;
import static dev.nokee.commons.hamcrest.gradle.NamedMatcher.named;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

class FlattenProviderIntegrationTests {
	Project project;
	@TempDir Path testDirectory;

	@BeforeEach
	void setup() {
		project = ProjectBuilder.builder().withProjectDir(testDirectory.toFile()).build();
	}

	@Test
	void convertsProviderConvertibleTypeIntoNestedProviderThroughCallable() {
		TaskProvider<Task> innerProvider = project.getTasks().register("inner");
		Provider<TaskProvider<Task>> nestedProviders = project.provider(() -> innerProvider);

//		assertThat(nestedProviders, buildDependencies(emptyIterable()));
		assertThat(flatten(nestedProviders), buildDependencies(contains(named("inner"))));
	}
}
