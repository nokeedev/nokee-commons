package dev.nokee.commons.gradle.provider;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static dev.nokee.commons.gradle.provider.ProviderUtils.flatten;
import static dev.nokee.commons.hamcrest.gradle.BuildDependenciesMatcher.buildDependencies;
import static dev.nokee.commons.hamcrest.gradle.NamedMatcher.named;
import static dev.nokee.commons.hamcrest.gradle.provider.ProviderOfMatcher.providerOf;
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

	@Test
	void viewElementsMockUp() {
		List<String> knownElements = new ArrayList<>();
		knownElements.add(project.getTasks().register("task1").getName());
		knownElements.add(project.getTasks().register("task2").getName());
		knownElements.add(project.getTasks().register("task3").getName());

		class View {
			public Provider<Set<Task>> getElements() {
				return flatten(project.provider(() -> {
					SetProperty<Task> result = project.getObjects().setProperty(Task.class);
					for (String name : knownElements) {
						result.add(project.getTasks().named(name));
					}
					return result;
				}));
			}
		}
		assertThat(new View().getElements(), providerOf(contains(named("task1"), named("task2"), named("task3"))));
	}
}
