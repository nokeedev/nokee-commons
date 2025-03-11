package dev.nokee.commons.names;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static dev.nokee.commons.names.ElementName.taskName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class TaskNamingSchemeTests {
	@Nested
	class NonQualifiedTests {
		@Test
		void testVerbOnlyTaskName() {
			assertThat(taskName("compile").toString(NamingScheme.taskName()), equalTo("compile"));
		}

		@Test
		void testFullTaskName() {
			assertThat(taskName("compile", "cpp").toString(NamingScheme.taskName()), equalTo("compileCpp"));
		}

		@Test
		void testObjectOnlyTaskName() {
			assertThat(taskName().forObject("objects").toString(NamingScheme.taskName()), equalTo("objects"));
		}
	}


	@Nested
	class QualifiedTests {
		@Test
		void testVerbOnlyTaskName() {
			assertThat(taskName("compile").qualifiedBy(Names.of("test")).toString(NamingScheme.taskName()), equalTo("compileTest"));
		}

		@Test
		void testFullTaskName() {
			assertThat(taskName("compile", "cpp").qualifiedBy(Names.of("test")).toString(NamingScheme.taskName()), equalTo("compileTestCpp"));
		}

		@Test
		void testObjectOnlyTaskName() {
			assertThat(taskName().forObject("objects").qualifiedBy(Names.of("test")).toString(NamingScheme.taskName()), equalTo("testObjects"));
		}
	}

	@Nested
	class MainQualifiedTests {
		@Test
		void testVerbOnlyTaskName() {
			assertThat(taskName("compile").qualifiedBy(Names.ofMain()).toString(NamingScheme.taskName()), equalTo("compile"));
		}

		@Test
		void testFullTaskName() {
			assertThat(taskName("compile", "cpp").qualifiedBy(Names.ofMain()).toString(NamingScheme.taskName()), equalTo("compileCpp"));
		}

		@Test
		void testObjectOnlyTaskName() {
			assertThat(taskName().forObject("objects").qualifiedBy(Names.ofMain()).toString(NamingScheme.taskName()), equalTo("objects"));
		}
	}
}
