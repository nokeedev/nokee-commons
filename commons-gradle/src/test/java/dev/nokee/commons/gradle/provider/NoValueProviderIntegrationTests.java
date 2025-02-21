package dev.nokee.commons.gradle.provider;

import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static dev.nokee.commons.gradle.provider.ProviderUtils.noValue;
import static dev.nokee.commons.hamcrest.gradle.provider.NoValueProviderMatcher.noValueProvider;
import static org.hamcrest.MatcherAssert.assertThat;

class NoValueProviderIntegrationTests {
	ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	@Test
	void canCreateNoValueProvider() {
		assertThat(providerFactory().provider(noValue()), noValueProvider());
	}
}
