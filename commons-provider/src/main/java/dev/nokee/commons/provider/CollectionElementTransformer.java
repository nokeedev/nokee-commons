package dev.nokee.commons.provider;

import org.gradle.api.Transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class CollectionElementTransformer<OutputType extends Collection<OutputElementType>, IntermediateType, OutputElementType, InputElementType> implements Transformer<OutputType, Iterable<? extends InputElementType>> {
	private final CollectionBuilder.Factory<OutputElementType> collectionFactory;
	private final AddFunction<OutputElementType, IntermediateType> merger;
	private final Transformer<? extends IntermediateType, ? super InputElementType> mapper;

	private CollectionElementTransformer(CollectionBuilder.Factory<OutputElementType> collectionFactory, AddFunction<OutputElementType, IntermediateType> merger, Transformer<? extends IntermediateType, ? super InputElementType> mapper) {
		this.collectionFactory = collectionFactory;
		this.merger = merger;
		this.mapper = mapper;
	}

	@Override
	public OutputType transform(Iterable<? extends InputElementType> elements) {
		CollectionBuilder<OutputElementType> builder = collectionFactory.create();
		for (InputElementType element : elements) {
			merger.add(builder, mapper.transform(element));
		}

		@SuppressWarnings("unchecked")
		final OutputType result = (OutputType) builder.build();
		return result;
	}

	public <C extends Collection<OutputElementType>> Transformer<C, Iterable<? extends InputElementType>> toCollection(CollectionBuilder.Factory<OutputElementType> factory) {
		return new CollectionElementTransformer<>(Objects.requireNonNull(factory), merger, mapper);
	}

	public Transformer<Set<OutputElementType>, Iterable<? extends InputElementType>> toSet() {
		return new CollectionElementTransformer<>(LinkedHashSetBuilder::new, merger, mapper);
	}

	/**
	 * Adapts an element mapper to transform each element individually of the collection.
	 * The result will apply a proper map algorithm to the provided collection.
	 *
	 * @param mapper  an element mapper
	 * @param <OutputElementType>  output element type resulting from the transform
	 * @param <InputElementType>  input element type to transform
	 * @return a {@link Transformer} instance to transform each the element of an iterable, never null.
	 */
	public static <OutputElementType, InputElementType> CollectionElementTransformer<List<OutputElementType>, OutputElementType, OutputElementType, InputElementType> transformEach(Transformer<? extends OutputElementType, InputElementType> mapper) {
		return new CollectionElementTransformer<>(ArrayListBuilder::new, new AddElementFunction<>(), Objects.requireNonNull(mapper));
	}

	/**
	 * Adapts a flat element mapper to transform each element individually of the collection.
	 * The result will apply a proper flatMap algorithm to the provided collection.
	 *
	 * @param mapper  an element mapper
	 * @param <OutputElementType>  output element type resulting from the transform
	 * @param <InputElementType>  input element type to transform
	 * @return a {@link Transformer} instance to flat transform each the element of an iterable, never null.
	 */
	public static <OutputElementType, InputElementType> CollectionElementTransformer<List<OutputElementType>, Iterable<? extends OutputElementType>, OutputElementType, InputElementType> flatTransformEach(Transformer<? extends Iterable<? extends OutputElementType>, InputElementType> mapper) {
		return new CollectionElementTransformer<>(ArrayListBuilder::new, new AddAllElementsFunction<>(), Objects.requireNonNull(mapper));
	}

	public interface CollectionBuilder<T> {
		interface Factory<T> {
			CollectionBuilder<T> create();
		}

		CollectionBuilder<T> add(T element);

		default CollectionBuilder<T> addAll(Iterable<? extends T> elements) {
			for (T element : elements) {
				add(element);
			}
			return this;
		}

		Collection<T> build();
	}

	private interface AddFunction<E, ElementToAddType> {
		void add(CollectionBuilder<E> self, ElementToAddType e);
	}

	private static final class AddElementFunction<E> implements AddFunction<E, E> {
		@Override
		public void add(CollectionBuilder<E> self, E element) {
			if (element != null) { // to support filter transformer
				self.add(element);
			}
		}
	}

	private static final class AddAllElementsFunction<E> implements AddFunction<E, Iterable<? extends E>> {
		@Override
		public void add(CollectionBuilder<E> self, Iterable<? extends E> elements) {
			self.addAll(elements);
		}
	}

	private static final class ArrayListBuilder<T> implements CollectionBuilder<T> {
		private final List<T> values = new ArrayList<>();

		@Override
		public CollectionBuilder<T> add(T element) {
			values.add(element);
			return this;
		}

		@Override
		public Collection<T> build() {
			return values;
		}
	}

	private static final class LinkedHashSetBuilder<T> implements CollectionBuilder<T> {
		private final Set<T> values = new LinkedHashSet<>();

		@Override
		public CollectionBuilder<T> add(T value) {
			values.add(value);
			return this;
		}

		@Override
		public Collection<T> build() {
			return values;
		}
	}
}
