package dev.nokee.commons.names;

import org.gradle.api.tasks.SourceSet;

/**
 * Task and configuration names for {@literal java}, {@literal java-library}, {@literal groovy}, and Kotlin plugins.
 */
public class JvmNames {
	/*public*/ static ForSourceSet of(SourceSet sourceSet) {
		if (isMain(sourceSet)) {
			return new ForSourceSet(Names.ofMain());
		}
		return new ForSourceSet(Names.of(sourceSet.getName()));
	}

	// Backport of SourceSet#isMain(SourceSet)
	private static boolean isMain(SourceSet sourceSet) {
		return sourceSet.getName().equals("main");
	}

	/*public*/ static class ForSourceSet extends ForwardingNames<ForSourceSet> implements Names {
		private final Names delegate;

		public ForSourceSet(Names delegate) {
			this.delegate = delegate;
		}

		public FullyQualifiedName annotationProcessorConfigurationName() {
			return configurationName("annotationProcessor");
		}

		public FullyQualifiedName apiConfigurationName() {
			return configurationName("api");
		}

		public FullyQualifiedName apiElementsConfigurationName() {
			return configurationName("apiElements");
		}

		public FullyQualifiedName classesTaskName() {
			return taskName(it -> it.forObject("classes"));
		}

		public FullyQualifiedName compileClasspathConfigurationName() {
			return configurationName("compileClasspath");
		}

		public FullyQualifiedName compileGroovyTaskName() {
			return taskName("compile", "groovy");
		}

		public FullyQualifiedName compileJavaTaskName() {
			return taskName("compile", "java");
		}

		public FullyQualifiedName compileKotlinTaskName() {
			return taskName("compile", "kotlin");
		}

		public FullyQualifiedName compileOnlyApiConfigurationName() {
			return configurationName("compileOnlyApi");
		}

		public FullyQualifiedName compileOnlyConfigurationName() {
			return configurationName("compileOnly");
		}

		public FullyQualifiedName compileTaskName(String language) {
			return taskName("compile", language);
		}

		public FullyQualifiedName groovydocElementsConfigurationName() {
			return configurationName("groovydocElements");
		}

		public FullyQualifiedName groovydocJarTaskName() {
			return taskName(it -> it.forObject("groovydocJar"));
		}

		public FullyQualifiedName groovydocTaskName() {
			return taskName(it -> it.forObject("groovydoc"));
		}

		public FullyQualifiedName implementationConfigurationName() {
			return configurationName("implementation");
		}

		public FullyQualifiedName jarTaskName() {
			return taskName(it -> it.forObject( "jar"));
		}

		public FullyQualifiedName javadocElementsConfigurationName() {
			return configurationName("javadocElements");
		}

		public FullyQualifiedName javadocJarTaskName() {
			return taskName(it -> it.forObject("javadocJar"));
		}

		public FullyQualifiedName javadocTaskName() {
			return taskName(it -> it.forObject("javadoc"));
		}

		public FullyQualifiedName pluginUnderTestMetadataTaskName() {
			return taskName(it -> it.forObject("pluginUnderTestMetadata"));
		}

		public FullyQualifiedName processResourcesTaskName() {
			return taskName("process", "resources");
		}

		public FullyQualifiedName runtimeClasspathConfigurationName() {
			return configurationName("runtimeClasspath");
		}

		public FullyQualifiedName runtimeElementsConfigurationName() {
			return configurationName("runtimeElements");
		}

		public FullyQualifiedName runtimeOnlyConfigurationName() {
			return configurationName("runtimeOnly");
		}

		public FullyQualifiedName sourcesElementsConfigurationName() {
			return configurationName("sourcesElements");
		}

		public FullyQualifiedName sourcesJarTaskName() {
			return taskName(it -> it.forObject("sourcesJar"));
		}

		@Override
		protected Names delegate() {
			return delegate;
		}
	}

