package dev.nokee.commons.gradle.tasks.options;

import dev.nokee.commons.gradle.tasks.SourceTask;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Nested;

import java.io.File;

/**
 * Represents an object with source options.
 *
 * @param <T>  the source options type
 */
public interface SourceOptionsAware<T> extends Task {
	default SourceOptionsAware<T> source(Object sourcePath, Action<? super T> configureAction) {
		SourceTask.source(this, sourcePath);
		getSourceOptions().configure(sourcePath, configureAction);
		return this;
	}

	@Nested
	Options<T> getOptions();

	interface Options<T> {
		Provider<T> forSource(File file);

		Provider<Iterable<SourceOptions<T>>> forAllSources();
	}

	@Nested
	AllSourceOptionsEx2<T> getSourceOptions();
}
