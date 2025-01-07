package dev.nokee.commons.names;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class DirNamingSchemeTests {
	@Test
	void testDirNamesScheme() {
		Names names = Names.ofMain().append("debug").append("cpp");
		assertThat(names.toString(NameBuilder.dirNames()), equalTo("main/debug/cpp"));
	}

	@Test
	void testDirNamesSchemeMadeOfMultipleSegments() {
		Names names = Names.ofMain().append(new OtherName() {
			private final NameString name = Qualifiers.of(Qualifiers.of("debug"), Qualifiers.ofMain(Qualifiers.of("macos")), Qualifiers.of("x86"));

			@Override
			public QualifyingName qualifiedBy(Qualifier qualifier) {
				return new DefaultQualifyingName(qualifier, this, builder -> {
					qualifier.appendTo(builder);
					return builder.append(name).toString();
				});
			}

			@Override
			public String toString(NameBuilder builder) {
				return builder.append(name).toString();
			}

			@Override
			public void appendTo(NameBuilder builder) {
				builder.append(name);
			}
		}).append("cpp");
		assertThat(names.toString(NameBuilder.dirNames()), equalTo("main/debug/x86/cpp"));
	}
}
