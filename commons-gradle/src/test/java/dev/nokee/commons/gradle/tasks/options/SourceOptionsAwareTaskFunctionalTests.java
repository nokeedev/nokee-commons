package dev.nokee.commons.gradle.tasks.options;

import dev.gradleplugins.buildscript.io.GradleSettingsFile;
import dev.gradleplugins.runnerkit.BuildResult;
import dev.gradleplugins.runnerkit.GradleRunner;
import dev.nokee.commons.fixtures.GradleBuild;
import dev.nokee.commons.gradle.tasks.SourceTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static dev.gradleplugins.buildscript.blocks.BuildscriptBlock.classpath;
import static dev.gradleplugins.buildscript.blocks.DependencyNotation.files;
import static dev.gradleplugins.buildscript.syntax.Syntax.groovyDsl;
import static dev.gradleplugins.buildscript.syntax.Syntax.importClass;
import static dev.gradleplugins.runnerkit.GradleExecutor.gradleTestKit;
import static dev.nokee.commons.fixtures.BuildResultExMatchers.*;
import static dev.nokee.commons.fixtures.BuildResultExMatchers.TaskOutOfDateReasonMatcher.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;

class SourceOptionsAwareTaskFunctionalTests {
	GradleBuild build;
	GradleRunner runner;
	BuildResult result;

	@BeforeEach
	void setup(@TempDir Path testDirectory) {
		runner = GradleRunner.create(gradleTestKit()).inDirectory(testDirectory).withPluginClasspath().withArgument("-i").withArgument("--configuration-cache").forwardOutput().withTasks("verify");

		build = GradleBuild.inDirectory(testDirectory);
		build.settingsFile(settingsFile -> settingsFile.buildscript(it -> it.dependencies(classpath(files(runner.getPluginClasspath())))));
		build.rootProject(buildFile -> {
			buildFile.append(importClass(SourceTask.class));
			buildFile.append(importClass(SourceFileOptions.class));
			buildFile.append(importClass(SourceOptionsAware.class));
			buildFile.append(groovyDsl("""
				interface MyOptions {
					@Input
					ListProperty<String> getArgs()
				}
				abstract class MyTask extends DefaultTask implements SourceTask, SourceOptionsAware<MyOptions> {
					@TaskAction
					void doAction() {
						for (SourceFileOptions<MyOptions> fileOptions : allSourceOptions.orNull) {
							println("processing ${fileOptions}")
						}
					}

					@OutputDirectory
					abstract DirectoryProperty getOutputDirectory();
				}

				tasks.register('verify', MyTask) {
					outputDirectory = layout.buildDirectory.dir('out')
				}
				"""));
		});
	}

	@Test
	void canUseKnowledgeableProviderInSourceOptions() throws IOException {
		Files.createFile(build.getLocation().resolve("foo.txt"));
		build.rootProject(buildFile -> {
			buildFile.append(groovyDsl("""
				tasks.named('verify', MyTask) {
					def providers = project.providers // for configuration cache
					source('foo.txt') { args.add(providers.gradleProperty('arg')) }
				}
				"""));
		});
		result = runner.withArgument("-Parg=foo").build();
		result = runner.withArgument("-Parg=foo").build();
		assertThat(result, taskSkipped(":verify"));

		result = runner.withArgument("-Parg=bar").build();
		assertThat(result, not(taskSkipped(":verify")));
		assertThat(result.task(":verify"), outOfDateBecause(valueOfInputPropertyHasChanged("allSourceOptions.[foo.txt].args")));
	}

	@Test
	void canDifferentiateSourceOptionsBetweenSameRelativePath() {
		// Same file and relative path, but different absolute file location
		build.file("dir1/a/foo.txt");
		build.file("dir2/a/foo.txt");
		build.rootProject(buildFile -> {
			buildFile.append(groovyDsl("""
				tasks.named('verify', MyTask) {
					def providers = project.providers // for configuration cache
					source(fileTree('dir1')) { args.addAll(providers.gradleProperty('arg1').map { [it] }.orElse([])) }
					source(fileTree('dir2')) { args.addAll(providers.gradleProperty('arg2').map { [it] }.orElse([])) }
				}
				"""));
		});

		// Ensure up-to-date
		result = runner.build();
		result = runner.build();
		assertThat(result, taskSkipped(":verify"));

		result = runner.withArgument("-Parg1=foo").build();
		assertThat(result, not(taskSkipped(":verify")));
		assertThat(result.task(":verify"), outOfDateBecause(valueOfInputPropertyHasChanged("allSourceOptions.[dir1/a/foo.txt].args")));

		result = runner.withArgument("-Parg1=foo").withArgument("-Parg2=bar").build();
		assertThat(result, not(taskSkipped(":verify")));
		assertThat(result.task(":verify"), outOfDateBecause(valueOfInputPropertyHasChanged("allSourceOptions.[dir2/a/foo.txt].args")));
	}

