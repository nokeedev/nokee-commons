package dev.nokee.commons.provider;

import org.gradle.api.Transformer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static dev.nokee.commons.provider.PeekTransformer.peek;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

class PeekTransformerTests {
	List<String> result = new ArrayList<>();
	Transformer<String, String> subject = peek(result::add);

	@Test
	void returnsTransformInput() {
		assertThat(subject.transform("foo"), equalTo("foo"));
	}

	@Test
	void callsActionWithTransformInput() {
		subject.transform("foo");
		assertThat(result, contains("foo"));
	}
}
