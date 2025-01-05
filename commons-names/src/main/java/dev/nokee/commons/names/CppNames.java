package dev.nokee.commons.names;

import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.CppComponent;

import static dev.nokee.commons.names.StringUtils.uncapitalize;

public final class CppNames {
	private static final CppNames INSTANCE = new CppNames();

	public static ForBinary of(CppBinary binary) {
		return new ForBinary(binary);
	}

	public static ForComponent of(CppComponent component) {
		return new ForComponent(component);
	}

	// TODO: implements ConfigurationName
	private static class IncomingConfigurationName extends NameSupport implements ElementName {
		private final String value;

		private IncomingConfigurationName(String value) {
			this.value = value;
		}

		@Override
		public FullyQualifiedName qualifiedBy(Qualifier qualifier) {
			return new FullyQualifiedName() {
				@Override
				public String toString() {
					return NameBuilder.toStringCase().append(value).append(qualifier).toString();
				}
			};
		}

		@Override
		public String toString() {
			return value;
		}
	}

	public static class ForBinary extends NameSupport implements Names {
		private final CppBinary binary;

		private ForBinary(CppBinary binary) {
			this.binary = binary;
		}

		public String linkElementsConfigurationName() {
			// <qualifyingName>LinkElements
			//   releaseLinkElements
			//   linuxDebugLinkElements
			// Note: does not exists for CppTestExecutable
			return INSTANCE.linkElementsConfigurationName().forBinary(binary).toString();
		}

		public String runtimeElementsConfigurationName() {
			// <qualifyingName>RuntimeElements
			//   releaseRuntimeElements
			//   linuxDebugRuntimeElements
			// Note: does not exists for CppTestExecutable
			return INSTANCE.runtimeElementsConfigurationName().forBinary(binary).toString();
		}

		public String nativeLinkConfigurationName() {
			// nativeLink<qualifyingName>
			//   nativeLinkRelease
			//   nativeLinkLinuxDebug
			//   nativeLinkTest
			return INSTANCE.nativeLinkConfigurationName().forBinary(binary).toString();
		}

		public String nativeRuntimeConfigurationName() {
			// nativeRuntime<qualifyingName>
			//   nativeRuntimeRelease
			//   nativeRuntimeLinuxDebug
			//   nativeRuntimeTest
			return INSTANCE.nativeRuntimeConfigurationName().forBinary(binary).toString();
		}

		public String compileTaskName() {
			// compile<qualifyingName>Cpp
			//   compileReleaseCpp
			//   compileLinuxDebugCpp
			//   compileTestCpp
			return INSTANCE.compileTaskName().forBinary(binary).toString();
		}

		public String compileTaskName(String language) {
			// compile<qualifyingName><language>
			// Note: for convenience only
			return INSTANCE.compileTaskName(language).forBinary(binary).toString();
		}

		public String linkTaskName() {
			// link<qualifyingName>
			//   linkRelease
			//   linkLinuxRelease
			//   linkTest
			return INSTANCE.linkTaskName().forBinary(binary).toString();
		}

		public String implementationConfigurationName() {
			// <qualifyingName>Implementation
			//   mainReleaseImplementation
			//   mainLinuxDebugImplementation
			//   testExecutableImplementation - for test executable
			// Essentially: <binaryName>Implementation
			return INSTANCE.implementationConfigurationName().forBinary(binary).toString();
		}

		public String cppCompileConfigurationName() {
			// cppCompile<qualifyingName>
			//   cppCompileRelease
			//   cppCompileLinuxDebug
			//   cppCompileTest
			return INSTANCE.cppCompileConfigurationName().forBinary(binary).toString();
		}

		@Override
		public void appendTo(NameBuilder sb) {
			qualifyingName(binary).appendTo(sb);
		}

		@Override
		public void accept(Visitor visitor) {
			qualifyingName(binary).accept(visitor);
		}

		@Override
		public String toString(NamingScheme scheme) {
			return qualifyingName(binary).toString(scheme);
		}

		@Override
		public String toString() {
			return toString(NamingScheme.lowerCamelCase());
		}
	}

	public static final class ForComponent extends NameSupport implements Names {
		private final CppComponent component;

		private ForComponent(CppComponent component) {
			this.component = component;
		}

		public String cppApiElementsConfigurationName() {
			// <qualifyingName>CppApiElements
			//   cppApiElements
			// Note: does not exists for CppTestExecutable, for convenience only
			return INSTANCE.cppApiElementsConfigurationName().forComponent(component).toString();
		}

		public String implementationConfigurationName() {
			// <qualifyingName>Implementation
			//   implementation - for ProductionCppComponent
			//   testImplementation - for CppTestSuite
			return INSTANCE.implementationConfigurationName().forComponent(component).toString();
		}

		public String apiConfigurationName() {
			// <qualifyingName>Api
			//   api - for CppLibrary
			// Note: does not exists for anything else, for convenience only
			return INSTANCE.apiConfigurationName().forComponent(component).toString();
		}

		@Override
		public void appendTo(NameBuilder sb) {
			qualifyingName(component).appendTo(sb);
		}

		@Override
		public void accept(Visitor visitor) {
			qualifyingName(component).accept(visitor);
		}

