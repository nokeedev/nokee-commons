package dev.nokee.commons.hamcrest.gradle.provider;

import org.gradle.api.provider.Provider;
import org.hamcrest.FeatureMatcher;

import static org.hamcrest.core.Is.is;

abstract class AbstractProviderStateMatcher<T> extends FeatureMatcher<Provider<? extends T>, AbstractProviderStateMatcher.ProviderState> {
	protected AbstractProviderStateMatcher(ProviderState expected) {
		super(is(expected), "provider", "provider");
	}

	protected enum ProviderState {
		present, absent
	}

	@Override
	protected ProviderState featureValueOf(Provider<? extends T> actual) {
		if (actual.isPresent()) {
			return ProviderState.present;
		} else {
			return ProviderState.absent;
		}
	}
}
