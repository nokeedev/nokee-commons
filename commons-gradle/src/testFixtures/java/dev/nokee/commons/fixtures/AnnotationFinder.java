package dev.nokee.commons.fixtures;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationFinder {

    /**
     * Recursively finds an annotation on a method's declaration—from the method itself
     * or any parent class/interface versions of the same method signature.
     *
     * @param declaringClass   The class in which we found the method
     * @param method           The Method instance for which we want an annotation
     * @param annotationClass  The Class of the annotation we're looking for
     * @param <A>              The annotation type
     * @return                 The annotation from the method or parent methods, or null if not found
     */
    public static <A extends Annotation> A getMethodOrParentAnnotation(
            Class<?> declaringClass, Method method, Class<A> annotationClass) {

        // 1) Check if the method itself has the annotation
        A annotation = method.getAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }

        // 2) Recurse up the superclass
        Class<?> superClass = declaringClass.getSuperclass();
        if (superClass != null && superClass != Object.class) {
			Method superMethod = null;
            try {
                // Find the same method signature in the superclass
                superMethod = superClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException ignored) {
                // The superclass doesn't declare the same method signature; continue.
            }
			if (superMethod == null) {
				try {
					// Find the same method signature in the superclass
					superMethod = superClass.getMethod(method.getName(), method.getParameterTypes());
				} catch (NoSuchMethodException ignored) {
					// The superclass doesn't declare the same method signature; continue.
				}
			}

			if (superMethod != null) {
				annotation = getMethodOrParentAnnotation(superClass, superMethod, annotationClass);
				if (annotation != null) {
					return annotation;
				}
			}
        }

        // 3) Check all implemented interfaces
        for (Class<?> iface : declaringClass.getInterfaces()) {
            try {
                // Find the same method signature in the interface
                Method interfaceMethod = iface.getMethod(method.getName(), method.getParameterTypes());
                annotation = getMethodOrParentAnnotation(iface, interfaceMethod, annotationClass);
                if (annotation != null) {
                    return annotation;
                }
            } catch (NoSuchMethodException ignored) {
                // This interface doesn't declare the same method signature; continue.
            }
        }

        // 4) No annotation found on this method or any parent declarations
        return null;
    }
}
