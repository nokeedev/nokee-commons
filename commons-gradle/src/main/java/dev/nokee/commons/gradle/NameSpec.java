package dev.nokee.commons.gradle;

import org.gradle.api.Namer;
import org.gradle.api.specs.Spec;

/**
 * Represent a spec that match an object's name.
 * Use {@link #using(Namer)} to specify the custom namer to use.
 *
 * @param <T>  the object type
 * @see FilterAwareSpec for matching action
 */
public interface NameSpec<T> extends Spec<T>, FilterAwareSpec<T> {
	<S> NameSpec<S> using(Namer<S> namer);
}
