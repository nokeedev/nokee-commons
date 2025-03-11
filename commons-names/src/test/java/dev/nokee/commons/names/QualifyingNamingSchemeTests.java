package dev.nokee.commons.names;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class QualifyingNamingSchemeTests {
	abstract class Tester {
		abstract NamingSchemeTester subject();
	}

	@Nested
	class PrefixTests extends Tester {
		@Test
		void testQualifyingName() {
			assertThat(subject().format(), equalTo("windowsReleaseLinkElements"));
		}

		@Override
		NamingSchemeTester subject() {
			return new NamingSchemeTester(NamingScheme.prefixQualifyingName(), Map.of("elementName", "linkElements", "qualifyingName", "windowsRelease"));
		}
	}

	@Nested
	class SuffixTests extends Tester {
		@Test
		void testQualifyingName() {
			assertThat(subject().format(), equalTo("nativeLinkWindowsRelease"));
		}

		@Override
		NamingSchemeTester subject() {
			return new NamingSchemeTester(NamingScheme.suffixQualifyingName(), Map.of("elementName", "nativeLink", "qualifyingName", "windowsRelease"));
		}
	}

	@Nested
	class TaskTests extends Tester {
		@Test
		void testQualifyingName() {
			assertThat(subject().format(), equalTo("compileWindowsReleaseCpp"));
		}

		@Test
		void verbOnlyTaskName() {
			assertThat(subject().without("object").format(), equalTo("compileWindowsRelease"));
		}

		@Test
		void objectsOnlyTaskName() {
			assertThat(subject().without("verb").format(), equalTo("windowsReleaseCpp"));
		}

		@Test
		void nonQualifiedTaskName() {
			assertThat(subject().without("qualifyingName").format(), equalTo("compileCpp"));
		}

		@Override
		NamingSchemeTester subject() {
			return new NamingSchemeTester(NamingScheme.taskName(), Map.of("verb", "compile", "qualifyingName", "windowsRelease", "object", "cpp"));
		}
	}
}
