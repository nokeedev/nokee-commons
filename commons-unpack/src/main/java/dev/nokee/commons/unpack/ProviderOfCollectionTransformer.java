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

import org.gradle.api.Transformer;
import org.gradle.api.provider.HasMultipleValues;
import org.gradle.api.provider.Provider;

import java.util.Collection;

public final class ProviderOfCollectionTransformer<OutputType extends Collection<ElementType>, ElementType> implements Transformer<Provider<? extends Collection<ElementType>>, Iterable<? extends Provider<? extends ElementType>>> {
	private final CollectionContainerFactory<ElementType> containerFactory;

	private ProviderOfCollectionTransformer(CollectionContainerFactory<ElementType> containerFactory) {
		this.containerFactory = containerFactory;
	}

	@Override
	public Provider<? extends Collection<ElementType>> transform(Iterable<? extends Provider<? extends ElementType>> providers) {
		@SuppressWarnings("unchecked")
		final HasMultipleValues<ElementType> container = containerFactory.create((Class<ElementType>) Object.class);
		for (Provider<? extends ElementType> provider : providers) {
			container.add(provider);
		}

		@SuppressWarnings("unchecked")
		Provider<OutputType> result = (Provider<OutputType>) container;
		return result;
	}

	public static <ElementType> Transformer<Provider<? extends Collection<ElementType>>, Iterable<? extends Provider<? extends ElementType>>> toProviderOfCollection(CollectionContainerFactory<ElementType> containerFactory) {
		return new ProviderOfCollectionTransformer<>(containerFactory);
	}

	public interface CollectionContainerFactory<ElementType> {
		HasMultipleValues<ElementType> create(Class<ElementType> elementType);
	}
}
