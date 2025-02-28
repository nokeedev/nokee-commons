package dev.nokee.commons.fixtures;

import dev.nokee.commons.gradle.tasks.options.SourceOptions;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.io.File;

import static org.hamcrest.Matchers.equalTo;

public class SourceOptionsMatchers {
	public static Matcher<SourceOptions<?>> sourceFile(File file) {
		return new FeatureMatcher<SourceOptions<?>, File>(equalTo(file), "", "") {
			@Override
			protected File featureValueOf(SourceOptions<?> actual) {
				return actual.getSourceFile().get().getAsFile();
			}
		};
	}

	public static Matcher<SourceOptions<?>> sourceFile(Matcher<? super File> matcher) {
		return new FeatureMatcher<SourceOptions<?>, File>(matcher, "", "") {
			@Override
			protected File featureValueOf(SourceOptions<?> actual) {
				return actual.getSourceFile().get().getAsFile();
			}
		};
	}
}
