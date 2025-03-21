package dev.nokee.commons.fixtures;

import dev.gradleplugins.runnerkit.BuildResult;
import dev.gradleplugins.runnerkit.GradleRunner;
import dev.gradleplugins.runnerkit.TaskOutcome;
import dev.nokee.commons.sources.GradleBuildElement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static dev.gradleplugins.buildscript.syntax.Syntax.groovyDsl;
import static dev.gradleplugins.runnerkit.GradleExecutor.gradleTestKit;
import static dev.nokee.commons.fixtures.BuildResultExMatchers.taskPerformsFullRebuild;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

public interface SourceOptionsAwareFunctionalTester {
	@Test
	default void canCacheWithSourceOptions(TaskUnderTest taskUnderTest, @TempDir Path testDirectory, @GradleProject("project-with-source-options") GradleBuildElement project) throws Exception {
		System.out.println("Test Directory: " + testDirectory);

		GradleBuildElement firstBuild = project.writeToDirectory(testDirectory.resolve("first"));
		GradleBuildElement secondBuild = project.writeToDirectory(testDirectory.resolve("second"));
		GradleRunner runner = GradleRunner.create(gradleTestKit()).forwardOutput().withPluginClasspath()
			.withTasks(taskUnderTest.cleanIt(), taskUnderTest.toString())
			.withBuildCacheEnabled().withGradleUserHomeDirectory(testDirectory.resolve("user-home").toFile());
		BuildResult result;

		result = runner.inDirectory(firstBuild.getLocation()).build();
		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.SUCCESS));

		result = runner.inDirectory(firstBuild.getLocation()).build();
		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.FROM_CACHE));

		result = runner.inDirectory(secondBuild.getLocation()).build();
		assertThat("restore from cache", result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.FROM_CACHE));
	}

	@Test
	default void moveFileFromOneBucketToAnother(TaskUnderTest taskUnderTest, @TempDir Path testDirectory, @GradleProject("project-with-source-options") GradleBuildElement project) {
		System.out.println("Test Directory: " + testDirectory);

		GradleBuildElement build = project.writeToDirectory(testDirectory);
		GradleRunner runner = GradleRunner.create(gradleTestKit()).inDirectory(build.getLocation()).forwardOutput().withPluginClasspath();
		BuildResult result;

		build.getBuildFile().append(groovyDsl("""
			subject.configure {
				source([providers.gradleProperty('bucket1').orElse([]), file3]) { /* do something */ }
				source([providers.gradleProperty('bucket2').orElse([]), file4]) { /* do something */ }
			}
		""".stripIndent()));

		result = runner.withArgument("-Pbucket1=src/main/cpp/join.cpp").withTasks(taskUnderTest.toString()).build();
		result = runner.withArgument("-Pbucket1=src/main/cpp/join.cpp").withTasks(taskUnderTest.toString()).build();

		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));

		result = runner.withArgument("-i").withArgument("-Pbucket2=src/main/cpp/join.cpp").withTasks(taskUnderTest.toString()).build();
		assertThat(result, taskPerformsFullRebuild(taskUnderTest.toString()));
	}

	@Test
	default void testAddingNoOpSourceOptionsDoesNotRenderOutOfDate(TaskUnderTest taskUnderTest, @TempDir Path testDirectory, @GradleProject("project-with-source-options") GradleBuildElement project) throws Exception {
		System.out.println("Test Directory: " + testDirectory);

		GradleBuildElement build = project.writeToDirectory(testDirectory);
		GradleRunner runner = GradleRunner.create(gradleTestKit()).inDirectory(build.getLocation()).forwardOutput().withPluginClasspath();
		BuildResult result;

		build.getBuildFile().append(groovyDsl("""
			subject.configure {
				source(file1) { /* do something */ }
			}
		"""));

		result = runner.withTasks(taskUnderTest.toString()).build();
		result = runner.withTasks(taskUnderTest.toString()).build();

		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));

		build.getBuildFile().append(groovyDsl("""
			subject.configure {
				source(file1) { /* do nothing */ }
			}
		"""));

		result = runner.withTasks(taskUnderTest.toString()).build();
		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));
	}

	@Test
	default void additionalSourceBucketNotMatchingSourceFilesHasNoEffect(TaskUnderTest taskUnderTest, @TempDir Path testDirectory, @GradleProject("project-with-source-options") GradleBuildElement project) throws Exception {
		System.out.println("Test Directory: " + testDirectory);

		GradleBuildElement build = project.writeToDirectory(testDirectory);
		GradleRunner runner = GradleRunner.create(gradleTestKit()).inDirectory(build.getLocation()).forwardOutput().withPluginClasspath();
		BuildResult result;

		result = runner.withTasks(taskUnderTest.toString()).build();
		result = runner.withTasks(taskUnderTest.toString()).build();

		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));

		build.getBuildFile().append(groovyDsl("""
			subject.configure {
				sourceOptions.configure(file1) { /* do something */ }
			}
		"""));

		result = runner.withTasks(taskUnderTest.toString()).build();
		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));
	}

	@Test
	default void addingSourceOptionsForMissingFileKeepsUpToDate(TaskUnderTest taskUnderTest, @TempDir Path testDirectory, @GradleProject("project-with-source-options") GradleBuildElement project) throws Exception {
		System.out.println("Test Directory: " + testDirectory);

		GradleBuildElement build = project.writeToDirectory(testDirectory);
		GradleRunner runner = GradleRunner.create(gradleTestKit()).inDirectory(build.getLocation()).forwardOutput().withPluginClasspath();
		BuildResult result;

		result = runner.withTasks(taskUnderTest.toString()).build();
		result = runner.withTasks(taskUnderTest.toString()).build();

		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));

		build.getBuildFile().append(groovyDsl("""
			subject.configure {
				source(file('some-missing-file.cpp'), missingSourceBucket)
			}
		"""));

		result = runner.withTasks(taskUnderTest.toString()).build();
		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));
	}

	@Test
	default void detectsNewSourceOptionsOnExistingFile(TaskUnderTest taskUnderTest, @TempDir Path testDirectory, @GradleProject("project-with-source-options") GradleBuildElement project) throws Exception {
		System.out.println("Test Directory: " + testDirectory);

		GradleBuildElement build = project.writeToDirectory(testDirectory);
		GradleRunner runner = GradleRunner.create(gradleTestKit()).inDirectory(build.getLocation()).forwardOutput().withPluginClasspath();
		BuildResult result;

		result = runner.withTasks(taskUnderTest.toString()).build();
		result = runner.withTasks(taskUnderTest.toString()).build();

		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));

		build.getBuildFile().append(groovyDsl("""
			subject.configure {
				source(file1, moreSourceOptions)
			}
		"""));

		result = runner.withArgument("-i").withTasks(taskUnderTest.toString()).build();
		assertThat(result, taskPerformsFullRebuild(taskUnderTest.toString()));
	}

	@Test
	default void detectsChangesInSourceCompilerArgsWithGeneratedFiles(TaskUnderTest taskUnderTest, @TempDir Path testDirectory, @GradleProject("project-with-source-options-on-generated-source") GradleBuildElement project) throws Exception {
		System.out.println("Test Directory: " + testDirectory);

		GradleBuildElement build = project.writeToDirectory(testDirectory);
		GradleRunner runner = GradleRunner.create(gradleTestKit()).inDirectory(build.getLocation()).forwardOutput().withPluginClasspath();
		BuildResult result;

		result = runner.withTasks(taskUnderTest.toString()).build();

		assertThat(result.getExecutedTaskPaths(), hasItem(":generator"));

		result = runner.withTasks(taskUnderTest.toString()).build();

		assertThat(result.getExecutedTaskPaths(), hasItem(":generator"));
		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));


		build.getBuildFile().append(groovyDsl("""
			subject.configure { source(generateTask, moreSourceOptions) }
		""".stripIndent()));

		result = runner.withArgument("-i").withTasks(taskUnderTest.toString()).build();

		assertThat(result.getExecutedTaskPaths(), hasItem(":generator"));
		assertThat(result, taskPerformsFullRebuild(taskUnderTest.toString()));
	}

	@Test
	default void detectsTaskDependenciesOnSourceOptionsForStaticSourceFiles(TaskUnderTest taskUnderTest, @TempDir Path testDirectory, @GradleProject("project-with-generated-source-options-on-static-source") GradleBuildElement project) throws IOException {
		System.out.println("Test Directory: " + testDirectory);

		GradleBuildElement build = project.writeToDirectory(testDirectory);
		GradleRunner runner = GradleRunner.create(gradleTestKit()).inDirectory(build.getLocation()).forwardOutput().withPluginClasspath();
		BuildResult result;

		result = runner.withTasks(taskUnderTest.toString()).build();

		assertThat(result.getExecutedTaskPaths(), hasItems(":args", taskUnderTest.toString()));

		result = runner.withTasks(taskUnderTest.toString()).build();

		assertThat(result.getExecutedTaskPaths(), hasItems(":args", taskUnderTest.toString()));
		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));

		result = runner.withTasks("cleanArgs", taskUnderTest.toString()).build();

		assertThat(result.getExecutedTaskPaths(), hasItems(":args", taskUnderTest.toString()));
		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));
	}

	@Test
	default void detectsTaskDependenciesFromSourceOptionsConfigurationViaBuildableContract(TaskUnderTest taskUnderTest, @TempDir Path testDirectory, @GradleProject("project-with-generated-source-options-on-generated-source") GradleBuildElement project) throws IOException {
		System.out.println("Test Directory: " + testDirectory);

		GradleBuildElement build = project.writeToDirectory(testDirectory);
		GradleRunner runner = GradleRunner.create(gradleTestKit()).inDirectory(build.getLocation()).forwardOutput().withPluginClasspath();
		BuildResult result;

		// We execute the build twice
		//   because the generated source on the second build would no longer be missing when building the task graph
		result = runner.withTasks(taskUnderTest.toString()).build();

		assertThat(result.getExecutedTaskPaths(), hasItems(":generator", ":args", taskUnderTest.toString()));
		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.SUCCESS));

		result = runner.withTasks(taskUnderTest.toString()).build();

		assertThat(result.getExecutedTaskPaths(), hasItems(":generator", ":args", taskUnderTest.toString()));
		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));

		// Despite regenerating the args, the args content shouldn't have changed
		result = runner.withTasks("cleanArgs", taskUnderTest.toString()).build();

		assertThat(result.getExecutedTaskPaths(), hasItems(":generator", ":args", taskUnderTest.toString()));
		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));
	}

