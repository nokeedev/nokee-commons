package dev.nokee.commons.names;

/**
 * Represent an element name that was qualified.
 *
 * @see Qualifiable#qualifiedBy(Qualifier)
 */
public interface QualifiedName extends Name {
	default QualifiedName with(String propName, Object value) {
		throw new UnsupportedOperationException();
	}
}
