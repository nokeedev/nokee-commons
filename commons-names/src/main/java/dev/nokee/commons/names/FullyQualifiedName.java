package dev.nokee.commons.names;

public interface FullyQualifiedName extends QualifiedName {
	// TODO: Should be a factory or some kind of object to align with NamingScheme
	String toString(NameBuilder builder);

	// TODO: Maybe use relativize?
	RelativeName relativeTo(Qualifier qualifier);
}
