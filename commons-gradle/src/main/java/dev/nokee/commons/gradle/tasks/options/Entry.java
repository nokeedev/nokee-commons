package dev.nokee.commons.gradle.tasks.options;

import org.gradle.api.Action;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.Set;

// Reprensent "source files" to action applied to "source options"
public class Entry<T> implements Action<T>, SourceConfiguration {
	private final Action<? super T> configureAction;
	private FileCollection sourceCollection;
	private Set<File> sourceFiles;

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

	public interface SourceDetails {
		File getSourceFile();

		void thisEntryParticipateInTheConfiguration();
	}

	public void execute(SourceDetails details) {
		if (sourceFiles == null) {
			sourceFiles = sourceCollection.getFiles();
			sourceCollection = null;
		}

		if (sourceFiles.contains(details.getSourceFile())) {
			details.thisEntryParticipateInTheConfiguration();
		}
	}
}
