package dev.nokee.commons.hamcrest.gradle;

import org.gradle.api.specs.Spec;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public final class SpecMatcher<T> extends TypeSafeMatcher<Spec<T>> {
	private final T value;

	private SpecMatcher(T value) {
		this.value = value;
	}

	@Override
	protected boolean matchesSafely(Spec<T> tSpec) {
		return tSpec.isSatisfiedBy(value);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("spec satisfied by ").appendValue(value);
	}

	public static <T> SpecMatcher<T> satisfiedBy(T value) {
		return new SpecMatcher<>(value);
	}
}
