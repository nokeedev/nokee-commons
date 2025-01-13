package dev.nokee.commons.names;

import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.component.AdhocComponentWithVariants;

/**
 * Represents an element name of a domain object, typically non-qualified.
 * Those names are non-unique across all domain objects of a project.
 * For example, the element name of a task that assemble the output of a component is {@literal assemble}.
 * Its fully qualified name may be {@literal assembleTestDebug} for the {@literal debug} binary of the {@literal test} component.
 *
 * <p>
 *   In some cases, the element name may be the same as the fully qualified name such as the {@literal assemble} task of the {@literal main} component.
 *   However, it's best to qualify the task name with the appropriate qualifier as both the element name and fully qualified name represent different things.
 *   For example: {@code ElementName.taskName('assemble')} vs {@code Names.ofMain().taskName('assemble')}.
 * </p>
 *
 * <p>
 *   Some element names may participate in further qualification, i.e. {@link org.gradle.language.cpp.CppBinary}, while other won't, i.e. {@link Task} name or {@link Configuration} name, or {@link AdhocComponentWithVariants} name.
 * </p>
 *
 * @see OtherName#qualifiedBy(Qualifier)
 * @see Qualifiable#qualifiedBy(Qualifier)
 */
public interface ElementName extends Qualifiable, Name {
	/**
	 * Creates a main element name for the specified name usable as a qualifier.
	 *
	 * @param name  the name, must not be null
	 * @return a qualifying name instance
	 */
	static OtherName ofMain(String name) {
		return new OtherElementName(NameString.ofMain(NameString.of(name)));
	}

	/**
	 * Creates a non-main element name for the specified name usable as a qualifier.
	 *
	 * @param name  the name, must not be null
	 * @return a qualifying name instance
	 */
	static OtherName of(String name) {
		return new OtherElementName(NameString.of(name));
	}

	/**
	 * Creates a task name for the specified name.
	 *
	 * @param verb  the verb (prefix), must not be null
	 * @return a qualifiable name instance
	 */
	static TaskName taskName(String verb) {
		return TaskName.of(verb);
	}

	/**
	 * Creates a task name for the specified name.
	 *
	 * @param verb  the verb (prefix), must not be null
	 * @param object  the object (suffix), must not be null
	 * @return a qualifiable name instance
	 */
	static TaskName taskName(String verb, String object) {
		return TaskName.of(verb, object);
	}

	/**
	 * Returns a builder to create complex task name.
	 *
	 * @return a task name builder
	 * @see TaskName#builder()
	 */
	static TaskName.Builder taskName() {
		return TaskName.builder();
	}

	/**
	 * Creates a configuration name prefixing qualifier (standard) for the specified name.
	 *
	 * @param name  the name, must not be null
	 * @return a configuration name instance
	 */
	static ConfigurationName configurationName(String name) {
		return ConfigurationName.of(name);
	}

	/**
	 * Returns a builder to create non-standard configuration name.
	 *
	 * @return a configuration name builder
	 * @see ConfigurationName#builder()
	 */
	static ConfigurationName.Builder configurationName() {
		return ConfigurationName.builder();
	}

	/**
	 * Creates a software component name suffixing qualifier (standard) for the specified name.
	 *
	 * @param name  the name, must not be null
	 * @return an element name instance
	 */
	static ElementName componentName(String name) {
		return SoftwareComponentName.of(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	FullyQualifiedName qualifiedBy(Qualifier qualifier);
}
