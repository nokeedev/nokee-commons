package dev.nokee.commons.provider;

import org.gradle.api.Transformer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;

class TransformEachTests {
	@Nested
	class WhenElementsMapOneToOne {
		Transformer<List<String>, Iterable<? extends String>> subject = CollectionElementTransformer.transformEach(prefixWith("a-"));
		List<String> result = subject.transform(Arrays.asList("bar", "foo", "far"));

		@Test
		void checkTransformation() {
			assertThat(result, contains("a-bar", "a-foo", "a-far"));
		}
	}

	@Nested
	class WhenElementsMapToAnotherType {
		Transformer<List<Integer>, Iterable<? extends String>> subject = CollectionElementTransformer.transformEach(String::length);
		List<Integer> result = subject.transform(Arrays.asList("bar", "foobar", "foobarfar"));

		@Test
		void checkTransformation() {
			assertThat(result, contains(3, 6, 9));
		}
	}

	@Nested
	class WhenNoElementsToMap {
		Transformer<List<String>, Iterable<? extends String>> subject = CollectionElementTransformer.transformEach(alwaysThrows());
		List<String> result = subject.transform(Collections.emptyList());

		@Test
		void checkTransformation() {
			assertThat(result, emptyIterable());
		}
	}

	private static Transformer<String, String> prefixWith(String prefix) {
		return s -> prefix + s;
	}

	private static <OUT, IN> Transformer<OUT, IN> alwaysThrows() {
		return __ -> { throw new UnsupportedOperationException(); };
	}
}
