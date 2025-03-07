package dev.nokee.commons.names;

import dev.nokee.commons.fixtures.SkipWhenNoSubject;
import dev.nokee.commons.fixtures.Subject;
import dev.nokee.commons.fixtures.SubjectExtension;
import dev.nokee.commons.fixtures.Subjects;
import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.language.cpp.*;
import org.gradle.nativeplatform.test.cpp.CppTestExecutable;
import org.gradle.nativeplatform.test.cpp.CppTestSuite;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static dev.nokee.commons.hamcrest.gradle.NamedMatcher.named;
import static dev.nokee.commons.names.CppNames.*;
import static java.util.Collections.singletonList;
import static org.gradle.nativeplatform.Linkage.SHARED;
import static org.gradle.nativeplatform.Linkage.STATIC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasToString;

@ExtendWith(SubjectExtension.class)
class CppNamesIntegrationTests {
	static Project project;

	interface Namer<T> {
		Object determineName(T o);
	}

	interface FullNamer<T> extends Namer<T> {
		FullyQualifiedName determineName(T o);
	}

	abstract class ComponentBasedNameTester {
		@Test
		void testImplementationConfigurationName(@Subject CppComponent component, @Subject("implementation-component") Namer<CppComponent> namer, @Subject("implementation-component") String expectedName) {
			assertThat(namer.determineName(component), hasToString(expectedName));
		}

		@Test
		void testCppApiElementsConfigurationName(@Subject CppComponent component, @Subject("cpp-api-elements") Namer<CppComponent> namer, @Subject("cpp-api-elements") String expectedName) {
			assertThat(namer.determineName(component), hasToString(expectedName));
		}

		@Test
		void testApiConfigurationName(@Subject CppComponent component, @Subject("api") Namer<CppComponent> namer, @Subject("api") String expectedName) {
			assertThat(namer.determineName(component), hasToString(expectedName));
		}

		@Test
		void testAssembleTaskName(@Subject CppComponent component, @Subject("assemble-component") Namer<CppComponent> namer, @Subject("assemble-component") String expectedName) {
			assertThat(namer.determineName(component), hasToString(expectedName));
		}

		@Subject("implementation-component") abstract String implementationComponentConfigurationName();
		@Subject("cpp-api-elements") abstract String cppApiElementsConfigurationName();
		@Subject("api") abstract String apiConfigurationName();
		@Subject("assemble-component") abstract String assembleComponentTaskName();
	}



	abstract class BinaryBasedNameTester {
		@Nested
		@Subjects("implementation")
		class ImplementationConfigurationNameTests extends BinaryNamesTester<CppBinary> {}

		@Nested
		@Subjects("link-elements")
		class LinkElementsConfigurationNameTests extends BinaryNamesTester<CppBinary> {}

		@Nested
		@Subjects("runtime-elements")
		class RuntimeElementsConfigurationNameTests extends BinaryNamesTester<CppBinary> {}

		@Nested
		@Subjects("native-link")
		class NativeLinkConfigurationNameTests extends BinaryNamesTester<CppBinary> {}

		@Nested
		@Subjects("native-runtime")
		class NativeRuntimeConfigurationNameTests extends BinaryNamesTester<CppBinary> {}

		@Nested
		@Subjects("cpp-compile")
		class CppCompileConfigurationNameTests extends BinaryNamesTester<CppBinary> {}

		@Nested
		@Subjects("compile-cpp")
		class CompileCppTaskNameTests extends BinaryNamesTester<CppBinary> {}

		@Nested
		@Subjects("link")
		class LinkTaskNameTests extends BinaryNamesTester<CppBinary> {}

		@Nested
		@Subjects("create")
		class CreateTaskNameTests extends BinaryNamesTester<CppBinary> {}

		@Nested
		@Subjects("assemble")
		class AssembleTaskNameTests extends BinaryNamesTester<CppBinary> {}

		@Nested
		@Subjects("install")
		class InstallTaskNameTests extends BinaryNamesTester<CppBinary> {}

		@Nested
		@Subjects("relocate-main")
		class RelocateMainForBinaryTaskNameTests extends BinaryNamesTester<CppTestExecutable> {}

		@Nested
		@Subjects("extract-symbols")
		class ExtractSymbolsTaskNameTests extends BinaryNamesTester<CppBinary> {}

		@Nested
		@Subjects("strip-symbols")
		class StripSymbolsTaskNameTests extends BinaryNamesTester<CppBinary> {}

		@Nested
		@Subjects("run")
		class RunTaskNameTests extends BinaryNamesTester<CppTestExecutable> {}
	}

	abstract class BinaryNamesTester<T extends CppBinary> {
		@Test
		@SkipWhenNoSubject // TODO: Should be put on the class for RunTaskNameTests and RelocateMainForBinaryTaskNameTests
		void testHostName(@Subject("host") T binary, @Subject Namer<T> namer, @Subject ExpectedNames expectedNames) {
			assertThat(namer.determineName(binary), hasToString(expectedNames.host()));
		}

