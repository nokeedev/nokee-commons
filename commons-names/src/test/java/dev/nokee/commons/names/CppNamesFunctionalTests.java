package dev.nokee.commons.names;

import dev.gradleplugins.buildscript.GradleDsl;
import dev.gradleplugins.buildscript.ast.expressions.Expression;
import dev.gradleplugins.buildscript.io.GradleBuildFile;
import dev.gradleplugins.buildscript.io.GradleSettingsFile;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import static dev.gradleplugins.buildscript.blocks.BuildscriptBlock.classpath;
import static dev.gradleplugins.buildscript.blocks.DependencyNotation.files;
import static dev.gradleplugins.buildscript.syntax.Syntax.*;

class CppNamesFunctionalTests {
	@TempDir Path testDirectory;
	GradleBuildFile buildFile;
	GradleSettingsFile settingsFile;
	GradleRunner runner;

	@BeforeEach
	void setUp() {
		runner = GradleRunner.create().forwardOutput().withPluginClasspath().withProjectDir(testDirectory.toFile());

		buildFile = GradleBuildFile.inDirectory(testDirectory);
		settingsFile = GradleSettingsFile.inDirectory(testDirectory);

		buildFile.append(groovyDsl(
			"tasks.register('verify')"
		));
		buildFile.append(staticImportClass(CppNames.class));
		buildFile.append(staticImportClass(NameBuilder.class));
		settingsFile.buildscript(it -> it.dependencies(classpath(files(runner.getPluginClasspath()))));

		runner.withArguments("verify");
	}

	Expression verifyThat(String... assertions) {
		return groovyDsl(
			"tasks.named('verify') {",
			"  doLast {",
			Arrays.stream(assertions).map(it -> "    assert " + it).collect(Collectors.joining("\n")),
			"  }",
			"}"
		);
	}

	abstract class ComponentNamesTester {
		abstract String componentDsl();

