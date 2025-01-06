package dev.nokee.commons.names;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

class Prop<OUT> {
	private final Map<String, Function<Object, OUT>> map = new HashMap<>();

	public Prop(Map<String, Function<Object, OUT>> map) {
		this.map.putAll(map);
	}

	public OUT with(String propName, Object value) {
		return (OUT) map.get(propName).apply(value);
	}

	public Set<String> names() {
		return map.keySet();
	}

	public static class Builder<OUT> {
		final Map<String, Function<Object, OUT>> map = new HashMap<>();
		private final Class<OUT> outType;

		public Builder(Class<OUT> outType) {
			this.outType = outType;
		}

		public <IN> Builder<OUT> with(String propName, Function<? super IN, ? extends OUT> method) {
			map.put(propName, (Function<Object, OUT>) method);
			return this;
		}

		public <T> Builder<OUT> elseWith(T obj, Function<? super T, ? extends OUT> method) {
			assert obj instanceof IParameterizedObject;
			for (String propName : ((IParameterizedObject<T>) obj).propSet()) {
				map.computeIfAbsent(propName, n -> it -> method.apply(((IParameterizedObject<T>) obj).with(n, it)));
			}
			return this;
		}

		public Prop<OUT> build() {
			return new Prop<>(map);
		}
	}
}
