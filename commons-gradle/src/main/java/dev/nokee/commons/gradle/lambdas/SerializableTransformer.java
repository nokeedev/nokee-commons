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
package dev.nokee.commons.gradle.lambdas;

import org.gradle.api.Transformer;

import java.io.Serializable;

/**
 * A {@link Serializable} version of {@link Transformer}.
 */
public interface SerializableTransformer<OUT, IN> extends Transformer<OUT, IN>, Serializable {}
