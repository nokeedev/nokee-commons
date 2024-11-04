package dev.nokee.commons.provider;

import org.junit.jupiter.api.Test;

import static dev.nokee.commons.provider.FilterTransformer.filter;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

class FilterTransformerTests {
	static final String testString = "my string is an example";

	@Test
	void returnsTransformedValueWhenSpecIsSatisfied() {
		assertThat(filter((String it) -> it.contains("my string")).transform(testString), equalTo(testString));
	}

	@Test
	void returnNullWhenSpecNotSatisfied() {
		assertThat(filter((String it) -> it.contains("my bool")).transform(testString), nullValue());
	}
}
