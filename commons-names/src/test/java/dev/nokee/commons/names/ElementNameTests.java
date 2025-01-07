package dev.nokee.commons.names;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ElementNameTests {
	@Nested
	class MainNameTests implements NameTester, QualifiableTester {
		OtherName subject = ElementName.ofMain("executable");

		@Override
		public ElementName subject() {
			return subject;
		}

		@Override
		public String releaseWindowsQualifiedName() {
			return "releaseWindowsExecutable";
		}

		@Override
		public String debugQualifiedName() {
			return "debugExecutable";
		}

		@Override
		public String name() {
			return "executable";
		}

		@Test
		void test() {
			assertThat(subject.qualifiedBy(new TestQualifier(Qualifiers.of("foo"))), isA(Qualifier.class));
			assertThat(ElementName.configurationName("linkElements").qualifiedBy(subject.qualifiedBy(new TestQualifier(Qualifiers.of("foo")))), hasToString("fooLinkElements"));
		}
	}

	@Nested
	class OtherNameTests implements NameTester, QualifiableTester {
		OtherName subject = ElementName.of("sharedLibrary");

		@Override
		public ElementName subject() {
			return subject;
		}

		@Override
		public String releaseWindowsQualifiedName() {
			return "releaseWindowsSharedLibrary";
		}

		@Override
		public String debugQualifiedName() {
			return "debugSharedLibrary";
		}

		@Override
		public String name() {
			return "sharedLibrary";
		}

		@Test
		void test() {
			assertThat(subject.qualifiedBy(new TestQualifier(Qualifiers.of("foo"))), isA(Qualifier.class));
			assertThat(ElementName.configurationName("linkElements").qualifiedBy(subject.qualifiedBy(new TestQualifier(Qualifiers.of("foo")))), hasToString("fooSharedLibraryLinkElements"));
		}
	}

	@Nested
	class VerbOnlyTaskNameTests implements NameTester, QualifiableTester {
		TaskName subject = ElementName.taskName("link");

		@Override
		public TaskName subject() {
			return subject;
		}

		@Override
		public String releaseWindowsQualifiedName() {
			return "linkReleaseWindows";
		}

		@Override
		public String debugQualifiedName() {
			return "linkDebug";
		}

		@Override
		public String name() {
			return "link";
		}

		@Test
		void qualifiedNameAreNotQualifier() {
			assertThat(subject.qualifiedBy(new TestQualifier(Qualifiers.of("foo"))), not(isA(Qualifier.class)));
		}
	}

	@Nested
	class TaskNameTests implements NameTester, QualifiableTester {
		TaskName subject = ElementName.taskName("compile", "cpp");

		@Override
		public TaskName subject() {
			return subject;
		}

		@Override
		public String releaseWindowsQualifiedName() {
			return "compileReleaseWindowsCpp";
		}

		@Override
		public String debugQualifiedName() {
			return "compileDebugCpp";
		}

		@Override
		public String name() {
			return "compileCpp";
		}

		@Test
		void qualifiedNameAreNotQualifier() {
			assertThat(subject.qualifiedBy(new TestQualifier(Qualifiers.of("foo"))), not(isA(Qualifier.class)));
		}
	}

	@Nested
	class ConfigurationNameTests implements NameTester, QualifiableTester {
		ConfigurationName subject = ElementName.configurationName("headerSearchPaths");

		@Override
		public ElementName subject() {
			return subject;
		}

		@Override
		public String releaseWindowsQualifiedName() {
			return "releaseWindowsHeaderSearchPaths";
		}

		@Override
		public String debugQualifiedName() {
			return "debugHeaderSearchPaths";
		}

		@Override
		public String name() {
			return "headerSearchPaths";
		}

		@Test
		void qualifiedNameAreNotQualifier() {
			assertThat(subject.qualifiedBy(new TestQualifier(Qualifiers.of("foo"))), not(isA(Qualifier.class)));
		}
	}

	@Nested
	class ComponentNameTests implements NameTester, QualifiableTester {
		SoftwareComponentName subject = ElementName.componentName("cpp");

		@Override
		public ElementName subject() {
			return subject;
		}

		@Override
		public String releaseWindowsQualifiedName() {
			return "cppReleaseWindows";
		}

		@Override
		public String debugQualifiedName() {
			return "cppDebug";
		}

		@Override
		public String name() {
			return "cpp";
		}

		@Test
		void qualifiedNameAreNotQualifier() {
			assertThat(subject.qualifiedBy(new TestQualifier(Qualifiers.of("foo"))), not(isA(Qualifier.class)));
		}
	}
}
