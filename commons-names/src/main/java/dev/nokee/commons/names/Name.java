package dev.nokee.commons.names;

import org.gradle.api.Named;

import javax.annotation.Nullable;

/**
 * Represents a name object.
 * The contract of a name object can be a simple having the {@code Object#toString()} returns the string representation of the name.
 * Hence, a loose representation of a name object could be any {@code Object} implementation that respect this contract.
 * For this reason, we recommend treating name object as {@code Object} unless a strongly typed interface is required.
 */
public interface Name extends Comparable<Name> {
	@Override
	default int compareTo(Name name) {
		return toString().compareTo(name.toString());
	}

	/**
	 * Convert any object into a name object.
	 *
	 * @param name  the name to convert, can be a Name itself or any Object respecting the contract, must not be null.
	 * @return a name instance representing the object.
	 */
	static Name of(Object name) {
		if (name instanceof Name) {
			return (Name) name;
		} else if (name instanceof Named) {
			return new GradleNamedAdapter((Named) name);
		} else {
			return new ObjectNameAdapter(name);
		}
	}

	/**
	 * Returns the value of the queried property name on this name.
	 *
	 * @param propertyName  the property name to query, must not be null
	 * @return the value of the property, may be null
	 */
	@Nullable
	default Object get(String propertyName) {
		return null;
	}

	/**
	 * Replaces the property name of this name with the specified value as a new name.
	 *
	 * @param propName  the property name to replace, must not be null
	 * @param value  the value to use, must not be null
	 * @return a new name with the replaced property, never null
	 */
	default Name with(String propName, Object value) {
		return this;
	}

	/**
	 * Creates the name represented by {@link Named} object.
	 * A Gradle named object is always a fully qualified name.
	 *
	 * @param name  the named object, must not be null
	 * @return a new name representing the {@link Named} object, never null
	 */
	static FullyQualifiedName of(Named name) {
		return new GradleNamedAdapter(name);
	}

	// TODO: Revisit
	default String toString(NameBuilder builder) {
		throw new UnsupportedOperationException();
	}
}
