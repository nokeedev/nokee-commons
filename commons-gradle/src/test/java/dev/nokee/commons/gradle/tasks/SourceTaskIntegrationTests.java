package dev.nokee.commons.gradle.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.language.cpp.tasks.CppCompile;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static dev.nokee.commons.gradle.tasks.SourceTask.source;
import static dev.nokee.commons.gradle.tasks.SourceTask.sourceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.io.FileMatchers.aFileNamed;

class SourceTaskIntegrationTests {
	Project project;

	@BeforeEach
	void setup(@TempDir File testDirectory) {
		project = ProjectBuilder.builder().withProjectDir(testDirectory).build();
	}

	@Nested
	class SourceTaskContractImplementationUsingGradleDecorationTests {
		File sourceFile;
		MySourceTask subject;

		@BeforeEach
		void setup() throws IOException {
			sourceFile = project.file("foo.cpp");
			sourceFile.createNewFile();
			subject = project.getTasks().register("verify", MySourceTask.class).get();
		}

		@Test
		void canUseSourceMethodToAppendSourceFileWithoutPreviouslyHavingSources() {
			subject.source(sourceFile);
			assertThat(subject.getSource(), contains(sourceFile));
		}

		@Test
		void canUseSourceMethodToAppendSourceFilesWithoutPreviouslyHavingSources() {
			subject.source(project.files(sourceFile));
			assertThat(subject.getSource(), contains(sourceFile));
		}
	}

	@Nested
	class AbstractNativeCompileTaskTests {
		CppCompile subject;

		@BeforeEach
		void setup() {
			subject = project.getTasks().register("verify", CppCompile.class).get();
		}

		@Test
		void canRetrieveSource() throws IOException {
			FileTree taskSource = sourceOf(subject);
			assertThat(taskSource, emptyIterable());

			subject.getSource().from(Files.createFile(project.file("foo.cpp").toPath()));
			assertThat(taskSource, contains(aFileNamed(equalTo("foo.cpp"))));
		}

		@Test
		void canAppendAdditionalSource() throws IOException {
			assertThat(source(subject, Files.createFile(project.file("foo.cpp").toPath())), is(subject));
			assertThat(subject.getSource(), contains(aFileNamed(equalTo("foo.cpp"))));
		}
	}

	@Nested
	class GradleSourceTaskTests {
		org.gradle.api.tasks.SourceTask subject;

		@BeforeEach
		void setup() {
			subject = project.getTasks().register("verify", org.gradle.api.tasks.SourceTask.class).get();
		}

		@Test
		void canRetrieveSource() throws IOException {
			FileTree taskSource = sourceOf(subject);
			assertThat(taskSource, emptyIterable());

			subject.source(Files.createFile(project.file("foo.txt").toPath()));
			assertThat(taskSource, contains(aFileNamed(equalTo("foo.txt"))));
		}

		@Test
		void canAppendAdditionalSource() throws IOException {
			assertThat(source(subject, Files.createFile(project.file("foo.txt").toPath())), is(subject));
			assertThat(subject.getSource(), contains(aFileNamed(equalTo("foo.txt"))));
		}
	}

	@Nested
	class NokeeSourceTaskTests {
		SourceTask subject;

		@BeforeEach
		void setup() {
			subject = project.getTasks().register("verify", MySourceTask.class).get();
		}

		@Test
		void canRetrieveSource() throws IOException {
			FileTree taskSource = sourceOf(subject);
			assertThat(taskSource, emptyIterable());

			subject.source(Files.createFile(project.file("foo.txt").toPath()));
			assertThat(taskSource, contains(aFileNamed(equalTo("foo.txt"))));
		}

		@Test
		void canAppendAdditionalSource() throws IOException {
			assertThat(source(subject, Files.createFile(project.file("foo.txt").toPath())), is(subject));
			assertThat(subject.getSource(), contains(aFileNamed(equalTo("foo.txt"))));
		}

		@Test
		void canReassignSource() throws IOException {
			subject.source(Files.createFile(project.file("foo.txt").toPath()));
			subject.source(Files.createFile(project.file("bar.txt").toPath()));

			subject.setSource(subject.getSource().filter(it -> !it.getName().equals("bar.txt")).getAsFileTree());

			assertThat(subject.getSource(), contains(aFileNamed(equalTo("foo.txt"))));
		}
	}

	public static abstract class MySourceTask extends DefaultTask implements SourceTask {}


	@Nested
	class AdhocSourceTask_SourceTakingSingleObjectTests {
		AdhocSourceTask_SingleObject subject;

		@BeforeEach
		void setup() {
			subject = project.getTasks().register("verify", AdhocSourceTask_SingleObject.class).get();
		}

		@Test
		void canRetrieveSource() throws IOException {
			FileTree taskSource = sourceOf(subject);
			assertThat(taskSource, emptyIterable());

			subject.source(Files.createFile(project.file("foo.txt").toPath()));
			assertThat(taskSource, contains(aFileNamed(equalTo("foo.txt"))));
		}

		@Test
		void canAppendAdditionalSource() throws IOException {
			assertThat(source(subject, Files.createFile(project.file("foo.txt").toPath())), is(subject));
			assertThat(subject.getSource(), contains(aFileNamed(equalTo("foo.txt"))));
		}
	}

	public static abstract class AdhocSourceTask_SingleObject extends DefaultTask {
		private final ConfigurableFileCollection source = getProject().getObjects().fileCollection();

		public FileCollection getSource() {
			return source;
		}

		public void source(Object path) {
			source.from(path);
		}
	}

	@Nested
	class AdhocSourceTask_SourceTakingObjectArrayTests {
		AdhocSourceTask_ObjectArray subject;

		@BeforeEach
		void setup() {
			subject = project.getTasks().register("verify", AdhocSourceTask_ObjectArray.class).get();

		}

		@Test
		void canRetrieveSource() throws IOException {
			FileTree taskSource = sourceOf(subject);
			assertThat(taskSource, emptyIterable());

			subject.source(Files.createFile(project.file("foo.txt").toPath()));
			assertThat(taskSource, contains(aFileNamed(equalTo("foo.txt"))));
		}

		@Test
		void canAppendAdditionalSource() throws IOException {
			assertThat(source(subject, Files.createFile(project.file("foo.txt").toPath())), is(subject));
			assertThat(subject.getSource(), contains(aFileNamed(equalTo("foo.txt"))));
		}
	}

	public static abstract class AdhocSourceTask_ObjectArray extends DefaultTask {
		private final ConfigurableFileCollection source = getProject().getObjects().fileCollection();

		public FileCollection getSource() {
			return source;
		}

		public void source(Object... path) {
			source.from(path);
		}
	}
}
