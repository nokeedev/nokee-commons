package dev.nokee.commons.gradle;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.PolymorphicDomainObjectContainer;

public class PolymorphicDomainObjectRegistry<T> extends NamedDomainObjectRegistry<T> {
	private final PolymorphicDomainObjectContainer<T> delegate;

	public PolymorphicDomainObjectRegistry(PolymorphicDomainObjectContainer<T> delegate) {
		super(delegate);
		this.delegate = delegate;
	}

	public <S extends T> NamedDomainObjectProvider<S> register(Object name, Class<S> type) {
		return delegate.register(name.toString(), type);
	}

	public <S extends T> NamedDomainObjectProvider<S> registerIfAbsent(Object name, Class<S> type) {
		if (delegate.getNames().contains(name.toString())) {
			return delegate.named(name.toString(), type);
		}
		return delegate.register(name.toString(), type);
	}
}
