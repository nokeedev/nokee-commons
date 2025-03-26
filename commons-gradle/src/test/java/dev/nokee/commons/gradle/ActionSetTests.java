package dev.nokee.commons.gradle;

import org.gradle.api.Action;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.mockito.Mockito.verifyNoInteractions;

class ActionSetTests {
	@Test
	void executesAllActionsSequentially() {
		Action<Object> a1 = Mockito.mock();
		Action<Object> a2 = Mockito.mock();
		Action<Object> a3 = Mockito.mock();
		ActionSet<Object> subject = () -> Stream.of(a1, a2, a3).iterator();

		verifyNoInteractions(a1, a2, a3);

		Object value = new Object();
		subject.execute(value);

		InOrder inOrder = Mockito.inOrder(a1, a2, a3);
		inOrder.verify(a1).execute(value);
		inOrder.verify(a2).execute(value);
		inOrder.verify(a3).execute(value);
	}
}
