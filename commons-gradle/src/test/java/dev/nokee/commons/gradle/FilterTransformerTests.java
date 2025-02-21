package dev.nokee.commons.gradle;

import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static dev.nokee.commons.gradle.TransformerUtils.filter;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class FilterTransformerTests {
	ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	static final String testString = "my string is an example";

	@Test
	void returnsTransformedValueWhenSpecIsSatisfied() {
		String actual = providerFactory().provider(() -> testString).map(filter(it -> it.contains("my string"))).getOrNull();
		assertThat(actual, sameInstance(testString));
	}

	@Test
	void returnNullWhenSpecNotSatisfied() {
		String actual = providerFactory().provider(() -> testString).map(filter(it -> it.contains("my bool"))).getOrNull();
		assertThat(actual, nullValue());
	}
}
