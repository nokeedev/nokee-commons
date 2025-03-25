package dev.nokee.commons.gradle;

import org.gradle.api.*;

public class NamedDomainObjectRegistry<T> {
	private final NamedDomainObjectContainer<T> delegate;

	public NamedDomainObjectRegistry(NamedDomainObjectContainer<T> delegate) {
		this.delegate = delegate;
	}

	public NamedDomainObjectProvider<T> register(Object name) {
		return delegate.register(name.toString());
	}

	public NamedDomainObjectProvider<T> registerIfAbsent(Object name) {
		if (delegate.getNames().contains(name.toString())) {
			return delegate.named(name.toString());
		}
		return delegate.register(name.toString());
	}
}
