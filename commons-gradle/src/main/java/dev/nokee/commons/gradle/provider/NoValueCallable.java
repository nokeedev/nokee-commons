package dev.nokee.commons.gradle.provider;

import java.util.concurrent.Callable;

final class NoValueCallable<V> implements Callable<V> {
	@Override
	public V call() throws Exception {
		return null;
	}

	@SuppressWarnings("unchecked")
	public <S> NoValueCallable<S> withNarrowedType() {
		return (NoValueCallable<S>) this;
	}

	@Override
	public String toString() {
		return "<no-value>";
	}
}
