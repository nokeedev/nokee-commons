package dev.nokee.commons.fixtures;

import dev.gradleplugins.runnerkit.BuildResult;
import dev.gradleplugins.runnerkit.TaskOutcome;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;

public class BuildResultExMatchers {
	public static Matcher<BuildResult> taskPerformsFullRebuild(String taskPath) {
		return allOf(new FeatureMatcher<BuildResult, TaskOutcome>(equalTo(TaskOutcome.SUCCESS), "", "") {
			@Override
			protected TaskOutcome featureValueOf(BuildResult actual) {
				return actual.task(taskPath).getOutcome();
			}
		},
		// make sure the output was info
		new FeatureMatcher<BuildResult, List<String>>(hasItem(matchesRegex("^" + taskPath + " (.+) started.$")), "", "") {
			@Override
			protected List<String> featureValueOf(BuildResult actual) {
				return actual.getOutput().lines().filter(it -> it.startsWith(taskPath)).toList();
			}
		},
		new FeatureMatcher<BuildResult, String>(containsString("The input changes require a full rebuild for incremental task '" + taskPath + "'."), "", "") {
			@Override
			protected String featureValueOf(BuildResult actual) {
				return actual.task(taskPath).getOutput();
			}
		});
	}

	public static Matcher<BuildResult> taskPerformsIncrementalBuild(String taskPath) {
		return allOf(new FeatureMatcher<BuildResult, TaskOutcome>(equalTo(TaskOutcome.SUCCESS), "", "") {
			@Override
			protected TaskOutcome featureValueOf(BuildResult actual) {
				return actual.task(taskPath).getOutcome();
			}
		}, new FeatureMatcher<BuildResult, String>(not(containsString("The input changes require a full rebuild for incremental task '" + taskPath + "'.")), "", "") {
			@Override
			protected String featureValueOf(BuildResult actual) {
				return actual.task(taskPath).getOutput();
			}
		});
	}

	public static Matcher<BuildResult> taskExecuted(String taskPath) {
		return new FeatureMatcher<BuildResult, List<String>>(hasItem(taskPath), "", "") {
			@Override
			protected List<String> featureValueOf(BuildResult actual) {
				return actual.getExecutedTaskPaths();
			}
		};
	}

	public static Matcher<BuildResult> taskSkipped(String taskPath) {
		return new FeatureMatcher<BuildResult, List<String>>(hasItem(taskPath), "", "") {
			@Override
			protected List<String> featureValueOf(BuildResult actual) {
				return actual.getSkippedTaskPaths();
			}
		};
	}
}
