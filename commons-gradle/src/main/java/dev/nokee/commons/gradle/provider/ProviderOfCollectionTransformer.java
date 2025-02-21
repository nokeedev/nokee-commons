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
package dev.nokee.commons.gradle.provider;

import org.gradle.api.Transformer;
import org.gradle.api.provider.HasMultipleValues;
import org.gradle.api.provider.Provider;

import java.util.Collection;

final class ProviderOfCollectionTransformer<ContainerType extends Provider<CollectionType> & HasMultipleValues<ElementType>, CollectionType extends Collection<ElementType>, ElementType> implements Transformer<Provider<CollectionType>, Iterable<? extends Provider<? extends ElementType>>> {
	private final Factory<ContainerType, CollectionType, ElementType> containerFactory;

	public ProviderOfCollectionTransformer(Factory<ContainerType, CollectionType, ElementType> containerFactory) {
		this.containerFactory = containerFactory;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Provider<CollectionType> transform(Iterable<? extends Provider<? extends ElementType>> providers) {
		final Provider<CollectionType> container = containerFactory.create();
		for (Provider<? extends ElementType> provider : providers) {
			((HasMultipleValues<ElementType>) container).add(provider);
		}

		return container;
	}

	public interface Factory<ContainerType extends Provider<CollectionType> & HasMultipleValues<ElementType>, CollectionType extends Collection<ElementType>, ElementType> {
		ContainerType create();
	}
}
