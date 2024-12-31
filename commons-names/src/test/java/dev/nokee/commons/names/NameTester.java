package dev.nokee.commons.names;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;

public interface NameTester {
	Name subject();

	@Test
	default void testName() {
		assertThat(subject(), hasToString(name()));
	}

	String name();
}
