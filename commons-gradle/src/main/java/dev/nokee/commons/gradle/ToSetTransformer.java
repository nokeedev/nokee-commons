package dev.nokee.commons.gradle;

import org.gradle.api.Transformer;

import java.util.LinkedHashSet;
import java.util.Set;

final class ToSetTransformer<T> implements Transformer<Set<T>, Iterable<? extends T>> {
	public ToSetTransformer() {}

	@Override
	public Set<T> transform(Iterable<? extends T> values) {
		if (values instanceof Set) {
			@SuppressWarnings("unchecked")
			final Set<T> result = (Set<T>) values;
			return result;
		} else {
			final Set<T> result = new LinkedHashSet<>();
			for (T v : values) {
				result.add(v);
			}
			return result;
		}
	}

	@SuppressWarnings("unchecked")
	public <S> ToSetTransformer<S> withNarrowedType() {
		return (ToSetTransformer<S>) this;
	}

	@Override
	public String toString() {
		return "toSet";
	}
}
