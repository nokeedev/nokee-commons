package dev.nokee.commons.names;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;

class CleanTaskNameTests {
	@Test
	void createsCleanLifecycleTaskNameForTaskName() {
		TaskName subject = TaskName.clean(TaskName.of("link"));
		assertThat(subject, hasToString("cleanLink"));
		assertThat(Names.of("executable").append(subject), hasToString("cleanLinkExecutable"));
	}

	@Test
	void createsCleanLifecycleTaskNameForObjectOnlyTaskName() {
		TaskName subject = TaskName.clean(TaskName.builder().forObject("classes"));
		assertThat(subject, hasToString("cleanClasses"));
		assertThat(Names.of("test").append(subject), hasToString("cleanTestClasses"));
	}

	@Test
	void createsCleanLifecycleTaskNameForVerbAndObjectTaskName() {
		TaskName subject = TaskName.clean(TaskName.of("compile", "cpp"));
		assertThat(subject, hasToString("cleanCompileCpp"));
		assertThat(Names.of("test").append(subject), hasToString("cleanCompileTestCpp"));
	}
}
