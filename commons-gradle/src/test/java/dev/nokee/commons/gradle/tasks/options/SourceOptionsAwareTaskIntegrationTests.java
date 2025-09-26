package dev.nokee.commons.gradle.tasks.options;

import dev.nokee.commons.gradle.tasks.SourceTask;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static dev.nokee.commons.fixtures.ActionTestUtils.doSomething;
import static dev.nokee.commons.fixtures.SourceOptionsMatchers.sourceFile;
import static dev.nokee.commons.hamcrest.gradle.FileSystemMatchers.aFileNamed;
import static dev.nokee.commons.hamcrest.gradle.provider.ProviderOfMatcher.providerOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SourceOptionsAwareTaskIntegrationTests {
	Project project;
	MyTask subject;

	@BeforeEach
	void setup(@TempDir File testDirectory) {
		project = ProjectBuilder.builder().withProjectDir(testDirectory).build();
		subject = project.getTasks().register("verify", MyTask.class).get();
	}

	@Test
	void noOptionsForUnrelatedSourceFile() throws IOException {
		project.file("foo.txt").createNewFile();
		assertThat(subject.getSourceOptions().forFile(project.file("foo.txt")).getOptions(), nullValue());
	}

	@Test
	void canConfigureSourceOptionsOnFileSpec() throws IOException {
		project.file("foo.txt").createNewFile();
		project.file("bar.txt").createNewFile();
		project.file("far.txt").createNewFile();
		subject.source("foo.txt", "bar.txt", "far.txt");
		subject.getSourceOptions().forFilesMatching(it -> Arrays.asList("foo.txt", "far.txt").contains(it.getName()), doSomething());

		assertThat(subject.getSourceOptions(), emptyIterable());
		assertThat(subject.getAllSourceOptions(), providerOf(contains(sourceFile(aFileNamed("foo.txt")), sourceFile(aFileNamed("far.txt")))));
	}

	public static abstract class MyTask extends DefaultTask implements SourceTask, SourceOptionsAware<Options> {}
}
