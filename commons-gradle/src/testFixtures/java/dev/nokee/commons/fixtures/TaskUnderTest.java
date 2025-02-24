package dev.nokee.commons.fixtures;

import org.codehaus.groovy.runtime.StringGroovyMethods;

import java.nio.file.Path;
import java.util.List;

// TODO: implement TargetTask -> type from GradleRunner kit
public class TaskUnderTest {
	private final String taskName;

	public TaskUnderTest(String taskName) {
		this.taskName = taskName;
	}

	public String cleanIt() {
		return ":clean" + StringGroovyMethods.capitalize(taskName.substring(1));
	}

	@Override
	public String toString() {
		return taskName;
	}
}
