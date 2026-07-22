package dev.nokee.commons.gradle.tasks.options;

import dev.nokee.commons.gradle.Factory;
import dev.nokee.commons.gradle.tasks.SourceTask;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.reflect.TypeOf;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static dev.nokee.commons.gradle.tasks.SourceTask.sourceOf;

/**
 * Represents an object with source options.
 *
 * @param <OptionsType>  the type of the source options
 */
public interface SourceOptionsAware<OptionsType> extends Task {
	/**
	 * Adds some source to this task and configure their options using the specified action.
	 * The given source objects will be evaluated as per {@link org.gradle.api.Project#files(Object...)}.
	 * <p>
	 * <b>Note:</b> The source options configuration action must be side effect free.
	 *
	 * @param sourcePath  the source to add and configure the source options
	 * @param configureAction  the source options configure action
	 * @return this
	 */
	default SourceOptionsAware<OptionsType> source(Object sourcePath, Action<? super OptionsType> configureAction) {
		SourceTask.source(this, sourcePath);
		getSourceOptions().forFiles(getProject().files(sourcePath).getAsFileTree()).options(configureAction);
		return this;
	}

	/**
	 * Uses this property to access the source options restricted to {@literal this} task's source.
	 * An absent provider means the user didn't configure any source options.
	 *
	 * @return a provider to the source options restricted to this task's source.
	 */
	@Nested
	@Optional
	Property<SourceOptions<OptionsType>> getAllSourceOptions();

	/**
	 * Developers can override this method to control the implementation of the source options creation.
	 * For example, when a developer needs to tap the factory to include default values.
	 *
	 * @return the source options factory
	 */
	@Internal
	default Factory<OptionsType> getSourceOptionsFactory() {
		// Infers options type by finding the implementation of SourceOptionsAware.
		Class<OptionsType> inferredOptionsType = null;
		for (Type type : getClass().getSuperclass().getGenericInterfaces()) {
			if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().equals(SourceOptionsAware.class)) {
				@SuppressWarnings("unchecked")
				final Class<OptionsType> optionsType = (Class<OptionsType>) ((ParameterizedType) type).getActualTypeArguments()[0];
				final ObjectFactory objects = getProject().getObjects();
				return () -> objects.newInstance(optionsType);
			}
		}

		throw new IllegalStateException("Could not infer Factory<OptionsType> for source options.");
	}

	/**
	 * {@return the configurable source options for this task}
	 */
	@Internal("tracked by SourceOptionsAware#getAllSourceOptions")
	default ConfigurableSourceOptions<OptionsType> getSourceOptions() {
		// Configuration cache doesn't allow stuffing "field-like" variable into task extension.
		// Instead, we are using a Gradle property that we will initialize if-and-only-if the user uses source options.
		// Using source options simply means calling this getter.
		ConfigurableSourceOptions<OptionsType> allSourceOptions = (ConfigurableSourceOptions<OptionsType>) getAllSourceOptions().getOrNull();

		if (allSourceOptions == null) {
			Factory<OptionsType> factory = getSourceOptionsFactory();
			Class<SourceOptionsSpec<OptionsType>> propertyType = new TypeOf<SourceOptionsSpec<OptionsType>>() {}.getConcreteClass();
			allSourceOptions = getProject().getObjects().newInstance(propertyType, factory)
				.forFiles(sourceOf(this)); // We restrict the source options to this task's source (see SourceTask contract).

			dependsOn(((SourceOptionsSpec<OptionsType>) ((ConfigurableSourceOptionsInternal) allSourceOptions).getRootSpec()).getTaskDependencies());

			// Saves the source options restricted to this task's source
			getAllSourceOptions().set(allSourceOptions);
		}

		@SuppressWarnings("unchecked")
		SourceOptionsSpec<OptionsType> result = (SourceOptionsSpec<OptionsType>) ((ConfigurableSourceOptionsInternal) allSourceOptions).getRootSpec();
		return result;
	}
}
