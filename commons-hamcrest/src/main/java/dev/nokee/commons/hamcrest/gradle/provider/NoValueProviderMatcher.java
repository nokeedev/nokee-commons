package dev.nokee.commons.hamcrest.gradle.provider;

public final class NoValueProviderMatcher<T> extends AbstractProviderStateMatcher<T> {
	public NoValueProviderMatcher() {
		super(ProviderState.absent);
	}

	public static <T> NoValueProviderMatcher<T> noValueProvider() {
		return new NoValueProviderMatcher<>();
	}
}
