/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.nokee.commons.unpack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

public final class CompositeUnpacker implements UnpackerEx {
	private final Collection<UnpackerEx> unpackers = new ArrayList<>();

	public CompositeUnpacker(Iterable<UnpackerEx> unpackers) {
		unpackers.forEach(this.unpackers::add);
	}

	@Nullable
	@Override
	public Object unpack(@Nullable Object target) {
		for (UnpackerEx unpacker : unpackers) {
			if (unpacker.canUnpack(target)) {
				return unpacker.unpack(target);
			}
		}
		return target;
//		throw new UnsupportedOperationException("cannot unpack object");
	}

	@Override
	public boolean canUnpack(@Nullable Object target) {
		return unpackers.stream().anyMatch(unpacker -> unpacker.canUnpack(target));
	}
}
