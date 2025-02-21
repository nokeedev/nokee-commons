package dev.nokee.commons.gradle;

import org.gradle.api.Action;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FactoryTests {
	@Test
	void tapsFactoryWithAction() {
		Object expected = new Object();
		Action<Object> action = Mockito.mock();
		Factory<Object> subject = ((Factory<Object>) () -> expected).tap(action);

		verifyNoInteractions(action);
		Object actual = subject.create();

		assertThat(actual, sameInstance(expected));
		verify(action).execute(expected);
	}
}
