package dev.nokee.commons.gradle.provider;

import org.gradle.api.DomainObjectCollection;
import org.gradle.api.NonExtensible;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.HasMultipleValues;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Converts provider of collection to collection provider.
 * In earlier Gradle, the {@link DomainObjectCollection#addAllLater(Provider)} API requires a collection provider type (created via {@link ObjectFactory#listProperty(Class)} or {@link ObjectFactory#setProperty(Class)}).
 * Normal provider (created via {@link ProviderFactory#provider(Callable)}) will never be a collection provider type even if they return a collection.
 * It's an important distinction.
 */
@NonExtensible
public /*final*/ class CollectionProviderConverter {
	private final ObjectFactory objects;

	@Inject
	public CollectionProviderConverter(ObjectFactory objects) {
		this.objects = objects;
	}

	/**
	 * Creates a collection provider from the specified provider to collection.
	 *
	 * <p>
	 *   Note that if the specified provider is already a collection provider, nothing is done.
	 * </p>
	 *
	 * @param provider  the provider to collection to convert
	 * @return a collection provider for the specified provider to collection
	 * @param <T>  the collection element type
	 */
	@SuppressWarnings({"unchecked", "UnstableApiUsage"})
	public <T> Provider<? extends Collection<T>> toCollectionProvider(Provider<? extends Collection<T>> provider) {
		// If the provider is already a collection provider
		if (provider instanceof HasMultipleValues) {
			return provider;
		}

		final ListProperty<T> result = (ListProperty<T>) objects.listProperty(Object.class).value(provider);
		result.disallowChanges();
		return result;
	}
}
