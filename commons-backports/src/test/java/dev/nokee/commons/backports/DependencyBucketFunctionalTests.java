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

class DependencyBucketFunctionalTests {
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
		buildFile.append(importClass(DependencyBucket.class));
//		buildFile.append(groovyDsl("def dependencyFactory = objects.newInstance(DependencyFactory)"));
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
	void createsBucketUsingGradleNestedAnnotation() {
		buildFile.append(groovyDsl(
			"interface Dependencies {",
				"  @Nested DependencyBucket getImplementation()",
				"}",
				"def subject = extensions.create('myDeps', Dependencies)",
				"myDeps {",
				"  implementation.addDependency('com.example:foo:1.0')",
				"}"
			));
		buildFile.append(verifyThat("subject.implementation.dependencies.get().collect { \"${it.group}:${it.name}:${it.version}\" } == ['com.example:foo:1.0']"));
		runner.build();
	}
}
