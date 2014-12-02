/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ofg.infrastructure.spock;

import org.spockframework.runtime.extension.ExtensionAnnotation;

import java.lang.annotation.*;

/**
 * Quick and dirty @see{spock.util.environments.RestoreSystemProperties} variant which works at a specification level.
 *
 * System properties are restored to the state before the first setupSpec method after all tests and all cleanupSpec methods.
 *
 * A process to make it a part of Spock 1.0 is in progress.
 *
 * <p><strong>Note:</strong> Temporarily changing the values of system properties is only safe when specs are
 * run in a single thread per JVM. Even though many execution environments do limit themselves to one thread
 * per JVM, keep in mind that Spock cannot enforce this.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ExtensionAnnotation(ClassLevelRestoreSystemPropertiesExtension.class)
public @interface ClassLevelRestoreSystemProperties {}
