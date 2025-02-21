package dev.nokee.commons.gradle;

import org.gradle.api.Transformer;
import org.gradle.api.provider.Provider;
import org.gradle.api.specs.Spec;

import javax.annotation.Nullable;

/**
 * Filters the transformed elements by returning {@literal null} for each element not satisfied by the spec.
 * The main purpose of this transformer is to backport the {@literal Provider#filter(Spec)} from Gradle 8.5 via the {@link Provider#map(Transformer)} API.
 *
 * <code>
 * import static dev.nokee.commons.gradle.provider.FilterTransformer.filter
 * Provider<String> provider = provider { 'my-string' }.map(filter { !it.startsWith('my-') }
 * println("My filtered provider: ${provider.orNull}")
 * </code>
 *
 * @param <ValueType>  the element type to filter
 */
final class FilterTransformer<ValueType> implements Transformer</*@Nullable*/ ValueType, ValueType> {
	private final Spec<? super ValueType> spec;

	public FilterTransformer(Spec<? super ValueType> spec) {
		this.spec = spec;
	}

	@Override
	@Nullable @SuppressWarnings("NullableProblems") // It's safe to return {@literal null}
	public ValueType transform(ValueType t) {
		if (spec.isSatisfiedBy(t)) {
			return t;
		} else {
			return null; // signal a no-value provider
		}
	}

	@Override
	public String toString() {
		return "filter(" + spec + ")";
	}
}
