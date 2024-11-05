package dev.nokee.commons.hamcrest.gradle.provider;

public final class PresentProviderMatcher<T> extends AbstractProviderStateMatcher<T> {
	public PresentProviderMatcher() {
		super(ProviderState.present);
	}

	public static <T> PresentProviderMatcher<T> presentProvider() {
		return new PresentProviderMatcher<>();
	}
}
