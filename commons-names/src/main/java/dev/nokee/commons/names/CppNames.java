package dev.nokee.commons.names;

import org.gradle.api.Named;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.CppComponent;
import org.gradle.language.cpp.CppSharedLibrary;
import org.gradle.language.cpp.CppStaticLibrary;
import org.gradle.nativeplatform.test.cpp.CppTestExecutable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static dev.nokee.commons.names.StringUtils.uncapitalize;
import static org.gradle.nativeplatform.MachineArchitecture.X86;
import static org.gradle.nativeplatform.MachineArchitecture.X86_64;
import static org.gradle.nativeplatform.OperatingSystemFamily.*;

public final class CppNames {
	private static final CppNames INSTANCE = new CppNames();

	public static ForBinary of(CppBinary binary) {
		return new ForBinary(binary);
	}

	public static ForComponent of(CppComponent component) {
		return new ForComponent(component);
	}

	public static class ForBinary extends ForwardingNames<ForBinary> implements Names {
		private final Names delegate;
		private final CppBinary binary;

		private ForBinary(CppBinary binary) {
			this.binary = binary;
			this.delegate = new DefaultNames(qualifyingName(binary));
		}

		public FullyQualifiedName linkElementsConfigurationName() {
			// <qualifyingName>LinkElements
			//   releaseLinkElements
			//   linuxDebugLinkElements
			// Note: does not exists for CppTestExecutable
			return INSTANCE.linkElementsConfigurationName().forBinary(binary);
		}

		public FullyQualifiedName runtimeElementsConfigurationName() {
			// <qualifyingName>RuntimeElements
			//   releaseRuntimeElements
			//   linuxDebugRuntimeElements
			// Note: does not exists for CppTestExecutable
			return INSTANCE.runtimeElementsConfigurationName().forBinary(binary);
		}

		public FullyQualifiedName nativeLinkConfigurationName() {
			// nativeLink<qualifyingName>
			//   nativeLinkRelease
			//   nativeLinkLinuxDebug
			//   nativeLinkTest
			return INSTANCE.nativeLinkConfigurationName().forBinary(binary);
		}

		public FullyQualifiedName nativeRuntimeConfigurationName() {
			// nativeRuntime<qualifyingName>
			//   nativeRuntimeRelease
			//   nativeRuntimeLinuxDebug
			//   nativeRuntimeTest
			return INSTANCE.nativeRuntimeConfigurationName().forBinary(binary);
		}

		public FullyQualifiedName compileTaskName() {
			// compile<qualifyingName>Cpp
			//   compileReleaseCpp
			//   compileLinuxDebugCpp
			//   compileTestCpp
			return INSTANCE.compileTaskName().forBinary(binary);
		}

		public FullyQualifiedName compileTaskName(String language) {
			// compile<qualifyingName><language>
			// Note: for convenience only
			return INSTANCE.compileTaskName(language).forBinary(binary);
		}

		public FullyQualifiedName linkTaskName() {
			// link<qualifyingName>
			//   linkRelease
			//   linkLinuxRelease
			//   linkTest
			return INSTANCE.linkTaskName().forBinary(binary);
		}

		public FullyQualifiedName implementationConfigurationName() {
			// <qualifyingName>Implementation
			//   mainReleaseImplementation
			//   mainLinuxDebugImplementation
			//   testExecutableImplementation - for test executable
			// Essentially: <binaryName>Implementation
			return INSTANCE.implementationConfigurationName().forBinary(binary);
		}

		public FullyQualifiedName cppCompileConfigurationName() {
			// cppCompile<qualifyingName>
			//   cppCompileRelease
			//   cppCompileLinuxDebug
			//   cppCompileTest
			return INSTANCE.cppCompileConfigurationName().forBinary(binary);
		}

		@Override
		protected Names delegate() {
			return delegate;
		}
	}

	public static final class ForComponent extends ForwardingNames<ForComponent> implements Names {
		private final Names delegate;
		private final CppComponent component;

		private ForComponent(CppComponent component) {
			this.component = component;
			this.delegate = new DefaultNames(qualifyingName(component));
		}

		public FullyQualifiedName cppApiElementsConfigurationName() {
			// <qualifyingName>CppApiElements
			//   cppApiElements
			// Note: does not exists for CppTestExecutable, for convenience only
			return INSTANCE.cppApiElementsConfigurationName().forComponent(component);
		}

		public FullyQualifiedName implementationConfigurationName() {
			// <qualifyingName>Implementation
			//   implementation - for ProductionCppComponent
			//   testImplementation - for CppTestSuite
			return INSTANCE.implementationConfigurationName().forComponent(component);
		}

		public FullyQualifiedName apiConfigurationName() {
			// <qualifyingName>Api
			//   api - for CppLibrary
			// Note: does not exists for anything else, for convenience only
			return INSTANCE.apiConfigurationName().forComponent(component);
		}

		@Override
		protected Names delegate() {
			return delegate;
		}
	}

	private static class NameSegmentIterator {
		private String name;

		private NameSegmentIterator(Named obj) {
			this.name = obj.getName();
		}

		public Optional<String> consumeNext(String... words) {
			for (String word : words) {
				if (name.startsWith(word)) {
					name = uncapitalize(name.substring(word.length()));
					return Optional.of(word);
				}
			}
			return Optional.empty();
		}
	}

