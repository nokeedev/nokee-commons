package dev.nokee.commons.gradle.tasks.options;

import org.gradle.api.Action;
import org.gradle.api.NonExtensible;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;

/**
 * Represent the options of a specific source file.
 *
 * @param <T>  the source specific options
 */
@NonExtensible
public interface SourceOptions<T> {
	/**
	 * {@return the source file the specified options applies}
	 */
	Provider<RegularFile> getSourceFile();

	/**
	 * {@return the options for this source}
	 */
	T getOptions();

	/**
	 * Configures the options for this source.
	 *
	 * @param configureAction  the configure action to execute on the options
	 */
	void options(Action<? super T> configureAction);
}