	@Test
	void canDifferentiateSourceOptionsBetweenSameFilenameWithDifferentRelativePath() {
		// Same file, but different relative path
		build.file("dir1/a/foo.txt");
		build.file("dir2/foo.txt");
		build.rootProject(buildFile -> {
			buildFile.append(groovyDsl("""
				tasks.named('verify', MyTask) {
					def providers = project.providers // for configuration cache
					source('dir1') { args.addAll(providers.gradleProperty('arg1').map { [it] }.orElse([])) }
					source('dir2') { args.addAll(providers.gradleProperty('arg2').map { [it] }.orElse([])) }
				}
				"""));
		});

		result = runner.build();
		result = runner.build();
		assertThat(result, taskSkipped(":verify"));

		result = runner.withArgument("-Parg1=foo").build();
		assertThat(result, not(taskSkipped(":verify")));
		assertThat(result.task(":verify"), outOfDateBecause(valueOfInputPropertyHasChanged("allSourceOptions.[dir1/a/foo.txt].args")));

		result = runner.withArgument("-Parg1=foo").withArgument("-Parg2=bar").build();
		assertThat(result, not(taskSkipped(":verify")));
		assertThat(result.task(":verify"), outOfDateBecause(valueOfInputPropertyHasChanged("allSourceOptions.[dir2/foo.txt].args")));
	}

	@Test
	void ignoresUnrelatedSourceOptions() {
		build.file("foo.txt");
		build.file("bar.txt");
		build.rootProject(buildFile -> {
			buildFile.append(groovyDsl("""
				tasks.named('verify', MyTask) {
					def providers = project.providers // for configuration cache
					source('foo.txt') { args.addAll(providers.gradleProperty('arg1').map { [it] }.orElse([])) }

					// Strictly add a source options, does not add source
					sourceOptions.forFile('bar.txt') { args.addAll(providers.gradleProperty('arg2').map { [it] }.orElse([])) }
				}
				"""));
		});

		result = runner.build();
		result = runner.build();
		assertThat(result, taskSkipped(":verify"));

		result = runner.withArgument("-Parg1=foo").build();
		assertThat(result, not(taskSkipped(":verify")));
		assertThat(result.task(":verify"), outOfDateBecause(valueOfInputPropertyHasChanged("allSourceOptions.[foo.txt].args")));

		result = runner.withArgument("-Parg1=foo").withArgument("-Parg2=bar").build();
		assertThat(result, taskSkipped(":verify"));
	}

	@Test
	void doesNotInterpretStringValuesAsTaskPathDuringTaskDependenciesCalculation() {
		build.file("foo.txt");
		build.file("bar.txt");
		build.rootProject(buildFile -> {
			buildFile.append(groovyDsl("""
				tasks.named('verify', MyTask) {
					def providers = project.providers
					source('foo.txt') { args.add('-DFOO') }
					source('bar.txt') { args.add(providers.provider { '-DBAR' }) }
				}
				"""));
		});

		result = runner.build();
		assertThat(result, not(taskSkipped(":verify")));

		result = runner.build();
		assertThat(result, taskSkipped(":verify"));
	}

	@Test
	void detectsWhenSourceFileMovesToAnotherOptionsBucket() {
		build.file("file-1.txt");
		build.file("file-2.txt");
		build.file("file-3.txt");

		build.rootProject(buildFile -> {
			buildFile.append(groovyDsl("""
				tasks.named('verify', MyTask) {
					def providers = project.providers
					source([providers.gradleProperty('bucket-1-file').orElse([]), 'file-2.txt']) { args.add('-DFOO') }
					source([providers.gradleProperty('bucket-2-file').orElse([]), 'file-3.txt']) { args.add('-DBAR') }
				}
				"""));
		});

		result = runner.withArgument("-Pbucket-1-file=file-1.txt").build();
		result = runner.withArgument("-Pbucket-1-file=file-1.txt").build();
		assertThat(result, taskSkipped(":verify"));

		result = runner.withArgument("-Pbucket-2-file=file-1.txt").build();
		assertThat(result, not(taskSkipped(":verify")));
		assertThat(result.task(":verify"), outOfDateBecause(valueOfInputPropertyHasChanged("allSourceOptions.[file-1.txt].args")));
	}

