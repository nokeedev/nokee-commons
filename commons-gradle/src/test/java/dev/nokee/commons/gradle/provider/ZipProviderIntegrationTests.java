/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.nokee.commons.gradle.provider;

import org.gradle.api.Buildable;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static dev.nokee.commons.hamcrest.gradle.BuildDependenciesMatcher.buildDependencies;
import static dev.nokee.commons.hamcrest.gradle.NamedMatcher.named;
import static dev.nokee.commons.hamcrest.gradle.provider.NoValueProviderMatcher.noValueProvider;
import static dev.nokee.commons.hamcrest.gradle.provider.ProviderOfMatcher.providerOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsInAnyOrder;

class ZipProviderIntegrationTests {
	private static ObjectFactory objectFactory() {
		return ProjectBuilder.builder().build().getObjects();
	}

	private static ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	private static Project rootProject() {
		return ProjectBuilder.builder().build();
	}

	private static <T> Provider<T> provider(T value) {
		return providerFactory().provider(() -> value);
	}

	ZipProvider.Factory factory = objectFactory().newInstance(ZipProvider.Factory.class);


	interface ZipProviderBuilderTester {
		ZipProvider subject();

		@Test
		default void doesNotCallCombinerWhenZippedProviderNotRealized() {
			Assertions.assertDoesNotThrow(() -> subject().zip(alwaysThrowCombiner()));
		}

		@Test
		default void discardValueWhenCombining() {
			MatcherAssert.assertThat(subject().zip(__ -> null), noValueProvider());
		}
	}

	@Test
	void canZipTwoProvidersUsingBiFunctionDirectlyFromFactory() {
		MatcherAssert.assertThat(factory.zip(provider("first-value"), provider(42), (a, b) -> new Object[] { a, b }), //
			providerOf(arrayContaining("first-value", 42)));
	}

	@Nested
	class TwoElementsToZipProvider implements ZipProviderBuilderTester {
		ZipProvider.Builder2<String, Integer> subject = objectFactory().newInstance(ZipProvider.Factory.class).zipProvider() //
			.value(provider("first-value")) //
			.value(provider(42));

		@Override
		public ZipProvider subject() {
			return subject;
		}

		@Test
		void returnsProviderOfTwoElementsZipped() {
			MatcherAssert.assertThat(subject.zip(ZipProvider.ValuesToZip::values), providerOf(arrayContaining("first-value", 42)));
		}

		@Test
		void returnsProviderOfTwoElementsZippedUsingFunction() {
			MatcherAssert.assertThat(subject.zip((a, b) -> new Object[] {a, b}), providerOf(arrayContaining("first-value", 42)));
		}
	}

	@Nested
	class MultipleElementsToZipProvider implements ZipProviderBuilderTester {
		ZipProvider.BuilderX subject = objectFactory().newInstance(ZipProvider.Factory.class).zipProvider() //
			.value(provider("first-value")) //
			.value(provider(42)) //
			.value(provider(false)) //
			.value(provider(4.2f));

		@Override
		public ZipProvider subject() {
			return subject;
		}

		@Test
		void returnsProviderOfMultipleElementsZipped() {
			MatcherAssert.assertThat(subject.zip(ZipProvider.ValuesToZip::values), providerOf(arrayContaining("first-value", 42, false, 4.2f)));
		}
	}

	@Nested
	class ImplicitTaskDependencies {
		@Test
		void mergesImplicitTaskDependenciesWhenZippingTwoValues() {
			Project project = rootProject();
			Buildable taskDependencies = project.files(factory.zipProvider()
				.value(project.getTasks().register("firstTask").map(__ -> "first"))
				.value(project.getTasks().register("secondTask").map(__ -> "second"))
				.zip(alwaysThrowCombiner()));
			assertThat(taskDependencies, buildDependencies(containsInAnyOrder(named("firstTask"), named("secondTask"))));
		}

		@Test
		void mergesImplicitTaskDependenciesWhenZippingMultipleValues() {
			Project project = rootProject();
			Buildable taskDependencies = project.files(factory.zipProvider()
				.value(project.getTasks().register("firstTask").map(__ -> "first"))
				.value(project.getTasks().register("secondTask").map(__ -> "second"))
				.value(project.getTasks().register("thirdTask").map(__ -> "third"))
				.value(project.getTasks().register("fourthTask").map(__ -> "fourth"))
				.zip(alwaysThrowCombiner()));
			assertThat(taskDependencies, buildDependencies(containsInAnyOrder(named("firstTask"), named("secondTask"), named("thirdTask"), named("fourthTask"))));
		}
	}

	@Nested
	class ZipProviderBuilderIsolation {
		ZipProvider.Builder1<String> builder1 = objectFactory().newInstance(ZipProvider.Factory.class).zipProvider()
			.value(provider("first"));
		ZipProvider.Builder2<String, String> builder2 = builder1
			.value(provider("second"));
		ZipProvider.BuilderX builder3 = builder2
			.value(provider("third"))
			.value(provider("fourth"));

		@Test
		void dualValueBuilderDoesNotContainsAddedValues() {
			MatcherAssert.assertThat(builder2.zip(ZipProvider.ValuesToZip::values),
				providerOf(arrayContaining("first", "second")));
		}

		@Test
		void multipleZippedValueOnlyContainsValuesUpToZipping() {
			Provider<Object[]> provider = builder3.zip(ZipProvider.ValuesToZip::values);

			// this value should not be present in provider
			builder3.value(providerFactory().provider(() -> "fifth"));

			assertThat(provider, providerOf(arrayContaining("first", "second", "third", "fourth")));
		}
	}

	static <R> ZipProvider.Combiner<R> alwaysThrowCombiner() {
		return __ -> { throw new UnsupportedOperationException(); };
	}

	// TODO: Test knowledge is kept
}
