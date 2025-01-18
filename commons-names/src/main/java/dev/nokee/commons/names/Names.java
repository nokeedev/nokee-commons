package dev.nokee.commons.names;

import java.util.function.Function;

/**
 * Represents a naming hierarchy.
 * Names are a special type of qualifying names that fits in the middle of additional names, ex: {@literal <componentName>.<binaryName>.<taskName>}, both {@literal componentName} and {@literal binaryName} would be node names.
 * Configuration, task, or software component names are generally leaf names.
 */
public interface Names extends QualifyingName {
	/**
	 * Creates a name hierarchy using the conventional {@literal main} node.
	 *
	 * @return a name hierarchy
	 */
	static Names ofMain() {
		return new DefaultNames(ElementName.ofMain("main"));
	}

	/**
	 * Creates a name hierarchy using the specified name as the root node.
	 *
	 * @param name  the root name, must not be null
	 * @return a name hierarchy
	 */
	static Names of(String name) {
		return new DefaultNames(ElementName.of(name));
	}

	/**
	 * Same as {@code append(ElementName.of(name))}.
	 *
	 * @param name  the other name to append, must not be null
	 * @return a name hierarchy
	 * @see #append(OtherName) for more information.
	 */
	default Names append(String name) {
		return append(ElementName.of(name));
	}

	/**
	 * Creates a child name hierarchy (node) for the specified element name.
	 *
	 * @param name  the other name to append, must not be null
	 * @return a name hierarchy
	 */
	default Names append(OtherName name) {
		return new DefaultNames(name.qualifiedBy(this));
	}

	/**
	 * Creates a fully qualified name (leaf) for the specified element name.
	 *
	 * @param name  the element name to append, must not be null
	 * @return a name
	 */
	default FullyQualifiedName append(ElementName name) {
		return name.qualifiedBy(this);
	}

	/**
	 * Same as {@code append(ElementName.taskName(verb))}.
	 *
	 * @param verb  the task prefix, must not be null
	 * @return a name
	 * @see #append(ElementName) convenience for {@code append(ElementName.taskName(verb)}.
	 */
	default FullyQualifiedName taskName(String verb) {
		return ElementName.taskName(verb).qualifiedBy(this);
	}

	/**
	 * Same as {@code append(ElementName.taskName(verb, object))}.
	 *
	 * @param verb  the task prefix, must not be null
	 * @param object  the task suffix, must not be null
	 * @return a name
	 * @see #append(ElementName) convenience for {@code append(ElementName.taskName(verb, object)}.
	 */
	default FullyQualifiedName taskName(String verb, String object) {
		return ElementName.taskName(verb, object).qualifiedBy(this);
	}

	/**
	 * Same as {@code append(action.apply(ElementName.taskName())}.
	 *
	 * @param action  the builder configuration, must not be null
	 * @return a name
	 * @see #append(ElementName) convenience for {@code append(action.apply(ElementName.taskName())}.
	 */
	default FullyQualifiedName taskName(Function<? super TaskName.Builder, ? extends TaskName> action) {
		return action.apply(ElementName.taskName()).qualifiedBy(this);
	}

	/**
	 * Same as {@code append(ElementName.configurationName(name))}.
	 *
	 * @param name  the configuration name, must not be null
	 * @return a name
	 * @see #append(ElementName) convenience for {@code append(ElementName.configurationName(name))}.
	 */
	default FullyQualifiedName configurationName(String name) {
		return ElementName.configurationName(name).qualifiedBy(this);
	}

	/**
	 * Same as {@code append(action.apply(ElementName.configurationName()))}.
	 *
	 * @param action  the builder configuration, must not be null
	 * @return a name
	 * @see #append(ElementName) convenience for {@code append(action.apply(ElementName.configurationName()))}.
	 */
	default FullyQualifiedName configurationName(Function<? super ConfigurationName.Builder, ? extends ConfigurationName> action) {
		return action.apply(ElementName.configurationName()).qualifiedBy(this);
	}

	/**
	 * Same as {@code append(ElementName.componentName(name))}.
	 *
	 * @param name  the software component name, must not be null
	 * @return a name
	 * @see #append(ElementName) convenience for {@code append(ElementName.componentName(name))}.
	 */
	default FullyQualifiedName componentName(String name) {
		return ElementName.componentName(name).qualifiedBy(this);
	}
}
