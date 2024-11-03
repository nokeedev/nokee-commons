package dev.nokee.commons.provider;

import org.gradle.api.Action;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OptionalProviderIfPresentTests {
	static ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	@Test
	void invokesActionWhenProviderValueIsPresent() {
		OptionalProvider<String> subject = OptionalProvider.of(providerFactory().provider(() -> "value"));
		List<String> values = new ArrayList<>();
		subject.ifPresent(values::add);
		assertThat(values, contains("value"));
	}

	@Test
	void doesNotInvokeActionWhenProviderValueIsAbsent() {
		OptionalProvider<String> subject = OptionalProvider.of(providerFactory().provider(() -> null));
		assertDoesNotThrow(() -> subject.ifPresent(alwaysThrows()));
	}

	private static <T> Action<T> alwaysThrows() {
		return __ -> { throw new UnsupportedOperationException(); };
	}
}
