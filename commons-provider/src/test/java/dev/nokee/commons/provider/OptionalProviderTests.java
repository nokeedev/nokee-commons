package dev.nokee.commons.provider;

import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OptionalProviderTests {
	static ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	@Test
	void doesNotRealizeProviderOnOptionalProviderInstantiation() {
		assertDoesNotThrow(() -> OptionalProvider.of(providerFactory().provider(() -> { throw new UnsupportedOperationException(); })));
	}
}
