package dev.nokee.commons.gradle.tasks.options;

import org.gradle.api.Action;

public interface ConfigurableSourceFileOptions<T> extends SourceFileOptions<T> {
	/**
	 * Configures the options for this source.
	 *
	 * @param configureAction  the configure action to execute on the options
	 */
	void options(Action<? super T> configureAction);
}
