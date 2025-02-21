package dev.nokee.commons.gradle;

import org.gradle.api.specs.Spec;

final class NegateSpec<T> implements Spec<T> {
	private final Spec<T> delegate;

	public NegateSpec(Spec<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public boolean isSatisfiedBy(T t) {
		return !delegate.isSatisfiedBy(t);
	}
}
