package dev.nokee.commons.names;

import org.gradle.api.Named;
import org.gradle.language.cpp.*;
import org.gradle.nativeplatform.test.cpp.CppTestExecutable;
import org.gradle.nativeplatform.test.cpp.CppTestSuite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static dev.nokee.commons.names.StringUtils.uncapitalize;
import static org.gradle.nativeplatform.MachineArchitecture.X86;
import static org.gradle.nativeplatform.MachineArchitecture.X86_64;
import static org.gradle.nativeplatform.OperatingSystemFamily.*;

/**
 * Task and configuration names for {@literal cpp-library}, {@literal cpp-application} and {@literal cpp-unit-test} plugins.
 */
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
			return INSTANCE.linkElementsConfigurationName().forBinary(binary);
		}

		public FullyQualifiedName runtimeElementsConfigurationName() {
			return INSTANCE.runtimeElementsConfigurationName().forBinary(binary);
		}

		public FullyQualifiedName nativeLinkConfigurationName() {
			return INSTANCE.nativeLinkConfigurationName().forBinary(binary);
		}

		public FullyQualifiedName nativeRuntimeConfigurationName() {
			return INSTANCE.nativeRuntimeConfigurationName().forBinary(binary);
		}

		public FullyQualifiedName compileTaskName() {
			return INSTANCE.compileTaskName().forBinary(binary);
		}

		public FullyQualifiedName compileTaskName(String language) {
			return INSTANCE.compileTaskName(language).forBinary(binary);
		}

		public FullyQualifiedName linkTaskName() {
			return INSTANCE.linkTaskName().forBinary(binary);
		}

		public FullyQualifiedName createTaskName() {
			return INSTANCE.createTaskName().forBinary(binary);
		}

		public FullyQualifiedName installTaskName() {
			return INSTANCE.installTaskName().forBinary(binary);
		}

		public FullyQualifiedName assembleTaskName() {
			return INSTANCE.assembleTaskName().forBinary(binary);
		}

		public FullyQualifiedName implementationConfigurationName() {
			return INSTANCE.implementationConfigurationName().forBinary(binary);
		}

		public FullyQualifiedName cppCompileConfigurationName() {
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
			return INSTANCE.cppApiElementsConfigurationName().forComponent(component);
		}

		public FullyQualifiedName implementationConfigurationName() {
			return INSTANCE.implementationConfigurationName().forComponent(component);
		}

		public FullyQualifiedName apiConfigurationName() {
			return INSTANCE.apiConfigurationName().forComponent(component);
		}

		public FullyQualifiedName assembleTaskName() {
			return INSTANCE.assembleTaskName().forComponent(component);
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

	/**
	 * Returns the qualifying name for nested domain object of the specified C++ component or binary.
	 * <p>
	 * We use deep modeling of the domain object name to allow horizontal name translation.
	 * Because of the accordion naming scheme, some dimensions of the name may be lost.
	 * For this reason, we will try our best to account for those lost dimensions.
	 *
	 * @param componentOrBinary  the C++ component/binary object to extract the qualifying name from, must not be null
	 * @return a qualifying name
	 */
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
	/*public*/ interface ForComponentBuilder {
		FullyQualifiedName forComponent(CppComponent component);
	}

	/*public*/ interface ForBinaryBuilder {
		FullyQualifiedName forBinary(CppBinary binary);
	}

	/*public*/ ForComponentBuilder cppApiElementsConfigurationName() {
		return component -> ElementName.configurationName("cppApiElements").qualifiedBy(qualifyingName(component));
	}

	/*public*/ interface ImplementationConfigurationNameBuilder extends ForComponentBuilder, ForBinaryBuilder {}

	/*public*/ ImplementationConfigurationNameBuilder implementationConfigurationName() {
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

	/*public*/ ForComponentBuilder apiConfigurationName() {
		return component -> ElementName.configurationName("api").qualifiedBy(qualifyingName(component));
	}

	/*public*/ ForBinaryBuilder linkElementsConfigurationName() {
		return binary -> ElementName.configurationName("linkElements").qualifiedBy(qualifyingName(binary));
	}

	/*public*/ ForBinaryBuilder runtimeElementsConfigurationName() {
		return binary -> ElementName.configurationName("runtimeElements").qualifiedBy(qualifyingName(binary));
	}

	/*public*/ ForBinaryBuilder nativeLinkConfigurationName() {
		return binary -> ElementName.configurationName().prefix("nativeLink").qualifiedBy(qualifyingName(binary));
	}

	/*public*/ ForBinaryBuilder nativeRuntimeConfigurationName() {
		return binary -> ElementName.configurationName().prefix("nativeRuntime").qualifiedBy(qualifyingName(binary));
	}

	/*public*/ ForBinaryBuilder cppCompileConfigurationName() {
		return binary -> ElementName.configurationName().prefix("cppCompile").qualifiedBy(qualifyingName(binary));
	}

	/*public*/ ForBinaryBuilder compileTaskName() {
		return binary -> ElementName.taskName("compile", "cpp").qualifiedBy(qualifyingName(binary));
	}

	/*public*/ ForBinaryBuilder compileTaskName(String language) {
		return binary -> ElementName.taskName("compile", language).qualifiedBy(qualifyingName(binary));
	}

	/*public*/ ForBinaryBuilder linkTaskName() {
		return binary -> ElementName.taskName("link").qualifiedBy(qualifyingName(binary));
	}

	/*public*/ ForBinaryBuilder createTaskName() {
		return binary -> ElementName.taskName("create").qualifiedBy(qualifyingName(binary));
	}

	/*public*/ ForBinaryBuilder installTaskName() {
		return binary -> ElementName.taskName("install").qualifiedBy(qualifyingName(binary));
	}

	/*public*/ interface AssembleTaskNameBuilder extends ForComponentBuilder, ForBinaryBuilder {}

	/*public*/ AssembleTaskNameBuilder assembleTaskName() {
		return new AssembleTaskNameBuilder() {
			@Override
			public FullyQualifiedName forBinary(CppBinary binary) {
				return ElementName.taskName("assemble").qualifiedBy(qualifyingName(binary));
			}

			@Override
			public FullyQualifiedName forComponent(CppComponent component) {
				return ElementName.taskName("assemble").qualifiedBy(qualifyingName(component));
			}
		};
	}
	//endregion

	//region C++ component names
	/**
	 * Returns the <code><i>qualifyingName</i>CppApiElements</code> configuration name.
	 * For example:
	 * <ul>
	 *   <li>cppApiElements</li>
	 * </ul>
	 * <b>Note:</b> does not exist for {@link CppTestSuite}, for convenience only
	 *
	 * @param component  the C++ component object that qualify the configuration name, must not be null
	 * @return a configuration name
	 */
	public static String cppApiElementsConfigurationName(CppComponent component) {
		return of(component).cppApiElementsConfigurationName().toString();
	}

	/**
	 * Returns the <code><i>qualifyingName</i>Implementation</code> configuration name.
	 * For example:
	 * <ul>
	 *   <li>implementation - for {@link ProductionCppComponent}</li>
	 *   <li>testImplementation - for {@link CppTestSuite}</li>
	 * </ul>
	 *
	 * @param component  the C++ component object that qualify the configuration name, must not be null
	 * @return a configuration name
	 * @see #implementationConfigurationName(CppBinary) for {@link CppBinary} variant of this configuration name
	 */
	public static String implementationConfigurationName(CppComponent component) {
		return of(component).implementationConfigurationName().toString();
	}

	/**
	 * Returns the <code><i>qualifyingName</i>Api</code> configuration name.
	 * For example:
	 * <ul>
	 *   <li>api - for {@link CppLibrary}</li>
	 * </ul>
	 * <b>Note:</b> does not exist for anything else, for convenience only.
	 *
	 * @param component  the C++ component object that qualify the configuration name, must not be null
	 * @return a configuration name
	 */
	public static String apiConfigurationName(CppComponent component) {
		return of(component).apiConfigurationName().toString();
	}

	/**
	 * Returns the <code>assemble<i>QualifyingName</i></code> task name.
	 * For example:
	 * <ul>
	 *   <li>assemble - for {@link CppLibrary} or {@link CppApplication}</li>
	 *   <li>assembleTest - for {@link CppTestSuite}</li>
	 * </ul>
	 *
	 * @param component  the C++ component object that qualify the task name, must not be null
	 * @return a task name
	 */
	public static String assembleTaskName(CppComponent component) {
		return of(component).assembleTaskName().toString();
	}
	//endregion

	//region C++ binary names
	/**
	 * Returns the <code><i>binaryName</i>Implementation</code> configuration name.
	 * For example:
	 * <ul>
	 *   <li><u>mainRelease</u>Implementation</li>
	 *   <li><u>mainLinuxDebug</u>Implementation</li>
	 *   <li><u>testExecutable</u>Implementation - for {@link CppTestExecutable}</li>
	 * </ul>
	 * <b>Note:</b> this name diverge from the typical naming scheme.
	 *
	 * @param binary  the C++ binary object that qualify the configuration name, must not be null
	 * @return a configuration name
	 */
	public static String implementationConfigurationName(CppBinary binary) {
		return of(binary).implementationConfigurationName().toString();
	}

	/**
	 * Returns the <code><i>qualifyingName</i>LinkElements</code> configuration name.
	 * For example:
	 * <ul>
	 *   <li><u>release</u>LinkElements</li>
	 *   <li><u>linuxDebug</u>LinkElements</li>
	 * </ul>
	 * <b>Note:</b> does not exist for {@link CppTestExecutable}.
	 *
	 * @param binary  the C++ binary object that qualify the configuration name, must not be null
	 * @return a configuration name
	 */
	public static String linkElementsConfigurationName(CppBinary binary) {
		return of(binary).linkElementsConfigurationName().toString();
	}

	/**
	 * Returns the <code><i>qualifyingName</i>RuntimeElements</code> configuration name.
	 * For example:
	 * <ul>
	 *   <li><u>release</u>RuntimeElements</li>
	 *   <li><u>linuxDebug</u>RuntimeElements</li>
	 * </ul>
	 * <b>Note:</b> does not exist for {@link CppTestExecutable}.
	 *
	 * @param binary  the C++ binary object that qualify the configuration name, must not be null
	 * @return a configuration name
	 */
	public static String runtimeElementsConfigurationName(CppBinary binary) {
		return of(binary).runtimeElementsConfigurationName().toString();
	}

	/**
	 * Returns the <code>nativeLink<i>QualifyingName</i></code> configuration name.
	 * For example:
	 * <ul>
	 *   <li>nativeLink<u>Release</u></li>
	 *   <li>nativeLink<u>LinuxDebug</u></li>
	 *   <li>nativeLink<u>Test</u></li>
	 * </ul>
	 *
	 * @param binary  the C++ binary object that qualify the configuration name, must not be null
	 * @return a configuration name
	 */
	public static String nativeLinkConfigurationName(CppBinary binary) {
		return of(binary).nativeLinkConfigurationName().toString();
	}

	/**
	 * Returns the <code>nativeRuntime<i>QualifyingName</i></code> configuration name.
	 * For example:
	 * <ul>
	 *   <li>nativeRuntime<u>Release</u></li>
	 *   <li>nativeRuntime<u>LinuxDebug</u></li>
	 *   <li>nativeRuntime<u>Test</u></li>
	 * </ul>
	 *
	 * @param binary  the C++ binary object that qualify the configuration name, must not be null
	 * @return a configuration name
	 */
	public static String nativeRuntimeConfigurationName(CppBinary binary) {
		return of(binary).nativeRuntimeConfigurationName().toString();
	}

	/**
	 * Returns the <code>compile<i>QualifyingName</i>Cpp</code> task name.
	 * For example:
	 * <ul>
	 *   <li>compile<u>Release</u>Cpp</li>
	 *   <li>compile<u>LinuxDebug</u>Cpp</li>
	 *   <li>compile<u>Test</u>Cpp</li>
	 * </ul>
	 *
	 * @param binary  the C++ binary object that qualify the task name, must not be null
	 * @return a task name
	 */
	public static String compileTaskName(CppBinary binary) {
		return of(binary).compileTaskName().toString();
	}

	/**
	 * Returns the <code>compile<i>QualifyingName</i><i>Language</i></code> task name.
	 * For example:
	 * <ul>
	 *   <li>compile<u>Release</u>C</li>
	 *   <li>compile<u>TestDebug</u>ObjCpp</li>
	 * </ul>
	 * <b>Note:</b> mixed language compilation does not exist, for convenience only.
	 *
	 * @param binary  the C++ binary object that qualify the task name, must not be null
	 * @param language  the implementation language to compile, must not be null
	 * @return a task name
	 */
	public static String compileTaskName(CppBinary binary, String language) {
		return of(binary).compileTaskName(language).toString();
	}

	/**
	 * Returns the <code>link<i>QualifyingName</i></code> task name.
	 * For example:
	 * <ul>
	 *   <li>link<u>Release</u></li>
	 *   <li>link<u>LinuxDebug</u></li>
	 *   <li>link<u>Test</u></li>
	 * </ul>
	 *
	 * @param binary  the C++ binary object that qualify the task name, must not be null
	 * @return a task name
	 */
	public static String linkTaskName(CppBinary binary) {
		return of(binary).linkTaskName().toString();
	}

	/**
	 * Returns the <code>create<i>QualifyingName</i></code> task name.
	 * For example:
	 * <ul>
	 *   <li>create<u>Release</u></li>
	 *   <li>create<u>LinuxDebug</u></li>
	 *   <li>create<u>Test</u></li>
	 * </ul>
	 *
	 * @param binary  the C++ binary object that qualify the task name, must not be null
	 * @return a task name
	 */
	public static String createTaskName(CppBinary binary) {
		return of(binary).createTaskName().toString();
	}

	/**
	 * Returns the <code>assemble<i>QualifyingName</i></code> task name.
	 * For example:
	 * <ul>
	 *   <li>assemble<u>Release</u></li>
	 *   <li>assemble<u>LinuxDebug</u></li>
	 *   <li>assemble<u>Test</u></li>
	 * </ul>
	 *
	 * @param binary  the C++ binary object that qualify the task name, must not be null
	 * @return a task name
	 */
	public static String assembleTaskName(CppBinary binary) {
		return of(binary).assembleTaskName().toString();
	}


	/**
	 * Returns the <code>install<i>QualifyingName</i></code> task name.
	 * For example:
	 * <ul>
	 *   <li>install<u>Release</u></li>
	 *   <li>install<u>LinuxDebug</u></li>
	 *   <li>install<u>Test</u></li>
	 * </ul>
	 *
	 * @param binary  the C++ binary object that qualify the task name, must not be null
	 * @return a task name
	 */
	public static String installTaskName(CppBinary binary) {
		return of(binary).installTaskName().toString();
	}

	/**
	 * Returns the <code>cppCompile<i>QualifyingName</i></code> configuration name.
	 * For example:
	 * <ul>
	 *   <li>cppCompile<u>Release</u></li>
	 *   <li>cppCompile<u>LinuxDebug</u></li>
	 *   <li>cppCompile<u>Test</u></li>
	 * </ul>
	 *
	 * @param binary  the C++ binary object that qualify the configuration name, must not be null
	 * @return a configuration name
	 */
	public static String cppCompileConfigurationName(CppBinary binary) {
		return of(binary).cppCompileConfigurationName().toString();
	}
	//endregion
}
