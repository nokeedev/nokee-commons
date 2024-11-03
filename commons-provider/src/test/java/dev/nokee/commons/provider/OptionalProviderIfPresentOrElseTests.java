package dev.nokee.commons.provider;

import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

class OptionalProviderIfPresentOrElseTests {
	static ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	@Test
	void invokesActionWhenProviderValueIsPresent() {
		OptionalProvider<String> subject = OptionalProvider.of(providerFactory().provider(() -> "value"));
		List<String> values = new ArrayList<>();
		subject.ifPresentOrElse(values::add, () -> values.add("null"));
		assertThat(values, contains("null"));
	}

	@Test
	void invokeEmptyActionWhenProviderValueIsAbsent() {
		OptionalProvider<String> subject = OptionalProvider.of(providerFactory().provider(() -> null));
		List<String> values = new ArrayList<>();
		subject.ifPresentOrElse(values::add, () -> values.add("null"));
		assertThat(values, contains("null"));
	}
}
