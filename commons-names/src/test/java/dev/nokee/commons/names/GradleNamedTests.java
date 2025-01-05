package dev.nokee.commons.names;

import org.gradle.api.Named;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;

class GradleNamedTests implements NameTester {
	Name subject = Name.of(new TestNamedObject());

	@Override
	public Name subject() {
		return subject;
	}

	@Override
	public String name() {
		return "myName";
	}

	@Test
	void isFullyQualifiedName() {
		assertThat(subject, isA(FullyQualifiedName.class));
	}

	static class TestNamedObject implements Named {
		@Override
		public String getName() {
			return "myName";
		}
	}
}
