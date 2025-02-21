package dev.nokee.commons.gradle;

import org.gradle.api.Transformer;

final class NoOpTransformer<T> implements Transformer<T, T> {
	public NoOpTransformer() {}

	@Override
	public T transform(T t) {
		return t;
	}

	@SuppressWarnings("unchecked")
	public <S> NoOpTransformer<S> withNarrowedType() {
		return (NoOpTransformer<S>) this;
	}
}
