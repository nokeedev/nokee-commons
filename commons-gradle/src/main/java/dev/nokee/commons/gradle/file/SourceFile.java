package dev.nokee.commons.gradle.file;

import org.gradle.api.file.FileTreeElement;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

import java.io.File;
import java.util.Objects;

public final class SourceFile {
	private final String path;
	private final File file;

	public SourceFile(String path, File file) {
		this.path = path;
		this.file = file;
	}

	public static SourceFile of(FileTreeElement details) {
		return new SourceFile(details.getPath(), details.getFile());
	}

	@Input
	public String getPath() {
		return path;
	}

	@InputFile
	@PathSensitive(PathSensitivity.NONE)
	public File getFile() {
		return file;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		SourceFile that = (SourceFile) o;
		return Objects.equals(path, that.path) && Objects.equals(file, that.file);
	}

	@Override
	public int hashCode() {
		return Objects.hash(path, file);
	}

	@Override
	public String toString() {
		return "source file '" + path + "'";
	}
}
