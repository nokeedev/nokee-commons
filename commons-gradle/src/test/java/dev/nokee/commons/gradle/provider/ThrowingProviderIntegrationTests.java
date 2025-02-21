package dev.nokee.commons.gradle.provider;

import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static dev.nokee.commons.gradle.provider.ProviderUtils.alwaysThrows;
import static dev.nokee.commons.hamcrest.With.with;
import static dev.nokee.commons.hamcrest.gradle.ThrowableMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

class ThrowingProviderIntegrationTests {
	ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	@Test
	void throwsRuntimeExceptionWithoutMessageByDefault() {
		assertThat(() -> providerFactory().provider(alwaysThrows()).get(),
			throwsException(allOf(with(noMessage()), instanceOf(RuntimeException.class))));
	}

	@Test
	void throwsSpecifiedException() {
		assertThat(() -> providerFactory().provider(alwaysThrows(() -> new Exception("a custom message"))).get(),
			throwsException(allOf(with(message("a custom message")), instanceOf(Exception.class))));
	}
}
