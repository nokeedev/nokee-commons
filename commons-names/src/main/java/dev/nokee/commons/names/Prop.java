package dev.nokee.commons.names;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

final class Prop<OUT> {
	private final Map<String, Function<Object, OUT>> map = new HashMap<>();
	private final Class<OUT> outType;
	private final List<Consumer<? super Builder<OUT>>> builders;
	private final Map<String, Supplier<Object>> props = new HashMap<>();

	public Prop(Class<OUT> outType, Map<String, Function<Object, OUT>> map, List<Consumer<? super Builder<OUT>>> builders, Map<String, Supplier<Object>> props) {
		this.outType = outType;
		this.builders = builders;
		this.map.putAll(map);
		this.props.putAll(props);
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

	public Object get(String propName) {
		return Optional.ofNullable(props.get(propName)).map(Supplier::get).orElse(null);
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

	public static class Builder<OUT> {
		final Map<String, Function<Object, OUT>> map = new HashMap<>();
		final Map<String, Supplier<Object>> props = new HashMap<>();
		private final List<Consumer<? super Builder<OUT>>> builders = new ArrayList<>();
		private final Class<OUT> outType;

		public Builder(Class<OUT> outType) {
			this.outType = outType;
		}

		public <IN> Builder<OUT> with(String propName, Function<? super IN, ? extends OUT> method, Supplier<? extends IN> getter) {
			map.put(propName, (Function<Object, OUT>) method);
			props.put(propName, (Supplier<Object>) getter);

			// TODO: defer this to the end.
			IN obj = getter.get();
			if (obj instanceof IParameterizedObject) {
				for (String propName2 : ((IParameterizedObject<IN>) obj).propSet()) {
					map.computeIfAbsent(propName2, n -> it -> method.apply(((IParameterizedObject<IN>) obj).with(n, it)));
				}
			}
			if (obj instanceof IHasProp) {
				for (String propName2 : ((IHasProp) obj).propSet()) {
					props.computeIfAbsent(propName2, n -> () -> ((IHasProp) obj).get(n));
				}
			}
			return this;
		}

		public <T> Builder<OUT> elseWith(T obj, Function<? super T, ? extends OUT> method) {
			if (obj instanceof IParameterizedObject) {
				for (String propName : ((IParameterizedObject<T>) obj).propSet()) {
					map.computeIfAbsent(propName, n -> it -> method.apply(((IParameterizedObject<T>) obj).with(n, it)));
				}
			}
			if (obj instanceof IHasProp) {
				for (String propName : ((IHasProp) obj).propSet()) {
					props.computeIfAbsent(propName, n -> () -> ((IHasProp) obj).get(n));
				}
			}
			return this;
		}

		public <T> Builder<OUT> prop(T obj, Function<? super String, ? extends Object> method) {
			if (obj instanceof IParameterizedObject) {
				for (String propName : ((IParameterizedObject<T>) obj).propSet()) {
					props.computeIfAbsent(propName, n -> () -> method.apply(n));
				}
			}
			return this;
		}

		public <T> Builder<OUT> prop(T obj) {
			if (obj instanceof IHasProp) {
				for (String propName : ((IHasProp) obj).propSet()) {
					props.computeIfAbsent(propName, n -> () -> ((IHasProp) obj).get(n));
				}
			}
			return this;
		}

		public Prop<OUT> build() {
			return new Prop<>(outType, map, builders, props);
		}
	}
}
