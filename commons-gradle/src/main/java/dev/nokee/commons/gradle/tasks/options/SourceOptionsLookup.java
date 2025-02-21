package dev.nokee.commons.gradle.tasks.options;

import org.gradle.api.provider.Provider;

import java.io.File;

public interface SourceOptionsLookup<O> {
	Provider<O> get(File file);
	Provider<Iterable<SourceOptions<O>>> getAll();
}
