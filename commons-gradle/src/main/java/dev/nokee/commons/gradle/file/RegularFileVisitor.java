package dev.nokee.commons.gradle.file;

import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;

public abstract class RegularFileVisitor implements FileVisitor {
	@Override
	public final void visitDir(FileVisitDetails details) {
		// do nothing
	}
}
