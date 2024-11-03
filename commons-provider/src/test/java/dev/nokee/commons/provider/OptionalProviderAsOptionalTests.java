package dev.nokee.commons.provider;

import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class OptionalProviderAsOptionalTests {
	static ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	@Test
	void convertToEmptyOptional() {
		OptionalProvider<String> subject = OptionalProvider.of(providerFactory().provider(() -> null));
		assertThat(subject.asOptional(), is(emptyOptional()));
	}

	@Test
	void convertToOptionalWithValue() {
		OptionalProvider<String> subject = OptionalProvider.of(providerFactory().provider(() -> "value"));
		assertThat(subject.asOptional(), is(optionalWithValue("value")));
	}
}
