package dev.nokee.commons.names;

import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.component.AdhocComponentWithVariants;

/**
 * An element name represent the name of a domain object regardless of its owners.
 * The name is non-unique across all domain objects of a project.
 * For example, the element name of a task that assemble the output of a component is {@literal assemble}.
 * However, it's fully qualified name may be {@literal assembleFoo} for the {@literal foo} component.
 * In some cases, the element name may be the same as the fully qualified name such as the assemble task of the {@literal main} component.
 * It's important to keep in mind that both name represent different things.
 * Some element names may participate in further qualification while other won't, aka {@link Task} name or {@link Configuration} name, or {@link AdhocComponentWithVariants} name.
 */
public interface ElementName extends Qualifiable, Name {
	static OtherName ofMain(String name) {
		return new OtherElementName(NameString.ofMain(NameString.of(name)));
	}

	static OtherName of(String name) {
		return new OtherElementName(NameString.of(name));
	}

	static TaskName taskName(String verb) {
		return TaskName.of(verb);
	}

	static TaskName taskName(String verb, String object) {
		return TaskName.of(verb, object);
	}

	static TaskName.Builder taskName() {
		return TaskName.builder();
	}

	static ConfigurationName configurationName(String name) {
		return ConfigurationName.of(name);
	}

	static ConfigurationName.Builder configurationName() {
		return ConfigurationName.builder();
	}

	static SoftwareComponentName componentName(String name) {
		return SoftwareComponentName.of(name);
	}

	@Override
	FullyQualifiedName qualifiedBy(Qualifier qualifier);
}
