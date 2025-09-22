package dev.nokee.commons.fixtures;

import dev.gradleplugins.runnerkit.BuildResult;
import dev.gradleplugins.runnerkit.BuildTask;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class BuildResultExMatchers {
	public static Matcher<BuildResult> taskPerformsFullRebuild(String taskPath) {
		return allOf(
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
		return new FeatureMatcher<BuildResult, String>(not(containsString("The input changes require a full rebuild for incremental task '" + taskPath + "'.")), "", "") {
			@Override
			protected String featureValueOf(BuildResult actual) {
				return actual.task(taskPath).getOutput();
			}
		};
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

	public static final class TaskOutOfDateReasonMatcher {
		public static Matcher<Reason> noHistoryIsAvailable() {
			return hasToString("No history is available.");
		}

		@SafeVarargs
		public static <T> Matcher<T/* extends CharSequence | BuildTask*/> outOfDateBecause(Matcher<? super Reason>... matchers) {
			return new FeatureMatcher<T, Iterable<Reason>>(hasItems(matchers), "", "") {
				@Override
				protected Iterable<Reason> featureValueOf(T actual) {
					String taskOutput = null;
					if (actual instanceof CharSequence) {
						// TODO: Should assert single task reference
						taskOutput = actual.toString(); // assuming it's a task output
					} else if (actual instanceof BuildTask) {
						taskOutput = ((BuildTask) actual).getOutput();
					} else {
						throw new UnsupportedOperationException("not a task output or model");
					}

					boolean found = false;
					List<Reason> result = new ArrayList<>();
					for (String line : taskOutput.lines().toList()) {
						if (!found && line.startsWith("Task ':") && line.endsWith("' is not up-to-date because:")) {
							found = true;
						} else if (!found && line.startsWith("Skipping task ':") && line.endsWith("' as it is up-to-date.")) {
							throw new RuntimeException("expecting out-of-date, but was up-to-date");
						} else if (found && line.startsWith("  ")) {
							String s = line.trim();
							result.add(new Reason() {
								@Override
								public String toString() {
									return s;
								}
							});
						} else if (found) {
							return result;
						}
					}
					throw new RuntimeException("no out-of-date reason");
				}
			};
		}

		public static Matcher<Reason> valueOfInputPropertyHasChanged(String propertyName) {
			return inputProperty(propertyName).valueChanged();
		}

		public static Reason.InputPropertyReason inputProperty(String propertyName) {
			return new Reason.InputPropertyReason() {
				@Override
				public Matcher<Reason> fileChanged(Path file) {
					try {
						return hasToString("Input property '" + propertyName + "' file " + file.toRealPath() + " has changed.");
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}

				@Override
				public Matcher<Reason> valueChanged() {
					return hasToString(startsWith("Value of input property '" + propertyName + "' has changed"));
				}
			};
		}

		public interface Reason {
			interface InputPropertyReason {
				default Matcher<Reason> fileChanged(File file) {
					return fileChanged(file.toPath());
				}
				Matcher<Reason> fileChanged(Path file);

				Matcher<Reason> valueChanged();
			}
		}
	}
}
