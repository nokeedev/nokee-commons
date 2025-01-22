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
			return "releaseWindows";
		}

		@Override
		public String debugQualifiedName() {
			return "debug";
		}

		@Override
		public String name() {
			return "executable";
		}

		@Test
		void test() {
			assertThat(subject.qualifiedBy(new TestQualifier(NameString.of("foo"))), isA(Qualifier.class));
			assertThat(ElementName.configurationName("linkElements").qualifiedBy(subject.qualifiedBy(new TestQualifier(NameString.of("foo")))), hasToString("fooLinkElements"));
		}

		@Nested
		class QualifiedTests implements QualifiedNameTester {
			@Override
			public QualifiedName subject() {
				return subject.qualifiedBy(new TestQualifier(NameString.of("debugOpt")));
			}

			@Override
			public String otherNameQualifiedName() {
				return "debugOptOther";
			}

			@Override
			public String otherTaskNameQualifiedName() {
				return "doDebugOptSomething";
			}

			@Override
			public String otherQualifierQualifiedName() {
				return "other";
			}
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
			assertThat(subject.qualifiedBy(new TestQualifier(NameString.of("foo"))), isA(Qualifier.class));
			assertThat(ElementName.configurationName("linkElements").qualifiedBy(subject.qualifiedBy(new TestQualifier(NameString.of("foo")))), hasToString("fooSharedLibraryLinkElements"));
		}

		@Nested
		class QualifiedTests implements QualifiedNameTester {
			@Override
			public QualifiedName subject() {
				return subject.qualifiedBy(new TestQualifier(NameString.of("debugOpt")));
			}

			@Override
			public String otherNameQualifiedName() {
				return "debugOptOther";
			}

			@Override
			public String otherTaskNameQualifiedName() {
				return "doDebugOptSomething";
			}

			@Override
			public String otherQualifierQualifiedName() {
				return "otherSharedLibrary";
			}
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
			assertThat(subject.qualifiedBy(new TestQualifier(NameString.of("foo"))), not(isA(Qualifier.class)));
		}

		@Nested
		class QualifiedTests implements QualifiedNameTester {
			@Override
			public QualifiedName subject() {
				return subject.qualifiedBy(new TestQualifier(NameString.of("debugOpt")));
			}

			@Override
			public String otherNameQualifiedName() {
				return "debugOptOther";
			}

			@Override
			public String otherTaskNameQualifiedName() {
				return "doDebugOptSomething";
			}

			@Override
			public String otherQualifierQualifiedName() {
				return "linkOther";
			}
		}
	}

	@Nested
	class ObjectOnlyTaskNameTests implements NameTester, QualifiableTester {
		TaskName subject = ElementName.taskName().forObject("objects");

		@Override
		public TaskName subject() {
			return subject;
		}

		@Override
		public String releaseWindowsQualifiedName() {
			return "releaseWindowsObjects";
		}

		@Override
		public String debugQualifiedName() {
			return "debugObjects";
		}

		@Override
		public String name() {
			return "objects";
		}

		@Test
		void qualifiedNameAreNotQualifier() {
			assertThat(subject.qualifiedBy(new TestQualifier(NameString.of("foo"))), not(isA(Qualifier.class)));
		}

		@Nested
		class QualifiedTests implements QualifiedNameTester {
			@Override
			public QualifiedName subject() {
				return subject.qualifiedBy(new TestQualifier(NameString.of("debugOpt")));
			}

			@Override
			public String otherNameQualifiedName() {
				return "debugOptOther";
			}

			@Override
			public String otherTaskNameQualifiedName() {
				return "doDebugOptSomething";
			}

			@Override
			public String otherQualifierQualifiedName() {
				return "otherObjects";
			}
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
			assertThat(subject.qualifiedBy(new TestQualifier(NameString.of("foo"))), not(isA(Qualifier.class)));
		}

		@Nested
		class QualifiedTests implements QualifiedNameTester {
			@Override
			public QualifiedName subject() {
				return subject.qualifiedBy(new TestQualifier(NameString.of("debugOpt")));
			}

			@Override
			public String otherNameQualifiedName() {
				return "debugOptOther";
			}

			@Override
			public String otherTaskNameQualifiedName() {
				return "doDebugOptSomething";
			}

			@Override
			public String otherQualifierQualifiedName() {
				return "compileOtherCpp";
			}
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
			assertThat(subject.qualifiedBy(new TestQualifier(NameString.of("foo"))), not(isA(Qualifier.class)));
		}

		@Nested
		class QualifiedTests implements QualifiedNameTester {
			@Override
			public QualifiedName subject() {
				return subject.qualifiedBy(new TestQualifier(NameString.of("debugOpt")));
			}

			@Override
			public String otherNameQualifiedName() {
				return "debugOptOther";
			}

			@Override
			public String otherTaskNameQualifiedName() {
				return "doDebugOptSomething";
			}

			@Override
			public String otherQualifierQualifiedName() {
				return "otherHeaderSearchPaths";
			}
		}
	}

	@Nested
	class ComponentNameTests implements NameTester, QualifiableTester {
		ElementName subject = ElementName.componentName("cpp");

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
			assertThat(subject.qualifiedBy(new TestQualifier(NameString.of("foo"))), not(isA(Qualifier.class)));
		}

		@Nested
		class QualifiedTests implements QualifiedNameTester {
			@Override
			public QualifiedName subject() {
				return subject.qualifiedBy(new TestQualifier(NameString.of("debugOpt")));
			}

			@Override
			public String otherNameQualifiedName() {
				return "debugOptOther";
			}

			@Override
			public String otherTaskNameQualifiedName() {
				return "doDebugOptSomething";
			}

			@Override
			public String otherQualifierQualifiedName() {
				return "cppOther";
			}
		}
	}
}