	//region JVM names focusing on backport and non-modeled names
	/**
	 * Returns the <code><i>qualifyingName</i>CompileOnlyApi</code> configuration name.
	 * For example:
	 * <ul>
	 *   <li>compileOnlyApi</li>
	 * </ul>
	 *
	 * <p><b>Note 1:</b> only exists for {@literal java-library} project.</p>
	 * <p><b>Note 2:</b> From Gradle 6.7 and later, use {@link SourceSet#getCompileOnlyApiConfigurationName()}.</p>
	 *
	 * @param sourceSet  the source set object that qualify the configuration name, must not be null
	 * @return a configuration name
	 */
	public static String compileOnlyApiConfigurationName(SourceSet sourceSet) {
		return of(sourceSet).compileOnlyApiConfigurationName().toString();
	}

	/**
	 * Returns the <code><i>qualifyingName</i>GroovydocJar</code> task name.
	 * For example:
	 * <ul>
	 *   <li>groovydocJar</li>
	 *   <li><u>test</u>GroovydocJar</li>
	 * </ul>
	 *
	 * <p><b>Note:</b> does not exist on {@literal groovy} project, for convenience only.</p>
	 *
	 * @param sourceSet  the source set object that qualify the task name, must not be null
	 * @return a task name
	 */
	public static String groovydocJarTaskName(SourceSet sourceSet) {
		return of(sourceSet).groovydocJarTaskName().toString();
	}

	/**
	 * Returns the <code><i>qualifyingName</i>Groovydoc</code> task name.
	 * For example:
	 * <ul>
	 *   <li>groovydoc</li>
	 *   <li><u>test</u>Groovydoc</li>
	 * </ul>
	 *
	 * <p><b>Note:</b> exists only on {@literal groovy} project.</p>
	 *
	 * @param sourceSet  the source set object that qualify the task name, must not be null
	 * @return a task name
	 */
	public static String groovydocTaskName(SourceSet sourceSet) {
		return of(sourceSet).groovydocTaskName().toString();
	}

	/**
	 * Returns the <code><i>qualifyingName</i>GroovydocElements</code> configuration name.
	 * For example:
	 * <ul>
	 *   <li>groovydocElements</li>
	 *   <li><u>test</u>GroovydocElements</li>
	 * </ul>
	 *
	 * <p><b>Note:</b> does not exist on {@literal groovy} project, for convenience only and symmetry with {@link SourceSet#getJavadocElementsConfigurationName()}</p>
	 *
	 * @param sourceSet  the source set object that qualify the configuration name, must not be null
	 * @return a configuration name
	 */
	public static String groovydocElementsConfigurationName(SourceSet sourceSet) {
		return of(sourceSet).groovydocElementsConfigurationName().toString();
	}

	/**
	 * Returns the <code>pluginUnderTestMetadata</code> task name.
	 * <p><b>Note:</b> exist only for {@literal java-gradle-plugin} project.</p>
	 *
	 * @return a task name
	 */
	public static String pluginUnderTestMetadataTaskName() {
		return ElementName.taskName().forObject("pluginUnderTestMetadata").toString();
	}

	/**
	 * Returns the <code>compile<i>QualifyingName</i>Kotlin</code> task name.
	 * For example:
	 * <ul>
	 *   <li>compileKotlin</li>
	 *   <li>compile<u>Test</u>Kotlin</li>
	 * </ul>
	 * <p><b>Note:</b> exist only for Kotlin project, for convenience only.</p>
	 *
	 * @return a task name
	 */
	public static String compileKotlinTaskName(SourceSet sourceSet) {
		return of(sourceSet).compileKotlinTaskName().toString();
	}

	/**
	 * Returns the <code>compile<i>QualifyingName</i>Groovy</code> task name.
	 * For example:
	 * <ul>
	 *   <li>compileGroovy</li>
	 *   <li>compile<u>Test</u>Groovy</li>
	 * </ul>
	 * <p><b>Note:</b> exist only for {@literal groovy} project, for convenience only.</p>
	 *
	 * @return a task name
	 */
	public static String compileGroovyTaskName(SourceSet sourceSet) {
		return of(sourceSet).compileGroovyTaskName().toString();
	}
	//endregion
}
