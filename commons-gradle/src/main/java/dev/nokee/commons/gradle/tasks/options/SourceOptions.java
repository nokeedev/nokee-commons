package dev.nokee.commons.gradle.tasks.options;

import org.gradle.api.file.FileTree;

import java.io.File;
import java.util.Collection;

/**
 * Represents the source options for each source files.
 *
 * @param <OptionsType>  the type of the source options
 */
public interface SourceOptions<OptionsType> extends Iterable<SourceFileOptions<OptionsType>> {
	/**
	 * {@return a source file options for the specified source file, {@link SourceFileOptions#getOptions()} will be null when source file has no source options}
	 * @param sourceFile  the source file to query
	 */
	SourceFileOptions<OptionsType> forFile(File sourceFile);

	/**
	 * {@return the source options for the specified source files}
	 * @param sourceFiles
	 */
	SourceOptions<OptionsType> forFiles(FileTree sourceFiles);

	/**
	 * Groups the source files by options.
	 *
	 * @return a list of grouped options
	 */
	Iterable<Group<OptionsType>> groupedByOptions();

	/**
	 * Represents the source files grouped by options.
	 *
	 * @param <OptionsType>  the type of the source options
	 */
	interface Group<OptionsType> extends Iterable<SourceFileOptions<OptionsType>> {
		/**
		 * {@return the source options for this group}
		 */
		OptionsType getOptions(); // option aware

		/**
		 * {@return the source files for this source options}
		 */
		Collection<File> getSourceFiles();

		/**
		 * {@return a {@link String} identifier which will be unique to this source options group}
		 *
		 * Use this unique id to differentiate the source options groups between each other.
		 * The unique id is an opaque string that is safe to use in a file path.
		 * It's safe to assume the unique id is stable between build invocation for the same machine and configuration.
		 * Do not assume anything about the unique id, format included.
		 */
		String getUniqueId();
	}
}
