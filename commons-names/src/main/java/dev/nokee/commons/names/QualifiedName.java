package dev.nokee.commons.names;

/**
 * Represents a name that was qualified with a {@link Qualifier}.
 *
 * @see Qualifiable#qualifiedBy(Qualifier)
 */
public interface QualifiedName extends Name {
	/**
	 * {@inheritDoc}
	 */
	@Override
	QualifiedName with(String propName, Object value);
}
