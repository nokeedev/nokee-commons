package dev.nokee.commons.names;

/**
 * Represents an element name that is also a qualifying name, e.g. source set name or binary name.
 */
public interface OtherName extends ElementName, QualifyingName {
	/**
	 * {@inheritDoc}
	 */
	@Override
	QualifyingName qualifiedBy(Qualifier qualifier);
}
