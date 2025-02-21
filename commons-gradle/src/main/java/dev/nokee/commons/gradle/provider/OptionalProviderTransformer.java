package dev.nokee.commons.gradle.provider;

import org.gradle.api.Transformer;
import org.gradle.api.provider.Provider;

import java.util.Optional;

final class OptionalProviderTransformer<T> implements Transformer<Optional<T>, Provider<T>> {
	public OptionalProviderTransformer() {}

	@Override
	public Optional<T> transform(Provider<T> tProvider) {
		return Optional.ofNullable(tProvider.getOrNull());
	}

	@SuppressWarnings("unchecked")
	public <S> OptionalProviderTransformer<S> withNarrowedType() {
		return (OptionalProviderTransformer<S>) this;
	}
}
