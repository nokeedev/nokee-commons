package dev.nokee.commons.names;

/**
 * Represents a name derived from a {@link FullyQualifiedName} relative to a parent {@link Qualifier}.
 *
 * @see FullyQualifiedName#relativeTo(Qualifier)
 */
public interface RelativeName extends QualifiedName {
	/**
	 * Returns the associated fully qualified name represented by this relative name.
	 *
	 * @return the name this relative name was derived from, never null
	 */
	FullyQualifiedName toFullName();
}
