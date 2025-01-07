package dev.nokee.commons.names;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;

public interface QualifiableTester {
	Qualifiable subject();

	@Test
	default void canBeQualifiedWith_debugWindows() {
		assertThat(subject().qualifiedBy(new TestQualifier(Qualifiers.of(Qualifiers.of("release"), Qualifiers.of("windows")))), hasToString(releaseWindowsQualifiedName()));
	}

	String releaseWindowsQualifiedName();

	@Test
	default void canBeQualifiedWith_debugMainLinux() {
		assertThat(subject().qualifiedBy(new TestQualifier(Qualifiers.of(Qualifiers.of("debug"), Qualifiers.ofMain(Qualifiers.of("linux"))))), hasToString(debugQualifiedName()));
	}

	String debugQualifiedName();
}
