package dev.nokee.commons.gradle;

import dev.nokee.commons.fixtures.Subject;
import org.gradle.api.Named;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static dev.nokee.commons.hamcrest.gradle.ThrowableMatchers.doesNotThrowException;
import static org.hamcrest.MatcherAssert.assertThat;

class NamedDomainObjectRegistryIntegrationTests implements NamedDomainObjectRegistryTester<NamedDomainObjectRegistryIntegrationTests.TestObject> {
	Project project;

	@BeforeEach
	void setup(@TempDir File testDirectory) {
		project = ProjectBuilder.builder().withProjectDir(testDirectory).build();
	}

	@Subject NamedDomainObjectRegistry<TestObject> newSubject() {
		return new NamedDomainObjectRegistry<>(project.getObjects().domainObjectContainer(TestObject.class, TestObject::new));
	}

	@Subject("with-existing-element") NamedDomainObjectRegistry<TestObject> newSubjectWithExistingElements() {
		NamedDomainObjectContainer<TestObject> container = project.getObjects().domainObjectContainer(TestObject.class, TestObject::new);
		assertThat(() -> container.register("my-existing-element"), doesNotThrowException());
		return new NamedDomainObjectRegistry<>(container);
	}

	static final class TestObject implements Named {
		private final String name;

		private TestObject(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
