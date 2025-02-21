package dev.nokee.commons.gradle.provider;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderConvertible;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static dev.nokee.commons.hamcrest.gradle.BuildDependenciesMatcher.buildDependencies;
import static dev.nokee.commons.hamcrest.gradle.NamedMatcher.named;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

class CallableProviderConvertibleIntegrationTests {
	Project project;
	@TempDir Path testDirectory;

	@BeforeEach
	void setup() {
		project = ProjectBuilder.builder().withProjectDir(testDirectory.toFile()).build();
	}

	public static abstract class MyTask extends DefaultTask {
		@OutputDirectory
		public abstract DirectoryProperty getDestinationDirectory();
	}

	@Test
	void convertsProviderConvertibleTypeIntoNestedProviderThroughCallable() {
		Provider<Directory> provider = project.getTasks().register("test", MyTask.class).flatMap(MyTask::getDestinationDirectory);
		ProviderConvertible<Directory> subject = (CallableProviderConvertible<Directory>) () -> provider;
		assertThat(project.files(subject), buildDependencies(contains(named("test"))));
	}
}
