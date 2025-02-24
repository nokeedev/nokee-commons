package dev.nokee.commons.fixtures;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Field;

public class SubjectExtension implements ParameterResolver {
	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return parameterContext.isAnnotated(Subject.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		for (Field field : extensionContext.getRequiredTestClass().getDeclaredFields()) {
			Subject annotation = field.getAnnotation(Subject.class);
			if (annotation != null) {
				if (parameterContext.getParameter().getType().isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					try {
						return field.get(extensionContext.getRequiredTestInstance());
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		throw new UnsupportedOperationException();
	}
}
