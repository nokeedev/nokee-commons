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

import lombok.EqualsAndHashCode;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@EqualsAndHashCode
public final class PredicateBasedUnpacker implements UnpackerEx {
	private final UnpackerEx delegate;
	private final Predicate<Object> predicate;

	public PredicateBasedUnpacker(UnpackerEx delegate, Predicate<Object> predicate) {
		this.delegate = delegate;
		this.predicate = predicate;
	}

	@Nullable
	@Override
	public Object unpack(@Nullable Object target) {
		return delegate.unpack(target);
	}

	@Override
	public boolean canUnpack(@Nullable Object target) {
		return predicate.test(target);
	}
}