		@Test
		void testImplementationConfigurationName() {
			buildFile.append(verifyThat("implementationConfigurationName(" + componentDsl() + ") == " + implementationConfigurationName().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract Expression implementationConfigurationName();

		@Test
		void testCppApiElementsConfigurationName() {
			buildFile.append(verifyThat("cppApiElementsConfigurationName(" + componentDsl() + ") == " + cppApiElementsConfigurationName().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract Expression cppApiElementsConfigurationName();

		@Test
		void testApiConfigurationName() {
			buildFile.append(verifyThat("apiConfigurationName(" + componentDsl() + ") == " + apiConfigurationName().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract Expression apiConfigurationName();
	}

	abstract class BinaryNamesTester {
		@Test
		void testNativeLinkConfigurationName() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { nativeLinkConfigurationName(it) } == " + nativeLinkConfigurationNames().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract Expression nativeLinkConfigurationNames();

		@Test
		void testNativeRuntimeConfigurationName() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { nativeRuntimeConfigurationName(it) } == " + nativeRuntimeConfigurationNames().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract Expression nativeRuntimeConfigurationNames();

		@Test
		void testRuntimeElementsConfigurationName() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { runtimeElementsConfigurationName(it) } == " + runtimeElementsConfigurationNames().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract Expression runtimeElementsConfigurationNames();

		@Test
		void testLinkElementsConfigurationName() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { linkElementsConfigurationName(it) } == " + linkElementsConfigurationNames().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract Expression linkElementsConfigurationNames();

		@Test
		void testImplementationConfigurationName() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { implementationConfigurationName(it) } == " + implementationConfigurationNames().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract Expression implementationConfigurationNames();

		@Test
		void testCppCompileConfigurationName() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { cppCompileConfigurationName(it) } == " + cppCompileConfigurationNames().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract Expression cppCompileConfigurationNames();

		@Test
		void testCompileTaskName() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { compileTaskName(it) } == " + compileCppTaskNames().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract Expression compileCppTaskNames();

		@Test
		void testCompileCTaskName() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { compileTaskName(it, 'c') } == " + compileCTaskNames().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract Expression compileCTaskNames();

		@Test
		void testLinkTaskName() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { linkTaskName(it) } == " + linkTaskNames().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract Expression linkTaskNames();
	}

	interface ExpectedNames {
		Expression shortName();
		Expression longName();
	}

	abstract class PropTester {
		@Test
		void testDefaultQualifyingNames() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { qualifyingName(it).toString() } == " + qualifyingNames().shortName().toString(GradleDsl.GROOVY)));
			buildFile.append(verifyThat("components.withType(CppBinary).collect { qualifyingName(it).toString(lowerCamelCase()) } == " + qualifyingNames().longName().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract ExpectedNames qualifyingNames();

		@Test
		void testBuildTypeQualifyingNames() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { qualifyingName(it).with('buildTypeName', 'dbgopt').toString() } as Set == " + dbgoptQualifyingNames().shortName().toString(GradleDsl.GROOVY) + " as Set"));
			buildFile.append(verifyThat("components.withType(CppBinary).collect { qualifyingName(it).with('buildTypeName', 'dbgopt').toString(lowerCamelCase()) } as Set == " + dbgoptQualifyingNames().longName().toString(GradleDsl.GROOVY) + " as Set"));
			runner.build();
		}

		abstract ExpectedNames dbgoptQualifyingNames();

		@Test
		void testOperatingSystemFamilyQualifyingNames() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { qualifyingName(it).with('operatingSystemFamilyName', 'freebsd').toString() } == " + freebsdQualifyingNames().shortName().toString(GradleDsl.GROOVY)));
			buildFile.append(verifyThat("components.withType(CppBinary).collect { qualifyingName(it).with('operatingSystemFamilyName', 'freebsd').toString(lowerCamelCase()) } == " + freebsdQualifyingNames().longName().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract ExpectedNames freebsdQualifyingNames();

		@Test
		void testMachineArchitectureQualifyingNames() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { qualifyingName(it).with('architectureName', 'mips').toString() } == " + mipsQualifyingNames().shortName().toString(GradleDsl.GROOVY)));
			buildFile.append(verifyThat("components.withType(CppBinary).collect { qualifyingName(it).with('architectureName', 'mips').toString(lowerCamelCase()) } == " + mipsQualifyingNames().longName().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract ExpectedNames mipsQualifyingNames();
	}

	@Nested
	class LibraryComponentNames extends ComponentNamesTester {
		@BeforeEach
		void setup() {
			buildFile.plugins(it -> it.id("cpp-library"));
		}

		String componentDsl() {
			return "library";
		}

		@Override
		Expression implementationConfigurationName() {
			return string("implementation");
		}

		@Override
		Expression cppApiElementsConfigurationName() {
			return string("cppApiElements");
		}

		@Override
		Expression apiConfigurationName() {
			return string("api");
		}

		@Nested
		class QualifyingNameTests extends PropTester {
			@Override
			ExpectedNames qualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("debug"), string("release"));
					}

					@Override
					public Expression longName() {
						return listOf(string("mainDebugSharedLinuxAarch64"), string("mainReleaseSharedLinuxAarch64"));
					}
				};
			}

			@Override
			ExpectedNames dbgoptQualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("dbgopt"));
					}

					@Override
					public Expression longName() {
						return listOf(string("mainDbgoptSharedLinuxAarch64"));
					}
				};
			}

			@Override
			ExpectedNames freebsdQualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("debugFreebsd"), string("releaseFreebsd"));
					}

					@Override
					public Expression longName() {
						return listOf(string("mainDebugSharedFreebsdAarch64"), string("mainReleaseSharedFreebsdAarch64"));
					}
				};
			}

			@Override
			ExpectedNames mipsQualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("debugMips"), string("releaseMips"));
					}

					@Override
					public Expression longName() {
						return listOf(string("mainDebugSharedLinuxMips"), string("mainReleaseSharedLinuxMips"));
					}
				};
			}

