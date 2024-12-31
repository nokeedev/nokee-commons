package dev.nokee.commons.names;

abstract class NameSupport {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;

		Name other = Name.of(obj);
		return toString().equals(other.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public abstract String toString();

	static abstract class ForQualifiedName extends NameSupport implements FullyQualifiedName {}
}
