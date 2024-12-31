package dev.nokee.commons.names;

import org.junit.jupiter.api.Test;

import static dev.nokee.commons.names.ElementName.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;

public interface NamesTester extends NameTester {
	Names subject();

	@Test
	default void testConfigurationNames() {
		assertThat(subject().configurationName("headerSearchPaths"), hasToString(headerSearchPathsConfigurationName()));
		assertThat(subject().configurationName("linkLibraries"), hasToString(linkLibrariesConfigurationName()));
		assertThat(subject().configurationName("implementation"), hasToString(implementationConfigurationName()));
		assertThat(subject().configurationName("linkElements"), hasToString(linkElementsConfigurationName()));

		assertThat(subject().append(configurationName("headerSearchPaths")), hasToString(headerSearchPathsConfigurationName()));
		assertThat(subject().append(configurationName("linkLibraries")), hasToString(linkLibrariesConfigurationName()));
		assertThat(subject().append(configurationName("implementation")), hasToString(implementationConfigurationName()));
		assertThat(subject().append(configurationName("linkElements")), hasToString(linkElementsConfigurationName()));
	}

	String headerSearchPathsConfigurationName();
	String linkLibrariesConfigurationName();
	String implementationConfigurationName();
	String linkElementsConfigurationName();

	@Test
	default void testTaskNames() {
		assertThat(subject().taskName("compile", "cpp"), hasToString(compileCppTaskName()));
		assertThat(subject().taskName("link"), hasToString(linkTaskName()));

		assertThat(subject().append(taskName("compile", "cpp")), hasToString(compileCppTaskName()));
		assertThat(subject().append(taskName("link")), hasToString(linkTaskName()));
	}

	String compileCppTaskName();
	String linkTaskName();

	@Test
	default void testComponentNames() {
		assertThat(subject().componentName("java"), hasToString(javaComponentName()));
		assertThat(subject().componentName("cpp"), hasToString(cppComponentName()));

		assertThat(subject().append(componentName("java")), hasToString(javaComponentName()));
		assertThat(subject().append(componentName("cpp")), hasToString(cppComponentName()));
	}

	String javaComponentName();
	String cppComponentName();

	@Test
	default void testAppend() {
		assertThat(subject().append("signed").configurationName("runtimeLibraries"), hasToString(signedRuntimeLibrariesConfigurationName()));
		assertThat(subject().append("signed").taskName("compile", "cpp"), hasToString(compileSignedCppTaskName()));
		assertThat(subject().append("signed").taskName("run"), hasToString(runSignedTaskName()));
	}

	String signedRuntimeLibrariesConfigurationName();
	String compileSignedCppTaskName();
	String runSignedTaskName();

	@Test
	default void testAppendMainQualifier() {
		assertThat(subject().append(ofMain("bundle")).configurationName("runtimeLibraries"), hasToString(mainBundleRuntimeLibrariesConfigurationName()));
		assertThat(subject().append(ofMain("bundle")).taskName("compile", "cpp"), hasToString(compileMainBundleCppTaskName()));
		assertThat(subject().append(ofMain("bundle")).taskName("run"), hasToString(runMainBundleTaskName()));
	}

	String mainBundleRuntimeLibrariesConfigurationName();
	String compileMainBundleCppTaskName();
	String runMainBundleTaskName();
}
