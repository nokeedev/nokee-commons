package dev.nokee.commons.names;

import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.CppComponent;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class CppNamesTests {
	private static CppBinary newBinary(String name) {
		CppBinary result = Mockito.mock(CppBinary.class);
		Mockito.when(result.getName()).thenReturn(name);
		return result;
	}

	private static CppComponent newComponent(String name) {
		CppComponent result = Mockito.mock(CppComponent.class);
		Mockito.when(result.getName()).thenReturn(name);
		return result;
	}

	@Nested
	class ForCppBinary {
		CppNames.ForBinary subject = CppNames.of(newBinary("mainRelease"));

		@Test
		void testCompileCppTaskName() {
			assertThat(subject.compileTaskName(), equalTo("compileReleaseCpp"));
		}

		@Test
		void testCompileLanguageTaskName() {
			assertThat(subject.compileTaskName("c"), equalTo("compileReleaseC"));
		}

		@Test
		void testLinkTaskName() {
			assertThat(subject.linkTaskName(), equalTo("linkRelease"));
		}

		@Test
		void testRuntimeElementsConfigurationName() {
			assertThat(subject.runtimeElementsConfigurationName(), equalTo("releaseRuntimeElements"));
		}

		@Test
		void testLinkElementsConfigurationName() {
			assertThat(subject.linkElementsConfigurationName(), equalTo("releaseLinkElements"));
		}

		@Test
		void testImplementationConfigurationName() {
			assertThat(subject.implementationConfigurationName(), equalTo("mainReleaseImplementation"));
		}

		@Test
		void testCppCompileConfigurationName() {
			assertThat(subject.cppCompileConfigurationName(), equalTo("cppCompileRelease"));
		}

		@Test
		void testNativeLinkConfigurationName() {
			assertThat(subject.nativeLinkConfigurationName(), equalTo("nativeLinkRelease"));
		}

		@Test
		void testNativeRuntimeConfigurationName() {
			assertThat(subject.nativeRuntimeConfigurationName(), equalTo("nativeRuntimeRelease"));
		}
	}

	@Nested
	class ForCppTestExecutable {
		CppNames.ForBinary subject = CppNames.of(newBinary("testExecutable"));

		@Test
		void testCompileCppTaskName() {
			assertThat(subject.compileTaskName(), equalTo("compileTestCpp"));
		}

		@Test
		void testCompileLanguageTaskName() {
			assertThat(subject.compileTaskName("c"), equalTo("compileTestC"));
		}

		@Test
		void testLinkTaskName() {
			assertThat(subject.linkTaskName(), equalTo("linkTest"));
		}

		@Test
		void testRuntimeElementsConfigurationName() {
			assertThat(subject.runtimeElementsConfigurationName(), equalTo("testRuntimeElements"));
		}

		@Test
		void testLinkElementsConfigurationName() {
			assertThat(subject.linkElementsConfigurationName(), equalTo("testLinkElements"));
		}

		@Test
		void testImplementationConfigurationName() {
			assertThat(subject.implementationConfigurationName(), equalTo("testExecutableImplementation"));
		}

		@Test
		void testCppCompileConfigurationName() {
			assertThat(subject.cppCompileConfigurationName(), equalTo("cppCompileTest"));
		}

		@Test
		void testNativeLinkConfigurationName() {
			assertThat(subject.nativeLinkConfigurationName(), equalTo("nativeLinkTest"));
		}

		@Test
		void testNativeRuntimeConfigurationName() {
			assertThat(subject.nativeRuntimeConfigurationName(), equalTo("nativeRuntimeTest"));
		}
	}

	@Nested
	class ForCppComponent {
		CppNames.ForComponent subject = CppNames.of(newComponent("main"));

		@Test
		void testCppApiElementsConfigurationName() {
			assertThat(subject.cppApiElementsConfigurationName(), equalTo("cppApiElements"));
		}

		@Test
		void testImplementationConfigurationName() {
			assertThat(subject.implementationConfigurationName(), equalTo("implementation"));
		}

		@Test
		void testApiConfigurationName() {
			assertThat(subject.apiConfigurationName(), equalTo("api"));
		}
	}

	@Nested
	class ForCppTestSuite {
		CppNames.ForComponent subject = CppNames.of(newComponent("test"));

		@Test
		void testCppApiElementsConfigurationName() {
			assertThat(subject.cppApiElementsConfigurationName(), equalTo("testCppApiElements"));
		}

		@Test
		void testImplementationConfigurationName() {
			assertThat(subject.implementationConfigurationName(), equalTo("testImplementation"));
		}

		@Test
		void testApiConfigurationName() {
			assertThat(subject.apiConfigurationName(), equalTo("testApi"));
		}
	}
}
