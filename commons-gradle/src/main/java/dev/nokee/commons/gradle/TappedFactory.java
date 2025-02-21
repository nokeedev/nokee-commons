package dev.nokee.commons.gradle;

import org.gradle.api.Action;

final class TappedFactory<T> implements Factory<T> {
	private final Factory<T> factory;
	private final Action<? super T> action;

	public TappedFactory(Factory<T> factory, Action<? super T> action) {
		this.factory = factory;
		this.action = action;
	}

	@Override
	public T create() {
		final T result = factory.create();
		action.execute(result);
		return result;
	}
}
