package dev.nokee.commons.gradle.tasks.options;

import org.gradle.api.tasks.*;

import java.io.File;
import java.util.Collection;

// Responsible for grouping the "source options" per "source files"
public final class GroupedSourceOptions<T> implements SourceOptionsGrouped<T> {
	private final Collection<File> sourceFiles;
	private final CacheableEntries<T>.ExecutableKey key;

	GroupedSourceOptions(Collection<File> sourceFiles, CacheableEntries<T>.ExecutableKey key) {
		this.sourceFiles = sourceFiles;
		this.key = key;
	}

	@Internal
	public boolean isDefault() {
		return key.isDefault();
	}

	@Nested
	public T getOptions() {
		return key.get();
	}

	@InputFiles
	@PathSensitive(PathSensitivity.NONE)
	public Collection<File> getSourceFiles() {
		return sourceFiles;
	}
}
