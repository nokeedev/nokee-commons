package dev.nokee.commons.gradle.provider;

import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static dev.nokee.commons.gradle.provider.ProviderUtils.asOptionalCollectionElement;
import static dev.nokee.commons.gradle.provider.ProviderUtils.noValue;
import static dev.nokee.commons.hamcrest.gradle.provider.ProviderOfMatcher.providerOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;

class OptionalCollectionElementIntegrationTests {
	ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	@Test
	void mapNoValueProviderToEmptyList() {
		assertThat(asOptionalCollectionElement(providerFactory().provider(noValue())), providerOf(emptyIterable()));
	}

	@Test
	void mapExistingProviderToSingleElementList() {
		assertThat(asOptionalCollectionElement(providerFactory().provider(() -> "foo")), providerOf(contains("foo")));
	}
}
