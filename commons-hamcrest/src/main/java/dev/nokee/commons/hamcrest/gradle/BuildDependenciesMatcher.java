package dev.nokee.commons.hamcrest.gradle;

import org.gradle.api.Buildable;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.provider.Provider;
import org.gradle.testfixtures.ProjectBuilder;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

public final class BuildDependenciesMatcher<T> extends FeatureMatcher<T, Iterable<Task>> {
	private static Project ROOT_PROJECT;

	public BuildDependenciesMatcher(Matcher<? super Iterable<Task>> matcher) {
		super(matcher, "a buildable object with dependencies", "buildable object with dependencies");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Iterable<Task> featureValueOf(T actual) {
		if (actual instanceof Buildable) {
			return (Iterable<Task>) ((Buildable) actual).getBuildDependencies().getDependencies(null);
		} else if (actual instanceof Provider) {
			if (ROOT_PROJECT == null) {
				ROOT_PROJECT = ProjectBuilder.builder().build();
			}
			return (Iterable<Task>) ROOT_PROJECT.files(actual).getBuildDependencies().getDependencies(null);
		}
		throw new UnsupportedOperationException();
	}

	public static <T> BuildDependenciesMatcher<T> buildDependencies(Matcher<? super Iterable<Task>> matcher) {
		return new BuildDependenciesMatcher<>(matcher);
	}
}
