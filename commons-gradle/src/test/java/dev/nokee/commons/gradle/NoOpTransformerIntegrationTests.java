/*
 * Copyright 2021 the original author or authors.
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
package dev.nokee.commons.gradle;

import org.gradle.api.Transformer;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static dev.nokee.commons.gradle.TransformerUtils.noOpTransformer;
import static dev.nokee.commons.hamcrest.gradle.provider.ProviderOfMatcher.providerOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;

class NoOpTransformerIntegrationTests {
	ProviderFactory providerFactory() {
		return ProjectBuilder.builder().build().getProviders();
	}

	@Test
	void alwaysReturnTheInputObject() {
		Object obj1 = new Object();
		Object obj2 = "obj";
		Object obj3 = 4.2;

		assertThat(providerFactory().provider(() -> obj1).map(noOpTransformer()), providerOf(sameInstance(obj1)));
		assertThat(providerFactory().provider(() -> obj2).map(noOpTransformer()), providerOf(sameInstance(obj2)));
		assertThat(providerFactory().provider(() -> obj3).map(noOpTransformer()), providerOf(sameInstance(obj3)));
	}

//	@Test
//	void canUseNoOpTransformerWhenOutputTypeIsASuperTypeOfTheInputType() {
//		// compile-time assertion
//		inputTypeToSuperType(noOpTransformer());
//	}

	@Test
	void cannotUseNoOpTransformerWhenOutputTypeIsAChildTypeOfTheInputType() {
		// compile-time assertion, uncomment to test -> it should not compile
//		inputTypeToChildType(noOpTransformer());
	}

	@Test
	void canUseNoOpTransformerWhenOutputTypeIsTheSameAsInputType() {
		// compile-time assertion, uncomment to test -> it should not compile
		bothInputAndOutputTypeAreTheSame(noOpTransformer());
	}

	private static void inputTypeToSuperType(Transformer<Base, Child> transformer) {}
	private static void inputTypeToChildType(Transformer<Child, Base> transformer) {}
	private static void bothInputAndOutputTypeAreTheSame(Transformer<Base, Base> transformer) {}

	interface Base {}
	interface Child extends Base {}
}
