package dev.nokee.commons.names;

class OtherElementNameTests implements NamesTester {
	Names subject = ElementName.of("test");

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