		@Test
		@SkipWhenNoSubject // TODO: Should be put on FullNamer parameter
		void testHostNameWithCustomBuildType(@Subject("host") T binary, @Subject FullNamer<T> namer, @Subject ExpectedNames expectedNames) {
			assertThat(namer.determineName(binary).with("buildTypeName", "final"), hasToString(expectedNames.hostWithFinalBuildType()));
		}

		@Test
		@SkipWhenNoSubject // TODO: Should be put on FullNamer parameter
		void testHostNameWithCustomArchitecture(@Subject("host") T binary, @Subject FullNamer<T> namer, @Subject ExpectedNames expectedNames) {
			assertThat(namer.determineName(binary).with("architectureName", "sparc"), hasToString(expectedNames.hostWithSparcArchitecture()));
		}

		@Test
		@SkipWhenNoSubject // TODO: Should be put on FullNamer parameter
		void testHostNameWithCustomOperatingSystemFamily(@Subject("host") T binary, @Subject FullNamer<T> namer, @Subject ExpectedNames expectedNames) {
			assertThat(namer.determineName(binary).with("osFamilyName", "amiga"), hasToString(expectedNames.hostWithAmigaOperatingSystemFamily()));
		}
	}


	interface StringNamers {
		static @Subject("implementation-component") Namer<CppComponent> implementationComponentNamer() {
			return CppNames::implementationConfigurationName;
		}

		static @Subject("cpp-api-elements") Namer<CppComponent> cppApiElementsNamer() {
			return CppNames::cppApiElementsConfigurationName;
		}

		static @Subject("api") Namer<CppComponent> apiNamer() {
			return CppNames::apiConfigurationName;
		}

		static @Subject("assemble-component") Namer<CppComponent> assembleComponentNamer() {
			return CppNames::assembleTaskName;
		}

		static @Subject("implementation") Namer<CppBinary> implementationNamer() {
			return CppNames::implementationConfigurationName;
		}

		static @Subject("link-elements") Namer<CppBinary> linkElementsNamer() {
			return CppNames::linkElementsConfigurationName;
		}

		static @Subject("runtime-elements") Namer<CppBinary> runtimeElementsNamer() {
			return CppNames::runtimeElementsConfigurationName;
		}

		static @Subject("native-link") Namer<CppBinary> nativeLinkNamer() {
			return CppNames::nativeLinkConfigurationName;
		}

		static @Subject("native-runtime") Namer<CppBinary> nativeRuntimeNamer() {
			return CppNames::nativeRuntimeConfigurationName;
		}

		static @Subject("compile-cpp") Namer<CppBinary> compileCppNamer() {
			return CppNames::compileTaskName;
		}

		static @Subject("link") Namer<CppBinary> linkNamer() {
			return CppNames::linkTaskName;
		}

		static @Subject("create") Namer<CppBinary> createNamer() {
			return CppNames::createTaskName;
		}

		static @Subject("assemble") Namer<CppBinary> assembleBinaryNamer() {
			return CppNames::assembleTaskName;
		}

		static @Subject("install") Namer<CppBinary> installNamer() {
			return CppNames::installTaskName;
		}

		static @Subject("relocate-main") Namer<CppTestExecutable> relocateMainForBinaryNamer() {
			return CppNames::relocateMainForBinaryTaskName;
		}

		static @Subject("extract-symbols") Namer<CppBinary> extractSymbolsNamer() {
			return CppNames::extractSymbolsTaskName;
		}

		static @Subject("strip-symbols") Namer<CppBinary> stripSymbolsNamer() {
			return CppNames::stripSymbolsTaskName;
		}

		static @Subject("run") Namer<CppTestExecutable> runNamer() {
			return CppNames::runTaskName;
		}

		static @Subject("cpp-compile") Namer<CppBinary> cppCompileNamer() {
			return CppNames::cppCompileConfigurationName;
		}
	}

	interface CppNamesNamers {
		static @Subject("implementation-component") Namer<CppComponent> implementationComponentNamer() {
			return new CppNames().implementationConfigurationName()::forComponent;
		}

		static @Subject("cpp-api-elements") Namer<CppComponent> cppApiElementsNamer() {
			return new CppNames().cppApiElementsConfigurationName()::forComponent;
		}

		static @Subject("api") Namer<CppComponent> apiNamer() {
			return new CppNames().apiConfigurationName()::forComponent;
		}

		static @Subject("assemble-component") Namer<CppComponent> assembleComponentNamer() {
			return new CppNames().assembleTaskName()::forComponent;
		}

		static @Subject("implementation") FullNamer<CppBinary> implementationNamer() {
			return new CppNames().implementationConfigurationName()::forBinary;
		}

		static @Subject("link-elements") FullNamer<CppBinary> linkElementsNamer() {
			return new CppNames().linkElementsConfigurationName()::forBinary;
		}

		static @Subject("runtime-elements") FullNamer<CppBinary> runtimeElementsNamer() {
			return new CppNames().runtimeElementsConfigurationName()::forBinary;
		}

