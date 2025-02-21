package dev.nokee.commons.gradle.tasks.options;

import java.io.File;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;

// Responsible to return a fluent connection between the "key" and "cached options"
public class CacheableEntries<T> implements ICacheEntries<T> {
	private final IOptionsLookup<T> cache; // TODO: OptionCache would be more about factory or maybe a factory lookup
	private final ISourceKey.Lookup keys;

	public CacheableEntries(ISourceKey.Lookup keys, IOptionsLookup<T> cache) {
		this.keys = keys;
		this.cache = cache;
	}

	public ExecutableKey forFile(File sourceFile) {
		return new ExecutableKey(keys.forFile(sourceFile));
	}

	public class ExecutableKey implements Iterable<Integer>, Supplier<T> {
		private final ISourceKey key;

		public ExecutableKey(ISourceKey key) {
			this.key = key;
		}

		public boolean isDefault() {
			return key == ISourceKey.DEFAULT_KEY;
		}

		public T get() {
			return cache.get(key);
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			ExecutableKey that = (ExecutableKey) o;
			return Objects.equals(key, that.key);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(key);
		}

		@Override
		public Iterator<Integer> iterator() {
			return ((Entries.Key) key).iterator();
		}
	}
}
