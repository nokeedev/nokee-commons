package dev.nokee.commons.gradle.provider;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

final class ThrowingCallable<V> implements Callable<V> {
	private final Supplier<? extends Exception> exceptionSupplier;

	public ThrowingCallable(Supplier<? extends Exception> exceptionSupplier) {
		this.exceptionSupplier = exceptionSupplier;
	}

	@Override
	public V call() throws Exception {
		throw exceptionSupplier.get();
	}

	@Override
	public String toString() {
		return "alwaysThrows(" + exceptionSupplier + ")";
	}
}
