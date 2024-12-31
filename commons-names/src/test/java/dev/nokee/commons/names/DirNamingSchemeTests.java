package dev.nokee.commons.names;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class DirNamingSchemeTests {
	Names names = ElementName.ofMain().append("debug").append("cpp");

	@Test
	void testDirNamesScheme() {
		assertThat(names.toString(NamingScheme.dirNames()), equalTo("main/debug/cpp"));
	}
}
