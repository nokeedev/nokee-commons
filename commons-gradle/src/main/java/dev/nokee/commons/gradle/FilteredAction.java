package dev.nokee.commons.gradle;

import org.gradle.api.Action;
import org.gradle.api.specs.Spec;

import javax.annotation.Nonnull;

/**
 * Represents an action that only forward arguments on to the delegate if they satisfy the given spec.
 *
 * @param <T>  the type of item the action expects
 */
final class FilteredAction<T> implements Action<T> {
	private final Spec<? super T> spec;
	private final Action<? super T> delegate;

	public FilteredAction(Spec<? super T> spec, Action<? super T> delegate) {
		this.spec = spec;
		this.delegate = delegate;
	}

	@Override
	public void execute(@Nonnull T t) {
		if (spec.isSatisfiedBy(t)) {
			delegate.execute(t);
		}
	}

	@Override
	public String toString() {
		return "execute " + delegate + " only if " + spec;
	}
}
