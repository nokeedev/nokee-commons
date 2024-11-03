package dev.nokee.commons.provider;

import org.gradle.api.Action;
import org.gradle.api.Transformer;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class PeekTransformer<T> implements Transformer<T, T> {
	private final Action<? super T> action;

	private PeekTransformer(Action<? super T> action) {
		this.action = Objects.requireNonNull(action);
	}

	@Override
	@Nonnull
	public T transform(@Nonnull T in) {
		action.execute(in);
		return in;
	}

	public static <T> PeekTransformer<T> peek(Action<? super T> action) {
		return new PeekTransformer<>(action);
	}
}
