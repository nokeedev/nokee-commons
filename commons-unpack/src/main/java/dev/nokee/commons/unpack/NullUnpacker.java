package dev.nokee.commons.unpack;

import javax.annotation.Nullable;

final class NullUnpacker implements UnpackerEx {
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
