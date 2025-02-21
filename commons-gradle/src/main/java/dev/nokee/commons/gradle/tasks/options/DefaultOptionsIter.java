package dev.nokee.commons.gradle.tasks.options;

import org.gradle.api.model.ObjectFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

final class DefaultOptionsIter<T> implements OptionsIter<T> {
	private final Iterable<File> sourceFiles;
	private final CacheableEntries<T> entries;
	private final ObjectFactory objects;

	DefaultOptionsIter(Iterable<File> sourceFiles, CacheableEntries<T> entries, ObjectFactory objects) {
		this.sourceFiles = sourceFiles;
		this.entries = entries;
		this.objects = objects;
	}

	@Override
	public Iterable<GroupedSourceOptions<T>> grouped() {
		Map<CacheableEntries<T>.ExecutableKey, Collection<File>> mapping = new LinkedHashMap<>();
		for (File sourceFile : sourceFiles) {
			mapping.computeIfAbsent(entries.forFile(sourceFile), __ -> new ArrayList<>()).add(sourceFile);
		}

		return mapping.entrySet().stream().map(it -> {
			return new GroupedSourceOptions<>(it.getValue(), it.getKey());
		}).collect(Collectors.toList());
	}

	@Override
	public Iterable<SourceOptions<T>> unrolled() {
		return () -> new SourceOptionsIterator<>(sourceFiles, it -> entries.forFile(it).get(), objects);
	}
}
