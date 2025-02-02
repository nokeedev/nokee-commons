//package dev.nokee.commons.provider;
//
//import dev.nokee.commons.provider.unpack.Unpacker;
//import org.gradle.api.Transformer;
//import org.gradle.api.specs.Spec;
//
//import javax.annotation.Nullable;
//import java.lang.annotation.ElementType;
//import java.util.Objects;
//
//public class UnpackTransformer<OutputType> implements Transformer<OutputType, /*@Nullable*/ Object> {
//	private final Unpacker unpacker;
//	private final UntilType<OutputType> elementType = new DefaultType<>();
//
//	@Override
//	public OutputType transform(@Nullable Object o) {
//		if (o == null) {
//			return null;
//		}
//
//		Object value = null;
//		while (!elementType.isInstance(o) && value != o) {
//			o = value;
//			if (unpacker.canUnpack(o)) {
//				value = unpacker.unpack(o);
//			}
//		}
//
//		return elementType.cast(o);
//	}
//
//	interface UntilType<T> {
//		boolean isInstance(Object o);
//		T cast(Object o);
//	}
//
//	static final class DefaultType<T> implements UntilType<T> {
//		@Override
//		public boolean isInstance(Object o) {
//			return false;
//		}
//
//		@Override
//		@SuppressWarnings("unchecked")
//		public T cast(Object o) {
//			return (T) o;
//		}
//	}
//}
