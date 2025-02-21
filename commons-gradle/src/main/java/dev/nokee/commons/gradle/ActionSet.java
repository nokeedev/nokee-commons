package dev.nokee.commons.gradle;

import org.gradle.api.Action;

/**
 * Represents a set of actions executed as a single action.
 *
 * @param <T>  the type of object which this action accepts
 */
@FunctionalInterface
public interface ActionSet<T> extends Action<T>, Iterable<Action<T>> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	default void execute(T t) {
		forEach(action -> action.execute(t));
	}
}