		static @Subject("native-link") FullNamer<CppBinary> nativeLinkNamer() {
			return new CppNames().nativeLinkConfigurationName()::forBinary;
		}

		static @Subject("native-runtime") FullNamer<CppBinary> nativeRuntimeNamer() {
			return new CppNames().nativeRuntimeConfigurationName()::forBinary;
		}

		static @Subject("compile-cpp") FullNamer<CppBinary> compileCppNamer() {
			return new CppNames().compileTaskName()::forBinary;
		}

		static @Subject("link") FullNamer<CppBinary> linkNamer() {
			return new CppNames().linkTaskName()::forBinary;
		}

		static @Subject("create") FullNamer<CppBinary> createNamer() {
			return new CppNames().createTaskName()::forBinary;
		}

		static @Subject("assemble") FullNamer<CppBinary> assembleBinaryNamer() {
			return new CppNames().assembleTaskName()::forBinary;
		}

		static @Subject("install") FullNamer<CppBinary> installNamer() {
			return new CppNames().installTaskName()::forBinary;
		}

		static @Subject("relocate-main") FullNamer<CppTestExecutable> relocateMainForBinaryNamer() {
			return new CppNames().relocateMainForBinaryTaskName()::forBinary;
		}

		static @Subject("extract-symbols") FullNamer<CppBinary> extractSymbolsNamer() {
			return new CppNames().extractSymbolsTaskName()::forBinary;
		}

		static @Subject("strip-symbols") FullNamer<CppBinary> stripSymbolsNamer() {
			return new CppNames().stripSymbolsTaskName()::forBinary;
		}

		static @Subject("run") FullNamer<CppTestExecutable> runNamer() {
			return new CppNames().runTaskName()::forBinary;
		}

		static @Subject("cpp-compile") FullNamer<CppBinary> cppCompileNamer() {
			return new CppNames().cppCompileConfigurationName()::forBinary;
		}
	}

	interface NamesNamers {
		static @Subject("implementation-component") Namer<CppComponent> implementationComponentNamer() {
			return component -> CppNames.of(component).implementationConfigurationName();
		}

		static @Subject("cpp-api-elements") Namer<CppComponent> cppApiElementsNamer() {
			return component -> CppNames.of(component).cppApiElementsConfigurationName();
		}

		static @Subject("api") Namer<CppComponent> apiNamer() {
			return component -> CppNames.of(component).apiConfigurationName();
		}

		static @Subject("assemble-component") Namer<CppComponent> assembleComponentNamer() {
			return component -> CppNames.of(component).assembleTaskName();
		}

		static @Subject("implementation") FullNamer<CppBinary> implementationNamer() {
			return binary -> CppNames.of(binary).implementationConfigurationName();
		}

		static @Subject("link-elements") FullNamer<CppBinary> linkElementsNamer() {
			return binary -> CppNames.of(binary).linkElementsConfigurationName();
		}

		static @Subject("runtime-elements") FullNamer<CppBinary> runtimeElementsNamer() {
			return binary -> CppNames.of(binary).runtimeElementsConfigurationName();
		}

		static @Subject("native-link") FullNamer<CppBinary> nativeLinkNamer() {
			return binary -> CppNames.of(binary).nativeLinkConfigurationName();
		}

		static @Subject("native-runtime") FullNamer<CppBinary> nativeRuntimeNamer() {
			return binary -> CppNames.of(binary).nativeRuntimeConfigurationName();
		}

		static @Subject("compile-cpp") FullNamer<CppBinary> compileCppNamer() {
			return binary -> CppNames.of(binary).compileTaskName();
		}

		static @Subject("link") FullNamer<CppBinary> linkNamer() {
			return binary -> CppNames.of(binary).linkTaskName();
		}

		static @Subject("create") FullNamer<CppBinary> createNamer() {
			return binary -> CppNames.of(binary).createTaskName();
		}

		static @Subject("assemble") FullNamer<CppBinary> assembleBinaryNamer() {
			return binary -> CppNames.of(binary).assembleTaskName();
		}

		static @Subject("install") FullNamer<CppBinary> installNamer() {
			return binary -> CppNames.of(binary).installTaskName();
		}

		static @Subject("relocate-main") FullNamer<CppTestExecutable> relocateMainForBinaryNamer() {
			return binary -> CppNames.of(binary).relocateMainForBinaryTaskName();
		}

		static @Subject("extract-symbols") FullNamer<CppBinary> extractSymbolsNamer() {
			return binary -> CppNames.of(binary).extractSymbolsTaskName();
		}

		static @Subject("strip-symbols") FullNamer<CppBinary> stripSymbolsNamer() {
			return binary -> CppNames.of(binary).stripSymbolsTaskName();
		}

		static @Subject("run") FullNamer<CppTestExecutable> runNamer() {
			return binary -> CppNames.of(binary).runTaskName();
		}

		static @Subject("cpp-compile") FullNamer<CppBinary> cppCompileNamer() {
			return binary -> CppNames.of(binary).cppCompileConfigurationName();
		}
	}

