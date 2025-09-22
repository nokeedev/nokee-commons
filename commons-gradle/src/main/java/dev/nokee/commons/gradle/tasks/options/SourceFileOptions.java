package dev.nokee.commons.gradle.tasks.options;

import javax.annotation.Nullable;
import java.io.File;

/**
 * Represents the options of a single source file.
 *
 * @param <OptionsType>  the type of the source options
 */
public interface SourceFileOptions<OptionsType> {
	/**
	 * {@return the source file the specified options applies}
	 */
	File getSourceFile();

	/**
	 * {@return the options for this source, or null otherwise}
	 */
	@Nullable
	OptionsType getOptions();
}