		@Override
		public String toString(NamingScheme scheme) {
			return qualifyingName(component).toString(scheme);
		}

		@Override
		public String toString() {
			return toString(NamingScheme.lowerCamelCase());
		}
	}

	public static Qualifier qualifyingName(CppComponent component) {
		Qualifier result = Qualifiers.of(component.getName());
		if (component.getName().equals("main")) {
			result = Qualifiers.ofMain(result);
		}
		return result;
	}

	public static Qualifier qualifyingName(CppBinary binary) {
		String result = binary.getName();
		if (result.startsWith("main")) {
			result = uncapitalize(result.substring("main".length()));
		} else if (result.endsWith("Executable")) {
			result = result.substring(0, result.length() - "Executable".length());
		}
		return Qualifiers.of(result);
	}

	//region Name builders
	public interface ForComponentBuilder {
		FullyQualifiedName forComponent(CppComponent component);
	}

	public interface ForBinaryBuilder {
		FullyQualifiedName forBinary(CppBinary binary);
	}

	public ForComponentBuilder cppApiElementsConfigurationName() {
		return component -> ElementName.configurationName("cppApiElements").qualifiedBy(qualifyingName(component));
	}

	public interface ImplementationConfigurationNameBuilder extends ForComponentBuilder, ForBinaryBuilder {}

	public ImplementationConfigurationNameBuilder implementationConfigurationName() {
		return new ImplementationConfigurationNameBuilder() {
			@Override
			public FullyQualifiedName forBinary(CppBinary binary) {
				return ElementName.configurationName("implementation").qualifiedBy(Qualifiers.of(binary.getName()));
			}

			@Override
			public FullyQualifiedName forComponent(CppComponent component) {
				return ElementName.configurationName("implementation").qualifiedBy(qualifyingName(component));
			}
		};
	}

	public ForComponentBuilder apiConfigurationName() {
		return component -> ElementName.configurationName("api").qualifiedBy(qualifyingName(component));
	}

	public ForBinaryBuilder linkElementsConfigurationName() {
		return binary -> ElementName.configurationName("linkElements").qualifiedBy(qualifyingName(binary));
	}

	public ForBinaryBuilder runtimeElementsConfigurationName() {
		return binary -> ElementName.configurationName("runtimeElements").qualifiedBy(qualifyingName(binary));
	}

	public ForBinaryBuilder nativeLinkConfigurationName() {
		return binary -> new IncomingConfigurationName("nativeLink").qualifiedBy(qualifyingName(binary));
	}

	public ForBinaryBuilder nativeRuntimeConfigurationName() {
		return binary -> new IncomingConfigurationName("nativeRuntime").qualifiedBy(qualifyingName(binary));
	}

	public ForBinaryBuilder cppCompileConfigurationName() {
		return binary -> new IncomingConfigurationName("cppCompile").qualifiedBy(qualifyingName(binary));
	}

	public ForBinaryBuilder compileTaskName() {
		return binary -> ElementName.taskName("compile", "cpp").qualifiedBy(qualifyingName(binary));
	}

	public ForBinaryBuilder compileTaskName(String language) {
		return binary -> ElementName.taskName("compile", language).qualifiedBy(qualifyingName(binary));
	}

	public ForBinaryBuilder linkTaskName() {
		return binary -> ElementName.taskName("link").qualifiedBy(qualifyingName(binary));
	}
	//endregion

	//region C++ component names
	public static String cppApiElementsConfigurationName(CppComponent component) {
		return INSTANCE.cppApiElementsConfigurationName().forComponent(component).toString();
	}

	public static String implementationConfigurationName(CppComponent component) {
		return INSTANCE.implementationConfigurationName().forComponent(component).toString();
	}

	public static String implementationConfigurationName(CppBinary binary) {
		return INSTANCE.implementationConfigurationName().forBinary(binary).toString();
	}

	public static String apiConfigurationName(CppComponent component) {
		return INSTANCE.apiConfigurationName().forComponent(component).toString();
	}
	//endregion

	//region C++ binary names
	public static String linkElementsConfigurationName(CppBinary binary) {
		return INSTANCE.linkElementsConfigurationName().forBinary(binary).toString();
	}

	public static String runtimeElementsConfigurationName(CppBinary binary) {
		return INSTANCE.runtimeElementsConfigurationName().forBinary(binary).toString();
	}

	public static String nativeLinkConfigurationName(CppBinary binary) {
		return INSTANCE.nativeLinkConfigurationName().forBinary(binary).toString();
	}

	public static String nativeRuntimeConfigurationName(CppBinary binary) {
		return INSTANCE.nativeRuntimeConfigurationName().forBinary(binary).toString();
	}

	public static String compileTaskName(CppBinary binary) {
		return INSTANCE.compileTaskName().forBinary(binary).toString();
	}

	public String compileTaskName(CppBinary binary, String language) {
		return INSTANCE.compileTaskName(language).forBinary(binary).toString();
	}

	public String linkTaskName(CppBinary binary) {
		return INSTANCE.linkTaskName().forBinary(binary).toString();
	}

	public String cppCompileConfigurationName(CppBinary binary) {
		return INSTANCE.cppCompileConfigurationName().forBinary(binary).toString();
	}
	//endregion
}
