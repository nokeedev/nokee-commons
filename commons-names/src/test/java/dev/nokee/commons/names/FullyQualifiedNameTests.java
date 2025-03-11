package dev.nokee.commons.names;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class FullyQualifiedNameTests {
	abstract class QualifyingNameTester {
		abstract ElementName subject();

		@Test
		void hasQualifyingNameOnOtherQualifier() {
			assertThat(subject().qualifiedBy(Names.of("test")).get("qualifyingName"), equalTo("test"));
			assertThat(Names.of("test").append(subject()).get("qualifyingName"), equalTo("test"));
		}

		@Test
		void hasNoQualifyingNameOnMainQualifier() {
			assertThat(subject().qualifiedBy(Names.ofMain()).get("qualifyingName"), equalTo(null));
			assertThat(Names.ofMain().append(subject()).get("qualifyingName"), equalTo(null));
		}
	}

	@Test
	void mainNamesHasNoQualifyingName() {
		assertThat(Names.ofMain().get("qualifyingName"), equalTo(null));
	}

	@Test
	void otherNamesHasNoQualifyingName() {
		assertThat(Names.of("other").get("qualifyingName"), equalTo(null));
	}

	@Nested
	class OtherTests extends QualifyingNameTester {
		@Override
		ElementName subject() {
			return ElementName.of("other");
		}
	}

	@Nested
	class MainTests extends QualifyingNameTester {
		@Override
		ElementName subject() {
			return ElementName.ofMain("executable");
		}
	}

	@Nested
	class TaskTests extends QualifyingNameTester {
		@Override
		ElementName subject() {
			return ElementName.taskName("compile", "cpp");
		}
	}

	@Nested
	class VerbOnlyTaskTests extends QualifyingNameTester {
		@Override
		ElementName subject() {
			return ElementName.taskName("link");
		}
	}

	@Nested
	class ObjectOnlyTaskTests extends QualifyingNameTester {
		@Override
		ElementName subject() {
			return ElementName.taskName().forObject("objects");
		}
	}

	@Nested
	class ConfigurationTests extends QualifyingNameTester {
		@Override
		ElementName subject() {
			return ElementName.configurationName("linkElements");
		}
	}

	@Nested
	class ComponentTests extends QualifyingNameTester {
		@Override
		ElementName subject() {
			return ElementName.componentName("cpp");
		}
	}
}
