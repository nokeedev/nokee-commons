package dev.nokee.commons.gradle;

import org.gradle.api.Action;
import org.gradle.api.Transformer;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Executes a non-interfering action on the transformed elements.
 *
 * <p>
 *   This transformer implementation exists mainly to support debugging.
 * </p>
 *
 * @param <T> the transformed element type
 */
final class PeekTransformer<T> implements Transformer<T, T> {
	private final Action<? super T> action;

	public PeekTransformer(Action<? super T> action) {
		this.action = Objects.requireNonNull(action);
	}

	@Override
	@Nonnull
	public T transform(@Nonnull T in) {
		action.execute(in);
		return in;
	}
}
