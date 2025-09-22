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
import java.util.concurrent.Callable;

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
		FileTree sourceFiles = getProject().files(sources).getAsFileTree();
		if (getSource() != null) {
			sourceFiles = getSource().plus(sourceFiles);
		}
		setSource(sourceFiles);
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
	 * @return the source, will be null when no sources
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
	 * @return a FileTree of the sources, never null
	 */
	static FileTree sourceOf(Task task) {
		if (task instanceof AbstractNativeCompileTask) {
			// For this type of tasks, we know the method returns the final value
			return ((AbstractNativeCompileTask) task).getSource().getAsFileTree();
		}
		// For these other type of tasks, the returned value may or may not be the final value.
		//   So we defer the value to ensure the returned FileTree honors the imposed contract.
		return task.getProject().files((Callable<?>) () -> {
			if (task instanceof SourceTask) {
				return ((SourceTask) task).getSource();
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
					throw new NotSourceTaskException("No methods with the following contracts:\n\t> FileCollection getSource()", e);
				}
			}
		}).getAsFileTree();
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
				SourceTaskContract_sourceMethod.invoke(task, new Object[] { new Object[] { sourcePath, sourcePaths } });
			} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
				// Required because source(Object) contract is not clearly defined
				try {
					final Method SourceTaskContract_sourceMethod = task.getClass().getMethod("source", Object.class);
					SourceTaskContract_sourceMethod.setAccessible(true);
					SourceTaskContract_sourceMethod.invoke(task, sourcePath);
					SourceTaskContract_sourceMethod.invoke(task, new Object[] { sourcePaths });
				} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
					throw new NotSourceTaskException("No methods with the following contracts:\n\t> Any source(Object...)\n\t> Any source(Object)", ex);
				}
			}
		}
		return task;
	}
}
