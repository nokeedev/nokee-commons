package dev.nokee.commons.gradle.tasks.options;

import dev.nokee.commons.gradle.Factory;

public final class FactoryLookup<T> {
	private final IConfigureActionLookup<T> entries;
	private final Factory<T> factory;

	public FactoryLookup(IConfigureActionLookup<T> entries, Factory<T> factory) {
		this.entries = entries;
		this.factory = factory;
	}

	public Factory<T> get(ISourceKey key) {
		return factory.tap(entries.resolve(key));
	}
}
