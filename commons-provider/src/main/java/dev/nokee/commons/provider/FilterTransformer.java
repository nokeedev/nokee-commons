package dev.nokee.commons.provider;

import org.gradle.api.Transformer;
import org.gradle.api.specs.Spec;

import javax.annotation.Nullable;

/**
 * Filter a provider value through mappable transformer.
 * It serves as a backport of the {@literal Provider#filter(Spec)} from Gradle 8.5.
 * <code>
 * import static dev.nokee.commons.provider.FilterTransformer.filter
 * Provider<String> provider = provider { 'my-string' }.map(filter { !it.startsWith('my-') }
 * println("My filtered provider: ${provider.orNull}")
 * </code>
 *
 * @param <ValueType>  the element type to filter
 */
public final class FilterTransformer<ValueType> implements Transformer</*@Nullable*/ ValueType, ValueType> {
	private final Spec<? super ValueType> spec;

	/** Use {@link FilterTransformer#filter(Spec)}.  */
	private FilterTransformer(Spec<? super ValueType> spec) {
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

	public static <ElementType> FilterTransformer<ElementType> filter(Spec<? super ElementType> spec) {
		return new FilterTransformer<>(spec);
	}
}
