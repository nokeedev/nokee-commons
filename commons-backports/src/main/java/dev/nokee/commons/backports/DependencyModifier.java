package dev.nokee.commons.backports;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderConvertible;

import javax.inject.Inject;

public class DependencyModifier {
	private final DependencyFactory dependencies;
	private final DependencyCopier dependency;
	private final Action modifier;

	@Inject
	public DependencyModifier(ObjectFactory objects, Action modifier) {
		this.dependencies = objects.newInstance(DependencyFactory.class);
		this.dependency = objects.newInstance(DependencyCopier.class);
		this.modifier = modifier;
	}

	/**
	 * Create an {@link ExternalModuleDependency} from the given notation and modifies it to select the variant of the given module as described in the modifier action.
	 *
	 * @param dependencyNotation  the dependency notation
	 * @return the modified dependency
	 * @see DependencyFactory#create(CharSequence)
	 */
	public ExternalModuleDependency modify(CharSequence dependencyNotation) {
		return modify(dependencies.create(dependencyNotation));
	}

	/**
	 * Create an {@link ProjectDependency} from the given project and modifies it to select the variant of the given module as described in the modifier action.
	 *
	 * @param project  the project
	 * @return the modified dependency
	 * @see DependencyFactory#create(Project)
	 */
	public ProjectDependency modify(Project project) {
		return modify(dependencies.create(project));
	}

	/**
	 * Modify the specified {@code Provider} to a {@link ModuleDependency} and modifies the dependency to select the variant of the given module as described in the modifier action.
	 *
	 * @param dependencyProvider the provider
	 * @return a provider to the modified dependency
	 */
	public <DependencyType extends ModuleDependency> Provider<? extends DependencyType> modify(ProviderConvertible<? extends DependencyType> dependencyProvider) {
		return dependencyProvider.asProvider().map(this::modify);
	}

	/**
	 * Modify the specified {@code Provider} to a {@link ExternalModuleDependency} and modifies the dependency to select the variant of the given module as described in the modifier action.
	 *
	 * @param dependencyProvider  the provider
	 * @return a provider to the modified dependency
	 */
	public <DependencyType extends ModuleDependency> Provider<DependencyType> modify(Provider<DependencyType> dependencyProvider) {
		return dependencyProvider.map(this::modify);
	}

	/**
	 * Modify the specified {@link ModuleDependency} and modifies the dependency to select the variant of the given module as described in the modifier action.
	 * Dependency resolution may fail if the given module does not have a compatible variant.
	 *
	 * <p>
	 * The dependency will be copied, so the original dependency will not be modified.
	 * </p>
	 *
	 * @param dependency  the dependency to modify
	 * @param <DependencyType> the type of the {@link ModuleDependency}
	 * @return the modified dependency
	 */
	public <DependencyType extends ModuleDependency> DependencyType modify(DependencyType dependency) {
		// Enforce a copy of the dependency to avoid modifying the original dependency.
		DependencyType copy = this.dependency.copyOf(dependency);
		modifier.modify(copy);
		return copy;
	}

	/**
	 * Represent the modification action to apply on the specified dependency.
	 */
	@FunctionalInterface
	public interface Action {
		void modify(ModuleDependency dependency);
	}
}
