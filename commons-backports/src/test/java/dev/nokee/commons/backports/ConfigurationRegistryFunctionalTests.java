package dev.nokee.commons.backports;

import dev.gradleplugins.buildscript.ast.expressions.Expression;
import dev.gradleplugins.buildscript.io.GradleBuildFile;
import dev.gradleplugins.buildscript.io.GradleSettingsFile;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.gradleplugins.buildscript.blocks.BuildscriptBlock.classpath;
import static dev.gradleplugins.buildscript.blocks.DependencyNotation.files;
import static dev.gradleplugins.buildscript.syntax.Syntax.groovyDsl;
import static dev.gradleplugins.buildscript.syntax.Syntax.importClass;

class ConfigurationRegistryFunctionalTests {
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
		buildFile.append(importClass(ConfigurationRegistry.class));
		buildFile.append(groovyDsl("tasks.register('verify')"));

		runner = runner.withArguments("verify");
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

	static Expression subjectDsl(String methodName) {
		return groovyDsl("def subject = objects.newInstance(ConfigurationRegistry)." + methodName + "('foo').get()");
	}

	@Nested
	class OnGradle8_4AndLater extends Tester {
		@BeforeEach
		void setUp() {
			runner.withGradleVersion("8.4");
		}

		@Test
		void createsConsumableConfigurationType() {
			buildFile.append(subjectDsl("consumable"));
			buildFile.append(verifyThat("subject instanceof ConsumableConfiguration"));
			runner.build();
		}

		@Test
		void createsDependencyScopeConfigurationType() {
			buildFile.append(subjectDsl("dependencyScope"));
			buildFile.append(verifyThat("subject instanceof DependencyScopeConfiguration"));
			runner.build();
		}

		@Test
		void createsResolvableConfigurationType() {
			buildFile.append(subjectDsl("resolvable"));
			buildFile.append(verifyThat("subject instanceof ResolvableConfiguration"));
			runner.build();
		}
	}


	@Nested
	class OnGradleOlderThen8_4 extends Tester {
		@BeforeEach
		void setUp() {
			runner.withGradleVersion("8.3");
		}

		@Test
		void creates_Consumable_ConfigurationType() {
			buildFile.append(subjectDsl("consumable"));
			buildFile.append(verifyThat("subject instanceof Configuration"));
			runner.build();
		}

		@Test
		void creates_DependencyScope_ConfigurationType() {
			buildFile.append(subjectDsl("dependencyScope"));
			buildFile.append(verifyThat("subject instanceof Configuration"));
			runner.build();
		}

		@Test
		void creates_Resolvable_ConfigurationType() {
			buildFile.append(subjectDsl("resolvable"));
			buildFile.append(verifyThat("subject instanceof Configuration"));
			runner.build();
		}
	}

	@ParameterizedTest
	@ValueSource(strings = {"7.5"})
	void createsResolveEagerlyConsumableConfigurationOnProblematicGradle(String version) {
		buildFile.append(groovyDsl(
			"def realizedConfigurations = []",
			"configurations.configureEach {",
			"  realizedConfigurations << it.name",
			"}",
			"def registry = objects.newInstance(ConfigurationRegistry)",
			"registry.consumable('consumableConfig')",
			"registry.dependencyScope('dependencyScopeConfig')",
			"registry.resolvable('resolvableConfig')"
		));
		buildFile.append(verifyThat("realizedConfigurations.contains('consumableConfig')"));
		runner.withGradleVersion(version).build();
	}

	abstract class Tester {
		@Test
		void configuresAsConsumable() {
			buildFile.append(subjectDsl("consumable"));
			buildFile.append(verifyThat(
				"subject.canBeConsumed",
				"!subject.canBeResolved"
			));
			runner.build();
		}


		@Test
		void configuresAsDeclarable() {
			buildFile.append(subjectDsl("dependencyScope"));
			buildFile.append(verifyThat(
				"!subject.canBeConsumed",
				"!subject.canBeResolved"
			));
			runner.build();
		}

		@Test
		void configuresAsResolvable() {
			buildFile.append(subjectDsl("resolvable"));
			buildFile.append(verifyThat(
				"!subject.canBeConsumed",
				"subject.canBeResolved"
			));
			runner.build();
		}
	}
}
