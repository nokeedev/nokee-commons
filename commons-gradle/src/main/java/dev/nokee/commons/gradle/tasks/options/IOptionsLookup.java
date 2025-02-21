package dev.nokee.commons.gradle.tasks.options;

public interface IOptionsLookup<T> {
	T get(ISourceKey key);
}
