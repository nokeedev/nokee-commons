package dev.nokee.commons.names;

import org.gradle.api.tasks.SourceSet;

public class JvmNames {
	public static ForSourceSet of(SourceSet sourceSet) {
		if (isMain(sourceSet)) {
			return new ForSourceSet(Names.ofMain());
		}
		return new ForSourceSet(Names.of(sourceSet.getName()));
	}

	// backport of SourceSet#isMain(SourceSet)
	private static boolean isMain(SourceSet sourceSet) {
		return sourceSet.getName().equals("main");
	}

	public static class ForSourceSet extends NameSupport<ForSourceSet> implements Names {
		private final Names delegate;

		public ForSourceSet(Names delegate) {
			this.delegate = delegate;
		}

		public String annotationProcessorConfigurationName() {
			return configurationName("annotationProcessor");
		}

		public String apiConfigurationName() {
			return configurationName("api");
		}

		public String apiElementsConfigurationName() {
			return configurationName("apiElements");
		}

		public String classesTaskName() {
			return taskName(it -> it.forObject("classes"));
		}

		public String compileClasspathConfigurationName() {
			return configurationName("compileClasspath");
		}

		public String compileGroovyTaskName() {
			return taskName("compile", "groovy");
		}

		public String compileJavaTaskName() {
			return taskName("compile", "java");
		}

		public String compileKotlinTaskName() {
			return taskName("compile", "kotlin");
		}

		public String compileOnlyApiConfigurationName() {
			return configurationName("compileOnlyApi");
		}

		public String compileOnlyConfigurationName() {
			return configurationName("compileOnly");
		}

		public String compileTaskName(String language) {
			return taskName("compile", language);
		}

		public String groovydocElementsConfigurationName() {
			return configurationName("groovydocElements");
		}

		public String groovydocJarTaskName() {
			return taskName(it -> it.forObject("groovydocJar"));
		}

		public String groovydocTaskName() {
			return taskName(it -> it.forObject("groovydoc"));
		}

		public String implementationConfigurationName() {
			return configurationName("implementation");
		}

		public String jarTaskName() {
			return taskName(it -> it.forObject( "jar"));
		}

		public String javadocElementsConfigurationName() {
			return configurationName("javadocElements");
		}

		public String javadocJarTaskName() {
			return taskName(it -> it.forObject("javadocJar"));
		}

		public String javadocTaskName() {
			return taskName(it -> it.forObject("javadoc"));
		}

		public String pluginUnderTestMetadataTaskName() {
			return ElementName.taskName().forObject("pluginUnderTestMetadata").qualifiedBy(this).toString();
		}

		public String processResourcesTaskName() {
			return taskName("process", "resources");
		}

		public String runtimeClasspathConfigurationName() {
			return configurationName("runtimeClasspath");
		}

		public String runtimeElementsConfigurationName() {
			return configurationName("runtimeElements");
		}

		public String runtimeOnlyConfigurationName() {
			return configurationName("runtimeOnly");
		}

		public String sourcesElementsConfigurationName() {
			return configurationName("sourceElements");
		}

		public String sourcesJarTaskName() {
			return taskName(it -> it.forObject("sourcesJar"));
		}

		@Override
		public String toString() {
			return delegate.toString();
		}

		@Override
		public void appendTo(NameBuilder builder) {
			delegate.appendTo(builder);
		}

		@Override
		public String toString(NameBuilder builder) {
			return delegate.toString(builder);
		}
	}

	//region JVM names focusing on backport and non-modeled names
	public static String compileOnlyApiConfigurationName(SourceSet sourceSet) {
		return of(sourceSet).compileOnlyApiConfigurationName();
	}

	public static String groovydocJarTaskName(SourceSet sourceSet) {
		return of(sourceSet).groovydocJarTaskName();
	}

	public static String groovydocTaskName(SourceSet sourceSet) {
		return of(sourceSet).compileGroovyTaskName();
	}

	public static String pluginUnderTestMetadataTaskName(SourceSet sourceSet) {
		return of(sourceSet).pluginUnderTestMetadataTaskName();
	}

	public static String compileKotlinTaskName(SourceSet sourceSet) {
		return of(sourceSet).compileKotlinTaskName();
	}

	public static String compileGroovyTaskName(SourceSet sourceSet) {
		return of(sourceSet).compileGroovyTaskName();
	}
	//endregion
}
