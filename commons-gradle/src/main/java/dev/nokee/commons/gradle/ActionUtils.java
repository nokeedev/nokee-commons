package dev.nokee.commons.gradle;

import org.gradle.api.Action;
import org.gradle.api.Transformer;
import org.gradle.api.specs.Spec;

/**
 *
 */
public final class ActionUtils {
	private ActionUtils() {}

	private static final DoNothingAction DO_NOTHING_INSTANCE = new DoNothingAction();

	/**
	 * {@return an action that does nothing}
	 * @param <T> the object type
	 */
	public static <T> Action<T> doNothing() {
		return DO_NOTHING_INSTANCE.withNarrowType();
	}

	/**
	 * Transforms the input object before executing the specified action.
	 * For {@literal f: A->B} and {@literal g(B)}, we define composition as {@literal g(f(a))} for each {@literal a}.
	 *
	 * @param g  the action to execute on the transformed object
	 * @param f  the transformation to apply on the input object
	 * @return an action
	 * @param <A>  the transforming type (input of {@literal f})
	 * @param <B>  the transformed type (output of {@literal f} and input of {@literal g})
	 */
	public static <A, B> Action<A> transformBefore(Action<B> g, Transformer<? extends B, A> f) {
		return new TransformingActionAdapter<>(g, f);
	}

	/**
	 * Creates a new actions that only forwards arguments matching the spec.
	 *
	 * @param spec  the spec to match
	 * @param configureAction  the configure action to execute
	 * @return a new action matching only the spec
	 * @param <T>  the type of items to match
	 * @see org.gradle.api.DomainObjectCollection#matching(Spec)
	 */
	public static <T> Action<T> matching(Spec<T> spec, Action<? super T> configureAction) {
		return new FilteredAction<>(spec, configureAction);
	}

	/**
	 * Adapts a JDK {@link Runnable} to a Gradle {@link Action}.
	 * Use this adapter to make the intention of ignoring the Action's parameter.
	 *
	 * @param runnable  the action to run when executing
	 * @return an action
	 * @param <T> the object type
	 */
	public static <T> Action<T> ignored(Runnable runnable) {
		return new RunnableAction<>(runnable);
	}
}
