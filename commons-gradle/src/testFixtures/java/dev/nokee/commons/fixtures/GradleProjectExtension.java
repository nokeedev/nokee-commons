package dev.nokee.commons.fixtures;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class GradleProjectExtension implements ParameterResolver {
	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return parameterContext.isAnnotated(GradleProject.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		for (Method method : extensionContext.getRequiredTestClass().getDeclaredMethods()) {
			GradleProject project = method.getDeclaredAnnotation(GradleProject.class);
			if (project != null) {
				if (project.value().equals(parameterContext.findAnnotation(GradleProject.class).get().value())) {
					if (Modifier.isStatic(method.getModifiers())) {
						method.setAccessible(true);
						try {
							return method.invoke(null);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						} catch (InvocationTargetException e) {
							throw new RuntimeException(e);
						}
					} else {
						try {
							return method.invoke(extensionContext.getRequiredTestInstance());
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						} catch (InvocationTargetException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
		throw new UnsupportedOperationException("no method provide template for '" + parameterContext.findAnnotation(GradleProject.class).get().value() + "'");
	}
}
