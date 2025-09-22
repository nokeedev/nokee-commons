package dev.nokee.commons.fixtures;

import dev.nokee.commons.gradle.tasks.options.SourceFileOptions;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.io.File;

import static org.hamcrest.Matchers.equalTo;

public class SourceOptionsMatchers {
	public static Matcher<SourceFileOptions<?>> sourceFile(File file) {
		return new FeatureMatcher<SourceFileOptions<?>, File>(equalTo(file), "", "") {
			@Override
			protected File featureValueOf(SourceFileOptions<?> actual) {
				return actual.getSourceFile();
			}
		};
	}

	public static Matcher<SourceFileOptions<?>> sourceFile(Matcher<? super File> matcher) {
		return new FeatureMatcher<SourceFileOptions<?>, File>(matcher, "", "") {
			@Override
			protected File featureValueOf(SourceFileOptions<?> actual) {
				return actual.getSourceFile();
			}
		};
	}
}
