package dev.nokee.commons.gradle.provider;

import org.gradle.api.NonExtensible;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A model to zip multiple provider together.
 *
 * <p>The builder uses {@link ListProperty} to group provider together without loosing any implicit information.
 * For this reason, the builder needs an instance to {@link ObjectFactory}, {@code objects.newInstance(ZipProvider.Factory.class)}.
 */
public abstract class ZipProvider {
	@NonExtensible
	public static /*final*/ class Factory {
		private final ObjectFactory objects;

		@Inject
		public Factory(ObjectFactory objects) {
			this.objects = objects;
		}

		/**
		 * Creates a new builder with no values to zip.
		 *
		 * @return a no value zip builder
		 */
		public Builder0 zipProvider() {
			return new Builder0(objects);
		}

		public <T, U, R> Provider<R> zip(Provider<T> firstProvider, Provider<U> secondProvider, BiFunction<? super T, ? super U, ? extends /*@Nullable*/ R> combiner) {
			final ListProperty<Object> accumulator = objects.newInstance(ZipPropertyProvider.class).getZipProvider();
			accumulator.add(firstProvider);
			accumulator.add(secondProvider);
			return accumulator.map(it -> {
				final ValuesToZip values = new ValuesToZip(it);
				return combiner.apply(values.get(0), values.get(1));
			});
		}
	}

	/**
	 * Zip all values using the specified combiner function.
	 *
	 * @param combiner a function used to combine the values
	 * @return a provider containing the result of the combination of all the values
	 * @param <R> the return type
	 */
	public abstract <R> Provider<R> zip(Combiner<R> combiner);

	public interface Builder {
		/**
		 * Adds a value to zip.
		 *
		 * @param provider  the value provider
		 * @return a builder for zipping values
		 * @param <T> the value type
		 */
		<T> Builder value(Provider<? extends T> provider);
	}

	public static final class ValuesToZip {
		private final List<Object> values;

		private ValuesToZip(List<Object> values) {
			this.values = values;
		}

		public <V> V get(int index) {
			@SuppressWarnings("unchecked")
			final V result = (V) values.get(index);
			return result;
		}

		public Object[] values() {
			return values.toArray(new Object[0]);
		}
	}

	@FunctionalInterface
	public interface Combiner<R> {
		@Nullable // allow value discarding
		R combine(ValuesToZip values);

		default <V> Combiner<V> andThen(Function<R, V> after) {
			return it -> after.apply(combine(it));
		}
	}

	public static final class Builder0 implements Builder {
		private final ObjectFactory objects;

		// Use ZipProviders.Factory
		private Builder0(ObjectFactory objects) {
			this.objects = objects;
		}

		@Override
		public <T> Builder1<T> value(Provider<? extends T> provider) {
			return new Builder1<>(objects, provider);
		}
	}

	/**
	 * Convenient builder when a single value was specified.
	 *
	 * @param <T>  the first value type
	 */
	public static final class Builder1<T> implements Builder {
		private final ObjectFactory objects;
		private final Provider<? extends T> firstValue;

		// Use ZipProviders.Factory
		private Builder1(ObjectFactory objects, Provider<? extends T> firstValue) {
			this.objects = objects;
			this.firstValue = firstValue;
		}

		@Override
		public <S> Builder2<T, S> value(Provider<? extends S> provider) {
			return new Builder2<>(objects, firstValue, provider);
		}
	}

	/**
	 * Convenient builder when zipping two values.
	 * It stands as a backward compatible {@code Provider#zip}.
	 *
	 * @param <T>  the first value type
	 * @param <S>  the second value type
	 */
	public static final class Builder2<T, S> extends ZipProvider implements Builder {
		private final org.gradle.api.model.ObjectFactory objects;
		private final Provider<? extends T> firstValue;
		private final Provider<? extends S> secondValue;

		// Use ZipProviders.Factory
		private Builder2(org.gradle.api.model.ObjectFactory objects, Provider<? extends T> firstValue, Provider<? extends S> secondValue) {
			this.objects = objects;
			this.firstValue = firstValue;
			this.secondValue = secondValue;
		}

		@Override
		public <U> BuilderX value(Provider<? extends U> provider) {
			return new BuilderX(objects).value(firstValue).value(secondValue).value(provider);
		}

		// Analogous to `Provider#zip(Provider, BiFunction)` from Gradle v6.6 (https://docs.gradle.org/current/javadoc/org/gradle/api/provider/Provider.html#zip(org.gradle.api.provider.Provider,java.util.function.BiFunction))
		public <R> Provider<R> zip(BiFunction<? super T, ? super S, ? extends /*@Nullable*/ R> combiner) {
			return zip(values -> combiner.apply(values.get(0), values.get(1)));
		}

		@Override
		public <R> Provider<R> zip(Combiner<R> combiner) {
			final ListProperty<Object> accumulator = objects.newInstance(ZipPropertyProvider.class).getZipProvider();
			accumulator.add(firstValue);
			accumulator.add(secondValue);
			return accumulator.map(it -> combiner.combine(new ValuesToZip(it)));
		}
	}

	/**
	 * Convenient builder when zipping multiple values.
	 */
	public static final class BuilderX extends ZipProvider implements Builder {
		private final ObjectFactory objects;
		private final List<Provider<?>> values = new ArrayList<>();

		// Use ZipProviders.Factory
		private BuilderX(ObjectFactory objects) {
			this.objects = objects;
		}

		@Override
		public <U> BuilderX value(Provider<? extends U> provider) {
			values.add(provider);
			return this;
		}

		@Override
		public <R> Provider<R> zip(Combiner<R> combiner) {
			// We isolate the values so adding more values to this builder doesn't affect previous zip on that same builder
			final ListProperty<Object> accumulator = objects.newInstance(ZipPropertyProvider.class).getZipProvider();
			values.forEach(accumulator::add);
			return accumulator.map(it -> combiner.combine(new ValuesToZip(it)));
		}
	}

	/*private*/ interface ZipPropertyProvider {
		ListProperty<Object> getZipProvider();
	}
}
