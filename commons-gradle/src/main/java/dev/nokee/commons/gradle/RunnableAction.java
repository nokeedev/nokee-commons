package dev.nokee.commons.gradle;

import org.gradle.api.Action;

final class RunnableAction<T> implements Action<T> {
	private final Runnable delegate;

	public RunnableAction(Runnable delegate) {
		this.delegate = delegate;
	}

	@Override
	public void execute(T ignored) {
		delegate.run();
	}
}
