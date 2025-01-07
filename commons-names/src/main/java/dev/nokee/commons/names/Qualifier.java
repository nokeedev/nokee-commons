package dev.nokee.commons.names;

/**
 * Represent an object that can qualify a name.
 *
 * @see QualifyingName
 * @see QualifiedName
 */
public interface Qualifier extends IAppendTo {
	default void accept(Visitor visitor) {
		visitor.visit(this);
	}

	interface Visitor {
		void visit(Qualifier qualifier);
	}
}
