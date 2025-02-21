package dev.nokee.commons.gradle.tasks.options;

import java.util.HashMap;
import java.util.Map;

// Responsible for resolving a "key" to an "action"
//   and create the "options"
//   and cache the "options" based on the "key"
public class OptionCache<T> implements IOptionsLookup<T> {
	private final Map<ISourceKey, T> cache = new HashMap<>();
	private final FactoryLookup<T> factoryLookup;

	public OptionCache(FactoryLookup<T> factoryLookup) {
		this.factoryLookup = factoryLookup;
	}

	public T get(ISourceKey key) {
		return cache.computeIfAbsent(key, ignored -> {
			return factoryLookup.get(key).create();
		});
	}
}
