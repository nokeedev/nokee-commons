package dev.nokee.commons.gradle;

import org.gradle.api.Transformer;

final class CompositeTransformer<A, B, C> implements Transformer<C, A> {
	private final Transformer<C, B> g;
	private final Transformer<? extends B, A> f;

	public CompositeTransformer(Transformer<C, B> g, Transformer<? extends B, A> f) {
		this.g = g;
		this.f = f;
	}

	@Override
	public C transform(A a) {
		return g.transform(f.transform(a));
	}

	@Override
	public String toString() {
		return g + "(" + f + "(a))";
	}
}
