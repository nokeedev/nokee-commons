package dev.nokee.commons.hamcrest.gradle;

import org.gradle.api.Buildable;
import org.gradle.api.Task;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

public final class BuildDependenciesMatcher extends FeatureMatcher<Buildable, Iterable<Task>> {
	public BuildDependenciesMatcher(Matcher<? super Iterable<Task>> matcher) {
		super(matcher, "a buildable object with dependencies", "buildable object with dependencies");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Iterable<Task> featureValueOf(Buildable actual) {
		return (Iterable<Task>) actual.getBuildDependencies().getDependencies(null);
	}

	public static BuildDependenciesMatcher buildDependencies(Matcher<? super Iterable<Task>> matcher) {
		return new BuildDependenciesMatcher(matcher);
	}
}
