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
package dev.nokee.commons.gradle;

import org.gradle.api.Transformer;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.nokee.commons.gradle.TransformerUtils.flatTraverse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;

class FlatTraverseIntegrationTests {
	ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	@Test
	void traverseEachElementsInOrder() {
		List<String> mappedValues = providerFactory().provider(() -> List.of("bar", "foo", "far")).map(flatTraverse(it -> List.of("a-".concat(it)))).get();
		assertThat(mappedValues, contains("a-bar", "a-foo", "a-far"));
	}

	@Test
	void canRemoveElementFromResult() {
		List<String> mappedValues = providerFactory().provider(() -> List.of("bar", "foo", "far")).map(flatTraverse(it -> it.equals("foo") ? List.of(it) : List.of())).get();
		assertThat(mappedValues, contains("foo"));
	}

	@Test
	void doesNotTraverseWhenNoElements() {
		List<Object> mappedValues = providerFactory().provider(List::of).map(flatTraverse(alwaysThrows())).get();
		assertThat(mappedValues, emptyIterable());
	}

	private static <OUT, IN> Transformer<OUT, IN> alwaysThrows() {
		return __ -> { throw new UnsupportedOperationException(); };
	}
}
