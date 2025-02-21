package dev.nokee.commons.gradle;

import dev.nokee.commons.gradle.lambdas.SerializableTransformer;
import org.gradle.api.Action;
import org.gradle.api.Transformer;
import org.gradle.api.provider.HasMultipleValues;
import org.gradle.api.provider.Provider;
import org.gradle.api.specs.Spec;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Collections of common transformers.
 */
public final class TransformerUtils {
	private TransformerUtils() {}

	/**
	 * Mechanism to create {@link org.gradle.api.Transformer} from Java lambdas that are {@link Serializable}.
	 *
	 * <p><b>Note:</b> The returned {@code Transformer} will provide an {@link Object#equals(Object)}/{@link Object#hashCode()} implementation based on the serialized bytes.
	 * The goal is to ensure the Java lambdas can be compared between each other after deserialization.
	 */
	public static <OUT, IN> Transformer<OUT, IN> ofSerializableTransformer(SerializableTransformer<OUT, IN> transformer) {
		return transformer;
	}

	/**
	 * Returns the composition of two transformers.
	 * For {@literal f: A->B} and {@literal g: B->C}, we define composition as the transform {@literal h} such that {@literal h(a) == g(f(a))} for each {@literal a}.
	 *
	 * @param g  the second transform to apply
	 * @param f  the first transform to apply
	 * @return  the composition of {@literal f} and {@literal g}
	 * @param <A>  the transforming type (input to {@literal f})
	 * @param <B>  the intermediate type (output of {@literal f} and input of {@literal g})
	 * @param <C>  the transformed type (output of {@literal g})
	 * @see <a href="https://en.wikipedia.org/wiki/Function_composition">function composition</a>
	 */
	public static <A, B, C> Transformer<C, A> compose(Transformer<C, B> g, Transformer<? extends B, A> f) {
		return new CompositeTransformer<>(g, f);
	}

	/**
	 * Filters elements satisfying the specified spec by returning {@literal null}.
	 * Returning {@literal null} is the only supported contract from {@link Provider} to discard a value.
	 * Use this function as a backport of the {@link Provider#filter(Spec)}.
	 * Use this function with {@link #flatTraverse(Transformer)} to filter elements from a provider of elements.
	 *
	 * @param spec  the spec to satisfy
	 * @return a transformer where non-satisfying element will return {@literal null}
	 * @param <ElementType>  the transforming element type
	 */
	public static <ElementType> Transformer<ElementType, ElementType> filter(Spec<? super ElementType> spec) {
		return new FilterTransformer<>(spec);
	}

	/** @see #flatTraverse(Transformer) */
	@Deprecated // Use flatTraverse(Transformer)
	public static <OUT, IN> Transformer<List<OUT>, Iterable<? extends IN>> flatTransformEach(Transformer<? extends Iterable<? extends OUT>, IN> mapper) {
		return new FlatTraverseTransformer<>(mapper);
	}

	/**
	 * Applies the specified mapper to each element traversed on the transforming iterable.
	 * Returning {@literal null} or empty iterable will filter out the element from the resulting collection.
	 * Use {@code flatTraverse(filter(Spec))} to remove unwanted elements from provider of collection.
	 *
	 * @param mapper  the mapper for each traversed elements
	 * @return a transformer
	 * @param <OutputElementType>  the transformed element type
	 * @param <InputElementType>  the transforming element type
	 */
	public static <OutputElementType, InputElementType> Transformer<List<OutputElementType>, Iterable<? extends InputElementType>> flatTraverse(Transformer<? extends Iterable<? extends OutputElementType>, InputElementType> mapper) {
		return new FlatTraverseTransformer<>(mapper);
	}

	/** @see #traverse(Transformer) */
	@Deprecated // Use traverse(Transformer)
	public static <OUT, IN> Transformer<List<OUT>, Iterable<? extends IN>> transformEach(Transformer<OUT, IN> mapper) {
		return new TraverseTransformer<>(mapper);
	}

	/**
	 * Applies the specified mapper to each element traversed on the transforming iterable.
	 *
	 * @param mapper  the mapper for each traversed elements
	 * @return  a transformer
	 * @param <OutputElementType>  the transformed element type
	 * @param <InputElementType>  the transforming element type
	 */
	public static <OutputElementType, InputElementType> Transformer<List<OutputElementType>, Iterable<? extends InputElementType>> traverse(Transformer<OutputElementType, InputElementType> mapper) {
		return new TraverseTransformer<>(mapper);
	}

	private static final NoOpTransformer<Object> NO_OP_INSTANCE = new NoOpTransformer<>();

	/**
	 * {@return a no-op transformer, i.e. will pass the transformed object without any operation}
	 * @param <T>  transforming type
	 */
	public static <T> Transformer<T, T> noOpTransformer() {
		return NO_OP_INSTANCE.withNarrowedType();
	}

	private static final ToSetTransformer<Object> TO_SET_INSTANCE = new ToSetTransformer<>();

	/**
	 * Transforms the input iterable to a linked set.
	 *
	 * @return a transformer to set
	 * @param <ElementType>  the element type
	 */
	public static <ElementType> Transformer<Set<ElementType>, Iterable<? extends ElementType>> toSet() {
		return TO_SET_INSTANCE.withNarrowedType();
	}

	/**
	 * Transforms the returned iterable to a linked set.
	 * To use with {@link #traverse(Transformer)} or {@link #flatTraverse(Transformer)}: {@code provider.map(toSet(traverse(transformer)))}
	 *
	 * @param mapper  the iterable mapper
	 * @return a transformer to set
	 * @param <ElementType>  the element type
	 * @param <InputType>  the type to transform
	 */
	public static <ElementType, InputType> Transformer<Set<ElementType>, InputType> toSet(Transformer<? extends Iterable<? extends ElementType>, InputType> mapper) {
		return new CompositeTransformer<>(TO_SET_INSTANCE.withNarrowedType(), mapper);
	}

	/**
	 * Returns a no-op transformer that simply execute the provided action on each transformed element.
	 *
	 * @param action  a non-interfering action to execute on the transformed elements
	 * @return  a new transformer instance
	 * @param <T> the transformed element type
	 */
	public static <T> Transformer<T, T> peek(Action<? super T> action) {
		return new PeekTransformer<>(action);
	}
}
