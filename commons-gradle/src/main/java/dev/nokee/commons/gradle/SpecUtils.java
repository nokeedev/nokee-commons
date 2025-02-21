package dev.nokee.commons.gradle;

import org.gradle.api.Named;
import org.gradle.api.Namer;
import org.gradle.api.Transformer;
import org.gradle.api.specs.Spec;

public final class SpecUtils {
	private SpecUtils() {}

	/**
	 * Returns the composition of a function and a predicate. For every {@code x}, the generated
	 * predicate returns {@code predicate(function(x))}.
	 *
	 * @return the composition of the provided function and predicate
	 */
	public static <A, B> CompositionSpec<A, B> compose(Spec<B> spec, Transformer<? extends B, A> transformer) {
		return new CompositionSpec<>(spec, transformer);
	}

	/**
	 * Specification satisfying the name of the object.
	 * To apply the specification on non-{@link Named} object, use {@link NameSpec#using(Namer)}.
	 *
	 * @param spec  the name specification
	 * @return a specification that matches the object name
	 * @param <T>  the object type
	 */
	public static <T extends Named> NameSpec<T> named(Spec<String> spec) {
		return new NameFilteringSpec<>(T::getName, spec);
	}

	public static <T> Spec<T> negate(Spec<T> spec) {
		return new NegateSpec<>(spec);
	}
}
