//package dev.nokee.commons.provider.unpack;
//
//import javax.annotation.Nullable;
//
//public class ProviderConvertibleUnpacker implements Unpacker {
//	@Nullable
//	@Override
//	public Object unpack(@Nullable Object target) {
//		if (target instanceof ProviderConvertible) {
//			return ((ProviderConvertible<?>) target).asProvider();
//		}
//		throw new UnsupportedOperationException("cannot unpack object ...");
//	}
//
//	@Override
//	public boolean canUnpack(@Nullable Object target) {
//		return target instanceof ProviderConvertible;
//	}
//}
