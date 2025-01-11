package dev.nokee.commons.names;

import dev.gradleplugins.buildscript.GradleDsl;
import dev.gradleplugins.buildscript.ast.expressions.Expression;
import dev.gradleplugins.buildscript.io.GradleBuildFile;
import dev.gradleplugins.buildscript.io.GradleSettingsFile;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static dev.gradleplugins.buildscript.blocks.BuildscriptBlock.classpath;
import static dev.gradleplugins.buildscript.blocks.DependencyNotation.files;
import static dev.gradleplugins.buildscript.syntax.Syntax.*;

class JvmNamesFunctionalTests {
	@TempDir Path testDirectory;
	GradleBuildFile buildFile;
	GradleSettingsFile settingsFile;
	GradleRunner runner;

	@BeforeEach
	void setUp() {
		runner = GradleRunner.create().forwardOutput().withPluginClasspath().withProjectDir(testDirectory.toFile());

		buildFile = GradleBuildFile.inDirectory(testDirectory);
		settingsFile = GradleSettingsFile.inDirectory(testDirectory);

		buildFile.plugins(it -> it.id("java-base"));
		buildFile.append(groovyDsl("tasks.register('verify')"));
		buildFile.append(importClass(JvmNames.class));
		buildFile.append(staticImportClass(JvmNames.class));
		buildFile.append(staticImportClass(NameBuilder.class));
		settingsFile.buildscript(it -> it.dependencies(classpath(files(runner.getPluginClasspath()))));

		runner.withArguments("verify");
	}

	static Expression verifyThat(String... assertions) {
		return groovyDsl(
			"tasks.named('verify') {",
			"  doLast {",
			Arrays.stream(assertions).map(it -> "    assert " + it).collect(Collectors.joining("\n")),
			"  }",
			"}"
		);
	}

	interface GradleProject {
		void buildFile(Consumer<? super GradleBuildFile> action);
		BuildResult build();
	}

	interface JvmNamesTester {
		GradleProject project();

		@Test
		default void testAnnotationProcessorConfigurationNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).annotationProcessorConfigurationName() == " + annotationProcessorConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression annotationProcessorConfigurationName();

