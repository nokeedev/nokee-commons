package dev.nokee.commons.hamcrest.gradle.provider;

import org.gradle.api.provider.Provider;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.equalTo;

public final class ProviderOfMatcher<T> extends FeatureMatcher<Provider<? extends T>, T> {
	public ProviderOfMatcher(Matcher<? super T> matcher) {
		super(matcher, "provider of", "providing");
	}

	@Override
	protected T featureValueOf(Provider<? extends T> actual) {
		return actual.get();
	}

	public static <T> ProviderOfMatcher<T> providerOf(Matcher<? super T> matcher) {
		return new ProviderOfMatcher<>(matcher);
	}

	public static <T> ProviderOfMatcher<T> providerOf(T instance) {
		return new ProviderOfMatcher<>(equalTo(instance));
	}
}
