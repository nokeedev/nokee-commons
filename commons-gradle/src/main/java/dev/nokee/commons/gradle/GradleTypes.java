package dev.nokee.commons.gradle;

import java.util.concurrent.ConcurrentHashMap;

public final class GradleTypes {
	private static final ConcurrentHashMap<Class<?>, Class<?>> undecoratedTypes = new ConcurrentHashMap<>();

	public static <T> Class<T> toUndecoratedType(Class<T> type) {
		@SuppressWarnings("unchecked")
		Class<T> result = (Class<T>) undecoratedTypes.computeIfAbsent(type, GradleTypes::computeUndecoratedType);
		return result;
	}

	private static Class<?> computeUndecoratedType(Class<?> type) {
		if (type.getSimpleName().endsWith("_Decorated")) {
			if (type.getSuperclass().equals(Object.class)) {
				return type.getInterfaces()[0];
			} else {
				return type.getSuperclass();
			}
		}
		return type;
	}
}
