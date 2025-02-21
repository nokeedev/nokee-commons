package dev.nokee.commons.gradle;

import org.gradle.api.Action;

/**
 * Represents a generic factory which creates instances of type T.
 *
 * @param <T>  the type of object created
 */
@FunctionalInterface
public interface Factory<T> {
	/**
	 * Creates a new instance of type T.
	 *
	 * @return the new instance
	 */
	T create();

	/**
	 * Returns a new factory that configures the newly created instance with the specified action.
	 *
	 * @param postCreateAction  the action to execute on the newly created instance
	 * @return a new factory
	 */
	default Factory<T> tap(Action<? super T> postCreateAction) {
		return new TappedFactory<>(this, postCreateAction);
	}
}