	@Test
	void canBuildMultipleSourceOptionsBucketEfficiently() {
		for (int i = 0; i < 400; ++i) {
			final int idx = i;
			final Path file = build.file("src/file" + i + ".txt");
			final String path = build.getLocation().relativize(file).toString();
			build.rootProject(buildFile -> {
				buildFile.append(groovyDsl("""
					tasks.named('verify', MyTask) {
						source('%s') { args.add('-DMY_MACRO=%d') }
					}
				""".stripIndent().formatted(path, idx)));
			});
		}

		// warmup
		for (int i = 0; i < 4; ++i) {
			runner.build();
		}

		// benchmark
		long average = 0;
		for (int i = 0; i < 10; ++i) {
			long start = System.nanoTime();
			runner.build();
			average += (System.nanoTime() - start);
		}

		average /= 10;
		assertThat(TimeUnit.NANOSECONDS.toMillis(average), lessThan(800L));
	}

	@Nested
	class InputFileTests {
		GradleBuild build;
		GradleRunner runner;

		@BeforeEach
		void setup(@TempDir Path testDirectory) {
			build = GradleBuild.inDirectory(testDirectory);
			runner = runnerFor(build).withArgument("-i");

			build.settingsFile(withPluginClasspathFrom(runner));
			build.rootProject(buildFile -> {
				buildFile.append(importClass(SourceTask.class));
				buildFile.append(importClass(SourceFileOptions.class));
				buildFile.append(importClass(SourceOptionsAware.class));
				buildFile.append(groovyDsl("""
					abstract class MyTask extends DefaultTask implements SourceTask, SourceOptionsAware<MyOptions> {
						@TaskAction
						void doAction() {
							for (SourceFileOptions<MyOptions> fileOptions : allSourceOptions.orNull) {
								println fileOptions
							}
						}

						@OutputDirectory
						abstract DirectoryProperty getOutputDirectory();
					}

					tasks.register('verify', MyTask) {
						outputDirectory = layout.buildDirectory.dir('out')
					}

					abstract class GeneratorTask extends DefaultTask {
						@OutputFile
						abstract RegularFileProperty getOutputFile()

						@Input
						abstract ListProperty<String> getContentLines();

						@TaskAction
						void doGenerate() {
							outputFile.get().asFile.text = String.join(System.lineSeparator(), getContentLines().get())
						}
					}

					def generatorTask = tasks.register('generator', GeneratorTask) {
						outputFile = layout.buildDirectory.file('foo.txt')
						contentLines.addAll(providers.gradleProperty('additional-line').map { [it] }.orElse([]))
					}
					"""));
				// Purposefully didn't declare MyOptions interface
			});
		}

		@Test
		void fileCollectionOptions() {
			build.file("foo.txt");
			build.rootProject(buildFile -> {
				buildFile.append(groovyDsl("""
					interface MyOptions {
						@InputFiles
						ConfigurableFileCollection getFiles();
					}
					tasks.named('verify', MyTask) {
						def fileProvider = generatorTask.flatMap { it.outputFile }
						source('foo.txt') { files.from(fileProvider) }
					}
					"""));
			});

			result = runner.build();
			assertThat(result, not(taskSkipped(":verify")));

			result = runner.build();
			assertThat(result, taskSkipped(":verify"));

			result = runner.withArgument("-Padditional-line=foo").build();
			assertThat(result, not(taskSkipped(":verify")));
			assertThat(result.task(":verify"), outOfDateBecause(inputProperty("allSourceOptions.[foo.txt].files").fileChanged(build.getLocation().resolve("build/foo.txt"))));
		}

		@Test
		void regularFileOptions() {
			build.file("foo.txt");
			build.rootProject(buildFile -> {
				buildFile.append(groovyDsl("""
					interface MyOptions {
						@InputFile
						RegularFileProperty getFile();
					}

					tasks.named('verify', MyTask) {
						def fileProvider = generatorTask.flatMap { it.outputFile }
						source('foo.txt') { file = fileProvider }
					}
					"""));
			});

			result = runner.build();
			assertThat(result, not(taskSkipped(":verify")));

			result = runner.build();
			assertThat(result, taskSkipped(":verify"));

			result = runner.withArgument("-Padditional-line=foo").build();
			assertThat(result, not(taskSkipped(":verify")));
			assertThat(result.task(":verify"), outOfDateBecause(inputProperty("allSourceOptions.[foo.txt].file").fileChanged(build.getLocation().resolve("build/foo.txt"))));
		}
	}