		@Test
		default void testApiConfigurationNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).apiConfigurationName() == " + apiConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression apiConfigurationName();

		@Test
		default void testApiElementsConfigurationNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).apiElementsConfigurationName() == " + apiElementsConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression apiElementsConfigurationName();

		@Test
		default void testClassesTaskNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).classesTaskName() == " + classesTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression classesTaskName();

		@Test
		default void testCompileClasspathConfigurationNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).compileClasspathConfigurationName() == " + compileClasspathConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression compileClasspathConfigurationName();

		@Test
		default void testCompileGroovyTaskNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).compileGroovyTaskName() == " + compileGroovyTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression compileGroovyTaskName();

		@Test
		default void testCompileJavaTaskNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).compileJavaTaskName() == " + compileJavaTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression compileJavaTaskName();

		@Test
		default void testCompileKotlinTaskNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).compileKotlinTaskName() == " + compileKotlinTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression compileKotlinTaskName();

		@Test
		default void testCompileOnlyApiConfigurationNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).compileOnlyApiConfigurationName() == " + compileOnlyApiConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression compileOnlyApiConfigurationName();

		@Test
		default void testCompileOnlyConfigurationNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).compileOnlyConfigurationName() == " + compileOnlyConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression compileOnlyConfigurationName();

		@Test
		default void testCompileTaskNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).compileTaskName('swig') == " + compileSwigTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression compileSwigTaskName();

		@Test
		default void testGroovydocElementsConfigurationNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).groovydocElementsConfigurationName() == " + groovydocElementsConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression groovydocElementsConfigurationName();

		@Test
		default void testGroovydocJarTaskNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).groovydocJarTaskName() == " + groovydocJarTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression groovydocJarTaskName();

		@Test
		default void testGroovydocTaskNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).groovydocTaskName() == " + groovydocTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression groovydocTaskName();

		@Test
		default void testImplementationConfigurationNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).implementationConfigurationName() == " + implementationConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression implementationConfigurationName();

		@Test
		default void testJarTaskNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).jarTaskName() == " + jarTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression jarTaskName();

		@Test
		default void testJavadocElementsConfigurationNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).javadocElementsConfigurationName() == " + javadocElementsConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression javadocElementsConfigurationName();

		@Test
		default void testJavadocJarTaskNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).javadocJarTaskName() == " + javadocJarTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression javadocJarTaskName();

		@Test
		default void testJavadocTaskNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).javadocTaskName() == " + javadocTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression javadocTaskName();

		@Test
		default void testPluginUnderTestMetadataTaskNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).pluginUnderTestMetadataTaskName() == " + pluginUnderTestMetadataTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression pluginUnderTestMetadataTaskName();

		@Test
		default void testProcessResourcesTaskNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).processResourcesTaskName() == " + processResourcesTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression processResourcesTaskName();

		@Test
		default void testRuntimeClasspathConfigurationNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).runtimeClasspathConfigurationName() == " + runtimeClasspathConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression runtimeClasspathConfigurationName();

		@Test
		default void testRuntimeElementsConfigurationNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).runtimeElementsConfigurationName() == " + runtimeElementsConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression runtimeElementsConfigurationName();

		@Test
		default void testRuntimeOnlyConfigurationNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).runtimeOnlyConfigurationName() == " + runtimeOnlyConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression runtimeOnlyConfigurationName();

		@Test
		default void testSourcesElementsConfigurationNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).sourcesElementsConfigurationName() == " + sourcesElementsConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression sourcesElementsConfigurationName();

		@Test
		default void testSourcesJarTaskNames() {
			project().buildFile(it -> it.append(verifyThat("JvmNames.of(subject).sourcesJarTaskName() == " + sourcesJarTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression sourcesJarTaskName();
	}

	interface SourceSetNamesTester {
		GradleProject project();

		@Test
		default void testCompileOnlyApiConfigurationName() {
			project().buildFile(it -> it.append(verifyThat("compileOnlyApiConfigurationName(subject) == " + compileOnlyApiConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression compileOnlyApiConfigurationName();

		@Test
		default void testGroovydocJarTaskName() {
			project().buildFile(it -> it.append(verifyThat("groovydocJarTaskName(subject) == " + groovydocJarTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression groovydocJarTaskName();

		@Test
		default void testGroovydocTaskName() {
			project().buildFile(it -> it.append(verifyThat("groovydocTaskName(subject) == " + groovydocTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression groovydocTaskName();

		@Test
		default void testGroovydocElementsConfigurationName() {
			project().buildFile(it -> it.append(verifyThat("groovydocElementsConfigurationName(subject) == " + groovydocElementsConfigurationName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression groovydocElementsConfigurationName();

		@Test
		default void testPluginUnderTestMetadataTaskName() {
			project().buildFile(it -> it.append(verifyThat("pluginUnderTestMetadataTaskName(subject) == " + pluginUnderTestMetadataTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression pluginUnderTestMetadataTaskName();

		@Test
		default void testCompileKotlinTaskName() {
			project().buildFile(it -> it.append(verifyThat("compileKotlinTaskName(subject) == " + compileKotlinTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression compileKotlinTaskName();

		@Test
		default void testCompileGroovyTaskName() {
			project().buildFile(it -> it.append(verifyThat("compileGroovyTaskName(subject) == " + compileGroovyTaskName().toString(GradleDsl.GROOVY))));
			project().build();
		}

		Expression compileGroovyTaskName();
	}

	@Nested
	class MainSourceSetTests implements SourceSetNamesTester, JvmNamesTester {
		@BeforeEach
		void setup() {
			buildFile.append(groovyDsl("def subject = sourceSets.create('main')"));
		}

		@Override
		public GradleProject project() {
			return new GradleProject() {
				@Override
				public void buildFile(Consumer<? super GradleBuildFile> action) {
					action.accept(buildFile);
				}

				@Override
				public BuildResult build() {
					return runner.build();
				}
			};
		}

		@Override
		public Expression compileOnlyApiConfigurationName() {
			return string("compileOnlyApi");
		}

		@Override
		public Expression compileOnlyConfigurationName() {
			return string("compileOnly");
		}

		@Override
		public Expression compileSwigTaskName() {
			return string("compileSwig");
		}

		@Override
		public Expression groovydocJarTaskName() {
			return string("groovydocJar");
		}

		@Override
		public Expression groovydocTaskName() {
			return string("groovydoc");
		}

		@Override
		public Expression implementationConfigurationName() {
			return string("implementation");
		}

		@Override
		public Expression jarTaskName() {
			return string("jar");
		}

		@Override
		public Expression javadocElementsConfigurationName() {
			return string("javadocElements");
		}

		@Override
		public Expression javadocJarTaskName() {
			return string("javadocJar");
		}

		@Override
		public Expression javadocTaskName() {
			return string("javadoc");
		}

		@Override
		public Expression groovydocElementsConfigurationName() {
			return string("groovydocElements");
		}

		@Override
		public Expression pluginUnderTestMetadataTaskName() {
			return string("pluginUnderTestMetadata");
		}

		@Override
		public Expression processResourcesTaskName() {
			return string("processResources");
		}

		@Override
		public Expression runtimeClasspathConfigurationName() {
			return string("runtimeClasspath");
		}

		@Override
		public Expression runtimeElementsConfigurationName() {
			return string("runtimeElements");
		}

		@Override
		public Expression runtimeOnlyConfigurationName() {
			return string("runtimeOnly");
		}

		@Override
		public Expression sourcesElementsConfigurationName() {
			return string("sourcesElements");
		}

		@Override
		public Expression sourcesJarTaskName() {
			return string("sourcesJar");
		}

		@Override
		public Expression compileKotlinTaskName() {
			return string("compileKotlin");
		}

		@Override
		public Expression annotationProcessorConfigurationName() {
			return string("annotationProcessor");
		}

		@Override
		public Expression apiConfigurationName() {
			return string("api");
		}

		@Override
		public Expression apiElementsConfigurationName() {
			return string("apiElements");
		}

		@Override
		public Expression classesTaskName() {
			return string("classes");
		}

		@Override
		public Expression compileClasspathConfigurationName() {
			return string("compileClasspath");
		}

		@Override
		public Expression compileGroovyTaskName() {
			return string("compileGroovy");
		}

		@Override
		public Expression compileJavaTaskName() {
			return string("compileJava");
		}
	}

	@Nested
	class OtherSourceSetTests implements SourceSetNamesTester, JvmNamesTester {
		@BeforeEach
		void setup() {
			buildFile.append(groovyDsl("def subject = sourceSets.create('other')"));
		}

		@Override
		public GradleProject project() {
			return new GradleProject() {
				@Override
				public void buildFile(Consumer<? super GradleBuildFile> action) {
					action.accept(buildFile);
				}

				@Override
				public BuildResult build() {
					return runner.build();
				}
			};
		}

		@Override
		public Expression compileOnlyApiConfigurationName() {
			return string("otherCompileOnlyApi");
		}

		@Override
		public Expression compileOnlyConfigurationName() {
			return string("otherCompileOnly");
		}

		@Override
		public Expression compileSwigTaskName() {
			return string("compileOtherSwig");
		}

		@Override
		public Expression groovydocJarTaskName() {
			return string("otherGroovydocJar");
		}

		@Override
		public Expression groovydocTaskName() {
			return string("otherGroovydoc");
		}

		@Override
		public Expression implementationConfigurationName() {
			return string("otherImplementation");
		}

		@Override
		public Expression jarTaskName() {
			return string("otherJar");
		}

		@Override
		public Expression javadocElementsConfigurationName() {
			return string("otherJavadocElements");
		}

		@Override
		public Expression javadocJarTaskName() {
			return string("otherJavadocJar");
		}

		@Override
		public Expression javadocTaskName() {
			return string("otherJavadoc");
		}

		@Override
		public Expression groovydocElementsConfigurationName() {
			return string("otherGroovydocElements");
		}

		@Override
		public Expression pluginUnderTestMetadataTaskName() {
			return string("otherPluginUnderTestMetadata");
		}

		@Override
		public Expression processResourcesTaskName() {
			return string("processOtherResources");
		}

		@Override
		public Expression runtimeClasspathConfigurationName() {
			return string("otherRuntimeClasspath");
		}

		@Override
		public Expression runtimeElementsConfigurationName() {
			return string("otherRuntimeElements");
		}

		@Override
		public Expression runtimeOnlyConfigurationName() {
			return string("otherRuntimeOnly");
		}

		@Override
		public Expression sourcesElementsConfigurationName() {
			return string("otherSourcesElements");
		}

		@Override
		public Expression sourcesJarTaskName() {
			return string("otherSourcesJar");
		}

		@Override
		public Expression compileKotlinTaskName() {
			return string("compileOtherKotlin");
		}

		@Override
		public Expression annotationProcessorConfigurationName() {
			return string("otherAnnotationProcessor");
		}

		@Override
		public Expression apiConfigurationName() {
			return string("otherApi");
		}

		@Override
		public Expression apiElementsConfigurationName() {
			return string("otherApiElements");
		}

		@Override
		public Expression classesTaskName() {
			return string("otherClasses");
		}

		@Override
		public Expression compileClasspathConfigurationName() {
			return string("otherCompileClasspath");
		}

		@Override
		public Expression compileGroovyTaskName() {
			return string("compileOtherGroovy");
		}

		@Override
		public Expression compileJavaTaskName() {
			return string("compileOtherJava");
		}
	}
}
