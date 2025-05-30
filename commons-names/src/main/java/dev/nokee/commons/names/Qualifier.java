package dev.nokee.commons.names;

/**
 * Represents qualifying aspect of a {@link Name}.
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
