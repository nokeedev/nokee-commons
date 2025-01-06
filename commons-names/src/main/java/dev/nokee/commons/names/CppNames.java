package dev.nokee.commons.names;

import org.gradle.api.Named;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.CppComponent;
import org.gradle.language.cpp.CppSharedLibrary;
import org.gradle.language.cpp.CppStaticLibrary;
import org.gradle.nativeplatform.test.cpp.CppTestExecutable;

import java.util.*;
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

	public static class ForBinary extends NameSupport<ForBinary> implements Names {
		private final Names delegate;
		private final CppBinary binary;

		private ForBinary(CppBinary binary) {
			this.binary = binary;
			this.delegate = new DefaultNames(qualifyingName(binary));
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
		public void appendTo(NameBuilder builder) {
			delegate.appendTo(builder);
		}

		@Override
		public String toString() {
			return delegate.toString();
		}

		@Override
		public String toString(NameBuilder builder) {
			return delegate.toString(builder);
		}
	}

	public static final class ForComponent extends NameSupport<ForComponent> implements Names {
		private final Names delegate;
		private final CppComponent component;

		private ForComponent(CppComponent component) {
			this.component = component;
			this.delegate = new DefaultNames(qualifyingName(component));
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
		public void appendTo(NameBuilder builder) {
			delegate.appendTo(builder);
		}

		@Override
		public String toString() {
			return delegate.toString();
		}

		@Override
		public String toString(NameBuilder builder) {
			return delegate.toString(builder);
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

	private static final class BuildTypeQualifier implements Qualifier, IParameterizedObject<BuildTypeQualifier> {
		private final Qualifier value;

		private BuildTypeQualifier(Qualifier value) {
			this.value = value;
		}

		@Override
		public void appendTo(NameBuilder builder) {
			value.appendTo(builder);
		}

		@Override
		public Set<String> propSet() {
			return Collections.singleton("buildTypeName");
		}

		@Override
		public BuildTypeQualifier with(String propName, Object value) {
			if (propName.equals("buildTypeName")) {
				return new BuildTypeQualifier(Qualifiers.of((String) value));
			} else {
				return this;
			}
		}

		@Override
		public String toString() {
			return value.toString();
		}
	}

	private static final class OperatingSystemFamilyQualifier implements Qualifier, IParameterizedObject<OperatingSystemFamilyQualifier> {
		private final Qualifier value;

		private OperatingSystemFamilyQualifier(Qualifier value) {
			this.value = value;
		}

		@Override
		public void appendTo(NameBuilder builder) {
			value.appendTo(builder);
		}

		@Override
		public Set<String> propSet() {
			return Collections.singleton("operatingSystemFamilyName");
		}

		@Override
		public OperatingSystemFamilyQualifier with(String propName, Object value) {
			if (propName.equals("operatingSystemFamilyName")) {
				return new OperatingSystemFamilyQualifier(Qualifiers.of((String) value));
			} else {
				return this;
			}
		}

		@Override
		public String toString() {
			return value.toString();
		}
	}

	private static final class LinkageQualifier implements Qualifier, IParameterizedObject<LinkageQualifier> {
		private final Qualifier value;

		private LinkageQualifier(Qualifier value) {
			this.value = value;
		}

		@Override
		public void appendTo(NameBuilder builder) {
			value.appendTo(builder);
		}

		@Override
		public Set<String> propSet() {
			return Collections.singleton("linkageName");
		}

		@Override
		public LinkageQualifier with(String propName, Object value) {
			if (propName.equals("linkageName")) {
				return new LinkageQualifier(Qualifiers.of((String) value));
			} else {
				return this;
			}
		}

		@Override
		public String toString() {
			return value.toString();
		}
	}

	private static final class MachineArchitectureQualifier implements Qualifier, IParameterizedObject<MachineArchitectureQualifier> {
		private final Qualifier value;

		private MachineArchitectureQualifier(Qualifier value) {
			this.value = value;
		}

		@Override
		public void appendTo(NameBuilder builder) {
			value.appendTo(builder);
		}

		@Override
		public Set<String> propSet() {
			return Collections.singleton("architectureName");
		}

		@Override
		public MachineArchitectureQualifier with(String propName, Object value) {
			if (propName.equals("architectureName")) {
				return new MachineArchitectureQualifier(Qualifiers.of((String) value));
			} else {
				return this;
			}
		}

		@Override
		public String toString() {
			return value.toString();
		}
	}

	private static final class BinaryName extends NameSupport<BinaryName> implements OtherName {
		private final Qualifier binaryName;

		public BinaryName(Collection<Qualifier> binaryName) {
			this(Qualifiers.of(binaryName));
		}

		public BinaryName(Qualifier binaryName) {
			this.binaryName = binaryName;
		}

		@Override
		void init(Prop.Builder<BinaryName> builder) {
			builder.elseWith(binaryName, BinaryName::new);
		}

		@Override
		public QualifyingName qualifiedBy(Qualifier qualifier) {
			return new DefaultQualifyingName(qualifier, this, new Scheme() {
				@Override
				public String format(NameBuilder builder) {
					qualifier.appendTo(builder);
					builder.append(binaryName);
					return builder.toString();
				}
			});
		}

		@Override
		public String toString() {
			NameBuilder builder = NameBuilder.toStringCase();
			builder.append(binaryName);
			return builder.toString();
		}

		@Override
		public void appendTo(NameBuilder builder) {
			builder.append(binaryName);
		}

		@Override
		public String toString(NameBuilder builder) {
			builder.append(binaryName);
			return builder.toString();
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

		List<Qualifier> binaryName = new ArrayList<>();
		or(iter.consumeNext("debug", "release").map(Qualifiers::of).map(BuildTypeQualifier::new), () -> {
			if (componentOrBinary instanceof CppTestExecutable) {
				final CppBinary binary = (CppBinary) componentOrBinary;
				if (binary.isOptimized()) {
					return Optional.of(new BuildTypeQualifier(Qualifiers.ofMain(Qualifiers.of("release"))));
				} else {
					return Optional.of(new BuildTypeQualifier(Qualifiers.ofMain(Qualifiers.of("debug"))));
				}
			}
			return Optional.of(new BuildTypeQualifier(Qualifiers.of("")));
		}).ifPresent(binaryName::add);

		or(iter.consumeNext("shared", "static").map(Qualifiers::of).map(LinkageQualifier::new), () -> {
			if (componentOrBinary instanceof CppSharedLibrary) {
				return Optional.of(new LinkageQualifier(Qualifiers.ofMain(Qualifiers.of("shared"))));
			} else if (componentOrBinary instanceof CppStaticLibrary) {
				return Optional.of(new LinkageQualifier(Qualifiers.ofMain(Qualifiers.of("static"))));
			}
			return Optional.of(new LinkageQualifier(Qualifiers.of("")));
		}).ifPresent(binaryName::add);

		or(iter.consumeNext(LINUX, MACOS, WINDOWS).map(Qualifiers::of).map(OperatingSystemFamilyQualifier::new), () -> {
			if (componentOrBinary instanceof CppBinary) {
				final CppBinary binary = (CppBinary) componentOrBinary;
				return Optional.of(new OperatingSystemFamilyQualifier(Qualifiers.ofMain(Qualifiers.of(binary.getTargetMachine().getOperatingSystemFamily().getName()))));
			}
			return Optional.of(new OperatingSystemFamilyQualifier(Qualifiers.of("")));
		}).ifPresent(binaryName::add);

		or(iter.consumeNext(X86, X86_64, "aarch64").map(Qualifiers::of).map(MachineArchitectureQualifier::new), () -> {
			if (componentOrBinary instanceof CppBinary) {
				final CppBinary binary = (CppBinary) componentOrBinary;
				return Optional.of(new MachineArchitectureQualifier(Qualifiers.ofMain(Qualifiers.of(binary.getTargetMachine().getArchitecture().getName()))));
			}
			return Optional.of(new MachineArchitectureQualifier(Qualifiers.of("")));
		}).ifPresent(binaryName::add);

		if (binaryName.isEmpty()) {
			return result;
		}
		return result.append(new BinaryName(binaryName));
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

	public static String compileTaskName(CppBinary binary, String language) {
		return INSTANCE.compileTaskName(language).forBinary(binary).toString();
	}

	public static String linkTaskName(CppBinary binary) {
		return INSTANCE.linkTaskName().forBinary(binary).toString();
	}

	public static String cppCompileConfigurationName(CppBinary binary) {
		return INSTANCE.cppCompileConfigurationName().forBinary(binary).toString();
	}
	//endregion
}
