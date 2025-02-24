package dev.nokee.commons.gradle.tasks.options;

import dev.nokee.commons.gradle.tasks.TaskDependencyUtils;
import org.gradle.api.Action;
import org.gradle.api.Buildable;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.TaskDependency;

import javax.inject.Inject;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

// Reprensent "source files" to action applied to "source options"
public class Entry<T> implements Action<T>, SourceConfiguration {
	public final Action<? super T> configureAction;
	private final FileCollection sourceCollection;

	@Inject
	public Entry(ObjectFactory objects, Object source, Action<? super T> configureAction) {
		this.sourceCollection = objects.fileCollection().from(source);
		this.configureAction = configureAction;
	}

	public Entry(FileCollection sourceCollection, Action<? super T> configureAction) {
		this.sourceCollection = sourceCollection;
		this.configureAction = configureAction;
	}

	@Override
	public void execute(T t) {
		configureAction.execute(t);
	}

	@Override
	public TaskDependency getBuildDependencies() {
		if (configureAction instanceof Buildable) {
			return ((Buildable) configureAction).getBuildDependencies();
		} else {
			try {
				Method Buildable_getBuildDependencies = configureAction.getClass().getMethod("getBuildDependencies");
				return (TaskDependency) Buildable_getBuildDependencies.invoke(configureAction);
			} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
				return TaskDependencyUtils.empty();
			}
		}
	}

	public interface SourceDetails {
		File getSourceFile();

		void thisEntryParticipateInTheConfiguration();
	}

	public void execute(SourceDetails details) {
		if (sourceCollection.contains(details.getSourceFile())) {
			details.thisEntryParticipateInTheConfiguration();
		}
	}
}
