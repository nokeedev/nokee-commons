package dev.nokee.commons.gradle.tasks;

import org.gradle.api.Task;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Provider;

import java.io.File;
import java.util.Objects;

public final class TemporaryDirectory {
	private TemporaryDirectory() {}

	/**
	 * Returns the temporary task directory as a directory provider.
	 * Calling {@link Task#getTemporaryDir()} creates the directory immediately.
	 * To avoid unnecessary directory creation during the configuration phase, we can use this method.
	 *
	 * Note, this method can only be used during configuration phase.
	 *
	 * @param task  the task to return the temporary task directory, must not be null
	 * @return the task's temporary directory as {@link Provider}, never null
	 */
	@SuppressWarnings("UnstableApiUsage")
	public static DirectoryProperty temporaryDirectoryOf(Task task) {
		Objects.requireNonNull(task);
		final Provider<File> value = task.getProject().provider(task::getTemporaryDir);
		final DirectoryProperty result = task.getProject().getObjects().directoryProperty().fileProvider(value);
		result.disallowChanges();
		result.finalizeValueOnRead();
		return result;
	}

	/** @see #temporaryDirectoryOf(Task) */
	public static DirectoryProperty of(Task task) {
		return temporaryDirectoryOf(task);
	}
}