			@Test
			void testBundleLinkageName() {
				buildFile.append(verifyThat(
					"components.withType(CppBinary).collect { qualifyingName(it).with('linkageName', 'bundle').toString() } as Set == ['debugBundle', 'releaseBundle'] as Set",
					"components.withType(CppBinary).collect { qualifyingName(it).with('linkageName', 'bundle').toString(lowerCamelCase()) } as Set == ['mainDebugBundleLinuxAarch64', 'mainReleaseBundleLinuxAarch64'] as Set"
				));
				runner.build();
			}
		}

		@Nested
		class BinaryNames extends BinaryNamesTester {
			@BeforeEach
			void setup() {
				buildFile.append(groovyDsl(
					"library.linkage = [Linkage.SHARED, Linkage.STATIC]"
				));
			}

			@Override
			Expression nativeLinkConfigurationNames() {
				return listOf(string("nativeLinkDebugShared"), string("nativeLinkDebugStatic"), string("nativeLinkReleaseShared"), string("nativeLinkReleaseStatic"));
			}

			@Override
			Expression nativeRuntimeConfigurationNames() {
				return listOf(string("nativeRuntimeDebugShared"), string("nativeRuntimeDebugStatic"), string("nativeRuntimeReleaseShared"), string("nativeRuntimeReleaseStatic"));
			}

			@Override
			Expression runtimeElementsConfigurationNames() {
				return listOf(string("debugSharedRuntimeElements"), string("debugStaticRuntimeElements"), string("releaseSharedRuntimeElements"), string("releaseStaticRuntimeElements"));
			}

			@Override
			Expression linkElementsConfigurationNames() {
				return listOf(string("debugSharedLinkElements"), string("debugStaticLinkElements"), string("releaseSharedLinkElements"), string("releaseStaticLinkElements"));
			}

			@Override
			Expression implementationConfigurationNames() {
				return listOf(string("mainDebugSharedImplementation"), string("mainDebugStaticImplementation"), string("mainReleaseSharedImplementation"), string("mainReleaseStaticImplementation"));
			}

			@Override
			Expression cppCompileConfigurationNames() {
				return listOf(string("cppCompileDebugShared"), string("cppCompileDebugStatic"), string("cppCompileReleaseShared"), string("cppCompileReleaseStatic"));
			}

			@Override
			Expression compileCppTaskNames() {
				return listOf(string("compileDebugSharedCpp"), string("compileDebugStaticCpp"), string("compileReleaseSharedCpp"), string("compileReleaseStaticCpp"));
			}

			@Override
			Expression compileCTaskNames() {
				return listOf(string("compileDebugSharedC"), string("compileDebugStaticC"), string("compileReleaseSharedC"), string("compileReleaseStaticC"));
			}

			@Override
			Expression linkTaskNames() {
				return listOf(string("linkDebugShared"), string("linkDebugStatic"), string("linkReleaseShared"), string("linkReleaseStatic"));
			}
		}
	}

	@Nested
	class ApplicationComponentNames extends ComponentNamesTester {
		@BeforeEach
		void setup() {
			buildFile.plugins(it -> it.id("cpp-application"));
		}

		String componentDsl() {
			return "application";
		}

		@Override
		Expression implementationConfigurationName() {
			return string("implementation");
		}

		@Override
		Expression cppApiElementsConfigurationName() {
			return string("cppApiElements");
		}

		@Override
		Expression apiConfigurationName() {
			return string("api");
		}

		@Nested
		class QualifyingNameTests extends PropTester {
			@Override
			ExpectedNames qualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("debug"), string("release"));
					}

					@Override
					public Expression longName() {
						return listOf(string("mainDebugLinuxAarch64"), string("mainReleaseLinuxAarch64"));
					}
				};
			}

			@Override
			ExpectedNames dbgoptQualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("dbgopt"));
					}

					@Override
					public Expression longName() {
						return listOf(string("mainDbgoptLinuxAarch64"));
					}
				};
			}

			@Override
			ExpectedNames freebsdQualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("debugFreebsd"), string("releaseFreebsd"));
					}

					@Override
					public Expression longName() {
						return listOf(string("mainDebugFreebsdAarch64"), string("mainReleaseFreebsdAarch64"));
					}
				};
			}

			@Override
			ExpectedNames mipsQualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("debugMips"), string("releaseMips"));
					}

					@Override
					public Expression longName() {
						return listOf(string("mainDebugLinuxMips"), string("mainReleaseLinuxMips"));
					}
				};
			}
		}

		@Nested
		class BinaryNames extends BinaryNamesTester {
			@BeforeEach
			void setup() {
				buildFile.append(groovyDsl(
					"application.targetMachines = [machines.host(), machines.linux.x86]"
				));
			}

			@Override
			Expression nativeLinkConfigurationNames() {
				return listOf(string("nativeLinkDebugAarch64"), string("nativeLinkDebugX86"), string("nativeLinkReleaseAarch64"), string("nativeLinkReleaseX86"));
			}

			@Override
			Expression nativeRuntimeConfigurationNames() {
				return listOf(string("nativeRuntimeDebugAarch64"), string("nativeRuntimeDebugX86"), string("nativeRuntimeReleaseAarch64"), string("nativeRuntimeReleaseX86"));
			}

			@Override
			Expression runtimeElementsConfigurationNames() {
				return listOf(string("debugAarch64RuntimeElements"), string("debugX86RuntimeElements"), string("releaseAarch64RuntimeElements"), string("releaseX86RuntimeElements"));
			}

			@Override
			Expression linkElementsConfigurationNames() {
				return listOf(string("debugAarch64LinkElements"), string("debugX86LinkElements"), string("releaseAarch64LinkElements"), string("releaseX86LinkElements"));
			}

			@Override
			Expression implementationConfigurationNames() {
				return listOf(string("mainDebugAarch64Implementation"), string("mainDebugX86Implementation"), string("mainReleaseAarch64Implementation"), string("mainReleaseX86Implementation"));
			}

			@Override
			Expression cppCompileConfigurationNames() {
				return listOf(string("cppCompileDebugAarch64"), string("cppCompileDebugX86"), string("cppCompileReleaseAarch64"), string("cppCompileReleaseX86"));
			}

			@Override
			Expression compileCppTaskNames() {
				return listOf(string("compileDebugAarch64Cpp"), string("compileDebugX86Cpp"), string("compileReleaseAarch64Cpp"), string("compileReleaseX86Cpp"));
			}

			@Override
			Expression compileCTaskNames() {
				return listOf(string("compileDebugAarch64C"), string("compileDebugX86C"), string("compileReleaseAarch64C"), string("compileReleaseX86C"));
			}

			@Override
			Expression linkTaskNames() {
				return listOf(string("linkDebugAarch64"), string("linkDebugX86"), string("linkReleaseAarch64"), string("linkReleaseX86"));
			}
		}
	}

	@Nested
	class UnitTestComponentNames extends ComponentNamesTester {
		@BeforeEach
		void setup() {
			buildFile.plugins(it -> it.id("cpp-unit-test"));
		}

		String componentDsl() {
			return "unitTest";
		}

		@Override
		Expression implementationConfigurationName() {
			return string("testImplementation");
		}

		@Override
		Expression cppApiElementsConfigurationName() {
			return string("testCppApiElements");
		}

		@Override
		Expression apiConfigurationName() {
			return string("testApi");
		}

		@Nested
		class QualifyingNameTests extends PropTester {
			@Override
			ExpectedNames qualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("test"));
					}

					@Override
					public Expression longName() {
						return listOf(string("testDebugLinuxAarch64"));
					}
				};
			}

			@Override
			ExpectedNames dbgoptQualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("testDbgopt"));
					}

					@Override
					public Expression longName() {
						return listOf(string("testDbgoptLinuxAarch64"));
					}
				};
			}

			@Override
			ExpectedNames freebsdQualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("testFreebsd"));
					}

					@Override
					public Expression longName() {
						return listOf(string("testDebugFreebsdAarch64"));
					}
				};
			}

			@Override
			ExpectedNames mipsQualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("testMips"));
					}

					@Override
					public Expression longName() {
						return listOf(string("testDebugLinuxMips"));
					}
				};
			}
		}

		@Nested
		class BinaryNames extends BinaryNamesTester {
			@BeforeEach
			void setup() {
				buildFile.append(groovyDsl(
					"unitTest.targetMachines = [machines.host(), machines.windows]"
				));
			}

			@Override
			Expression nativeLinkConfigurationNames() {
				return listOf(string("nativeLinkTestLinux"));
			}

			@Override
			Expression nativeRuntimeConfigurationNames() {
				return listOf(string("nativeRuntimeTestLinux"));
			}

			@Override
			Expression runtimeElementsConfigurationNames() {
				return listOf(string("testLinuxRuntimeElements"));
			}

			@Override
			Expression linkElementsConfigurationNames() {
				return listOf(string("testLinuxLinkElements"));
			}

			@Override
			Expression implementationConfigurationNames() {
				return listOf(string("testLinuxExecutableImplementation"));
			}

			@Override
			Expression cppCompileConfigurationNames() {
				return listOf(string("cppCompileTestLinux"));
			}

			@Override
			Expression compileCppTaskNames() {
				return listOf(string("compileTestLinuxCpp"));
			}

			@Override
			Expression compileCTaskNames() {
				return listOf(string("compileTestLinuxC"));
			}

			@Override
			Expression linkTaskNames() {
				return listOf(string("linkTestLinux"));
			}
		}
	}

