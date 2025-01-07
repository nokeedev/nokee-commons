package dev.nokee.commons.names;

public interface FullyQualifiedName extends QualifiedName {
	String toString(NameBuilder builder);

	// TODO: Maybe use relativize?
	RelativeName relativeTo(Qualifier qualifier);
}