	public static QualifyingName qualifyingName(Named componentOrBinary) {
		final NameSegmentIterator iter = new NameSegmentIterator(componentOrBinary);
		Names result = null;

		result = iter.consumeNext("main", "test").map(it -> {
			if (it.equals("main")) {
				return Names.ofMain();
			} else if (it.equals("test")) {
				return Names.of("test");
			}
			return null;
		}).orElseThrow(() -> new IllegalStateException("Could not find main or test"));

		List<NameString> binaryName = new ArrayList<>();
		or(iter.consumeNext("debug", "release").map(NameString::of), () -> {
			if (componentOrBinary instanceof CppTestExecutable) {
				final CppBinary binary = (CppBinary) componentOrBinary;
				if (binary.isOptimized()) {
					return Optional.of(NameString.ofMain(NameString.of("release")));
				} else {
					return Optional.of(NameString.ofMain(NameString.of("debug")));
				}
			}
			return Optional.of(NameString.empty());
		}).map(it -> NameString.of("buildTypeName", it)).ifPresent(binaryName::add);

		or(iter.consumeNext("shared", "static").map(NameString::of), () -> {
			if (componentOrBinary instanceof CppSharedLibrary) {
				return Optional.of(NameString.ofMain(NameString.of("shared")));
			} else if (componentOrBinary instanceof CppStaticLibrary) {
				return Optional.of(NameString.ofMain(NameString.of("static")));
			}
			return Optional.of(NameString.empty());
		}).map(it -> NameString.of("linkageName", it)).ifPresent(binaryName::add);

		or(iter.consumeNext(LINUX, MACOS, WINDOWS).map(NameString::of), () -> {
			if (componentOrBinary instanceof CppBinary) {
				final CppBinary binary = (CppBinary) componentOrBinary;
				return Optional.of(NameString.ofMain(NameString.of(binary.getTargetMachine().getOperatingSystemFamily().getName())));
			}
			return Optional.of(NameString.empty());
		}).map(it -> NameString.of("osFamilyName", it)).ifPresent(binaryName::add);

		or(iter.consumeNext(X86, X86_64, "aarch64").map(NameString::of), () -> {
			if (componentOrBinary instanceof CppBinary) {
				final CppBinary binary = (CppBinary) componentOrBinary;
				return Optional.of(NameString.ofMain(NameString.of(binary.getTargetMachine().getArchitecture().getName())));
			}
			return Optional.of(NameString.empty());
		}).map(it -> NameString.of("architectureName", it)).ifPresent(binaryName::add);

		if (binaryName.isEmpty()) {
			return result;
		}
		return result.append(new OtherElementName(NameString.of(binaryName)));
	}

	// Backport of Optional#or(Supplier)
	private static <T> Optional<T> or(Optional<T> self, Supplier<? extends Optional<? extends T>> supplier) {
		Objects.requireNonNull(supplier);
		if (self.isPresent()) {
			return self;
		} else {
			Optional<T> r = (Optional)supplier.get();
			return (Optional)Objects.requireNonNull(r);
		}
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
				return ElementName.configurationName("implementation").qualifiedBy(builder -> builder.append(binary.getName()));
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
		return binary -> ElementName.configurationName().prefix("nativeLink").qualifiedBy(qualifyingName(binary));
	}

	public ForBinaryBuilder nativeRuntimeConfigurationName() {
		return binary -> ElementName.configurationName().prefix("nativeRuntime").qualifiedBy(qualifyingName(binary));
	}

	public ForBinaryBuilder cppCompileConfigurationName() {
		return binary -> ElementName.configurationName().prefix("cppCompile").qualifiedBy(qualifyingName(binary));
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
		return of(component).cppApiElementsConfigurationName().toString();
	}

	public static String implementationConfigurationName(CppComponent component) {
		return of(component).implementationConfigurationName().toString();
	}

	public static String apiConfigurationName(CppComponent component) {
		return of(component).apiConfigurationName().toString();
	}
	//endregion

	//region C++ binary names
	public static String implementationConfigurationName(CppBinary binary) {
		return of(binary).implementationConfigurationName().toString();
	}

	public static String linkElementsConfigurationName(CppBinary binary) {
		return of(binary).linkElementsConfigurationName().toString();
	}

	public static String runtimeElementsConfigurationName(CppBinary binary) {
		return of(binary).runtimeElementsConfigurationName().toString();
	}

	public static String nativeLinkConfigurationName(CppBinary binary) {
		return of(binary).nativeLinkConfigurationName().toString();
	}

	public static String nativeRuntimeConfigurationName(CppBinary binary) {
		return of(binary).nativeRuntimeConfigurationName().toString();
	}

	public static String compileTaskName(CppBinary binary) {
		return of(binary).compileTaskName().toString();
	}

	public static String compileTaskName(CppBinary binary, String language) {
		return of(binary).compileTaskName(language).toString();
	}

	public static String linkTaskName(CppBinary binary) {
		return of(binary).linkTaskName().toString();
	}

	public static String cppCompileConfigurationName(CppBinary binary) {
		return of(binary).cppCompileConfigurationName().toString();
	}
	//endregion
}
