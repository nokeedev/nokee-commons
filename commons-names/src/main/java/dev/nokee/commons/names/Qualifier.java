package dev.nokee.commons.names;

/**
 * Represent an object that can qualify a name.
 *
 * @see QualifyingName
 * @see QualifiedName
 */
public interface Qualifier extends IAppendTo {
	interface Visitor {
		void visit(Qualifier qualifier);
	}
	default void accept(Visitor visitor) {
		throw new UnsupportedOperationException();
	}

	default String toString(NamingScheme scheme) {
		throw new UnsupportedOperationException();
	}
}
