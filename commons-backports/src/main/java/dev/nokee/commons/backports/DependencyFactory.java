package dev.nokee.commons.backports;

import org.gradle.api.NonExtensible;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.artifacts.FileCollectionDependency;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.FileCollection;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Factory class for creating {@link Dependency} instances, with strong typing.
 *
 * <p>
 * Create your own instance of the factory using {@link org.gradle.api.model.ObjectFactory#newInstance(Class, Object...)}.
 * </p>
 *
 * <p>
 * <b>Note:</b> This class represent a backport of {@link org.gradle.api.artifacts.dsl.DependencyFactory} introduced in Gradle 7.6.
 * If you are targeting Gradle newer than 7.6, we recommend using the core API instead.
 * </p>
 */
@NonExtensible
public abstract /*final*/ class DependencyFactory {
	private final DependencyHandler delegate;

	@Inject
	public DependencyFactory(DependencyHandler delegate) {
		this.delegate = delegate;
	}

	/**
	 * Creates a {@link FileCollectionDependency} from the specified {@link FileCollection}.
	 *
	 * @param fileCollection  the file collection
	 * @return a new dependency instance
	 */
	public FileCollectionDependency create(FileCollection fileCollection) {
		return (FileCollectionDependency) delegate.create(fileCollection);
	}

	/**
	 * Creates an {@link ExternalModuleDependency} from the <code>"<i>group</i>:<i>name</i>:<i>version</i>:<i>classifier</i>@<i>extension</i>"</code> notation.
	 *
	 * <p>
	 * Classifier and extension may each separately be omitted.
	 * Version may be omitted if there is no classifier.
	 * </p>
	 *
	 * @param dependencyNotation the dependency notation
	 * @return the new dependency
	 */
	public ExternalModuleDependency create(CharSequence dependencyNotation) {
		return (ExternalModuleDependency) delegate.create(dependencyNotation);
	}

	/**
	 * Creates an {@link ExternalModuleDependency} from a series of strings.
	 *
	 * @param group  the group (optional)
	 * @param name  the name
	 * @param version  the version (optional)
	 * @return the new dependency instance
	 */
	public ExternalModuleDependency create(@Nullable String group, String name, @Nullable String version) {
		Map<String, Object> notation = new LinkedHashMap<>();
		notation.put("group", group);
		notation.put("name", name);
		notation.put("version", version);
		return (ExternalModuleDependency) delegate.create(notation);
	}

	/**
	 * Creates an {@link ExternalModuleDependency} from a series of strings.
	 *
	 * @param group the group (optional)
	 * @param name the name
	 * @param version the version (optional)
	 * @param classifier the classifier (optional)
	 * @param extension the extension (optional)
	 * @return the new dependency instance
	 */
	public ExternalModuleDependency create(@Nullable String group, @Nullable String name, @Nullable String version, @Nullable String classifier, @Nullable String extension) {
		Map<String, Object> notation = new LinkedHashMap<>();
		notation.put("group", group);
		notation.put("name", name);
		notation.put("version", version);
		notation.put("classifier", classifier);
		notation.put("ext", extension);
		return (ExternalModuleDependency) delegate.create(notation);
	}

	/**
	 * Creates an {@link ExternalModuleDependency} from map notation.
	 *
	 * <p>
	 * The supported keys:
	 * <ul>
	 *   <li>group: the group (optional)</li>
	 *   <li>name: the name</li>
	 *   <li>version: the version (optional)</li>
	 *   <li>classifier: the classifier (optional)</li>
	 *   <li>ext: the extension (optional)</li>
	 * </ul>
	 * </p>
	 *
	 * @return the new dependency instance
	 */
	public ExternalModuleDependency create(Map<String, ?> notation) {
		return (ExternalModuleDependency) delegate.create(notation);
	}

	/**
	 * Creates a {@link ProjectDependency} from the specified {@link Project}.
	 *
	 * @param project  the project
	 * @return a new dependency instance
	 */
	public ProjectDependency create(Project project) {
		return (ProjectDependency) delegate.create(project);
	}

	/**
	 * Creates a dependency on the API of the current version of Gradle.
	 *
	 * @return the dependency instance
	 */
	public Dependency gradleApi() {
		return delegate.gradleApi();
	}

	/**
	 * Creates a dependency on the <a href="https://docs.gradle.org/current/userguide/test_kit.html" target="_top">Gradle test-kit</a> API.
	 *
	 * @return the dependency instance
	 */
	public Dependency gradleTestKit() {
		return delegate.gradleTestKit();
	}

	/**
	 * Creates a dependency on the version of Groovy that is distributed with the current version of Gradle.
	 *
	 * @return the dependency instance
	 */
	public Dependency localGroovy() {
		return delegate.localGroovy();
	}

	// TODO: Add toString() method
}
