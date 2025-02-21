package dev.nokee.commons.gradle;

import org.gradle.api.Transformer;
import org.gradle.api.specs.Spec;

class CompositionSpec<A, B> implements Spec<A> {
	private final Spec<B> s;
	private final Transformer<? extends B, A> f;

	public CompositionSpec(Spec<B> s, Transformer<? extends B, A> f) {
		this.s = s;
		this.f = f;
	}

	@Override
	public boolean isSatisfiedBy(A a) {
		return s.isSatisfiedBy(f.transform(a));
	}

	@Override
	public String toString() {
		return s + "(" + f + "(a))";
	}
}
