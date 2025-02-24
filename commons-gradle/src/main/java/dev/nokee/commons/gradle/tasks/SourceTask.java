package dev.nokee.commons.gradle.tasks;

import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.language.nativeplatform.tasks.AbstractNativeCompileTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Represents a task that performs some operation on source files.
 */
public interface SourceTask extends Task {
	/**
	 * Adds some source to this task.
	 * The given source objects will be evaluated as per {@link org.gradle.api.Project#files(Object...)}.
	 *
	 * @param sources  the source to add
	 * @return this
	 */
	default SourceTask source(Object... sources) {
		setSource(getSource().plus(getProject().files(sources)).getAsFileTree());
		return this;
	}

	/**
	 * Returns the source for this task.
	 * Ignores source files which do not exist.
	 *
	 * <p>
	 * The {@link PathSensitivity} for the sources is configured to be {@link PathSensitivity#ABSOLUTE}.
	 * If your sources are less strict, please change it accordingly by overriding this method in your subclass.
	 * </p>
	 *
	 * @return the source.
	 */
	@InputFiles
	@SkipWhenEmpty
	@PathSensitive(PathSensitivity.ABSOLUTE)
	FileTree getSource();

	/**
	 * Sets the source for this task.
	 *
	 * @param source  the source
	 */
	void setSource(FileTree source);

	/**
	 * Returns the source of the specified task.
	 * Given the <i>source task</i> contract is not formally defined, we rely on the concept instead.
	 *
	 * @param task  the source task
	 * @return a FileTree of the sources
	 */
	static FileTree sourceOf(Task task) {
		if (task instanceof SourceTask) {
			return ((SourceTask) task).getSource();
		} else if (task instanceof AbstractNativeCompileTask) {
			return ((AbstractNativeCompileTask) task).getSource().getAsFileTree();
		} else {
			// Required because getSource() contract is not clearly defined
			try {
				final Method SourceTaskContract_getSourceMethod = task.getClass().getMethod("getSource");
				final Object result = SourceTaskContract_getSourceMethod.invoke(task);

				if (result instanceof FileCollection) {
					return ((FileCollection) result).getAsFileTree();
				} else {
					throw new UnsupportedOperationException();
				}
			} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Adds source to the specified task.
	 * Given the <i>source task</i> contract is not formally defined, we rely on the concept instead.
	 *
	 * @param task  the source task
	 * @param sourcePath  the source path to add
	 * @param sourcePaths  the other source paths to add
	 * @return the source task
	 * @param <T>  the source task type to configure
	 */
	static <T extends Task> T source(T task, Object sourcePath, Object... sourcePaths) {
		if (task instanceof SourceTask) {
			((SourceTask) task).source(sourcePath, sourcePaths);
		} else if (task instanceof AbstractNativeCompileTask) {
			((AbstractNativeCompileTask) task).source(new Object[] { sourcePath, sourcePaths });
		} else {
			// Required because source(Object...) contract is not clearly defined
			try {
				final Method SourceTaskContract_sourceMethod = task.getClass().getMethod("source", Object[].class);
				SourceTaskContract_sourceMethod.setAccessible(true);
				SourceTaskContract_sourceMethod.invoke(task, new Object[] { sourcePath, sourcePaths });
			} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
				// Required because source(Object) contract is not clearly defined
				try {
					final Method SourceTaskContract_sourceMethod = task.getClass().getMethod("source", Object.class);
					SourceTaskContract_sourceMethod.setAccessible(true);
					SourceTaskContract_sourceMethod.invoke(task, sourcePath);
					SourceTaskContract_sourceMethod.invoke(task, new Object[] { sourcePaths });
				} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
					throw new UnsupportedOperationException();
				}
			}
		}
		return task;
	}
}
