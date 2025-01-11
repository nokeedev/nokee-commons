package dev.nokee.commons.names;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;

public interface QualifiedNameTester {
	QualifiedName subject();

	@Test
	default void canReplaceElementName() {
		assertThat(subject().with("elementName", ElementName.of("other")), hasToString(otherNameQualifiedName()));
	}

	String otherNameQualifiedName();

	@Test
	default void canReplaceElementNameWithTaskName() {
		assertThat(subject().with("elementName", ElementName.taskName("do", "something")), hasToString(otherTaskNameQualifiedName()));
	}

	String otherTaskNameQualifiedName();

	@Test
	default void canReplaceQualifier() {
		assertThat(subject().with("qualifier", new TestQualifier(NameString.of(NameString.of("other")))), hasToString(otherQualifierQualifiedName()));
	}

	String otherQualifierQualifiedName();
}
