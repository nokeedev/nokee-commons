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
package dev.nokee.commons.provider;

import org.gradle.api.Transformer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;

class FlatTransformEachTests {
	@Nested
	class WhenElementsMapOneToOne {
		Transformer<List<String>, Iterable<? extends String>> subject = CollectionElementTransformer.flatTransformEach(prefixWith("a"));
		List<String> result = subject.transform(Arrays.asList("foo", "bar", "far"));

		@Test
		void checkTransformation() {
			assertThat(result, contains("a-foo", "a-bar", "a-far"));
		}
	}

	@Nested
	class WhenElementsMapOneToMultiple {
		Transformer<List<String>, Iterable<? extends String>> subject = CollectionElementTransformer.flatTransformEach(prefixWith("a", "b"));
		List<String> result = subject.transform(Arrays.asList("foo", "bar", "far"));

		@Test
		void checkTransformation() {
			assertThat(result, contains("a-foo", "b-foo", "a-bar", "b-bar", "a-far", "b-far"));
		}
	}

	@Nested
	class WhenElementsMapOneToNone {
		Transformer<List<String>, Iterable<? extends String>> subject = CollectionElementTransformer.flatTransformEach(onlyIf("foo"::equals));
		List<String> result = subject.transform(Arrays.asList("foo", "bar", "far"));

		@Test
		void checkTransformation() {
			assertThat(result, contains("foo"));
		}
	}


	@Nested
	class WhenNoElementsToMap {
		Transformer<List<String>, Iterable<? extends String>> subject = CollectionElementTransformer.flatTransformEach(alwaysThrows());
		List<String> result = subject.transform(Collections.emptyList());

		@Test
		void checkTransformation() {
			assertThat(result, emptyIterable());
		}
	}

	@Nested
	class WhenElementsMapToAnotherType {
		Transformer<List<Integer>, Iterable<? extends String>> subject = CollectionElementTransformer.flatTransformEach(length());
		List<Integer> result = subject.transform(Arrays.asList("bar", "foobar", "foobarfar"));

		@Test
		void checkTransformation() {
			assertThat(result, contains(3, 6, 9));
		}
	}

	private static Transformer<Iterable<String>, String> onlyIf(Predicate<? super String> predicate) {
		return s -> predicate.test(s) ? Collections.singletonList(s) : Collections.emptyList();
	}

	private static Transformer<List<String>, String> prefixWith(String... prefixes) {
		return s -> Stream.of(prefixes).map(it -> it + "-" + s).collect(Collectors.toList());
	}

	private static Transformer<Iterable<Integer>, String> length() {
		return s -> Collections.singletonList(s.length());
	}

	private static <OUT, IN> Transformer<Iterable<OUT>, IN> alwaysThrows() {
		return __ -> { throw new UnsupportedOperationException(); };
	}
}
