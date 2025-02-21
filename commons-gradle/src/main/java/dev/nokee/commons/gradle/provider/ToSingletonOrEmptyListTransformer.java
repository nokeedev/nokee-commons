package dev.nokee.commons.gradle.provider;

import org.gradle.api.Transformer;
import org.gradle.api.provider.Provider;

import java.util.Collections;
import java.util.List;

final class ToSingletonOrEmptyListTransformer<T> implements Transformer<Provider<List<T>>, Provider<T>> {
	@Override
	public Provider<List<T>> transform(Provider<T> tProvider) {
		return tProvider.map(Collections::singletonList).orElse(Collections.emptyList());
	}

	@SuppressWarnings("unchecked")
	public <S> ToSingletonOrEmptyListTransformer<S> withNarrowedType() {
		return (ToSingletonOrEmptyListTransformer<S>) this;
	}
}
