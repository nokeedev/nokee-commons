package dev.nokee.commons.gradle.tasks.options;

import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Nested;

import java.io.File;
import java.util.Collection;

public interface SourceOptionsGrouped<T> {
	@Nested
	T getOptions();

	@InputFiles
	Collection<File> getSourceFiles();
}
