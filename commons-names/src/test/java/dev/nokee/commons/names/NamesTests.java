package dev.nokee.commons.names;

import org.junit.jupiter.api.Nested;

class NamesTests {
	@Nested
	class MainElementNameTests implements NamesTester {
		Names subject = Names.ofMain();

		@Override
		public Names subject() {
			return subject;
		}

		@Override
		public String name() {
			return "main";
		}

		@Override
		public String headerSearchPathsConfigurationName() {
			return "headerSearchPaths";
		}

		@Override
		public String linkLibrariesConfigurationName() {
			return "linkLibraries";
		}

		@Override
		public String implementationConfigurationName() {
			return "implementation";
		}

		@Override
		public String linkElementsConfigurationName() {
			return "linkElements";
		}

		@Override
		public String compileCppTaskName() {
			return "compileCpp";
		}

		@Override
		public String linkTaskName() {
			return "link";
		}

		@Override
		public String javaComponentName() {
			return "java";
		}

		@Override
		public String cppComponentName() {
			return "cpp";
		}

		@Override
		public String signedRuntimeLibrariesConfigurationName() {
			return "signedRuntimeLibraries";
		}

		@Override
		public String compileSignedCppTaskName() {
			return "compileSignedCpp";
		}

		@Override
		public String runSignedTaskName() {
			return "runSigned";
		}

		@Override
		public String mainBundleRuntimeLibrariesConfigurationName() {
			return "runtimeLibraries";
		}

		@Override
		public String compileMainBundleCppTaskName() {
			return "compileCpp";
		}

		@Override
		public String runMainBundleTaskName() {
			return "run";
		}
	}

	@Nested
	class OtherElementNameTests implements NamesTester {
		Names subject = Names.of("test");

		@Override
		public Names subject() {
			return subject;
		}

		@Override
		public String name() {
			return "test";
		}

		@Override
		public String headerSearchPathsConfigurationName() {
			return "testHeaderSearchPaths";
		}

		@Override
		public String linkLibrariesConfigurationName() {
			return "testLinkLibraries";
		}

		@Override
		public String implementationConfigurationName() {
			return "testImplementation";
		}

		@Override
		public String linkElementsConfigurationName() {
			return "testLinkElements";
		}

		@Override
		public String compileCppTaskName() {
			return "compileTestCpp";
		}

		@Override
		public String linkTaskName() {
			return "linkTest";
		}

		@Override
		public String javaComponentName() {
			return "javaTest";
		}

		@Override
		public String cppComponentName() {
			return "cppTest";
		}

		@Override
		public String signedRuntimeLibrariesConfigurationName() {
			return "testSignedRuntimeLibraries";
		}

		@Override
		public String compileSignedCppTaskName() {
			return "compileTestSignedCpp";
		}

		@Override
		public String runSignedTaskName() {
			return "runTestSigned";
		}

		@Override
		public String mainBundleRuntimeLibrariesConfigurationName() {
			return "testRuntimeLibraries";
		}

		@Override
		public String compileMainBundleCppTaskName() {
			return "compileTestCpp";
		}

		@Override
		public String runMainBundleTaskName() {
			return "runTest";
		}
	}
}
