package dev.nokee.commons.gradle.file;

import java.util.Objects;

abstract class AbstractSourceFile implements SourceFile {
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SourceFile)) return false;
		SourceFile that = (SourceFile) o;
		return Objects.equals(getPath(), that.getPath()) && Objects.equals(getFile(), that.getFile());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPath(), getFile());
	}

	@Override
	public String toString() {
		return "source file '" + getPath() + "'";
	}
}
