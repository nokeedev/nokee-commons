package dev.nokee.commons.backports;

import org.gradle.api.Action;
import org.gradle.api.NonExtensible;
import org.gradle.api.Project;
import org.gradle.api.Transformer;
import org.gradle.api.artifacts.*;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderConvertible;
import org.gradle.api.provider.SetProperty;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
	}

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
		dependencies.add((Dependency) peek(configureAction).transform(dependency));
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
		dependencies.addAll(asOptional(dependencyProvider.map(peek(configureAction))));
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
		dependencies.addAll(asOptional(dependencyProvider.asProvider().map(peek(configureAction))));
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
	 * @param configuration  an action to configure the dependency constraint
	 */
	public void addConstraint(DependencyConstraint dependencyConstraint, Action<? super DependencyConstraint> configuration) {
		configuration.execute(dependencyConstraint);
		addConstraint(dependencyConstraint);
	}

	/**
	 * Adds a dependency constraint to this bucket, using a {@link Provider} to lazily create the constraint.
	 *
	 * @param dependencyConstraint  the dependency constraint to add
	 */
	public void addConstraint(Provider<? extends DependencyConstraint> dependencyConstraint) {
		dependencyConstraints.add(dependencyConstraint);
	}

	/**
	 * Adds a dependency constraint to this bucket and configure it, using a {@link Provider} to lazily create the constraint.
	 *
	 * @param dependencyConstraint  the dependency constraint to add
	 * @param configuration  an action to configure the dependency constraint
	 */
	public void addConstraint(Provider<? extends DependencyConstraint> dependencyConstraint, Action<? super DependencyConstraint> configuration) {
		addConstraint(dependencyConstraint.map(peek(configuration)));
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
		transformEach(peek(configureAction)).transform(bundle).forEach(it -> dependencies.add((Dependency) it));
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
		dependencies.addAll(bundleProvider.map(transformEach(peek(configureAction))).orElse(Collections.emptyList()));
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
		dependencies.addAll(bundleProvider.asProvider().map(transformEach(peek(configureAction))).orElse(Collections.emptyList()));
	}
	//endregion

	private static <T> Transformer<T, T> peek(Action<? super T> action) {
		return it -> {
			action.execute(it);
			return it;
		};
	}

	private static <OUT, IN> Transformer<List<OUT>, Iterable<? extends IN>> transformEach(Transformer<? extends OUT, ? super IN> mapper) {
		return it -> StreamSupport.stream(it.spliterator(), false).map(mapper::transform).collect(Collectors.toList());
	}

	private static <T> Provider<? extends Iterable<T>> asOptional(Provider<T> provider) {
		return provider.map(Collections::singletonList).orElse(Collections.emptyList());
	}

	private static <T extends Dependency> Transformer<List<Dependency>, Iterable<? extends T>> toDependencyList() {
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
	// TODO: Add decorator method for Groovy DSL
	// TODO: Add extension methods for Kotlin DSL
}
