package dev.nokee.commons.names;

/**
 * Represents a name derived from a {@link FullyQualifiedName} relative to a parent {@link Qualifier}.
 *
 * @see FullyQualifiedName#relativeTo(Qualifier)
 */
public interface RelativeName extends QualifiedName {
	FullyQualifiedName toFullName();
}
