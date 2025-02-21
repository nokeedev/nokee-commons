package dev.nokee.commons.gradle;

import org.gradle.api.Action;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static dev.nokee.commons.gradle.TransformerUtils.peek;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.verify;

class PeekIntegrationTests {
	ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	@Test
	void peeksAtTransformedValueWithoutAlteration() {
		Action<Object> action = Mockito.mock();
		Object expected = new Object();

		Object actual = providerFactory().provider(() -> expected).map(peek(action)).get();

		assertThat("does not alter transformed value", actual, sameInstance(expected));
		verify(action).execute(expected);
	}
}
