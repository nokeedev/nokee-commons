package dev.nokee.commons.names;

import org.junit.jupiter.api.Test;

import static dev.nokee.commons.names.NamingScheme.dirNames;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class DirNamingSchemeTests {
	@Test
	void testDirNamesScheme() {
		Names names = Names.ofMain().append("debug").append("cpp");
		assertThat(names.toString(dirNames()), equalTo("main/debug/cpp"));
	}

	@Test
	void testDirNamesSchemeMadeOfMultipleSegments() {
		Names names = Names.ofMain().append(new OtherElementName(NameString.of(NameString.of("debug"), NameString.ofMain(NameString.of("macos")), NameString.of("x86")))).append("cpp");
		assertThat(names.toString(dirNames()), equalTo("main/debug/x86/cpp"));
	}
}
