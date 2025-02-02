package dev.nokee.commons.unpack;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Unpack the target object and return the result.
 *
 * <p>A packed object is implementation specific and doesn't represent any specific types.
 * For example, a packer may target Java {@code Callable}, Gradle {@code Provider} or Kotlin {@code Function}.
 */
public interface Unpacker {
	@Nullable
	Object unpack(@Nullable Object target);

	default boolean canUnpack(@Nullable Object target) {
		return !Objects.equals(target, unpack(target)); // very inefficient implementation
	}
}
