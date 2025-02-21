package dev.nokee.commons.gradle;

import org.gradle.api.Transformer;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.nokee.commons.gradle.TransformerUtils.filter;
import static dev.nokee.commons.gradle.TransformerUtils.traverse;
import static dev.nokee.commons.hamcrest.With.with;
import static dev.nokee.commons.hamcrest.gradle.ThrowableMatchers.message;
import static dev.nokee.commons.hamcrest.gradle.ThrowableMatchers.throwsException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TraverseIntegrationTests {
	ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	@Test
	void traverseEachElementsInOrder() {
		List<String> mappedValues = providerFactory().provider(() -> List.of("bar", "foo", "far")).map(traverse("a-"::concat)).get();
		assertThat(mappedValues, contains("a-bar", "a-foo", "a-far"));
	}

	@Test
	void canMutateEachElementsInOrder() {
		List<Integer> mappedValues = providerFactory().provider(() -> List.of("bar", "foobar", "foobarfar")).map(traverse(String::length)).get();
		assertThat(mappedValues, contains(3, 6, 9));
	}

	@Test
	void doesNotTraverseWhenNoElements() {
		List<Object> mappedValues = providerFactory().provider(List::of).map(traverse(alwaysThrows())).get();
		assertThat(mappedValues, emptyIterable());
	}

	@Test
	void canFilterElements() {
		List<String> mappedValue = providerFactory().provider(() -> List.of("a", "b")).map(traverse(filter("a"::equals))).get();
		assertThat(mappedValue, contains("a"));
	}

	private static <OUT, IN> Transformer<OUT, IN> alwaysThrows() {
		return __ -> { throw new UnsupportedOperationException(); };
	}
}
