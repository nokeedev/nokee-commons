package dev.nokee.commons.backports;

import dev.gradleplugins.buildscript.ast.expressions.Expression;
import dev.gradleplugins.buildscript.io.GradleBuildFile;
import dev.gradleplugins.buildscript.io.GradleSettingsFile;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.gradleplugins.buildscript.blocks.BuildscriptBlock.classpath;
import static dev.gradleplugins.buildscript.blocks.DependencyNotation.files;
import static dev.gradleplugins.buildscript.syntax.Syntax.groovyDsl;
import static dev.gradleplugins.buildscript.syntax.Syntax.importClass;

class DependencyFactoryFunctionalTests {
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
		buildFile.append(importClass(DependencyFactory.class));
		buildFile.append(groovyDsl("def dependencyFactory = objects.newInstance(DependencyFactory)"));
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

	@Test
	void createsFileCollectionDependency() {
		buildFile.append(groovyDsl("def subject = dependencyFactory.create(files('foo.a'))"));
		buildFile.append(verifyThat("subject instanceof FileCollectionDependency"));
		runner.build();
	}

	@Nested
	class CreatesExternalModuleDependency {
		@Test
		void usingString() {
			buildFile.append(groovyDsl("def subject = dependencyFactory.create('com.example:foo:1.0')"));
			buildFile.append(verifyThat("subject instanceof ExternalModuleDependency"));
			runner.build();
		}

		@Test
		void usingMap_group_name_version() {
			buildFile.append(groovyDsl("def subject = dependencyFactory.create([group: 'com.example', name: 'foo', version: '1.0'])"));
			buildFile.append(verifyThat(
				"subject instanceof ExternalModuleDependency",
				"subject.group == 'com.example'",
				"subject.name == 'foo'",
				"subject.version == '1.0'"
			));
			runner.build();
		}

		@Test
		void usingMap_name() {
			buildFile.append(groovyDsl("def subject = dependencyFactory.create([name: 'foo'])"));
			buildFile.append(verifyThat(
				"subject instanceof ExternalModuleDependency",
				"subject.group == null",
				"subject.name == 'foo'",
				"subject.version == null"
			));
			runner.build();
		}

		@Test
		void usingMap_group_name() {
			buildFile.append(groovyDsl("def subject = dependencyFactory.create([group: 'com.example', name: 'foo'])"));
			buildFile.append(verifyThat(
				"subject instanceof ExternalModuleDependency",
				"subject.group == 'com.example'",
				"subject.name == 'foo'",
				"subject.version == null"
			));
			runner.build();
		}

		@Test
		void usingStrings_null_name_null() {
			buildFile.append(groovyDsl("def subject = dependencyFactory.create(null, 'foo', null)"));
			buildFile.append(verifyThat(
				"subject instanceof ExternalModuleDependency",
				"subject.group == null",
				"subject.name == 'foo'",
				"subject.version == null"
			));
			runner.build();
		}

		@Test
		void usingStrings_group_name_null() {
			buildFile.append(groovyDsl("def subject = dependencyFactory.create('com.example', 'foo', null)"));
			buildFile.append(verifyThat(
				"subject instanceof ExternalModuleDependency",
				"subject.group == 'com.example'",
				"subject.name == 'foo'",
				"subject.version == null"
			));
			runner.build();
		}

		@Test
		void usingStrings_group_name_version_null_null() {
			buildFile.append(groovyDsl("def subject = dependencyFactory.create('com.example', 'foo', '1.2', null, null)"));
			buildFile.append(verifyThat(
				"subject instanceof ExternalModuleDependency",
				"subject.group == 'com.example'",
				"subject.name == 'foo'",
				"subject.version == '1.2'"
			));
			runner.build();
		}

		@Test
		void usingStrings_group_name_version_classifier_null() {
			buildFile.append(groovyDsl("def subject = dependencyFactory.create('com.example', 'foo', '1.2', 'foo-bar', null)"));
			buildFile.append(verifyThat(
				"subject instanceof ExternalModuleDependency",
				"subject.group == 'com.example'",
				"subject.name == 'foo'",
				"subject.version == '1.2'"
			));
			runner.build();
		}

		@Test
		void usingStrings_null_name_null_classifier_null() {
			buildFile.append(groovyDsl("def subject = dependencyFactory.create(null, 'foo', null, 'foo-bar', null)"));
			buildFile.append(verifyThat(
				"subject instanceof ExternalModuleDependency",
				"subject.group == null",
				"subject.name == 'foo'",
				"subject.version == null"
			));
			runner.build();
		}
	}

	@Test
	void createsProjectDependency() {
		buildFile.append(groovyDsl("def subject = dependencyFactory.create(project)"));
		buildFile.append(verifyThat(
			"subject instanceof ProjectDependency"
		));
		runner.build();
	}
}
