package dev.nokee.commons.names;

/**
 * Represents a name that can be qualified with a {@link Qualifier}.
 *
 * @see QualifiedName
 * @see Qualifier
 */
public interface Qualifiable {
	/**
	 * Creates a new qualified name for the specified qualifier.
	 *
	 * @param qualifier  the qualifier for this name, must not be null
	 * @return a new {@link QualifiedName} representing the qualifying of this name
	 */
	QualifiedName qualifiedBy(Qualifier qualifier);
}
