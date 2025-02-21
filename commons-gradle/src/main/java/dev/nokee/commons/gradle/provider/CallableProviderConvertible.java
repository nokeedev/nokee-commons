package dev.nokee.commons.gradle.provider;

import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderConvertible;

import java.util.concurrent.Callable;

/**
 * An object that can be converted to a {@link Provider} compatible with Gradle {@literal Callable} aware APIs.
 *
 * @param <T> type of value represented by the provider
 */
public interface CallableProviderConvertible<T> extends Callable<Object>, ProviderConvertible<T> {
	/**
	 * Gradle-compatible {@literal Callable} provider conversion.
	 * <p>
	 * Some Gradle APIs, i.e. {@link org.gradle.api.Project#files(Object...)} and {@link org.gradle.api.Task#dependsOn(Object...)},
	 * accept {@link Callable} types as a legacy mechanic to the {@link Provider} API.
	 * In the majority of the cases, the API also accepts {@link Provider}.
	 * Gradle will unpack the {@link Callable} and use any resulting value.
	 * In our {@literal ProviderConvertible} case, it will result in an automatic {@link Provider} conversion.
	 *
	 * @return a {@link Provider}
	 */
	@Override
	default Object call() {
		return asProvider();
	}
}
