package dev.nokee.commons.gradle;

import org.gradle.api.NonExtensible;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

@NonExtensible
public class TaskRegistry extends PolymorphicDomainObjectRegistry<Task> {
	@Inject
	public TaskRegistry(TaskContainer delegate) {
		super(delegate);
	}

	@Override
	public <S extends Task> TaskProvider<S> register(Object name, Class<S> type) {
		return (TaskProvider<S>) super.register(name, type);
	}

	@Override
	public <S extends Task> TaskProvider<S> registerIfAbsent(Object name, Class<S> type) {
		return (TaskProvider<S>) super.registerIfAbsent(name, type);
	}

	@Override
	public TaskProvider<Task> register(Object name) {
		return (TaskProvider<Task>) super.register(name);
	}

	@Override
	public TaskProvider<Task> registerIfAbsent(Object name) {
		return (TaskProvider<Task>) super.registerIfAbsent(name);
	}
}
