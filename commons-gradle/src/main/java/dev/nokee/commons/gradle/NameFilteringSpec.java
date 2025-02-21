package dev.nokee.commons.gradle;

import org.gradle.api.Namer;
import org.gradle.api.specs.Spec;

final class NameFilteringSpec<T> implements NameSpec<T> {
	private final Namer<T> namer;
	private final Spec<String> spec;

	public NameFilteringSpec(Namer<T> namer, Spec<String> spec) {
		this.namer = namer;
		this.spec = spec;
	}

	@Override
	public boolean isSatisfiedBy(T t) {
		return spec.isSatisfiedBy(namer.determineName(t));
	}

	public <S> NameFilteringSpec<S> using(Namer<S> namer) {
		return new NameFilteringSpec<>(namer, spec);
	}
}
