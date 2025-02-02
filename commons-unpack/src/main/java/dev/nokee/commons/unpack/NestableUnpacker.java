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

public final class NestableUnpacker implements Unpacker {
	private final Unpacker delegate;

	public NestableUnpacker(Unpacker delegate) {
		this.delegate = delegate;
	}

	@Nullable
	@Override
	public Object unpack(@Nullable Object deferred) {
		Object current = deferred;
		while (delegate.canUnpack(current)) {
			Object value = delegate.unpack(current);
			if (value == current) {
				throw new RuntimeException("unpacked object is the same as packed object"); //TODO
			}
			current = value;
		}
		return current;
	}

	@Override
	public boolean canUnpack(@Nullable Object target) {
		return delegate.canUnpack(target);
	}
}
