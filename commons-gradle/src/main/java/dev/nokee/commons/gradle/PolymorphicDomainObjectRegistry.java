package dev.nokee.commons.gradle;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.PolymorphicDomainObjectContainer;

public class PolymorphicDomainObjectRegistry<T> extends NamedDomainObjectRegistry<T> {
	private final PolymorphicDomainObjectContainer<T> delegate;

	public PolymorphicDomainObjectRegistry(PolymorphicDomainObjectContainer<T> delegate) {
		super(delegate);
		this.delegate = delegate;
	}

	public <S extends T> NamedDomainObjectProvider<S> register(String name, Class<S> type) {
		return delegate.register(name, type);
	}

	public <S extends T> NamedDomainObjectProvider<S> registerIfAbsent(String name, Class<S> type) {
		if (delegate.getNames().contains(name)) {
			return delegate.register(name, type);
		}
		return delegate.named(name, type);
	}
}
