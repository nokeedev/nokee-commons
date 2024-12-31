package dev.nokee.commons.names;

import org.junit.jupiter.api.Test;

import static dev.nokee.commons.names.ElementName.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;

class NameCompareTests {
	@Test
	void testSameNames() {
		assertThat(ofMain(), comparesEqualTo(ofMain()));
		assertThat(ofMain(), comparesEqualTo(ofMain("main")));
		assertThat(of("compileCpp"), comparesEqualTo(taskName("compile", "cpp")));
	}
}
