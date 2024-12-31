package dev.nokee.commons.names;

/**
 * Strongly represent a name object.
 * A name object has a very simple contract where the {@code Object#toString()} returns the string representation of the name.
 * Hence, a loose representation of a name object could be any {@code Object} implementation that respect this contract.
 * For this reason, we recommend treating name object as {@code Object} unless a strongly typed interface is required.
 */
public interface Name {
//	void appendTo(NameBuilder builder);

	static Name asName(Object name) {
		if (name instanceof Name) {
			return (Name) name;
		} else {
			return new Name() {
				@Override
				public String toString() {
					return name.toString();
				}
			};
		}
	}
}