	// TODO: compile(language)

	abstract class ComponentTester {
		@Nested
		class StringTests extends BinaryBasedNameTester implements StringNamers {}

		@Nested
		class CppNamesTests extends BinaryBasedNameTester implements CppNamesNamers {}

		@Nested
		class NamesTests extends BinaryBasedNameTester implements NamesNamers {}

		@Subject("cpp-compile") abstract ExpectedNames cppCompileConfigurationName();
		@Subject("implementation") abstract ExpectedNames implementationConfigurationName();
		@Subject("native-runtime") abstract ExpectedNames nativeRuntimeConfigurationName();
		@Subject("native-link") abstract ExpectedNames nativeLinkConfigurationName();
		@Subject("compile-cpp") abstract ExpectedNames compileCppTaskName();
		@Subject("link-elements") abstract ExpectedNames linkElementsConfigurationName();
		@Subject("runtime-elements") abstract ExpectedNames runtimeElementsConfigurationName();
		@Subject("link") abstract ExpectedNames linkName();
		@Subject("create") abstract ExpectedNames createName();
		@Subject("assemble") abstract ExpectedNames assembleName();
		@Subject("compile-cpp") abstract ExpectedNames compileCppName();
		@Subject("run") abstract ExpectedNames runTaskName();
		@Subject("extract-symbols") abstract ExpectedNames extractSymbolsTaskName();
		@Subject("strip-symbols") abstract ExpectedNames stripSymbolsTaskName();
		@Subject("install") abstract ExpectedNames installTaskName();
		@Subject("relocate-main") abstract ExpectedNames relocateMainToBinaryTaskName();
	}

