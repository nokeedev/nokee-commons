package dev.nokee.commons.gradle;

import org.gradle.api.Action;

final class DoNothingAction implements Action<Object> {
	public DoNothingAction() {}

	@Override
	public void execute(Object t) {
		// do nothing...
	}

	@SuppressWarnings("unchecked")
	public <T> Action<T> withNarrowType() {
		return (Action<T>) this;
	}


	@Override
	public String toString() {
		return "does nothing";
	}
}
