package dev.nokee.commons.unpack;

import org.gradle.api.Transformer;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.HasMultipleValues;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.reflect.TypeOf;
import org.gradle.api.specs.Spec;
import org.gradle.api.specs.Specs;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class FlattenTransformer<OutputType, ElementType> implements Transformer<OutputType, /*@Nullable*/ Object> {
	private final G<OutputType, ElementType> builder;
	private final Flattener flattener;
	private final Spec<Object> spec;
	private final Spec<Object> predicate;

	public static <ElementType> FlattenTransformer<List<ElementType>, ElementType> flat() {
		return new FlattenTransformer<>(bob());
	}

	public FlattenTransformer(G<OutputType, ElementType> builder) {
		this(Object.class, builder, DEFAULT_FLATTENER);
	}

	public FlattenTransformer(Class<? super ElementType> elementType, G<OutputType, ElementType> builder, Flattener flattener) {
		this.builder = builder;
		this.flattener = flattener;
		if (elementType.equals(Object.class)) {
			this.spec = __ -> false;
		} else {
			this.spec = elementType::isInstance;
		}

		this.predicate = Specs.intersect(Specs.negate(spec), FlattenTransformer::isFlattenableType);
	}

	@Override
	public OutputType transform(Object o) {
		if (o == null) {
			return builder.empty();
		}

		final G.Builder<OutputType, ElementType> builder = this.builder.builder(this);
		final Deque<Object> queue = new ArrayDeque<>();
		queue.addFirst(o);
		while (!queue.isEmpty()) {
			Object value = queue.removeFirst();
			// Check if the object is what we are flattening into...
			if (predicate.isSatisfiedBy(value)) {
				final List<?> list = flattener.flatten(value);

				// Check if we couldn't flatten further
				if (list.size() == 1 && list.get(0).equals(value)) {
					builder.add(value);
				} else {
					final ListIterator<?> iterator = list.listIterator(list.size());
					while (iterator.hasPrevious()) {
						final Object item = iterator.previous();
						queue.addFirst(item);
					}
				}
			} else {
				builder.add(value);
			}
		}

		return builder.build();
	}

	public interface Flattener {
		List<Object> flatten(@Nullable Object target);

		boolean isFlattenableType(Object value);
	}

	public static final Flattener DEFAULT_FLATTENER = new Flattener() {
		@Override
		public List<Object> flatten(Object value) {
			final List<Object> result = new ArrayList<>();
			if (value instanceof Iterable) {
				((Iterable<?>) value).forEach(result::add);
			} else if (value instanceof Object[]) {
				Collections.addAll(result, (Object[]) value);
			} else {
				result.add(value);
			}
			return result;
		}

		@Override
		public boolean isFlattenableType(Object value) {
			return value instanceof Object[] || value instanceof Iterable;
		}
	};

	public static boolean isFlattenableType(Object value) {
		return value instanceof Object[] || value instanceof Iterable;
	}

	public static <OutputType, ElementType> FlattenTransformer<OutputType, ElementType> flat(G<OutputType, ElementType> g) {
		throw new UnsupportedOperationException();
	}

	public static <OutputType, ElementType> G<Provider<OutputType>, ElementType> toProvider(CollectionContainerFactory<ElementType> f) {
		throw new UnsupportedOperationException();
	}

	public static <R> R ofList(ObjectFactory objects) {
		throw new UnsupportedOperationException();
	}



	public static <InputType> Builder<InputType> flatten(Transformer<? extends Iterable<? extends Object>, InputType> mapper) {
		return new Builder<InputType>() {
			@Override
			public <ElementType> Transformer<Provider<Collection<ElementType>>, InputType> toProvider(CollectionContainerFactory<ElementType> factory) {
				return null;
			}

			@Override
			public <ElementType> Transformer<List<ElementType>, InputType> toList() {
				return null;
			}

			@Override
			public <ElementType> Transformer<Set<ElementType>, InputType> toSet() {
				return null;
			}
		};
	}

	public interface Builder<InputType> {
		<ElementType> Transformer<Provider<Collection<ElementType>>, InputType> toProvider(CollectionContainerFactory<ElementType> factory);
		<ElementType> Transformer<List<ElementType>, InputType> toList();
		<ElementType> Transformer<Set<ElementType>, InputType> toSet();
	}

	public interface CollectionContainerFactory<ElementType> {
		HasMultipleValues<ElementType> create(Class<ElementType> elementType);
	}

	interface G<OutputType, ElementType> {
		OutputType empty();

		Builder<OutputType, ElementType> builder(Transformer<OutputType, Object> self);

		interface Builder<OutputType, ElementType> {
			OutputType build();

			Builder<OutputType, ElementType> add(Object element);
		}
	}




	public static <ElementType> FlattenTransformer.G<List<ElementType>, ElementType> bob() {
		return new FlattenTransformer.G<List<ElementType>, ElementType>() {
			@Override
			public List<ElementType> empty() {
				return Collections.emptyList();
			}

			@Override
			public Builder<List<ElementType>, ElementType> builder(Transformer<List<ElementType>, Object> self) {
				List<ElementType> result = new ArrayList<>();
				return new Builder<List<ElementType>, ElementType>() {
					@Override
					public List<ElementType> build() {
						return result;
					}

					@Override
					public Builder<List<ElementType>, ElementType> add(Object element) {
						result.add((ElementType) element);
						return this;
					}
				};
			}
		};
	}

	public static <ElementType> FlattenTransformer.G<List<ElementType>, ElementType> bob(Class<ElementType> elementType) {
		return new FlattenTransformer.G<List<ElementType>, ElementType>() {
			@Override
			public List<ElementType> empty() {
				return Collections.emptyList();
			}

			@Override
			public Builder<List<ElementType>, ElementType> builder(Transformer<List<ElementType>, Object> self) {
				List<ElementType> result = new ArrayList<>();
				return new Builder<List<ElementType>, ElementType>() {
					@Override
					public List<ElementType> build() {
						return result;
					}

					@Override
					public Builder<List<ElementType>, ElementType> add(Object element) {
						if (elementType.isInstance(element)) {
							result.add(elementType.cast(element));
						} else {
							throw new UnsupportedOperationException("unexpected element type");
						}
						return this;
					}
				};
			}
		};
	}

	public static <ElementType> FlattenTransformer.G<List<ElementType>, ElementType> bob(TypeOf<ElementType> elementType) {
		return new FlattenTransformer.G<List<ElementType>, ElementType>() {
			@Override
			public List<ElementType> empty() {
				return Collections.emptyList();
			}

			@Override
			public Builder<List<ElementType>, ElementType> builder(Transformer<List<ElementType>, Object> self) {
				List<ElementType> result = new ArrayList<>();
				return new Builder<List<ElementType>, ElementType>() {
					@Override
					public List<ElementType> build() {
						return result;
					}

					@Override
					public Builder<List<ElementType>, ElementType> add(Object element) {
						if (elementType.getConcreteClass().isInstance(element)) {
							result.add(elementType.getConcreteClass().cast(element));
						} else {
							throw new UnsupportedOperationException("unexpected element type");
						}
						return this;
					}
				};
			}
		};
	}


	public static <ElementType> FlattenTransformer.G<Provider<List<ElementType>>, ElementType> alice(ObjectFactory objects, Class<ElementType> elementType) {
		return new FlattenTransformer.G<Provider<List<ElementType>>, ElementType>() {
			@Override
			public Provider<List<ElementType>> empty() {
				return objects.listProperty(elementType);
			}

			@Override
			public Builder<Provider<List<ElementType>>, ElementType> builder(Transformer<Provider<List<ElementType>>, Object> self) {
				ListProperty<ElementType> result = objects.listProperty(elementType);
				return new Builder<Provider<List<ElementType>>, ElementType>() {
					@Override
					public Provider<List<ElementType>> build() {
						return result;
					}

					@Override
					public Builder<Provider<List<ElementType>>, ElementType> add(Object element) {
						if (elementType.isInstance(element)) {
							result.add(elementType.cast(element));
						} else if (element instanceof Provider) {
							result.addAll(((Provider<Object>) element).flatMap(self));
						} else {
							throw new UnsupportedOperationException("unexpected element type");
						}
						return this;
					}
				};
			}
		};
	}
}