//	@Test
//	void failsOnAdditionalTaskDependenciesDiscoveryAfterTaskGraphBuildingButBeforeExecution_aka_DuringInputSnapshotting() {
//		build.getBuildFile().append(groovyDsl("""
//			abstract class ArgGeneratorTask extends DefaultTask {
//				@OutputFile
//				abstract RegularFileProperty getOutputFile()
//
//				@Internal
//				Provider<List<String>> getArgs() {
//					return outputFile.asFile.map { it.readLines().collect { it.trim() } }
//				}
//
//				@TaskAction
//				void doGenerate() {
//					outputFile.get().asFile.text = '''
//						-DMY_MACRO
//						-DMY_OTHER_MACRO
//					'''
//				}
//			}
//			def argTask = tasks.register('args', ArgGeneratorTask) {
//				outputFile = layout.buildDirectory.file('args.txt')
//			}
//
//
//			abstract class GeneratorTask extends DefaultTask {
//				@OutputFile
//				abstract RegularFileProperty getOutputFile()
//
//				@TaskAction
//				void doGenerate() {
//					outputFile.get().asFile.text = '''
//						int foobar() { return 42; }
//
//						#ifndef MY_MACRO
//						#  error "need macros"
//						#endif
//						#ifndef MY_OTHER_MACRO
//						#  error "need other macros"
//						#endif
//					'''
//				}
//			}
//			def generateTask = tasks.register('generator', GeneratorTask) {
//				outputFile = layout.buildDirectory.file('foo.cpp')
//			}
//
//			application {
//			  binaries.configureEach {
//			    // argTask dependency is hidden and should cause a failure
//			    compileTask.get().source(generateTask) {
//			        options.compilerArgs.addAll(argTask.flatMap { it.args })
//			    }
//			  }
//			}""".stripIndent()));
//
//		// The argTask dependency will not be detected as the generated source is missing
//		//   hence no matching source configuration
//		result = runner.withTasks("compileReleaseCpp").buildAndFail();
//
//		// TODO: Assert failure message
//	}

	@Test
	default void reorderedSourceStayUpToDate(TaskUnderTest taskUnderTest, @TempDir Path testDirectory, @GradleProject("project-with-source-options") GradleBuildElement project) throws Exception {
		System.out.println("Test Directory: " + testDirectory);

		GradleBuildElement build = project.writeToDirectory(testDirectory);
		GradleRunner runner = GradleRunner.create(gradleTestKit()).inDirectory(build.getLocation()).forwardOutput().withPluginClasspath().withArgument("-i");
		BuildResult result;

		build.getBuildFile().append(groovyDsl("""
			compileTask.source(file1) { compilerArgs.add('-DMY_MACRO1') }
			compileTask.source(file2) { compilerArgs.add('-DMY_MACRO2') }
			compileTask.source(file3) { compilerArgs.add('-DMY_MACRO3') }
			compileTask.source(file4) { compilerArgs.add('-DMY_MACRO4') }

			def sourceFiles = new ArrayList<>((file('src/main/cpp').listFiles() as List).sort())
			compileTask.source.setFrom(sourceFiles)
		"""));

		result = runner.withTasks(taskUnderTest.toString()).build();
		result = runner.withTasks(taskUnderTest.toString()).build();

		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));

		build.getBuildFile().append(groovyDsl("""
			static List rotate(List arr) {
				// i and j pointing to first and last
				// element respectively
				List result = new ArrayList(arr)

				int i = 0, j = arr.size() - 1
				while (i != j) {
					Object temp = result[i]
					result[i] = result[j]
					result[j] = temp
					i++
				}
				return result
			}

			subject.configure {
				compileTask.source.setFrom(rotate(sourceFiles))
			}
		"""));

		result = runner.withTasks(taskUnderTest.toString()).build();
		assertThat(result.task(taskUnderTest.toString()).getOutcome(), equalTo(TaskOutcome.UP_TO_DATE));
	}
}
