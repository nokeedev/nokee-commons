package dev.nokee.commons.gradle.provider;

import org.gradle.api.Transformer;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.file.FileSystemLocationProperty;
import org.gradle.api.provider.HasConfigurableValue;
import org.gradle.api.provider.HasMultipleValues;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Provider;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Operations on {@link Provider}.
 */
public final class ProviderUtils {
	private ProviderUtils() {}

	private static final FlattenProviderTransformer<Object> FLATTEN_PROVIDER_INSTANCE = new FlattenProviderTransformer<>();

	/**
	 * Flattens a nested provider.
	 * The outer provider's knowledge will be discarded in favor of the nested provider's knowledge.
	 * Generally, flattening a provider is strictly for providers created at a later time, meaning the outer provider is a simple holder/deferral wrapper.
	 *
	 * @param nestedProviders  the provider to flatten
	 * @return a flatten provider
	 * @param <T>  the provided type, should not be another provider
	 */
	public static <T> Provider<T> flatten(Provider<? extends Provider<T>> nestedProviders) {
		return FLATTEN_PROVIDER_INSTANCE.<T>withNarrowedType().transform(nestedProviders);
	}

	private static final OptionalProviderTransformer<Object> OPTIONAL_PROVIDER_INSTANCE = new OptionalProviderTransformer<>();

	/**
	 * Converts a Gradle Provider into a JDK Optional type.
	 * The provider is realized immediately upon calling.
	 * The Optional will hold the current value and any changes to the provider will not be reflected.
	 * Using the JDK Optional is the only way to perform a {@literal ifPresent} operation.
	 *
	 * @param provider  the provider to convert into a JDK Optional
	 * @return an JDK Optional representing the current value of the provider
	 * @param <T>  the provider type
	 */
	public static <T> Optional<T> asJdkOptional(Provider<T> provider) {
		return OPTIONAL_PROVIDER_INSTANCE.<T>withNarrowedType().transform(provider);
	}

	private static final ToSingletonOrEmptyListTransformer<Object> OPTIONAL_ELEMENT_INSTANCE = new ToSingletonOrEmptyListTransformer<>();

	/**
	 * Converts a collection element provider into an optional collection element by converting the provider into a single element list (if present) or empty list (if absent).
	 * This function is meant to be use with {@link org.gradle.api.provider.HasMultipleValues#addAll(Iterable)}.
	 *
	 * @param provider  an collection element provider
	 * @return an always present provider representing the collection element
	 * @param <T>  the provider type, must not be iterable
	 */
	public static <T> Provider<List<T>> asOptionalCollectionElement(Provider<T> provider) {
		return OPTIONAL_ELEMENT_INSTANCE.<T>withNarrowedType().transform(provider);
	}

	/**
	 * Convenient factory for {@link org.gradle.api.provider.ProviderFactory#provider(Callable)} that always throws a {@link RuntimeException}.
	 *
	 * @return an always throwing {@link Callable}
	 * @param <V>  the return type
	 */
	public static <V> Callable<V> alwaysThrows() {
		return new ThrowingCallable<>(new Supplier<Exception>() {
			@Override
			public Exception get() {
				return new RuntimeException();
			}

			@Override
			public String toString() {
				return "runtime exception";
			}
		});
	}

	/**
	 * Convenient factory for {@link org.gradle.api.provider.ProviderFactory#provider(Callable)} that always throws the supplied exception.
	 *
	 * @return an always throwing {@link Callable}
	 * @param <V>  the return type
	 */
	public static <V> Callable<V> alwaysThrows(Supplier<? extends Exception> exceptionSupplier) {
		return new ThrowingCallable<>(exceptionSupplier);
	}

	private static final NoValueCallable<Object> NO_VALUE_INSTANCE = new NoValueCallable<>();

	/**
	 * Convenient factory for {@link org.gradle.api.provider.ProviderFactory#provider(Callable)} that satisfy the no-value contract.
	 *
	 * @return an always null return {@link Callable}
	 * @param <V>  the return type
	 */
	public static <V> Callable<V> noValue() {
		return NO_VALUE_INSTANCE.withNarrowedType();
	}

