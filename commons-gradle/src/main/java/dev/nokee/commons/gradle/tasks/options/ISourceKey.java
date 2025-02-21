package dev.nokee.commons.gradle.tasks.options;

import java.io.File;

public interface ISourceKey {
	ISourceKey DEFAULT_KEY = new ISourceKey() {};

	interface Lookup {
		ISourceKey forFile(File sourceFile);
	}
}
