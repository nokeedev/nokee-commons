package dev.nokee.commons.gradle;

import org.gradle.api.*;

public class NamedDomainObjectRegistry<T> {
	private final NamedDomainObjectContainer<T> delegate;

	public NamedDomainObjectRegistry(NamedDomainObjectContainer<T> delegate) {
		this.delegate = delegate;
	}

	public NamedDomainObjectProvider<T> register(String name) {
		return delegate.register(name);
	}

	public NamedDomainObjectProvider<T> registerIfAbsent(String name) {
		if (delegate.getNames().contains(name)) {
			return delegate.register(name);
		}
		return delegate.named(name);
	}
}
