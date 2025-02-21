package dev.nokee.commons.gradle.attributes;

import dev.gradleplugins.buildscript.ast.expressions.Expression;
import dev.gradleplugins.buildscript.io.GradleBuildFile;
import dev.gradleplugins.buildscript.io.GradleSettingsFile;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.gradleplugins.buildscript.blocks.BuildscriptBlock.classpath;
import static dev.gradleplugins.buildscript.blocks.DependencyNotation.files;
import static dev.gradleplugins.buildscript.syntax.Syntax.groovyDsl;
import static dev.gradleplugins.buildscript.syntax.Syntax.importClass;

class AttributesFunctionalTests {
	@TempDir Path testDirectory;
	GradleRunner runner;
	GradleBuildFile buildFile;
	GradleSettingsFile settingsFile;

	@BeforeEach
	void setUp() {
		runner = GradleRunner.create().forwardOutput().withPluginClasspath().withProjectDir(testDirectory.toFile());
		buildFile = GradleBuildFile.inDirectory(testDirectory);
		settingsFile = GradleSettingsFile.inDirectory(testDirectory);

		settingsFile.buildscript(it -> it.dependencies(classpath(files(runner.getPluginClasspath()))));
		buildFile.append(importClass(Attributes.class));
		buildFile.append(groovyDsl("tasks.register('verify')"));
		buildFile.append(groovyDsl("ext.attributesOf = extensions.create('attributes', Attributes.Extension).&of"));
		buildFile.append(groovyDsl("configurations.all { /* just to resolve everything */ }"));

		runner = runner.withArguments("verify", "-s");
	}

	static Expression verifyThat(String... lines) {
		List<String> actualLines = new ArrayList<>();
		actualLines.addAll(Arrays.asList(
			"tasks.named('verify') {",
			"  doLast {"
		));
		Arrays.stream(lines).map(it -> "    assert " + it).forEach(actualLines::add);
		actualLines.addAll(Arrays.asList(
			"  }",
			"}"
		));
		return groovyDsl(actualLines.toArray(new String[0]));
	}

	@Test
	void convenience() {
		buildFile.append(groovyDsl(
			"configurations.consumable('foo') {",
			"  attributesOf(it) {",
			"    attribute Usage.USAGE_ATTRIBUTE of Usage.NATIVE_LINK",
			"    attribute CppBinary.LINKAGE_ATTRIBUTE of 'SHARED'",
			"  }",
			"}",
			"def subject = configurations.foo"
		));

		buildFile.append(verifyThat(
			"subject.attributes.getAttribute(Usage.USAGE_ATTRIBUTE).name == Usage.NATIVE_LINK",
			"subject.attributes.getAttribute(CppBinary.LINKAGE_ATTRIBUTE) == Linkage.SHARED"
		));

		runner.build();
	}

	@Test
	void convenience2() {
		buildFile.append(groovyDsl(
			"configurations.consumable('foo') {",
			"  attributesOf(it) {",
			"    attribute Usage.USAGE_ATTRIBUTE of Usage.NATIVE_LINK",
			"    attribute Attribute.of(Usage.USAGE_ATTRIBUTE.name, String) of Usage.NATIVE_RUNTIME",
			"  }",
			"}",
			"def subject = configurations.foo"
		));

		buildFile.append(verifyThat(
			"subject.attributes.getAttribute(Usage.USAGE_ATTRIBUTE).name == Usage.NATIVE_RUNTIME"
		));

		runner.build();
	}

	@Test
	void convenience3() {
		buildFile.append(groovyDsl(
			"configurations.consumable('foo') {",
			"  attributesOf(it) {",
			"    attribute CppBinary.LINKAGE_ATTRIBUTE of Linkage.SHARED",
			"    attribute Attribute.of(CppBinary.LINKAGE_ATTRIBUTE.name, String) of 'STATIC'",
			"  }",
			"}",
			"def subject = configurations.foo"
		));

		buildFile.append(verifyThat(
			"subject.attributes.getAttribute(CppBinary.LINKAGE_ATTRIBUTE).name == Linkage.STATIC"
		));

		runner.build();
	}

	@Test
	void useDslOnConfiguration() {
		buildFile.append(groovyDsl(
			"def subject = configurations.consumable('foo') {",
			"  attributesOf(it) {",
			"    attribute Usage.USAGE_ATTRIBUTE named Usage.NATIVE_LINK",
			"    attribute CppBinary.DEBUGGABLE_ATTRIBUTE of true",
			"  }",
			"}.get()"
		));

		buildFile.append(verifyThat(
			"subject.attributes.getAttribute(Usage.USAGE_ATTRIBUTE).name == Usage.NATIVE_LINK",
			"subject.attributes.getAttribute(CppBinary.DEBUGGABLE_ATTRIBUTE)"
		));

		runner.build();
	}

