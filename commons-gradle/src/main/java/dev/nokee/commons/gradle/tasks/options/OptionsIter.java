package dev.nokee.commons.gradle.tasks.options;

// Responsible for grouping or unrolling "source options"
public interface OptionsIter<T> {
	Iterable<GroupedSourceOptions<T>> grouped();

	Iterable<SourceOptions<T>> unrolled();
}
