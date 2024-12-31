package dev.nokee.commons.names;

// Prefixing: <qualifyingName><name>
// Suffixing: <name><qualifyingName>

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
public interface ElementName extends Name {
	// TODO: Change to QualifiedName
	FullyQualifiedName qualifiedBy(Qualifier qualifier);

	static Names ofMain() {
		return new DefaultNames(Qualifiers.ofMain(Qualifiers.of("main")));
	}

	static Names ofMain(String name) {
		return new DefaultNames(Qualifiers.ofMain(Qualifiers.of(name)));
	}

	static Names of(String name) {
		return new DefaultNames(Qualifiers.of(name));
	}

	static TaskName taskName(String verb) {
		return TaskName.of(verb);
	}

	static TaskName taskName(String verb, String object) {
		return TaskName.of(verb, object);
	}

	static ElementName configurationName(String name) {
		return new ElementName() {
			@Override
			public FullyQualifiedName qualifiedBy(Qualifier qualifier) {
				return new NameSupport.ForQualifiedName() {
					@Override
					public void appendTo(NameBuilder builder) {
						builder.append(qualifier);
						builder.append(name);
					}

					@Override
					public String toString() {
						return NamingScheme.lowerCamelCase().format(this);
					}
				};
			}

			@Override
			public String toString() {
				return name;
			}
		};
	}

	static ElementName componentName(String name) {
		return new ElementName() {
			@Override
			public FullyQualifiedName qualifiedBy(Qualifier qualifier) {
				return new NameSupport.ForQualifiedName() {
					@Override
					public void appendTo(NameBuilder builder) {
						builder.append(name);
						builder.append(qualifier);
					}

					@Override
					public String toString() {
						return NamingScheme.lowerCamelCase().format(this);
					}
				};
			}

			@Override
			public String toString() {
				return name;
			}
		};
	}
}
