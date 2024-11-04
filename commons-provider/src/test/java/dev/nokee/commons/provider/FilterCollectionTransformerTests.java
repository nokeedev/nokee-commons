package dev.nokee.commons.provider;

import org.gradle.api.Transformer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static dev.nokee.commons.provider.FilterTransformer.filter;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

class FilterCollectionTransformerTests {
	@Test
	void canFilterElementsUsingTransformEach() {
		Transformer<List<String>, Iterable<? extends String>> subject = CollectionElementTransformer.transformEach(filter((String it) -> it.length() < 5));
		List<String> result = subject.transform(Arrays.asList("bar", "foobar", "foo", "foobarfar"));
		assertThat(result, contains("bar", "foo"));
	}
}
