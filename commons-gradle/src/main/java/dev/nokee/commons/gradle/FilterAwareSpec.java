package dev.nokee.commons.gradle;

import org.gradle.api.Action;
import org.gradle.api.specs.Spec;

/**
 * Represents a filtering spec of an action.
 *
 * @param <T> the object type
 */
public interface FilterAwareSpec<T> extends Spec<T> {
	default <S extends T> Action<S> whenSatisfied(Action<? super S> satistiedAction) {
		return new FilteredAction<>(this, satistiedAction);
	}
}
