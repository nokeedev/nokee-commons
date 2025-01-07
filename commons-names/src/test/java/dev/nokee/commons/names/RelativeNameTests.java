package dev.nokee.commons.names;

import org.gradle.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RelativeNameTests {
	@Nested
	class NamesTests {
		@Test
		void canRelativizeMainComponent() {
			FullyQualifiedName subject = Names.ofMain().append("foo").append("bar");

			assertThat(subject.toString(NameBuilder.lowerCamelCase()), equalTo("mainFooBar"));
			assertThat(subject.relativeTo(Names.ofMain()).toString(NameBuilder.lowerCamelCase()), equalTo("fooBar"));
			assertThat(subject.relativeTo(Names.ofMain().append("foo")).toString(NameBuilder.lowerCamelCase()), equalTo("bar"));
		}

		@Test
		void canRelativizeTaskName() {
			FullyQualifiedName subject = Names.of("test").append("foo").taskName("compile", "cpp");

			assertThat(subject.toString(NameBuilder.lowerCamelCase()), equalTo("compileTestFooCpp"));
			assertThat(subject.relativeTo(Names.of("test")).toString(NameBuilder.lowerCamelCase()), equalTo("compileFooCpp"));
			assertThat(subject.relativeTo(Names.of("test").append("foo")).toString(NameBuilder.lowerCamelCase()), equalTo("compileCpp"));
		}

		@Test
		void throwsExceptionWhenNoCommonRoot() {
			FullyQualifiedName subject = Names.of("test").append("foo");

			assertThrows(RuntimeException.class, () -> subject.relativeTo(Names.ofMain()));
		}

		@Test
		void throwsExceptionWhenOverSpecifiedQualifier() {
			FullyQualifiedName subject = Names.of("test");

			assertThrows(RuntimeException.class, () -> subject.relativeTo(Names.of("test").append("foo")));
		}
	}



	@Nested
	class NamedTests {
		@Test
		void standardTaskName() {
			FullyQualifiedName subject = Name.of((Named) () -> "compileTestFooBarCpp");

			assertThat(subject.toString(NameBuilder.lowerCamelCase()), equalTo("compileTestFooBarCpp"));
			assertThat(subject.relativeTo(Names.of("test")).toString(NameBuilder.lowerCamelCase()), equalTo("compileFooBarCpp"));
			assertThat(subject.relativeTo(Names.of("test").append("foo")).toString(NameBuilder.lowerCamelCase()), equalTo("compileBarCpp"));
		}

		@Test
		void nativeIncomingConfigurationName() {
			FullyQualifiedName subject = Name.of((Named) () -> "nativeLinkTestFooBar");

			assertThat(subject.toString(NameBuilder.lowerCamelCase()), equalTo("nativeLinkTestFooBar"));
			assertThat(subject.relativeTo(Names.of("test")).toString(NameBuilder.lowerCamelCase()), equalTo("nativeLinkFooBar"));
			assertThat(subject.relativeTo(Names.of("test").append("foo")).toString(NameBuilder.lowerCamelCase()), equalTo("nativeLinkBar"));
		}

		@Test
		void declarativeConfigurationName() {
			FullyQualifiedName subject = Name.of((Named) () -> "testFooBarImplementation");

			assertThat(subject.toString(NameBuilder.lowerCamelCase()), equalTo("testFooBarImplementation"));
			assertThat(subject.relativeTo(Names.of("test")).toString(NameBuilder.lowerCamelCase()), equalTo("fooBarImplementation"));
			assertThat(subject.relativeTo(Names.of("test").append("foo")).toString(NameBuilder.lowerCamelCase()), equalTo("barImplementation"));
		}

		@Test
		void throwsExceptionWhenOverSpecifiedQualifier() {
			FullyQualifiedName subject = Name.of((Named) () -> "test");

			assertThrows(RuntimeException.class, () -> subject.relativeTo(Names.of("test").append("foo")));
		}
	}
}
