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
	void accept(Visitor visitor);

	String toString(NamingScheme scheme);
}
