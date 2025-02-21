package dev.nokee.commons.gradle.tasks.options;

import org.gradle.api.Action;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Nested;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Represents an object with source options.
 *
 * @param <T>  the source options type
 */
public interface SourceOptionsAware<T> {
	default SourceOptionsAware<T> source(Object sourcePath, Action<? super T> configureAction) {
		// Required because source(Object) contract is not clearly defined
		try {
			Method sourceMethod = getClass().getMethod("source", Object.class);
			sourceMethod.setAccessible(true);
			sourceMethod.invoke(this, sourcePath);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		getSourceOptions().configure(sourcePath, configureAction);
		return this;
	}

	@Nested
	Options<T> getOptions();

	interface Options<T> {
		Provider<T> forSource(File file);

		Provider<Iterable<SourceOptions<T>>> forAllSources();
	}

	@Nested
	AllSourceOptionsEx2<T> getSourceOptions();
}
