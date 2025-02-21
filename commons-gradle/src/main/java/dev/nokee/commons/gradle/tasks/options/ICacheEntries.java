package dev.nokee.commons.gradle.tasks.options;

import java.io.File;
import java.util.function.Supplier;

public interface ICacheEntries<T> {
	Supplier<T> forFile(File sourceFile);
}
