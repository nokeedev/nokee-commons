package dev.nokee.commons.backports;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.NonExtensible;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.util.GradleVersion;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Represents the registry functionality of the {@link ConfigurationContainer} that account for the newer factory methods.
 * On Gradle 8.4+, the registry uses the new factory methods.
 * On older Gradle, the registry register a standard {@link Configuration} with the proper configuration of {@literal canBeConsumed} and {@literal canBeResolved}.
 *
 * <p>
 * Create your own instance of the factory using {@link org.gradle.api.model.ObjectFactory#newInstance(Class, Object...)}.
 * </p>
 */
@NonExtensible
public abstract /*final*/ class ConfigurationRegistry {
	private final ConfigurationContainer configurations;

	/**
	 * Construct an instance for the specified {@link ConfigurationContainer}.
	 *
	 * @param configurations  the configuration container delegate
	 */
	@Inject
	public ConfigurationRegistry(ConfigurationContainer configurations) {
		this.configurations = configurations;
	}

	/**
	 * Registers a new {@literal ConsumableConfiguration} or equivalent.
	 * Unless otherwise configured, the registered {@link Configuration} ensure the following:
	 * <code>
	 *     def result = registry.consumable('...')
	 *     assert result.canBeConsumed
	 *     assert !result.canBeResolved
	 * </code>
	 *
	 * @param name  the configuration name, must not be null
	 * @return a domain object provider to the registered {@link Configuration}, never null
	 * @param <T> {@literal ConsumableConfiguration} on Gradle 8.4+ or {@literal Configuration} otherwise
	 */
	public <T extends Configuration> NamedDomainObjectProvider<T> consumable(Object name) {
		return realizeOnOlderGradleThen7_6(Registries.ConsumableConfiguration.register(configurations, name.toString()));
	}

	// See https://github.com/gradle/gradle/commit/415842f849a9d5c3c7e4375cb5e86e06e8b9e6d9
	// On older gradle, we need to realize the Configuration or else there will be an error.
	//   Aka withType(ConfigurationInternal) when filtering for consumable artifacts.
	//   For the affected Gradle version, we should automatically resolve the Configuration
	//   The goal is to avoid adding a configurations.all {} to work around the issue.
	//   Here, we already know which is a consumable configuration hence can resolve only those one.
	private static <T> NamedDomainObjectProvider<T> realizeOnOlderGradleThen7_6(NamedDomainObjectProvider<T> provider) {
		if (GradleVersion.current().compareTo(GradleVersion.version("7.6")) < 0) {
			provider.get();
		}
		return provider;
	}

	/**
	 * Register a new {@literal DependencyScopeConfiguration} or equivalent.
	 * Unless otherwise configured, the registered {@link Configuration} ensure the following:
	 * <code>
	 *     def result = registry.dependencyScope('...')
	 *     assert !result.canBeConsumed
	 *     assert !result.canBeResolved
	 * </code>
	 *
	 * @param name  the configuration name, must not be null
	 * @return a domain object provider to the registered {@link Configuration}, never null
	 * @param <T> {@literal DependencyScopeConfiguration} on Gradle 8.4+ or {@literal Configuration} otherwise
	 */
	public <T extends Configuration> NamedDomainObjectProvider<T> dependencyScope(Object name) {
		return Registries.DependencyScopeConfiguration.register(configurations, name.toString());
	}

	/**
	 * Register a new {@literal ResolvableConfiguration} or equivalent.
	 * Unless otherwise configured, the registered {@link Configuration} ensure the following:
	 * <code>
	 *     def result = registry.resolvable('...')
	 *     assert !result.canBeConsumed
	 *     assert result.canBeResolved
	 * </code>
	 *
	 * @param name  the configuration name, must not be null
	 * @return a domain object provider to the registered {@link Configuration}, never null
	 * @param <T> {@literal ResolvableConfiguration} on Gradle 8.4+ or {@literal Configuration} otherwise
	 */
	public <T extends Configuration> NamedDomainObjectProvider<T> resolvable(Object name) {
		return Registries.ResolvableConfiguration.register(configurations, name.toString());
	}

	private enum Registries implements Registry, Action<Configuration> {
		ConsumableConfiguration("consumable") {
			@Override
			public void execute(Configuration it) {
				it.setCanBeConsumed(true);
				it.setCanBeResolved(false);
			}
		},
		DependencyScopeConfiguration("dependencyScope") {
			@Override
			public void execute(Configuration it) {
				it.setCanBeConsumed(false);
				it.setCanBeResolved(false);
			}
		},
		ResolvableConfiguration("resolvable") {
			@Override
			public void execute(Configuration it) {
				it.setCanBeConsumed(false);
				it.setCanBeResolved(true);
			}
		};

		private final Registry delegate;

		Registries(String methodName) {
			delegate = tryFindCreateMethod(methodName).map(MethodBasedRegistry::new).map(Registry.class::cast).orElseGet(() -> new LegacyRegistry(this));
		}

		@Override
		public <T extends Configuration> NamedDomainObjectProvider<T> register(ConfigurationContainer self, String name) {
			return delegate.register(self, name);
		}
	}

	private interface Registry {
		<T extends Configuration> NamedDomainObjectProvider<T> register(ConfigurationContainer self, String name);
	}

	private static final class MethodBasedRegistry implements Registry {
		private final Method registryMethod;

		private MethodBasedRegistry(Method registryMethod) {
			this.registryMethod = registryMethod;
		}

		@Override
		public <T extends Configuration> NamedDomainObjectProvider<T> register(ConfigurationContainer self, String name) {
			try {
				@SuppressWarnings("unchecked")
				final NamedDomainObjectProvider<T> result = (NamedDomainObjectProvider<T>) registryMethod.invoke(self, name);
				return result;
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static final class LegacyRegistry implements Registry {
		private final Action<? super Configuration> defaultAction;

		private LegacyRegistry(Action<? super Configuration> defaultAction) {
			this.defaultAction = defaultAction;
		}

		@Override
		public <T extends Configuration> NamedDomainObjectProvider<T> register(ConfigurationContainer self, String name) {
			@SuppressWarnings("unchecked")
			final NamedDomainObjectProvider<T> result = (NamedDomainObjectProvider<T>) self.register(name);
			result.configure(defaultAction);
			return result;
		}
	}

	private static Optional<Method> tryFindCreateMethod(String methodName) {
		try {
			return Optional.of(ConfigurationContainer.class.getMethod(methodName, String.class));
		} catch (NoSuchMethodException e) {
			return Optional.empty();
		}
	}

	// TODO: Add toString() method
}
