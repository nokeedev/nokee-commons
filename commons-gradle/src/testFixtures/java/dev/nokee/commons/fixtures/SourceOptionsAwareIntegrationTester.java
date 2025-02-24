package dev.nokee.commons.fixtures;

import dev.nokee.commons.gradle.tasks.SourceTask;
import dev.nokee.commons.gradle.tasks.options.SourceOptions;
import dev.nokee.commons.gradle.tasks.options.SourceOptionsAware;
import org.gradle.api.file.FileTree;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.TestAbortedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;

import static dev.nokee.commons.fixtures.ActionTestUtils.doSomething;
import static dev.nokee.commons.hamcrest.With.with;
import static dev.nokee.commons.hamcrest.gradle.provider.NoValueProviderMatcher.noValueProvider;
import static dev.nokee.commons.hamcrest.gradle.provider.PresentProviderMatcher.presentProvider;
import static dev.nokee.commons.hamcrest.gradle.provider.ProviderOfMatcher.providerOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 * @param <T>
 */
@ExtendWith(SubjectExtension.class)
public abstract class SourceOptionsAwareIntegrationTester<T> {
	private static FileTree sourceOf(SourceOptionsAware<?> task) {
		try {
			return SourceTask.sourceOf(task);
		} catch (RuntimeException e) {
			throw new TestAbortedException(); // assumption
		}
	}

	protected abstract File file1();
	protected abstract File file2();

	protected abstract MissingTestFile missingFile();

	protected abstract MissingTestDirectory missingDirectory();

	protected interface MissingTestFile extends Callable<File> {
		default void create() throws IOException {
			Files.createFile(toFile().toPath());
		}
		File toFile();

		default File call() throws Exception {
			return toFile();
		}
	}

	protected interface MissingTestDirectory extends Callable<File> {
		default void create() throws IOException {
			Files.createDirectories(toFile().toPath());
		}

		File subFile();

		File toFile();

		default File call() throws Exception {
			return toFile();
		}
	}

	@Test
	public final void includesSourceFilesForSourceOptionsAsTaskSources(@Subject SourceOptionsAware<T> subject) {
		subject.source(file1(), doSomething());
		assertThat(sourceOf(subject), contains(file1()));

		subject.source(file2(), doSomething());
		assertThat(sourceOf(subject), contains(file1(), file2()));
	}

	@Test
	public final void ignoresMissingSourceFilesInAllSourceOptions(@Subject SourceOptionsAware<T> subject) throws IOException {
		subject.source(file1(), doSomething());
		subject.source(missingFile(), doSomething());
		assertThat(subject.getOptions().forAllSources(), providerOf(contains(with(sourceFile(file1())))));
	}

	@Test
	public final void includesPreviouslyMissingFilesInAllSourceOptions(@Subject SourceOptionsAware<T> subject) throws IOException {
		// TODO: Find better name for missing files (that will be created later)
		subject.source(missingFile(), doSomething());
		assertThat(subject.getOptions().forAllSources(), providerOf(emptyIterable()));

		missingFile().create();
		assertThat(subject.getOptions().forAllSources(), providerOf(contains(with(sourceFile(missingFile().toFile())))));
	}

	@Test
	public final void ignoresMissingSourceDirectoryInAllSourceOptions(@Subject SourceOptionsAware<T> subject) throws IOException {
		subject.source(file1(), doSomething());
		subject.source(missingDirectory(), doSomething());
		assertThat(subject.getOptions().forAllSources(), providerOf(contains(with(sourceFile(file1())))));
	}

	@Test
	public void includesFilesFromPreviouslyMissingDirectoryInAllSourceOptions(@Subject SourceOptionsAware<T> subject) throws IOException {
		subject.source(missingDirectory(), doSomething());
		assertThat(subject.getOptions().forAllSources(), providerOf(emptyIterable()));

		missingDirectory().create();
		Files.createFile(missingDirectory().subFile().toPath());
		assertThat(subject.getOptions().forAllSources(), providerOf(contains(with(sourceFile(missingDirectory().subFile())))));
	}

	@Test
	public final void doesNotIncludeAdditionalSourceOptionsAsTaskSources_aka_whenCopyingSourceOptionsFromAnotherTask(@Subject SourceOptionsAware<T> subject) throws IOException {
		// additional source options
		subject.getSourceOptions().configure(file2(), doSomething());

		// normal source options
		subject.source(file1(), doSomething());

		// NO additional options
		assertThat(subject.getOptions().forAllSources(), providerOf(contains(with(sourceFile(file1())))));
	}

	@Test
	public final void doesNotHaveSourceOptionsForNonTaskSources(@Subject SourceOptionsAware<T> subject) {
		subject.source(file1(), doSomething());

		assertThat(subject.getOptions().forSource(file1()), presentProvider());
		assertThat(subject.getOptions().forSource(file2()), noValueProvider());
	}

	static Matcher<SourceOptions<?>> sourceFile(File file) {
		return new FeatureMatcher<SourceOptions<?>, File>(equalTo(file), "", "") {
			@Override
			protected File featureValueOf(SourceOptions<?> actual) {
				return actual.getSourceFile().get().getAsFile();
			}
		};
	}
}