	@Test
	void useDslOnModuleDependency() {
		buildFile.append(groovyDsl(
			"configurations.dependencyScope('foo')",
			"dependencies {",
			"  foo('com.example:foo:1.0') {",
			"    attributesOf(it) {",
			"    	attribute CppBinary.LINKAGE_ATTRIBUTE of Linkage.STATIC",
			"    }",
			"  }",
			"}",
			"def subject = configurations.foo.dependencies.first()"
		));

		buildFile.append(verifyThat(
			"subject.attributes.getAttribute(CppBinary.LINKAGE_ATTRIBUTE) == Linkage.STATIC"
		));

		runner.build();
	}

	@Test
	void useDslOnArtifactView() {
		buildFile.append(groovyDsl(
			"configurations.resolvable('foo').get().incoming.artifactView {",
			"  attributesOf(it) {",
			"    attribute artifactType() of ArtifactTypeDefinition.DIRECTORY_TYPE",
			"  }",
			"}.files.asPath"
		));

		runner.build();
	}



	@Test
	void testttttt() {
		buildFile.append(groovyDsl(
			"def provider = tasks.register('attributeValueProvider').map { it.name }",
			"def subject = attributes.mapProperty()",
			"subject.configure { attribute Attribute.of(String) of provider }"
		));
		buildFile.append(groovyDsl(
			"tasks.named('verify') {",
			"  doLast {",
			"    subject.asMap.get()",
			"  }",
			"}"
		));

		runner.buildAndFail();
	}




	@Test
	void test() {
		buildFile.append(groovyDsl(
			"def provider = tasks.register('attributeValueProvider').map { it.name }",
			"configurations.resolvable('foo') {",
			"  attributesOf(it) {",
			"    attribute Attribute.of(String) of provider",
			"  }",
			"}"
		));
		buildFile.append(groovyDsl(
			"tasks.named('verify') {",
			"  doLast {",
			"    attributesOf(configurations.foo).asMap.get()",
			"  }",
			"}"
		));

		runner.buildAndFail();
	}

	@Test
	void testtt() {
		buildFile.append(groovyDsl(
			"def provider = tasks.register('attributeValueProvider').map { it.name }",
			"configurations.resolvable('foo') {",
			"  attributesOf(it) {",
			"    attribute(Attribute.of(String), provider)",
			"  }",
			"}"
		));
		buildFile.append(groovyDsl(
			"tasks.named('verify') {",
			"  doLast {",
			"    attributesOf(configurations.foo).asMap.get()",
			"  }",
			"}"
		));

		runner.buildAndFail();
	}

	@Test
	void testt() {
		buildFile.append(groovyDsl(
			"def provider = tasks.register('attributeValueProvider').map { it.name }",
			"configurations.resolvable('foo') {",
			"  attributesOf(it) {",
			"    attribute Attribute.of(String) of({ provider } as ProviderConvertible)",
			"  }",
			"}"
		));
		buildFile.append(groovyDsl(
			"tasks.named('verify') {",
			"  doLast {",
			"    attributesOf(configurations.foo).asMap.get()",
			"  }",
			"}"
		));

		runner.buildAndFail();
	}

	@Test
	void testttt() {
		buildFile.append(groovyDsl(
			"def provider = tasks.register('attributeValueProvider').map { it.name }",
			"configurations.resolvable('foo') {",
			"  attributesOf(it) {",
			"    attribute(Attribute.of(String), ({ provider } as ProviderConvertible))",
			"  }",
			"}"
		));
		buildFile.append(groovyDsl(
			"tasks.named('verify') {",
			"  doLast {",
			"    attributesOf(configurations.foo).asMap.get()",
			"  }",
			"}"
		));

		runner.buildAndFail();
	}

	// TODO: Filters attributes (liveness of the attributes)
	//   If you copy between configuration, subsequent add or mutation of the attributes should reflect into the other attribute
	// TODO: Configuration to Configuration
	// TODO: Attributes.MapProperty to Configuration
	// TODO: Attributes.MapProperty to Attributes.MapProperty
	// TODO: Configuration to Attributes.MapProperty
	// TODO: Attributes.MapProperty to ModuleDependency

	// TODO: Serialized attributes (component metadata rules, resolved result from remote repository)
	//   We should be able to getAttribute(...) using rich attribute
	// Make sure to include tests for int and boolean (not just Named and Enum)

	// TODO: Comparing attribute using simplified form
	//   Enum equalTo String
	//   Named equalTo String
}
