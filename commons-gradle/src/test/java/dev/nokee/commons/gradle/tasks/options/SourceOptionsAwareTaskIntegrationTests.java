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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

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

	public static abstract class MyTask extends DefaultTask implements SourceTask, SourceOptionsAware<Options> {}
}
