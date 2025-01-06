package dev.nokee.commons.names;

/**
 * Represent an object that can qualify a name.
 *
 * @see QualifyingName
 * @see QualifiedName
 */
public interface Qualifier extends IAppendTo {
	default String toString(NamingScheme scheme) {
		throw new UnsupportedOperationException();
	}
}
