package dev.nokee.commons.names;

import org.gradle.api.Named;

/**
 * Represents a fully qualified name of a domain object.
 * Those names are unique across all domain objects of a project.
 *
 * @see Name#of(Named) for adapting a Gradle Named object
 */
public interface FullyQualifiedName extends QualifiedName {
	// TODO: Should be a factory or some kind of object to align with NamingScheme
	String toString(NameBuilder builder);

	/**
	 * Creates a relative name based on the specified qualifier.
	 * Fully qualified names are relative to the project's root while relative names are relative to a parent qualifier of its fully qualified name.
	 *
	 * @param qualifier  a parent qualifier, must not be null
	 * @return a relative name instance
	 */
	// TODO: Maybe use relativize?
	RelativeName relativeTo(Qualifier qualifier);
}