//	@Nested
//	class Bob {
//		@BeforeEach
//		void setup() {
//			buildFile.plugins(it -> it.id("cpp-library"));
//		}
//
//		String componentDsl() {
//			return "library";
//		}
//		String binariesDsl() {
//			return componentDsl() + ".binaries";
//		}
//		String targetMachinesDsl() {
//			return componentDsl() + ".targetMachines";
//		}
//
//		String shortQualifyingName(String buildType) {
//			return buildType;
//		}
//
//		@Test
//		void testNativeLinkConfigurationName() {
//			buildFile.append(verifyThat(
//				binariesDsl() + ".get().collect { nativeLinkConfigurationName(it).toString() } == ['nativeLink" + shortQualifyingName() + "Debug', 'nativeLink" + shortQualifyingName() + "Release']"
//			));
//			runner.build();
//		}
//
//		@Test
//		void testNativeRuntimeConfigurationName() {
//			buildFile.append(verifyThat(
//				binariesDsl() + ".get().collect { nativeRuntimeConfigurationName(it).toString() } == ['nativeRuntime" + shortQualifyingName() + "Debug', 'nativeRuntime" + shortQualifyingName() + "Release']"
//			));
//			runner.build();
//		}
//
//		@Test
//		void testRuntimeElementsConfigurationName() {
//			buildFile.append(verifyThat(binariesDsl() + ".get().collect { runtimeElementsConfigurationName(it).toString() } == ['debug" + shortQualifyingName() + "RuntimeElements', 'release" + shortQualifyingName() + "RuntimeElements']"));
//			runner.build();
//		}
//
//		@Test
//		void testLinkElementsConfigurationName() {
//			buildFile.append(verifyThat(binariesDsl() + ".get().collect { linkElementsConfigurationName(it).toString() } == ['debug" + shortQualifyingName() + "LinkElements', 'release" + shortQualifyingName() + "LinkElements']"));
//			runner.build();
//		}
//
//		@Test
//		void testImplementationConfigurationName() {
//			buildFile.append(verifyThat(binariesDsl() + ".get().collect { implementationConfigurationName(it).toString() } == ['mainDebug" + shortQualifyingName() + "Implementation', 'mainRelease" + shortQualifyingName() + "Implementation']"));
//			runner.build();
//		}
//
//		@Test
//		void testCppCompileConfigurationName() {
//			buildFile.append(verifyThat(binariesDsl() + ".get().collect { cppCompileConfigurationName(it).toString() } == ['cppCompileDebug" + shortQualifyingName() + "', 'cppCompileRelease" + shortQualifyingName() + "']"));
//			runner.build();
//		}
//
//		@Test
//		void testCompileTaskName() {
//			buildFile.append(verifyThat(binariesDsl() + ".get().collect { compileTaskName(it).toString() } == ['compileDebug" + shortQualifyingName() + "Cpp', 'compileRelease" + shortQualifyingName() + "Cpp']"));
//			runner.build();
//		}
//
//		@Test
//		void testCompileCTaskName() {
//			buildFile.append(verifyThat(binariesDsl() + ".get().collect { compileTaskName(it, 'c').toString() } == ['compileDebug" + shortQualifyingName() + "C', 'compileRelease" + shortQualifyingName() + "C']"));
//			runner.build();
//		}
//
//		@Test
//		void testLinkTaskName() {
//			buildFile.append(verifyThat(binariesDsl() + ".get().collect { linkTaskName(it).toString() } == ['linkDebug" + shortQualifyingName() + "', 'linkRelease" + shortQualifyingName() + "']"));
//			runner.build();
//		}
//	}

	abstract class Tester {
		abstract String componentDsl();
		String binariesDsl() {
			return componentDsl() + ".binaries";
		}
		String targetMachinesDsl() {
			return componentDsl() + ".targetMachines";
		}

		abstract String baseShortName();
		abstract String baseLongName();

		@Test
		void zzzzzzcanCreateCppNames() {
			buildFile.append(verifyThat(
//				binariesDsl() + ".get().first().with { qualifyingName(it).propSet() } == 'foo'",
				binariesDsl() + ".get().first().with { qualifyingName(it).with('buildTypeName', 'dbgopt').toString() } == " + debugOptShortName(),
				binariesDsl() + ".get().first().with { qualifyingName(it).with('buildTypeName', 'dbgopt').toString(lowerCamelCase()) } == " + debugOptLongName()
			));
			runner.build();
		}

		abstract String debugOptShortName();
		abstract String debugOptLongName();

		@Test
		void canCreateCppNames() {
			buildFile.append(verifyThat(
				binariesDsl() + ".get().collect { qualifyingName(it).toString() } == " + baseShortName(),
				binariesDsl() + ".get().collect { qualifyingName(it).toString(lowerCamelCase()) } == " + baseLongName()
			));
			runner.build();
		}

		@Test
		void zzzcanCreateCppNames() {
			buildFile.append(groovyDsl(targetMachinesDsl() + " = [machines.host(), machines.windows]"));
			buildFile.append(verifyThat(
				binariesDsl() + ".get().collect { qualifyingName(it).toString() } == " + ambiguousOsFamilyShortName(),
				binariesDsl() + ".get().collect { qualifyingName(it).toString(lowerCamelCase()) } == " + ambiguousOsFamilyLongName()
			));
			runner.build();
		}

		abstract String ambiguousOsFamilyShortName();
		abstract String ambiguousOsFamilyLongName();

		@Test
		void zzzzcanCreateCppNames() {
			buildFile.append(groovyDsl(targetMachinesDsl() + " = [machines.host(), machines.linux.x86]"));
			buildFile.append(verifyThat(
				binariesDsl() + ".get().collect { qualifyingName(it).toString() } == " + ambiguousMachineArchitectureShortName(),
				binariesDsl() + ".get().collect { qualifyingName(it).toString(lowerCamelCase()) } == " + ambiguousMachineArchitectureLongName()
			));
			runner.build();
		}

		abstract String ambiguousMachineArchitectureShortName();
		abstract String ambiguousMachineArchitectureLongName();
	}

	@Nested
	class LibraryTests extends Tester {
		@BeforeEach
		void setup() {
			buildFile.plugins(it -> it.id("cpp-library"));
		}

		@Test
		void zcanCreateCppNames() {
			buildFile.append(groovyDsl("library.linkage = [Linkage.STATIC]"));
			buildFile.append(verifyThat(
				"library.binaries.get().collect { qualifyingName(it).toString() } == ['debug', 'release']",
				"library.binaries.get().collect { qualifyingName(it).toString(lowerCamelCase()) } == ['mainDebugStaticLinuxAarch64', 'mainReleaseStaticLinuxAarch64']"
			));
			runner.build();
		}

		@Override
		String debugOptLongName() {
			return "'mainDbgoptSharedLinuxAarch64'";
		}

		@Test
		void zzcanCreateCppNames() {
			buildFile.append(groovyDsl("library.linkage = [Linkage.SHARED, Linkage.STATIC]"));
			buildFile.append(verifyThat(
				"library.binaries.get().collect { qualifyingName(it).toString() } == ['debugShared', 'debugStatic', 'releaseShared', 'releaseStatic']",
				"library.binaries.get().collect { qualifyingName(it).toString(lowerCamelCase()) } == ['mainDebugSharedLinuxAarch64', 'mainDebugStaticLinuxAarch64', 'mainReleaseSharedLinuxAarch64', 'mainReleaseStaticLinuxAarch64']"
			));
			runner.build();
		}

		@Override
		String componentDsl() {
			return "library";
		}

		@Override
		String baseShortName() {
			return "['debug', 'release']";
		}

		@Override
		String baseLongName() {
			return "['mainDebugSharedLinuxAarch64', 'mainReleaseSharedLinuxAarch64']";
		}

		@Override
		String debugOptShortName() {
			return "'dbgopt'";
		}

		@Override
		String ambiguousOsFamilyShortName() {
			return "['debugLinux', 'releaseLinux']";
		}

		@Override
		String ambiguousOsFamilyLongName() {
			return "['mainDebugSharedLinuxAarch64', 'mainReleaseSharedLinuxAarch64']";
		}

		@Override
		String ambiguousMachineArchitectureShortName() {
			return "['debugAarch64', 'debugX86', 'releaseAarch64', 'releaseX86']";
		}

		@Override
		String ambiguousMachineArchitectureLongName() {
			return "['mainDebugSharedLinuxAarch64', 'mainDebugSharedLinuxX86', 'mainReleaseSharedLinuxAarch64', 'mainReleaseSharedLinuxX86']";
		}
	}

	@Nested
	class ApplicationTests extends Tester {
		@BeforeEach
		void setup() {
			buildFile.plugins(it -> it.id("cpp-application"));
		}

		@Override
		String componentDsl() {
			return "application";
		}

		@Override
		String baseShortName() {
			return "['debug', 'release']";
		}

		@Override
		String baseLongName() {
			return "['mainDebugLinuxAarch64', 'mainReleaseLinuxAarch64']";
		}

		@Override
		String debugOptShortName() {
			return "'dbgopt'";
		}

		@Override
		String debugOptLongName() {
			return "'mainDbgoptLinuxAarch64'";
		}

		@Override
		String ambiguousOsFamilyShortName() {
			return "['debugLinux', 'releaseLinux']";
		}

		@Override
		String ambiguousOsFamilyLongName() {
			return "['mainDebugLinuxAarch64', 'mainReleaseLinuxAarch64']";
		}

		@Override
		String ambiguousMachineArchitectureShortName() {
			return "['debugAarch64', 'debugX86', 'releaseAarch64', 'releaseX86']";
		}

		@Override
		String ambiguousMachineArchitectureLongName() {
			return "['mainDebugLinuxAarch64', 'mainDebugLinuxX86', 'mainReleaseLinuxAarch64', 'mainReleaseLinuxX86']";
		}
	}

	@Nested
	class UnitTestTests extends Tester {
		@BeforeEach
		void setup() {
			buildFile.plugins(it -> it.id("cpp-unit-test"));
		}

		@Override
		String componentDsl() {
			return "unitTest";
		}

		@Override
		String baseShortName() {
			return "['test']";
		}

		@Override
		String baseLongName() {
			return "['testDebugLinuxAarch64']";
		}

		@Override
		String debugOptShortName() {
			return "'testDbgopt'";
		}

		@Override
		String debugOptLongName() {
			return "'testDbgoptLinuxAarch64'";
		}

		@Override
		String ambiguousOsFamilyShortName() {
			return "['testLinux']";
		}

		@Override
		String ambiguousOsFamilyLongName() {
			return "['testDebugLinuxAarch64']";
		}

		@Override
		String ambiguousMachineArchitectureShortName() {
			return "['testAarch64', 'testX86']";
		}

		@Override
		String ambiguousMachineArchitectureLongName() {
			return "['testDebugLinuxAarch64', 'testDebugLinuxX86']";
		}
	}
}