	abstract class DebugTester extends ComponentTester {
		@Override
		ExpectedNames cppCompileConfigurationName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "cppCompileDebug";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "cppCompileFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "cppCompileDebugSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "cppCompileDebugAmiga";
				}
			};
		}

		@Override
		ExpectedNames implementationConfigurationName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "mainDebugImplementation";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "mainFinalImplementation";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "mainDebugSparcImplementation";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "mainDebugAmigaImplementation";
				}
			};
		}

		@Override
		ExpectedNames nativeRuntimeConfigurationName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "nativeRuntimeDebug";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "nativeRuntimeFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "nativeRuntimeDebugSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "nativeRuntimeDebugAmiga";
				}
			};
		}

		@Override
		ExpectedNames nativeLinkConfigurationName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "nativeLinkDebug";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "nativeLinkFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "nativeLinkDebugSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "nativeLinkDebugAmiga";
				}
			};
		}

		@Override
		ExpectedNames compileCppTaskName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "compileDebugCpp";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "compileFinalCpp";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "compileDebugSparcCpp";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "compileDebugAmigaCpp";
				}
			};
		}

		@Override
		ExpectedNames linkElementsConfigurationName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "debugLinkElements";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "finalLinkElements";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "debugSparcLinkElements";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "debugAmigaLinkElements";
				}
			};
		}

		@Override
		ExpectedNames runtimeElementsConfigurationName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "debugRuntimeElements";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "finalRuntimeElements";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "debugSparcRuntimeElements";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "debugAmigaRuntimeElements";
				}
			};
		}

		@Override
		ExpectedNames runTaskName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "runDebug";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "runFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "runDebugSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "runDebugAmiga";
				}
			};
		}

		@Override
		ExpectedNames extractSymbolsTaskName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "extractSymbolsDebug";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "extractSymbolsFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "extractSymbolsDebugSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "extractSymbolsDebugAmiga";
				}
			};
		}

		@Override
		ExpectedNames stripSymbolsTaskName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "stripSymbolsDebug";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "stripSymbolsFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "stripSymbolsDebugSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "stripSymbolsDebugAmiga";
				}
			};
		}

		@Override
		ExpectedNames installTaskName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "installDebug";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "installFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "installDebugSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "installDebugAmiga";
				}
			};
		}

		@Override
		ExpectedNames relocateMainToBinaryTaskName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "relocateMainToDebug";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "relocateMainToFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "relocateMainToDebugSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "relocateMainToDebugAmiga";
				}
			};
		}

		@Override
		ExpectedNames linkName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "linkDebug";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "linkFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "linkDebugSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "linkDebugAmiga";
				}
			};
		}

		@Override
		ExpectedNames createName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "createDebug";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "createFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "createDebugSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "createDebugAmiga";
				}
			};
		}

		@Override
		ExpectedNames assembleName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "assembleDebug";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "assembleFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "assembleDebugSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "assembleDebugAmiga";
				}
			};
		}

		@Override
		ExpectedNames compileCppName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "compileDebugCpp";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "compileFinalCpp";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "compileDebugSparcCpp";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "compileDebugAmigaCpp";
				}
			};
		}
	}

	abstract class ReleaseTester extends ComponentTester {
		@Override
		ExpectedNames cppCompileConfigurationName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "cppCompileRelease";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "cppCompileFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "cppCompileReleaseSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "cppCompileReleaseAmiga";
				}
			};
		}

		@Override
		ExpectedNames implementationConfigurationName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "mainReleaseImplementation";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "mainFinalImplementation";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "mainReleaseSparcImplementation";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "mainReleaseAmigaImplementation";
				}
			};
		}

		@Override
		ExpectedNames nativeRuntimeConfigurationName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "nativeRuntimeRelease";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "nativeRuntimeFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "nativeRuntimeReleaseSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "nativeRuntimeReleaseAmiga";
				}
			};
		}

		@Override
		ExpectedNames nativeLinkConfigurationName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "nativeLinkRelease";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "nativeLinkFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "nativeLinkReleaseSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "nativeLinkReleaseAmiga";
				}
			};
		}

		@Override
		ExpectedNames compileCppTaskName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "compileReleaseCpp";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "compileFinalCpp";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "compileReleaseSparcCpp";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "compileReleaseAmigaCpp";
				}
			};
		}

		@Override
		ExpectedNames linkElementsConfigurationName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "releaseLinkElements";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "finalLinkElements";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "releaseSparcLinkElements";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "releaseAmigaLinkElements";
				}
			};
		}

		@Override
		ExpectedNames runtimeElementsConfigurationName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "releaseRuntimeElements";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "finalRuntimeElements";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "releaseSparcRuntimeElements";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "releaseAmigaRuntimeElements";
				}
			};
		}

		@Override
		ExpectedNames runTaskName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "runRelease";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "runFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "runReleaseSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "runReleaseAmiga";
				}
			};
		}

		@Override
		ExpectedNames extractSymbolsTaskName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "extractSymbolsRelease";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "extractSymbolsFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "extractSymbolsReleaseSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "extractSymbolsReleaseAmiga";
				}
			};
		}

		@Override
		ExpectedNames stripSymbolsTaskName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "stripSymbolsRelease";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "stripSymbolsFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "stripSymbolsReleaseSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "stripSymbolsReleaseAmiga";
				}
			};
		}

		@Override
		ExpectedNames installTaskName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "installRelease";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "installFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "installReleaseSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "installReleaseAmiga";
				}
			};
		}

		@Override
		ExpectedNames relocateMainToBinaryTaskName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "relocateMainToRelease";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "relocateMainToFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "relocateMainToReleaseSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "relocateMainToReleaseAmiga";
				}
			};
		}

		@Override
		ExpectedNames linkName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "linkRelease";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "linkFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "linkReleaseSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "linkReleaseAmiga";
				}
			};
		}

		@Override
		ExpectedNames createName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "createRelease";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "createFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "createReleaseSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "createReleaseAmiga";
				}
			};
		}

		@Override
		ExpectedNames assembleName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "assembleRelease";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "assembleFinal";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "assembleReleaseSparc";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "assembleReleaseAmiga";
				}
			};
		}

		@Override
		ExpectedNames compileCppName() {
			return new ExpectedNames() {
				@Override
				public String host() {
					return "compileReleaseCpp";
				}

				@Override
				public String hostWithFinalBuildType() {
					return "compileFinalCpp";
				}

				@Override
				public String hostWithSparcArchitecture() {
					return "compileReleaseSparcCpp";
				}

				@Override
				public String hostWithAmigaOperatingSystemFamily() {
					return "compileReleaseAmigaCpp";
				}
			};
		}
	}

	abstract class ProductionComponentTester extends ComponentBasedNameTester {
		@Override
		String implementationComponentConfigurationName() {
			return "implementation";
		}

		@Override
		String cppApiElementsConfigurationName() {
			return "cppApiElements";
		}

		@Override
		String apiConfigurationName() {
			return "api";
		}

		@Override
		String assembleComponentTaskName() {
			return "assemble";
		}
	}

	interface ExpectedNames {
		String host();
		String hostWithFinalBuildType();
		String hostWithSparcArchitecture();
		String hostWithAmigaOperatingSystemFamily();
	}

	@Nested
	class CppApplicationTests {
		@BeforeAll
		static void setup(@TempDir File testDirectory) {
			project = ProjectBuilder.builder().withProjectDir(testDirectory).build();
			project.getPluginManager().apply("cpp-application");
			((ProjectInternal) project).evaluate();
		}

		@Nested
		class Component {
			@Subject CppComponent hostComponent() {
				return project.getExtensions().getByType(CppApplication.class);
			}

			@Nested
			class StringTests extends ProductionComponentTester implements StringNamers {}

			@Nested
			class CppNamesTests extends ProductionComponentTester implements CppNamesNamers {}

			@Nested
			class NamesTests extends ProductionComponentTester implements NamesNamers {}

			@Nested
			class ComponentDomainObjectTester {
				@Test
				void canLocateAssembleTask(@Subject CppComponent component) {
					assertThat(project.getTasks(), hasItem(named(assembleTaskName(component))));
				}

				@Test
				void canLocateImplementationConfiguration(@Subject CppComponent component) {
					assertThat(project.getConfigurations(), hasItem(named(implementationConfigurationName(component))));
				}
			}
		}

		@Nested
		class Debug extends DebugTester {
			@Subject("host") CppBinary hostDebugBinary() {
				return project.getExtensions().getByType(CppApplication.class).getBinaries().get().stream().filter(it -> !it.isOptimized()).findFirst().orElseThrow();
			}

			@Nested
			class BinaryDomainObjectTests extends CppExecutableTester {}
		}

		@Nested
		class Release extends ReleaseTester {
			@Subject("host") CppBinary hostReleaseBinary() {
				return project.getExtensions().getByType(CppApplication.class).getBinaries().get().stream().filter(it -> it.isOptimized()).findFirst().orElseThrow();
			}

			@Nested
			class BinaryDomainObjectTests extends CppExecutableTester implements SymbolsTasksTester {}
		}
	}

	@Nested
	class CppLibraryTests {
		@BeforeAll
		static void setup(@TempDir File testDirectory) {
			project = ProjectBuilder.builder().withProjectDir(testDirectory).build();
			project.getPluginManager().apply("cpp-library");
		}

		@Nested
		class Component {
			@Subject CppComponent hostComponent() {
				return project.getExtensions().getByType(CppLibrary.class);
			}

			@Nested
			class StringTests extends ProductionComponentTester implements StringNamers {}

			@Nested
			class CppNamesTests extends ProductionComponentTester implements CppNamesNamers {}

			@Nested
			class NamesTests extends ProductionComponentTester implements NamesNamers {}

			@Nested
			class ComponentDomainObjectTester {
				@Test
				void canLocateAssembleTask(@Subject CppComponent component) {
					assertThat(project.getTasks(), hasItem(named(assembleTaskName(component))));
				}

				@Test
				void canLocateImplementationConfiguration(@Subject CppComponent component) {
					assertThat(project.getConfigurations(), hasItem(named(implementationConfigurationName(component))));
				}

				@Test
				void canLocateApiConfiguration(@Subject CppComponent component) {
					assertThat(project.getConfigurations(), hasItem(named(CppNames.apiConfigurationName(component))));
				}

				@Test
				void canLocateCppApiElementsConfiguration(@Subject CppComponent component) {
					assertThat(project.getConfigurations(), hasItem(named(CppNames.cppApiElementsConfigurationName(component))));
				}
			}
		}

		@Nested
		class CppStaticLibraryTests {
			@BeforeAll
			static void setup(@TempDir File testDirectory) {
				project = ProjectBuilder.builder().withProjectDir(testDirectory).build();
				project.getPluginManager().apply("cpp-library");
				project.getExtensions().getByType(CppLibrary.class).getLinkage().set(singletonList(STATIC));
				((ProjectInternal) project).evaluate();
			}

			@Nested
			class Debug extends DebugTester {
				@Subject("host") CppBinary hostDebugBinary() {
					return project.getExtensions().getByType(CppLibrary.class).getBinaries().get().stream().filter(it -> !it.isOptimized()).findFirst().orElseThrow();
				}

				@Nested
				class BinaryDomainObjectTests extends CppStaticLibraryTester {
				}
			}

			@Nested
			class Release extends ReleaseTester {
				@Subject("host") CppBinary hostReleaseBinary() {
					return project.getExtensions().getByType(CppLibrary.class).getBinaries().get().stream().filter(it -> it.isOptimized()).findFirst().orElseThrow();
				}

				@Nested
				class BinaryDomainObjectTests extends CppStaticLibraryTester {}
			}
		}

		@Nested
		class CppSharedLibraryTests {
			@BeforeAll
			static void setup(@TempDir File testDirectory) {
				project = ProjectBuilder.builder().withProjectDir(testDirectory).build();
				project.getPluginManager().apply("cpp-library");
				project.getExtensions().getByType(CppLibrary.class).getLinkage().set(singletonList(SHARED));
				((ProjectInternal) project).evaluate();
			}

			@Nested
			class Debug extends DebugTester {
				@Subject("host") CppBinary hostDebugBinary() {
					return project.getExtensions().getByType(CppLibrary.class).getBinaries().get().stream().filter(it -> !it.isOptimized()).findFirst().orElseThrow();
				}

				@Nested
				class BinaryDomainObjectTests extends CppSharedLibraryTester {
				}
			}

			@Nested
			class Release extends ReleaseTester {
				@Subject("host") CppBinary hostReleaseBinary() {
					return project.getExtensions().getByType(CppLibrary.class).getBinaries().get().stream().filter(it -> it.isOptimized()).findFirst().orElseThrow();
				}

				@Nested
				class BinaryDomainObjectTests extends CppSharedLibraryTester implements SymbolsTasksTester {
				}
			}
		}
	}

	interface CppBinaryTaskTester {
		@Test
		default void canLocateCompileTask(@Subject("host") CppBinary binary) {
			assertThat(project.getTasks(), hasItem(named(compileTaskName(binary))));
		}

		@Test
		default void canLocateCppCompileConfiguration(@Subject("host") CppBinary binary) {
			assertThat(project.getConfigurations(), hasItem(named(cppCompileConfigurationName(binary))));
		}

		@Test
		default void canLocateNativeLinkConfiguration(@Subject("host") CppBinary binary) {
			assertThat(project.getConfigurations(), hasItem(named(nativeLinkConfigurationName(binary))));
		}

		@Test
		default void canLocateNativeRuntimeConfiguration(@Subject("host") CppBinary binary) {
			assertThat(project.getConfigurations(), hasItem(named(nativeRuntimeConfigurationName(binary))));
		}

		@Test
		default void canLocateImplementationConfiguration(@Subject("host") CppBinary binary) {
			assertThat(project.getConfigurations(), hasItem(named(implementationConfigurationName(binary))));
		}
	}

	public interface SymbolsTasksTester {
		@Test
		default void canLocateExtractSymbolsTask(@Subject("host") CppBinary binary) {
			assertThat(project.getTasks(), hasItem(named(extractSymbolsTaskName(binary))));
		}

		@Test
		default void canLocateStripSymbolsTask(@Subject("host") CppBinary binary) {
			assertThat(project.getTasks(), hasItem(named(stripSymbolsTaskName(binary))));
		}
	}

	public abstract class CppExecutableTester implements CppBinaryTaskTester {
		@Test
		void canLocateRuntimeElementsConfiguration(@Subject("host") CppBinary binary) {
			assertThat(project.getConfigurations(), hasItem(named(runtimeElementsConfigurationName(binary))));
		}

		@Test
		void canLocateLinkTask(@Subject("host") CppBinary binary) {
			assertThat(project.getTasks(), hasItem(named(linkTaskName(binary))));
		}

		@Test
		void canLocateInstallTask(@Subject("host") CppBinary binary) {
			assertThat(project.getTasks(), hasItem(named(installTaskName(binary))));
		}

		@Test
		void canLocateAssembleTask(@Subject("host") CppBinary binary) {
			assertThat(project.getTasks(), hasItem(named(assembleTaskName(binary))));
		}
	}

	public abstract class CppStaticLibraryTester implements CppBinaryTaskTester {
		@Test
		void canLocateCreateTask(@Subject("host") CppBinary binary) {
			assertThat(project.getTasks(), hasItem(named(createTaskName(binary))));
		}

		@Test
		void canLocateLinkElementsConfiguration(@Subject("host") CppBinary binary) {
			assertThat(project.getConfigurations(), hasItem(named(linkElementsConfigurationName(binary))));
		}

		@Test
		void canLocateRuntimeElementsConfiguration(@Subject("host") CppBinary binary) {
			assertThat(project.getConfigurations(), hasItem(named(runtimeElementsConfigurationName(binary))));
		}

		@Test
		void canLocateAssembleTask(@Subject("host") CppBinary binary) {
			assertThat(project.getTasks(), hasItem(named(assembleTaskName(binary))));
		}
	}

	public abstract class CppSharedLibraryTester implements CppBinaryTaskTester {
		@Test
		void canLocateLinkTask(@Subject("host") CppBinary binary) {
			assertThat(project.getTasks(), hasItem(named(linkTaskName(binary))));
		}

		@Test
		void canLocateLinkElementsConfiguration(@Subject("host") CppBinary binary) {
			assertThat(project.getConfigurations(), hasItem(named(linkElementsConfigurationName(binary))));
		}

		@Test
		void canLocateRuntimeElementsConfiguration(@Subject("host") CppBinary binary) {
			assertThat(project.getConfigurations(), hasItem(named(runtimeElementsConfigurationName(binary))));
		}

		@Test
		void canLocateAssembleTask(@Subject("host") CppBinary binary) {
			assertThat(project.getTasks(), hasItem(named(assembleTaskName(binary))));
		}
	}

	public abstract class CppTestExecutableTester implements CppBinaryTaskTester {
		@Test
		void canLocateRunTask(@Subject("host") CppTestExecutable binary) {
			assertThat(project.getTasks(), hasItem(named(CppNames.runTaskName(binary))));
		}

		@Test
		void canLocateInstallTask(@Subject("host") CppBinary binary) {
			assertThat(project.getTasks(), hasItem(named(installTaskName(binary))));
		}

		@Test
		void canLocateLinkTask(@Subject("host") CppBinary binary) {
			assertThat(project.getTasks(), hasItem(named(linkTaskName(binary))));
		}
	}

	@Nested
	class CppUnitTestTests {
		@BeforeAll
		static void setup(@TempDir File testDirectory) {
			project = ProjectBuilder.builder().withProjectDir(testDirectory).build();
			project.getPluginManager().apply("cpp-unit-test");
			((ProjectInternal) project).evaluate();
		}

		@Nested
		class Component {
			@Subject CppComponent hostComponent() {
				return project.getExtensions().getByType(CppTestSuite.class);
			}

			abstract class TestProductionComponentTester extends ComponentBasedNameTester {
				@Override
				String implementationComponentConfigurationName() {
					return "testImplementation";
				}

				@Override
				String cppApiElementsConfigurationName() {
					return "testCppApiElements";
				}

				@Override
				String apiConfigurationName() {
					return "testApi";
				}

				@Override
				String assembleComponentTaskName() {
					return "assembleTest";
				}
			}

			@Nested
			class StringTests extends TestProductionComponentTester implements StringNamers {}

			@Nested
			class CppNamesTests extends TestProductionComponentTester implements CppNamesNamers {}

			@Nested
			class NamesTests extends TestProductionComponentTester implements NamesNamers {}


			@Nested
			class ComponentDomainObjectTester {
				@Test
				@SkipWhenNoSubject
				void canLocateAssembleTask(@Subject ProductionCppComponent component) {
					assertThat(project.getTasks(), hasItem(named(assembleTaskName(component))));
				}

				@Test
				void canLocateImplementationConfiguration(@Subject CppComponent component) {
					assertThat(project.getConfigurations(), hasItem(named(implementationConfigurationName(component))));
				}
			}
		}

		@Nested
		class DefaultTests extends ComponentTester {
			@Subject("host") CppTestExecutable hostDefaultBinary() {
				return project.getExtensions().getByType(CppTestSuite.class).getBinaries().get().stream().map(CppTestExecutable.class::cast).findFirst().orElseThrow();
			}

			@Nested
			class BinaryDomainObjectTests extends CppTestExecutableTester {}

			@Override
			ExpectedNames cppCompileConfigurationName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "cppCompileTest";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "cppCompileTestFinal";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "cppCompileTestSparc";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "cppCompileTestAmiga";
					}
				};
			}

			@Override
			ExpectedNames implementationConfigurationName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "testExecutableImplementation";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "testFinalExecutableImplementation"; // ???
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "testSparcExecutableImplementation";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "testAmigaExecutableImplementation";
					}
				};
			}

			@Override
			ExpectedNames nativeRuntimeConfigurationName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "nativeRuntimeTest";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "nativeRuntimeTestFinal";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "nativeRuntimeTestSparc";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "nativeRuntimeTestAmiga";
					}
				};
			}

			@Override
			ExpectedNames nativeLinkConfigurationName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "nativeLinkTest";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "nativeLinkTestFinal";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "nativeLinkTestSparc";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "nativeLinkTestAmiga";
					}
				};
			}

			@Override
			ExpectedNames compileCppTaskName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "compileTestCpp";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "compileTestFinalCpp";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "compileTestSparcCpp";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "compileTestAmigaCpp";
					}
				};
			}

			@Override
			ExpectedNames linkElementsConfigurationName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "testLinkElements";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "testFinalLinkElements";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "testSparcLinkElements";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "testAmigaLinkElements";
					}
				};
			}

			@Override
			ExpectedNames runtimeElementsConfigurationName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "testRuntimeElements";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "testFinalRuntimeElements";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "testSparcRuntimeElements";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "testAmigaRuntimeElements";
					}
				};
			}

			@Override
			ExpectedNames linkName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "linkTest";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "linkTestFinal";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "linkTestSparc";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "linkTestAmiga";
					}
				};
			}

			@Override
			ExpectedNames createName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "createTest";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "createTestFinal";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "createTestSparc";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "createTestAmiga";
					}
				};
			}

			@Override
			ExpectedNames assembleName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "assembleTest";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "assembleTestFinal";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "assembleTestSparc";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "assembleTestAmiga";
					}
				};
			}

			@Override
			ExpectedNames compileCppName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "compileTestCpp";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "compileTestFinalCpp";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "compileTestSparcCpp";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "compileTestAmigaCpp";
					}
				};
			}

			@Override
			ExpectedNames runTaskName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "runTest";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "runTestFinal";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "runTestSparc";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "runTestAmiga";
					}
				};
			}

			@Override
			ExpectedNames extractSymbolsTaskName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "extractSymbolsTest";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "extractSymbolsTestFinal";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "extractSymbolsTestSparc";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "extractSymbolsTestAmiga";
					}
				};
			}

			@Override
			ExpectedNames stripSymbolsTaskName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "stripSymbolsTest";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "stripSymbolsTestFinal";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "stripSymbolsTestSparc";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "stripSymbolsTestAmiga";
					}
				};
			}

			@Override
			ExpectedNames installTaskName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "installTest";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "installTestFinal";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "installTestSparc";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "installTestAmiga";
					}
				};
			}

			@Override
			ExpectedNames relocateMainToBinaryTaskName() {
				return new ExpectedNames() {
					@Override
					public String host() {
						return "relocateMainForTest";
					}

					@Override
					public String hostWithFinalBuildType() {
						return "relocateMainForTestFinal";
					}

					@Override
					public String hostWithSparcArchitecture() {
						return "relocateMainForTestSparc";
					}

					@Override
					public String hostWithAmigaOperatingSystemFamily() {
						return "relocateMainForTestAmiga";
					}
				};
			}
		}
	}
}
