package dev.nokee.commons.gradle;

import dev.nokee.commons.fixtures.Subject;
import dev.nokee.commons.fixtures.SubjectExtension;
import org.gradle.api.NamedDomainObjectProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static dev.nokee.commons.hamcrest.With.with;
import static dev.nokee.commons.hamcrest.gradle.NamedMatcher.named;
import static dev.nokee.commons.hamcrest.gradle.ThrowableMatchers.message;
import static dev.nokee.commons.hamcrest.gradle.ThrowableMatchers.throwsException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(SubjectExtension.class)
public interface NamedDomainObjectRegistryTester<T> {
	@Test
	default void canRegisterOnAbsentElement(@Subject NamedDomainObjectRegistry<T> registry) {
		NamedDomainObjectProvider<T> element = registry.register(forName("my-new-element"));
		assertThat(element, not(nullValue()));
		assertThat(element, named("my-new-element"));
	}

	@Test
	default void canRegisterIfAbsentOnAbsentElement(@Subject NamedDomainObjectRegistry<T> registry) {
		NamedDomainObjectProvider<T> element = registry.registerIfAbsent(forName("my-new-element"));
		assertThat(element, not(nullValue()));
		assertThat(element, named("my-new-element"));
	}

	@Test
	default void throwsExceptionWhenRegisterOnExistingElement(@Subject("with-existing-element") NamedDomainObjectRegistry<T> registry) {
		assertThat(() -> registry.register(forName("my-existing-element")), throwsException(with(message(matchesPattern("Cannot add a \\w+ with name 'my-existing-element' as a \\w+ with that name already exists.")))));
	}

	@Test
	default void canRegisterIfAbsentOnExistingElement(@Subject("with-existing-element") NamedDomainObjectRegistry<T> registry) {
		NamedDomainObjectProvider<T> element = registry.registerIfAbsent(forName("my-existing-element"));
		assertThat(element, not(nullValue()));
		assertThat(element, named("my-existing-element"));
	}

	static Object forName(String name) {
		return new Object() {
			@Override
			public String toString() {
				return name;
			}
		};
	}
}
