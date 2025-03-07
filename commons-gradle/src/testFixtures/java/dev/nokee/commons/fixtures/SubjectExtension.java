package dev.nokee.commons.fixtures;

import com.google.common.reflect.TypeToken;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SubjectExtension implements ParameterResolver, ExecutionCondition {
	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return parameterContext.isAnnotated(Subject.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		Supplier<Object> subjectProvider = findSubjectProvider(extensionContext, parameterContext.getParameter());
		if (subjectProvider == null) {
			visitAllSubject(extensionContext, System.out::println);
			throw new UnsupportedOperationException("subject not found for " + parameterContext.getParameter().getAnnotation(Subject.class).value() + " of type " + parameterContext.getParameter().getType());
		}
		try {
			return subjectProvider.get();
		} catch (Throwable e) {
			throw new RuntimeException("Exception while getting subject", e);
		}
	}

	private void visitAllSubject(ExtensionContext extensionContext, Consumer<SubjectEntry> visitor) {
		for (Object instance : extensionContext.getRequiredTestInstances().getAllInstances()) {
			for (Field field : instance.getClass().getDeclaredFields()) {
				Subject annotation = field.getAnnotation(Subject.class);
				if (annotation != null) {
					visitor.accept(new SubjectEntry(field.getGenericType(), annotation.value().isBlank() ? null : annotation.value(), () -> ReflectionSupport.tryToReadFieldValue(field, instance).getOrThrow(RuntimeException::new)));
				}
			}

			ReflectionSupport.streamMethods(instance.getClass(), method -> {
				return method.getParameterCount() == 0;
			}, HierarchyTraversalMode.BOTTOM_UP).flatMap(method -> {
				Subject annotation = AnnotationFinder.getMethodOrParentAnnotation(instance.getClass(), method, Subject.class);
				if (annotation == null) {
					return Stream.empty();
				}
				return Stream.of(new SubjectEntry(method.getGenericReturnType(), annotation.value().isBlank() ? null : annotation.value(), () -> ReflectionSupport.invokeMethod(method, instance)));
			}).forEach(visitor::accept);
		}
	}

	private static final class SubjectEntry {
		private final Type type;
		private final String name;
		private final Supplier<Object> getter;

		private SubjectEntry(Type type, String name, Supplier<Object> getter) {
			this.type = type;
			this.name = name;
			this.getter = getter;
		}

		@Override
		public String toString() {
			return "SubjectEntry{" +
				"type=" + type +
				", name='" + name + '\'' +
				'}';
		}
	}

	private List<SubjectEntry> findAllSubjectProviders(ExtensionContext extensionContext) {
		List<SubjectEntry> result = new ArrayList<>();
		visitAllSubject(extensionContext, result::add);
		return result;
	}

	private Supplier<Object> findSubjectProvider(ExtensionContext extensionContext, Parameter parameter) {
		String s = parameter.getAnnotation(Subject.class).value();
		if (s.isBlank()) {
			s = extensionContext.getRequiredTestInstances().getAllInstances().stream().flatMap(it -> {
				return AnnotationSupport.findAnnotation(it.getClass(), Subjects.class).map(Stream::of).orElse(Stream.empty());
			}).findFirst().map(it -> it.value()).orElse(null);
		}
		if (s != null && s.isBlank()) {
			s = null;
		}
		final String subjectName = s;
		final TypeToken<?> subjectType = TypeToken.of(extensionContext.getRequiredTestClass()).resolveType(parameter.getParameterizedType());
		System.out.println("--- " + subjectType + " vs " + parameter.getParameterizedType() + " ---- " + extensionContext.getRequiredTestClass());

		for (SubjectEntry subjectProvider : findAllSubjectProviders(extensionContext)) {
			if (Objects.equals(subjectProvider.name, subjectName) && subjectType.isSupertypeOf(subjectProvider.type)) {
				return subjectProvider.getter;
			}
		}
		return null;
	}

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		if (context.getTestMethod().isEmpty() || context.getRequiredTestMethod().getAnnotation(SkipWhenNoSubject.class) == null) {
			return ConditionEvaluationResult.enabled("no skip when no subject");
		}
		for (Parameter parameter : context.getRequiredTestMethod().getParameters()) {
			if (null == findSubjectProvider(context, parameter)) {
				return ConditionEvaluationResult.disabled("no subject provider");
			}
		}
		return ConditionEvaluationResult.enabled("all subject are available");
	}
}
