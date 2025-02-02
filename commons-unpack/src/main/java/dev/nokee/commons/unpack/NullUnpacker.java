package dev.nokee.commons.unpack;

import javax.annotation.Nullable;

public final class NullUnpacker implements Unpacker {
	@Nullable
	@Override
	public Object unpack(@Nullable Object target) {
		assert target == null;
		return null;
	}

	@Override
	public boolean canUnpack(@Nullable Object target) {
		return target == null;
	}
}
