package dev.nokee.commons.provider;

import org.gradle.api.Action;
import org.gradle.api.provider.Provider;

import java.util.Objects;
import java.util.Optional;

/**
 * Represent a provider with an optional value.
 * Use in implementation only!
 *
 * @param <T>  type of value represented by the provider
 */
public final class OptionalProvider<T> {
	private final Provider<T> provider;

	private OptionalProvider(Provider<T> provider) {
		this.provider = Objects.requireNonNull(provider);
	}

	public static <T> OptionalProvider<T> of(Provider<T> provider) {
		return new OptionalProvider<>(provider);
	}

	/**
	 * If a value is present, invoke the specified action with the value, otherwise do nothing.
	 *
	 * @param action  the action to execute, if a value is present, must not be null
	 */
	public void ifPresent(Action<? super T> action) {
		Objects.requireNonNull(action);
		final T value = provider.getOrNull(); // Important to use this API as there is no guarantee the value stays the same
		if (value != null) {
			action.execute(value);
		}
	}

	/**
	 * If a value is present, performs the given action with the value, otherwise performs the given empty-based action.
	 *
	 * @param action  the action to be performed, if a value is present, must not be null
	 * @param emptyAction  the empty-based action to be performed, if no value is present, must not be null
	 */
	public void ifPresentOrElse(Action<? super T> action, Runnable emptyAction) {
		Objects.requireNonNull(action);
		Objects.requireNonNull(emptyAction);
		final T value = provider.getOrNull(); // Important to use this API as there is no guarantee the value stays the same
		if (value != null) {
			action.execute(value);
		} else {
			emptyAction.run();
		}
	}

	/**
	 * Realize the provider value as Java {@link Optional}.
	 *
	 * @return Java optional representing the {@link Provider}'s value, never null (but optional can be empty)
	 */
	public Optional<T> asOptional() {
		return Optional.ofNullable(provider.getOrNull());
	}
}
