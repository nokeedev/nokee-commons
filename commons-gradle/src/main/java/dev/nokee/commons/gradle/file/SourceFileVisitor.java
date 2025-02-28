package dev.nokee.commons.gradle.file;

import org.gradle.api.Action;
import org.gradle.api.file.FileVisitDetails;

import java.io.File;

public final class SourceFileVisitor extends RegularFileVisitor {
	private final ReusableSourceFile sourceFile = new ReusableSourceFile();
	private final Action<SourceFile> visitor;

	public SourceFileVisitor(Action<SourceFile> visitor) {
		this.visitor = visitor;
	}

	@Override
	public void visitFile(FileVisitDetails details) {
		sourceFile.details = details;
		visitor.execute(sourceFile);
	}

	private static final class ReusableSourceFile extends AbstractSourceFile {
		private FileVisitDetails details;

		public String getPath() {
			return details.getPath();
		}

		public File getFile() {
			return details.getFile();
		}
	}
}
