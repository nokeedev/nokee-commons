package dev.nokee.commons.gradle;

import org.gradle.api.Action;
import org.gradle.api.Transformer;

import static java.util.Objects.requireNonNull;

/**
 * g(f(x))
 * @param <A>
 * @param <B>
 */
final class TransformingActionAdapter<A, B> implements Action<A> {
	private final Action<B> g;
	private final Transformer<? extends B, A> f;

	public TransformingActionAdapter(Action<B> g, Transformer<? extends B, A> f) {
		this.g = requireNonNull(g);
		this.f = requireNonNull(f);
	}

	@Override
	public void execute(A in) {
		g.execute(f.transform(in));
	}

	@Override
	public String toString() {
		return g + "(" + f + "(a))";
	}
}