	/**
	 * Allows fluent call to {@link HasConfigurableValue#finalizeValue()}.
	 *
	 * @param value  the configurable value, must not be null
	 * @param <S>  the type of configurable value
	 * @return the specified configurable value, never null
	 */
	@SuppressWarnings("UnstableApiUsage")
	public static <S extends HasConfigurableValue> S finalizeValue(S value) {
		value.finalizeValue();
		return value;
	}

	/**
	 * Allows fluent call to {@link HasConfigurableValue#disallowChanges()}.
	 *
	 * @param value  the configurable value, must not be null
	 * @param <S>  the type of configurable value
	 * @return the specified configurable value, never null
	 */
	@SuppressWarnings("UnstableApiUsage")
	public static <S extends HasConfigurableValue> S disallowChanges(S value) {
		value.disallowChanges();
		return value;
	}

	/**
	 * Allows fluent call to {@link HasConfigurableValue#finalizeValueOnRead()}.
	 *
	 * @param self  the configurable value, must not be null
	 * @param <S>  the type of configurable value
	 * @return the specified configurable value, never null
	 */
	@SuppressWarnings("UnstableApiUsage")
	public static <S extends HasConfigurableValue> S finalizeValueOnRead(S self) {
		self.finalizeValueOnRead();
		return self;
	}

	/**
	 * Allows fluent mapping of a {@link FileSystemLocationProperty} to its location only.
	 *
	 * @param mapper  a mapper to the {@link FileSystemLocationProperty}
	 * @return a transform to a property's location only
	 * @param <InputType>  the transform input type
	 * @param <LocationType>  the location type
	 */
	public static <InputType, LocationType extends FileSystemLocation> Transformer<Provider<LocationType>, InputType> locationOnly(Transformer<FileSystemLocationProperty<LocationType>, InputType> mapper) {
		return new LocationOnlyTransformer<>(mapper);
	}

	/**
	 * Returns the element provider of a file collection by mapping the input object into the file collection first.
	 *
	 * @param mapper  the object to {@link FileCollection} mapper
	 * @param <T>  the object type
	 * @return a transformer for object to {@literal FileCollection} to {@literal FileSystemLocation} set provider
	 */
	public static <T> Transformer<Provider<Set<FileSystemLocation>>, T> elementsOf(Transformer<? extends FileCollection, ? super T> mapper) {
		return new ElementsOfTransformer<>(mapper);
	}

	/**
	 * Sequences an iterable of providers to element into a provider of collection of elements.
	 * The sequencing keep the provider knowledge of each element in the final provider.
	 * Use this function via {@link Provider#flatMap(Transformer)}: {@code provider.flatMap(toProviderOfCollection(objects::setProperty)}
	 *
	 * @param containerFactory  the container factory, a call to either {@link org.gradle.api.model.ObjectFactory#listProperty(Class)} or {@link org.gradle.api.model.ObjectFactory#setProperty(Class)}
	 * @return a sequence transformer to use with {@link Provider#flatMap(Transformer)}
	 * @param <ContainerType>  the container type, either {@link org.gradle.api.provider.ListProperty} or {@link org.gradle.api.provider.SetProperty}
	 * @param <CollectionType>  the collection type, either {@link List} or {@link Set}
	 * @param <ElementType>  the element type
	 */
	@SuppressWarnings("unchecked")
	public static <ContainerType extends Provider<CollectionType> & HasMultipleValues<ElementType>, CollectionType extends Collection<ElementType>, ElementType> Transformer<Provider<CollectionType>, Iterable<? extends Provider<? extends ElementType>>> toProviderOfCollection(Transformer<ContainerType, Class<ElementType>> containerFactory) {
		return new ProviderOfCollectionTransformer<>(() -> containerFactory.transform((Class<ElementType>) Object.class));
	}

	public static <K, V> Map<K, V> asJdkMap(MapProperty<K, V> property) {
		return new JdkMapToMapPropertyAdapter<>(property);
	}
}
