package dev.nokee.commons.names;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

final class Prop<OUT> {
	private final Map<String, Function<Object, OUT>> map = new HashMap<>();
	private final Class<OUT> outType;
	private final List<Consumer<? super Builder<OUT>>> builders;

	public Prop(Class<OUT> outType, Map<String, Function<Object, OUT>> map, List<Consumer<? super Builder<OUT>>> builders) {
		this.outType = outType;
		this.builders = builders;
		this.map.putAll(map);
	}

	public OUT with(String propName, Object value) {
		Iterator<Consumer<? super Builder<OUT>>> iter = builders.iterator();
		while (!map.containsKey(propName) && iter.hasNext()) {
			Consumer<? super Builder<OUT>> it = iter.next();
			iter.remove();
			Prop.Builder<OUT> builder = new Prop.Builder<>(outType);
			it.accept(builder);

			// TODO: Make sure we don't overwrite keys
			map.putAll(builder.build().map);
		}
		return (OUT) Objects.requireNonNull(map.get(propName), "no prop '" + propName + "'").apply(value);
	}

	public Set<String> names() {
		Iterator<Consumer<? super Builder<OUT>>> iter = builders.iterator();
		while (iter.hasNext()) {
			Consumer<? super Builder<OUT>> it = iter.next();
			iter.remove();
			Prop.Builder<OUT> builder = new Prop.Builder<>(outType);
			it.accept(builder);

			// TODO: Make sure we don't overwrite keys
			map.putAll(builder.build().map);
		}
		return map.keySet();
	}

	public static <T> Prop<T> empty() {
		return new Prop<>((Class<T>) Object.class, Collections.emptyMap(), Collections.emptyList());
	}

	public static class Builder<OUT> {
		final Map<String, Function<Object, OUT>> map = new HashMap<>();
		private final List<Consumer<? super Builder<OUT>>> builders = new ArrayList<>();
		private final Class<OUT> outType;

		public Builder(Class<OUT> outType) {
			this.outType = outType;
		}

		public <IN> Builder<OUT> with(String propName, Function<? super IN, ? extends OUT> method) {
			map.put(propName, (Function<Object, OUT>) method);
			return this;
		}

		public <T> Builder<OUT> elseWith(T obj, Function<? super T, ? extends OUT> method) {
			if (obj instanceof IParameterizedObject) {
				for (String propName : ((IParameterizedObject<T>) obj).propSet()) {
					map.computeIfAbsent(propName, n -> it -> method.apply(((IParameterizedObject<T>) obj).with(n, it)));
				}
			}
			return this;
		}

		public <T> Builder<OUT> elseWith(Consumer<? super Builder<OUT>> deferred) {
			builders.add(deferred);
			return this;
		}

		public Prop<OUT> build() {
			return new Prop<>(outType, map, builders);
		}
	}
}
