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

		buildFile.append(groovyDsl("tasks.register('verify')"));
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
		void testLinkageQualifyingNames() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { qualifyingName(it).with('linkageName', 'bundle').toString() } == " + bundleQualifyingNames().shortName().toString(GradleDsl.GROOVY)));
			buildFile.append(verifyThat("components.withType(CppBinary).collect { qualifyingName(it).with('linkageName', 'bundle').toString(lowerCamelCase()) } == " + bundleQualifyingNames().longName().toString(GradleDsl.GROOVY)));
			runner.build();
		}

		abstract ExpectedNames bundleQualifyingNames();

		@Test
		void testOperatingSystemFamilyQualifyingNames() {
			buildFile.append(verifyThat("components.withType(CppBinary).collect { qualifyingName(it).with('osFamilyName', 'freebsd').toString() } == " + freebsdQualifyingNames().shortName().toString(GradleDsl.GROOVY)));
			buildFile.append(verifyThat("components.withType(CppBinary).collect { qualifyingName(it).with('osFamilyName', 'freebsd').toString(lowerCamelCase()) } == " + freebsdQualifyingNames().longName().toString(GradleDsl.GROOVY)));
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
			ExpectedNames bundleQualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("debugBundle"), string("releaseBundle"));
					}

					@Override
					public Expression longName() {
						return listOf(string("mainDebugBundleLinuxAarch64"), string("mainReleaseBundleLinuxAarch64"));
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
			ExpectedNames bundleQualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("debugBundle"), string("releaseBundle"));
					}

					@Override
					public Expression longName() {
						return listOf(string("mainDebugBundleLinuxAarch64"), string("mainReleaseBundleLinuxAarch64"));
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
			ExpectedNames bundleQualifyingNames() {
				return new ExpectedNames() {
					@Override
					public Expression shortName() {
						return listOf(string("testBundle"));
					}

					@Override
					public Expression longName() {
						return listOf(string("testDebugBundleLinuxAarch64"));
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
}