	@Test
	void canSupportOptionalNoValueProviderAsInput(@TempDir Path testDirectory) {
		GradleBuild build = GradleBuild.inDirectory(testDirectory);
		GradleRunner runner = runnerFor(build).withArgument("-i");

		build.settingsFile(withPluginClasspathFrom(runner));
		build.rootProject(buildFile -> {
			buildFile.append(importClass(SourceTask.class));
			buildFile.append(importClass(SourceFileOptions.class));
			buildFile.append(importClass(SourceOptionsAware.class));
			buildFile.append(groovyDsl("""
				interface MyOptions {
					@InputFile
					@Optional
					RegularFileProperty getFile();

					@Input
					@Optional
					Property<String> getValue();
				}
				abstract class MyTask extends DefaultTask implements SourceTask, SourceOptionsAware<MyOptions> {
					@TaskAction
					void doAction() {
						for (SourceFileOptions<MyOptions> fileOptions : allSourceOptions.orNull) {
							println fileOptions
							println fileOptions.sourceFile
							println fileOptions.options.file.orNull
							println fileOptions.options.value.orNull
						}
					}

					@OutputDirectory
					abstract DirectoryProperty getOutputDirectory();
				}

				tasks.register('verify', MyTask) {
					outputDirectory = layout.buildDirectory.dir('out')
				}

				tasks.named('verify', MyTask) {
					source('foo.txt') { /* do something */ }
				}
				"""));
		});

		build.file("foo.txt");

		result = runner.build();
		assertThat(result, not(taskSkipped(":verify")));
	}

	private GradleRunner runnerFor(GradleBuild build) {
		return GradleRunner.create(gradleTestKit()).inDirectory(build.getLocation()).withPluginClasspath().withArgument("--configuration-cache").forwardOutput().withTasks("verify");
	}

	private Consumer<GradleSettingsFile> withPluginClasspathFrom(GradleRunner runner) {
		return settingsFile -> settingsFile.buildscript(it -> it.dependencies(classpath(files(runner.getPluginClasspath()))));
	}


	@Test
	void canTrackTaskDependenciesOnGeneratedSourceFileOptions() {
		build.file("foo.txt");
		build.file("bar.txt");
		build.rootProject(buildFile -> {
			buildFile.append(groovyDsl("""
				abstract class ArgGeneratorTask extends DefaultTask {
					@OutputFile
					abstract RegularFileProperty getOutputFile()

					@Internal
					Provider<List<String>> getArgs() {
						// See issue https://github.com/gradle/gradle/issues/35115
						return outputFile.zip(project.providers.fileContents(outputFile).asText.map { it.lines().collect { it.trim() } }) { a, b -> b }
					}

					@Input
					abstract ListProperty<String> getAllArgs()

					@TaskAction
					void doGenerate() {
						outputFile.get().asFile.text = String.join(System.lineSeparator(), allArgs.get())
					}
				}
				def argTask = tasks.register('args', ArgGeneratorTask) {
					outputFile = layout.buildDirectory.file('args.txt')
					allArgs.addAll(providers.gradleProperty('arg1').map { [it] }.orElse([]))
					allArgs.addAll(providers.gradleProperty('arg2').map { [it] }.orElse([]))
				}


				abstract class GeneratorTask extends DefaultTask {
					@OutputFile
					abstract RegularFileProperty getOutputFile()

					@TaskAction
					void doGenerate() {
						outputFile.get().asFile.text = '''
							# some generated file
						'''
					}
				}
				def generatorTask = tasks.register('generator', GeneratorTask) {
					outputFile = layout.buildDirectory.file('foo.txt')
				}

				tasks.named('verify', MyTask) {
					// Configuration Cache can't serialize the capture of the argTask argument
					//   In the following case, Gradle capture the fact that it access argTask which Gradle doesn't
					//   want to serialize because of the provider reference to a Task instance.
					//     source(generatorTask) { args.addAll(argTask.flatMap { it.args } }
					//   Instead, we force Gradle to capture the resulting provider
					//   which is a provider referencing a list of String.
					def provider = argTask.flatMap { it.args }
					source(generatorTask) { args.addAll(provider) }
				}
				"""));
		});

		result = runner.build();
		result = runner.build();
		assertThat(result, taskSkipped(":verify"));

		result = runner.withArgument("-Parg1=foo").build();
		assertThat(result, not(taskSkipped(":verify")));
		assertThat(result.task(":verify"), outOfDateBecause(valueOfInputPropertyHasChanged("allSourceOptions.[build/foo.txt].args")));

		result = runner.withArgument("-Parg1=foo").withArgument("-Parg2=bar").build();
		assertThat(result, not(taskSkipped(":verify")));
		assertThat(result.task(":verify"), outOfDateBecause(valueOfInputPropertyHasChanged("allSourceOptions.[build/foo.txt].args")));
	}
}
