package dev.nokee.commons.backports;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.runtime.HandleMetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.gradle.api.Action;
import org.gradle.api.NonExtensible;
import org.gradle.api.Project;
import org.gradle.api.Transformer;
import org.gradle.api.artifacts.*;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderConvertible;
import org.gradle.api.provider.SetProperty;
import org.gradle.util.GradleVersion;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents a bucket of dependencies of a component.
 *
 * <p>
 * Create your own instance of the factory using {@link org.gradle.api.model.ObjectFactory#newInstance(Class, Object...)}.
 * </p>
 */
@NonExtensible
public abstract /*final*/ class DependencyBucket {
	private final SetProperty<Dependency> dependencies;
	private final SetProperty<DependencyConstraint> dependencyConstraints;
	private final DependencyFactory dependencyFactory;

	/**
	 * Construct an instance.
	 *
	 * @param objects  the object factory for internal use
	 */
	@Inject
	public DependencyBucket(ObjectFactory objects) {
		this.dependencies = objects.setProperty(Dependency.class);
		this.dependencyConstraints = objects.setProperty(DependencyConstraint.class);
		this.dependencyFactory = objects.newInstance(DependencyFactory.class);
		this.mutabilityHandler = objects.newInstance(mutabilityHandlerType());

		this.dependencies.finalizeValueOnRead();
		this.dependencyConstraints.finalizeValueOnRead();
	}

	//region Ensure mutability for MinimalExternalModuleDependency
	private final MutabilityHandler mutabilityHandler;

	private static Class<? extends MutabilityHandler> mutabilityHandlerType() {
		if (GradleVersion.current().compareTo(GradleVersion.version("6.8")) >= 0) {
			return MinimalExternalModuleDependencyMutability.class;
		} else {
			return NoOpDependencyMutability.class;
		}
	}

	private <DependencyType extends Dependency> DependencyType ensureMutable(DependencyType dependency) {
		return mutabilityHandler.ensureMutable(dependency);
	}

	private interface MutabilityHandler {
		<DependencyType extends Dependency> DependencyType ensureMutable(DependencyType dependency);
	}

	@NonExtensible
	/*private*/ static /*final*/ class NoOpDependencyMutability implements MutabilityHandler {
		@Inject
		public NoOpDependencyMutability() {}

		@Override
		public <DependencyType extends Dependency> DependencyType ensureMutable(DependencyType dependency) {
			return dependency;
		}
	}

	@NonExtensible
	/*private*/ static abstract /*final*/ class MinimalExternalModuleDependencyMutability implements MutabilityHandler {
		private final DependencyHandler dependencies;

		@Inject
		public MinimalExternalModuleDependencyMutability(DependencyHandler dependencies) {
			this.dependencies = dependencies;
		}

		@SuppressWarnings("unchecked")
		public <DependencyType extends Dependency> DependencyType ensureMutable(DependencyType dependency) {
			// Only done to make MinimalExternalModuleDependency mutable for configuration
			return (DependencyType) dependencies.create(dependency);
		}
	}
	//endregion

	//region Dependency
	/**
	 * Add a dependency to this bucket.
	 *
	 * @param dependencyNotation  the dependency to add
	 * @see DependencyFactory#create(CharSequence) for valid dependency notation for this method
	 */
	public void addDependency(CharSequence dependencyNotation) {
		dependencies.add(dependencyFactory.create(dependencyNotation));
	}

	/**
	 * Add a dependency to this bucket and configure it.
	 *
	 * @param dependencyNotation  the dependency to add
	 * @param configureAction  an action to configure the dependency
	 * @see DependencyFactory#create(CharSequence) for valid dependency notation for this method
	 */
	public void addDependency(CharSequence dependencyNotation, Action<? super ExternalModuleDependency> configureAction) {
		dependencies.add((Dependency) peek(configureAction).transform(dependencyFactory.create(dependencyNotation)));
	}

	/**
	 * Add a dependency to this bucket and configure it.
	 *
	 * @param dependency  the dependency to add
	 */
	public void addDependency(Dependency dependency) {
		dependencies.add(dependency);
	}

	/**
	 * Add a dependency to this bucket and configure it.
	 *
	 * @param fileCollection  the files to add as a dependency
	 * @see DependencyFactory#create(FileCollection)
	 */
	public void addDependency(FileCollection fileCollection) {
		dependencies.add(dependencyFactory.create(fileCollection));
	}

	/**
	 * Add a dependency to this bucket and configure it.
	 *
	 * @param fileCollection  the files to add as a dependency
	 * @param configureAction  an action to configure the dependency
	 * @see DependencyFactory#create(FileCollection)
	 */
	public void addDependency(FileCollection fileCollection, Action<? super FileCollectionDependency> configureAction) {
		dependencies.add((Dependency) peek(configureAction).transform(dependencyFactory.create(fileCollection)));
	}

	/**
	 * Add a dependency to this bucket and configure it.
	 *
	 * @param dependency  the dependency to add
	 * @param configureAction  an action to configure the dependency
	 * @param <DependencyType>  the dependency type
	 */
	public <DependencyType extends Dependency> void addDependency(DependencyType dependency, Action<? super DependencyType> configureAction) {
		dependencies.add((Dependency) peek(configureAction).transform(ensureMutable(dependency)));
	}

	/**
	 * Add a dependency to this bucket and configure it, using a {@link Provider} to lazily create the dependency.
	 *
	 * @param dependencyProvider the dependency to add, may provide no value
	 * @param <DependencyType>  the dependency type
	 */
	public <DependencyType extends Dependency> void addDependency(Provider<DependencyType> dependencyProvider) {
		dependencies.addAll(asOptional(dependencyProvider));
	}

	/**
	 * Add a dependency to this bucket and configure it, using a {@link Provider} to lazily create the dependency.
	 *
	 * @param dependencyProvider  the dependency to add, may provide no value
	 * @param configureAction  an action to configure the dependency
	 * @param <DependencyType>  the dependency type
	 */
	public <DependencyType extends Dependency> void addDependency(Provider<DependencyType> dependencyProvider, Action<? super DependencyType> configureAction) {
		dependencies.addAll(asOptional(dependencyProvider.map(this::ensureMutable).map(peek(configureAction))));
	}

	/**
	 * Add a dependency to this bucket and configure it.
	 *
	 * @param dependencyProvider  the dependency to add, may provide no value
	 */
	public void addDependency(ProviderConvertible<? extends Dependency> dependencyProvider) {
		dependencies.addAll(asOptional(dependencyProvider.asProvider()));
	}

	/**
	 * Add a dependency to this bucket and configure it.
	 *
	 * @param dependencyProvider  the dependency to add, may provide no value
	 * @param configureAction  an action to configure the dependency
	 * @param <DependencyType>  the dependency type
	 */
	public <DependencyType extends Dependency> void addDependency(ProviderConvertible<DependencyType> dependencyProvider, Action<? super DependencyType> configureAction) {
		dependencies.addAll(asOptional(dependencyProvider.asProvider().map(this::ensureMutable).map(peek(configureAction))));
	}

	/**
	 * Add a dependency to this bucket and configure it.
	 *
	 * @param project  the project to add as a dependency
	 * @see DependencyFactory#create(Project)
	 */
	public void addDependency(Project project) {
		dependencies.add(dependencyFactory.create(project));
	}

	/**
	 * Add a dependency to this bucket and configure it.
	 *
	 * @param project  the project to add as a dependency
	 * @param configureAction  an action to configure the dependency
	 * @see DependencyFactory#create(Project)
	 */
	public void addDependency(Project project, Action<? super ProjectDependency> configureAction) {
		dependencies.add((Dependency) peek(configureAction).transform(dependencyFactory.create(project)));
	}
	//endregion

	//region Constraint
	/**
	 * Adds a dependency constraint to this bucket.
	 *
	 * @param dependencyConstraint  the dependency constraint to add
	 */
	public void addConstraint(DependencyConstraint dependencyConstraint) {
		dependencyConstraints.add(dependencyConstraint);
	}

	/**
	 * Adds a dependency constraint to this bucket and configure it.
	 *
	 * @param dependencyConstraint  the dependency constraint to add
	 * @param configureAction  an action to configure the dependency constraint
	 */
	public void addConstraint(DependencyConstraint dependencyConstraint, Action<? super DependencyConstraint> configureAction) {
		dependencyConstraints.add((DependencyConstraint) peek(configureAction).transform(dependencyConstraint));
	}

	/**
	 * Adds a dependency constraint to this bucket, using a {@link Provider} to lazily create the constraint.
	 *
	 * @param dependencyConstraint  the dependency constraint to add, may provide no value
	 */
	public void addConstraint(Provider<? extends DependencyConstraint> dependencyConstraint) {
		dependencyConstraints.addAll(asOptional(dependencyConstraint));
	}

	/**
	 * Adds a dependency constraint to this bucket and configure it, using a {@link Provider} to lazily create the constraint.
	 *
	 * @param dependencyConstraint  the dependency constraint to add, may provide no value
	 * @param configuration  an action to configure the dependency constraint
	 */
	public void addConstraint(Provider<? extends DependencyConstraint> dependencyConstraint, Action<? super DependencyConstraint> configuration) {
		dependencyConstraints.addAll(asOptional(dependencyConstraint.map(peek(configuration))));
	}
	//endregion

	//region Bundle
	/**
	 * Add a bundle to this bucket.
	 *
	 * @param bundle  the bundle to add
	 */
	public void addBundle(Iterable<? extends Dependency> bundle) {
		bundle.forEach(dependencies::add);
	}

	/**
	 * Add a bundle to this bucket and configure each dependency.
	 *
	 * @param bundle  the bundle to add
	 * @param configureAction  an action to configure each dependency in the bundle
	 * @param <DependencyType>  the dependency type
	 */
	public <DependencyType extends Dependency> void addBundle(Iterable<? extends DependencyType> bundle, Action<? super DependencyType> configureAction) {
		transformEach(peek(configureAction)).transform(transformEach((DependencyType it) -> ensureMutable(it)).transform(bundle)).forEach(it -> dependencies.add((Dependency) it));
	}

	/**
	 * Add a bundle to this bucket, using a {@link Provider} to lazily create the bundle.
	 *
	 * @param bundleProvider  the bundle to add, may provide no value
	 */
	public void addBundle(Provider<? extends Iterable<? extends Dependency>> bundleProvider) {
		dependencies.addAll(bundleProvider.map(toDependencyList()).orElse(Collections.emptyList()));
	}

	/**
	 * Add a bundle to this bucket and configure each dependency, using a {@link Provider} to lazily create the bundle.
	 *
	 * @param bundleProvider  the bundle to add, may provide no value
	 * @param configureAction  an action to configure each dependency in the bundle
	 * @param <DependencyType>  the dependency type
	 */
	public <DependencyType extends Dependency> void addBundle(Provider<? extends Iterable<? extends DependencyType>> bundleProvider, Action<? super DependencyType> configureAction) {
		dependencies.addAll(bundleProvider.map(transformEach(this::ensureMutable)).map(transformEach(peek(configureAction))).orElse(Collections.emptyList()));
	}

	/**
	 * Add a bundle to this bucket.
	 *
	 * @param bundleProvider  the bundle to add, may provide no value
	 */
	public void addBundle(ProviderConvertible<? extends Iterable<? extends Dependency>> bundleProvider) {
		dependencies.addAll(bundleProvider.asProvider().map(toDependencyList()).orElse(Collections.emptyList()));
	}

	/**
	 * Add a bundle to this bucket and configure each dependency.
	 *
	 * @param bundleProvider  the bundle to add, may provide no value
	 * @param configureAction  an action to configure each dependency in the bundle
	 * @param <DependencyType>  the dependency type
	 */
	public <DependencyType extends Dependency> void addBundle(ProviderConvertible<? extends Iterable<? extends DependencyType>> bundleProvider, Action<? super DependencyType> configureAction) {
		dependencies.addAll(bundleProvider.asProvider().map(transformEach(this::ensureMutable)).map(transformEach(peek(configureAction))).orElse(Collections.emptyList()));
	}
	//endregion

	private static <T> Transformer<T, T> peek(Action<? super T> action) {
		return it -> {
			action.execute(it);
			return it;
		};
	}

	private /*static*/ <OUT, IN> Transformer<List<OUT>, Iterable<? extends IN>> transformEach(Transformer<? extends OUT, ? super IN> mapper) {
		return it -> StreamSupport.stream(it.spliterator(), false).map(mapper::transform).collect(Collectors.toList());
	}

	private static <T> Provider<? extends Iterable<T>> asOptional(Provider<T> provider) {
		return provider.map(Collections::singletonList).orElse(Collections.emptyList());
	}

	private /*static*/ <T extends Dependency> Transformer<List<Dependency>, Iterable<? extends T>> toDependencyList() {
		return transformEach(Dependency.class::cast);
	}

	/**
	 * Returns all dependencies declared on this bucket.
	 *
	 * @return dependencies of this bucket
	 */
	public Provider<Set<Dependency>> getDependencies() {
		return dependencies;
	}

	/**
	 * Returns all dependency constraints declared on this bucket.
	 *
	 * @return dependency constraints of this bucket
	 */
	public Provider<Set<DependencyConstraint>> getDependencyConstraints() {
		return dependencyConstraints;
	}

	// TODO: Add toString() implementation
	// TODO: Add extension methods for Kotlin DSL

	/**
	 * Represents a factory for building dependency factory.
	 */
	@NonExtensible
	public static abstract /*final*/ class Factory {
		private final ObjectFactory objects;

		/**
		 * Constructs an instance.
		 *
		 * @param objects  the object factory for internal use
		 */
		@Inject
		public Factory(ObjectFactory objects) {
			this.objects = objects;
		}

		/**
		 * Creates a named dependency bucket.
		 *
		 * @param bucketName  the name of the dependency bucket
		 * @return a builder for the named bucket
		 */
		public Builder create(String bucketName) {
			return objects.newInstance(Builder.class, bucketName);
		}

		/**
		 * Represents a bucket being built.
		 * @see DependencyBucket
		 */
		public static abstract /*final*/ class Builder extends DependencyBucket {
			private final String bucketName;

			/**
			 * {@inheritDoc}
			 */
			@Inject
			public Builder(String bucketName, ObjectFactory objects) {
				super(objects);
				this.bucketName = bucketName;
			}

			private static final class MetaMethodClosure extends Closure {
				private final String bucketName;
				private final String method;

				public MetaMethodClosure(String bucketName, Object owner, MetaMethod method) {
					super(owner);
					this.bucketName = bucketName;
					this.method = method.getName();

					this.maximumNumberOfParameters = method.getParameterTypes().length;
					this.parameterTypes = method.getNativeParameterTypes();
				}

				@SuppressWarnings({"unchecked", "rawtypes"})
				private Object doCall(Object... arguments) {
					String method = this.method;
					Object[] parameters = arguments;
					if (parameters[0] instanceof Provider) {
						parameters = Arrays.copyOf(arguments, arguments.length);
						parameters[0] = ((Provider) parameters[0]).map(it -> {
							if (it instanceof Dependency) {
								return Collections.singletonList(it);
							}
							return it;
						});
						method = "addBundle";
					}
					return InvokerHelper.invokeMethod(((ExtensionAware) getDelegate()).getExtensions().getByName(bucketName), method, parameters);
				}
			}

			private static HandleMetaClass metaClassOf(Object object) {
				return Optional.ofNullable(((ExtensionAware) object).getExtensions().findByName("$__nk_metaClass"))
					.map(HandleMetaClass.class::cast)
					.orElseGet(() -> {
						HandleMetaClass result = new HandleMetaClass(((GroovyObject) object).getMetaClass(), object);
						((ExtensionAware) object).getExtensions().add("$__nk_metaClass", result);
						((GroovyObject) object).setMetaClass(result);
						return result;
					});
			}

			/**
			 * Registers bucket to specified object, necessary for Groovy DSL.
			 *
			 * @param object  the object to decorate
			 * @return this instance
			 */
			public Builder asExtension(Object object) {
				if (object instanceof ExtensionAware) {
					((ExtensionAware) object).getExtensions().add(bucketName, this);
				}

				if (object instanceof GroovyObject) {
					assert object instanceof ExtensionAware;

					final HandleMetaClass metaClass = metaClassOf(object);
					Stream.of(InvokerHelper.getMetaClass(DependencyBucket.class), InvokerHelper.getMetaClass(this))
						.flatMap(it -> Stream.concat(
							it.respondsTo(this, "addBundle").stream(),
							it.respondsTo(this, "addDependency").stream()
						))
						.forEach(m -> {
							metaClass.setProperty(bucketName, new MetaMethodClosure(bucketName, this, m));
						});
				}
				return this;
			}

			/**
			 * Connects bucket to specified configuration.
			 *
			 * @param configuration  the configuration to connect with the bucket
			 * @return this instance
			 */
			public Builder of(Configuration configuration) {
				// Note: This may not work because of badly behaving plugins (like Kotlin until version ???)
				configuration.getDependencies().addAllLater(getDependencies());
				configuration.getDependencyConstraints().addAllLater(getDependencyConstraints());
				return this;
			}

			@Override
			public String toString() {
				return "dependency bucket '" + bucketName + "'";
			}
		}
	}
}
