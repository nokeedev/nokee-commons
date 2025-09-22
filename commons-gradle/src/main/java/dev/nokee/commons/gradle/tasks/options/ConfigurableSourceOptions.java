package dev.nokee.commons.gradle.tasks.options;

import org.gradle.api.Action;
import org.gradle.api.file.FileTree;
import org.gradle.api.provider.Provider;

import java.io.File;

public interface ConfigurableSourceOptions<T> extends SourceOptions<T> {
	/**
	 * {@inheritDoc}
	 */
	ConfigurableSourceFileOptions<T> forFile(File sourceFile);

	/**
	 * Configures the options for the specified source file.
	 * <p>
	 * <b>Note:</b> The source options configuration action must be side effect free.
	 *
	 * @param sourceFile  source file to configure the options
	 * @param configureAction  the source options configure action
	 */
	void forFile(File sourceFile, Action<? super T> configureAction);

	/**
	 * {@inheritDoc}
	 */
	ConfigurableSourceOptions<T> forFiles(FileTree sourceFiles);

	ConfigurableSourceOptions<T> with(SourceOptions<T> sourceOptions);
	ConfigurableSourceOptions<T> with(Provider<? extends SourceOptions<T>> sourceOptionsProvider);

	/**
	 * Configures this source options.
	 * <p>
	 * <b>Note:</b> The source options configuration action must be side effect free.
	 *
	 * @param configureAction  the source options configure action
	 */
	void options(Action<? super T> configureAction);
}
