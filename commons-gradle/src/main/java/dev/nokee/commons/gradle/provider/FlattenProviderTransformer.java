package dev.nokee.commons.gradle.provider;

import org.gradle.api.Transformer;
import org.gradle.api.provider.Provider;

import static dev.nokee.commons.gradle.TransformerUtils.noOpTransformer;

final class FlattenProviderTransformer<T> implements Transformer<Provider<T>, Provider<? extends Provider<T>>> {
	public FlattenProviderTransformer() {}

	@Override
	public Provider<T> transform(Provider<? extends Provider<T>> tProvider) {
		return tProvider.flatMap(noOpTransformer());
	}

	@SuppressWarnings("unchecked")
	public <S> FlattenProviderTransformer<S> withNarrowedType() {
		return (FlattenProviderTransformer<S>) this;
	}
}
