package dev.nokee.commons.gradle.file;

import org.gradle.api.Action;
import org.gradle.api.file.FileVisitDetails;

public final class SourceFileVisitor extends RegularFileVisitor {
	private final Action<SourceFile> visitor;

	public SourceFileVisitor(Action<SourceFile> visitor) {
		this.visitor = visitor;
	}

	@Override
	public void visitFile(FileVisitDetails details) {
		visitor.execute(SourceFile.of(details));
	}
}
