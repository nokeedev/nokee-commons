/*
 * Copyright 2021 the original author or authors.
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

import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static dev.nokee.commons.gradle.TransformerUtils.toSet;
import static dev.nokee.commons.gradle.TransformerUtils.traverse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ToSetIntegrationTests {
	ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	Object a = new Object() {
		@Override
		public String toString() {
			return "a";
		}
	};
	Object b = new Object() {
		@Override
		public String toString() {
			return "b";
		}
	};
	Object c = new Object() {
		@Override
		public String toString() {
			return "c";
		}
	};

	@Test
	void convertListToSetWithoutAffectingValuesAndOrdering() {
		Set<Object> mappedValues = providerFactory().provider(() -> List.of(a, b, c)).map(toSet()).get();
		assertThat("returns set instance", mappedValues, instanceOf(Set.class));
		assertThat("retains ordering", mappedValues, contains(a, b, c));
	}

	@Test
	void noOpWhenTransformingIterableIsAlreadySet() {
		Set<Object> expected = Set.of(a, b, c);
		Set<Object> actual = providerFactory().provider(() -> expected).map(toSet()).get();
		assertThat("returns exact instance (no-op)", actual, sameInstance(expected));
	}

	@Test
	void toSetTraversedIterable() {
		Set<String> mappedValues = providerFactory().provider(() -> List.of(a, b, c)).map(toSet(traverse(Object::toString))).get();
		assertThat("return set instance", mappedValues, instanceOf(Set.class));
		assertThat("contains traversed transformation", mappedValues, contains("a", "b", "c"));
	}
}
