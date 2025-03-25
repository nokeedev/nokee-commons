package dev.nokee.commons.backports;

import org.gradle.api.*;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.component.SoftwareComponentContainer;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.util.GradleVersion;

import javax.inject.Inject;

/**
 * Represents the registry functionality of the {@link SoftwareComponentContainer} that natively support {@link AdhocComponentWithVariants} on Gradle versions prior to 8.1.
 * On Gradle 8.1+, the registry uses the {@link PolymorphicDomainObjectContainer#register(String, Class)} method.
 * On older Gradle, the registry uses the {@link SoftwareComponentFactory} to emulate the correct register behavior.
 *
 * <p>
 * Create your own instance using {@link SoftwareComponentRegistry#forProject(Project)}.
 * </p>
 */
public abstract class SoftwareComponentRegistry {
	private final String displayName;

	protected SoftwareComponentRegistry(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Registers a new software component of the specified name and type.
	 * Please refer to Gradle documentation to know which type are registrable on {@link SoftwareComponentContainer}.
	 *
	 * @param name  the element name to register
	 * @param type  the type to register
	 * @return a configurable provider to the registered element
	 * @param <S>  the component type to register
	 */
	public abstract <S extends SoftwareComponent> NamedDomainObjectProvider<S> register(Object name, Class<S> type);

	/**
	 * Registers a new software component of the specified name and type if absent.
	 * Please refer to Gradle documentation to know which type are registrable on {@link SoftwareComponentContainer}.
	 *
	 * @param name  the element name to register
	 * @param type  the type to register
	 * @return a configurable provider to the registered element
	 * @param <S>  the component type to register
	 */
	public abstract <S extends SoftwareComponent> NamedDomainObjectProvider<S> registerIfAbsent(Object name, Class<S> type);

	@Override
	public String toString() {
		return "software component registry for " + displayName;
	}

	/**
	 * Creates the software component registry for the specified project.
	 *
	 * @param project  the project to use
	 * @return a registry for the project's software components
	 */
	public static SoftwareComponentRegistry forProject(Project project) {
		if (GradleVersion.current().compareTo(GradleVersion.version("8.1")) < 0) {
			return project.getObjects().newInstance(PolymorphicDomainObjectRegistryAdapter.class, project.toString(), project.getComponents());
		} else {
			return project.getObjects().newInstance(LegacySoftwareComponentRegistry.class, project.toString(), project.getComponents());
		}
	}

	@NonExtensible
	/*private*/ static /*final*/ class LegacySoftwareComponentRegistry extends SoftwareComponentRegistry {
		private final SoftwareComponentFactory componentFactory;
		private final NamedDomainObjectContainer<SoftwareComponent> components;

		@Inject
		public LegacySoftwareComponentRegistry(String displayName, SoftwareComponentFactory componentFactory, NamedDomainObjectContainer<SoftwareComponent> components) {
			super(displayName);
			this.componentFactory = componentFactory;
			this.components = components;
		}

		@Override
		public <S extends SoftwareComponent> NamedDomainObjectProvider<S> register(Object name, Class<S> type) {
			if (type.equals(AdhocComponentWithVariants.class)) {
				components.add(componentFactory.adhoc(name.toString()));
				return components.named(name.toString(), type);
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public <S extends SoftwareComponent> NamedDomainObjectProvider<S> registerIfAbsent(Object name, Class<S> type) {
			if (components.getNames().contains(name.toString())) {
				return components.named(name.toString(), type);
			}
			return register(name, type);
		}
	}

	@NonExtensible
	/*private*/ static /*final*/ class PolymorphicDomainObjectRegistryAdapter extends SoftwareComponentRegistry {
		private final ExtensiblePolymorphicDomainObjectContainer<SoftwareComponent> delegate;

		@Inject
		public PolymorphicDomainObjectRegistryAdapter(String displayName, ExtensiblePolymorphicDomainObjectContainer<SoftwareComponent> delegate) {
			super(displayName);
			this.delegate = delegate;
		}

		@Override
		public <S extends SoftwareComponent> NamedDomainObjectProvider<S> register(Object name, Class<S> type) {
			return delegate.register(name.toString(), type);
		}

		@Override
		public <S extends SoftwareComponent> NamedDomainObjectProvider<S> registerIfAbsent(Object name, Class<S> type) {
			if (delegate.getNames().contains(name.toString())) {
				return delegate.named(name.toString(), type);
			}
			return delegate.register(name.toString(), type);
		}
	}
}
