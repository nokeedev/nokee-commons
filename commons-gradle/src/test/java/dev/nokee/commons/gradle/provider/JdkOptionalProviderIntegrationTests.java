package dev.nokee.commons.gradle.provider;

import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;
import static dev.nokee.commons.gradle.provider.ProviderUtils.asJdkOptional;
import static dev.nokee.commons.gradle.provider.ProviderUtils.noValue;
import static org.hamcrest.MatcherAssert.assertThat;

class JdkOptionalProviderIntegrationTests {
	ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	@Test
	void mapNoValueProviderToEmptyOptional() {
		assertThat(asJdkOptional(providerFactory().provider(noValue())), emptyOptional());
	}

	@Test
	void mapExistingProviderToSingleElementList() {
		assertThat(asJdkOptional(providerFactory().provider(() -> "foo")), optionalWithValue("foo"));
	}
}
