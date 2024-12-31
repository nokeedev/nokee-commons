package dev.nokee.commons.names;

class MainElementNameTests implements NamesTester {
	Names subject = ElementName.ofMain();

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
