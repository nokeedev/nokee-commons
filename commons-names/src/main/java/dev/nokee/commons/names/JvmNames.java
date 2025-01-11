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

	public static class ForSourceSet extends ForwardingNames<ForSourceSet> implements Names {
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
	public static String compileOnlyApiConfigurationName(SourceSet sourceSet) {
		return of(sourceSet).compileOnlyApiConfigurationName().toString();
	}

	public static String groovydocJarTaskName(SourceSet sourceSet) {
		return of(sourceSet).groovydocJarTaskName().toString();
	}

	public static String groovydocTaskName(SourceSet sourceSet) {
		return of(sourceSet).groovydocTaskName().toString();
	}

	public static String groovydocElementsConfigurationName(SourceSet sourceSet) {
		return of(sourceSet).groovydocElementsConfigurationName().toString();
	}

	public static String pluginUnderTestMetadataTaskName(SourceSet sourceSet) {
		return of(sourceSet).pluginUnderTestMetadataTaskName().toString();
	}

	public static String compileKotlinTaskName(SourceSet sourceSet) {
		return of(sourceSet).compileKotlinTaskName().toString();
	}

	public static String compileGroovyTaskName(SourceSet sourceSet) {
		return of(sourceSet).compileGroovyTaskName().toString();
	}
	//endregion
}
