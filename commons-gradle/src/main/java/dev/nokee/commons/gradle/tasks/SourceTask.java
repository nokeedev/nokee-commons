package dev.nokee.commons.gradle.tasks;

import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;

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
}
