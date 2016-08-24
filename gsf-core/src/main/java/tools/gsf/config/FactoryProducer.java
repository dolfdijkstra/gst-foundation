/*
 * Copyright 2016 Function1. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.gsf.config;

/**
 * This class defines a way by which factories can be returned for the scope specified.
 *
 * @author Tony Field
 * @since 2016-08-05
 */
public interface FactoryProducer {

    /**
     * Get the factory configured for the scope specified. If no factory is present for the
     * specified scope, an IllegalArgumentException is thrown.
     *
     * @param scope the scope
     * @return the factory
     * @throws IllegalArgumentException if no factory exists for the specified scope. null is
     *                                  not a valid scope.
     */
    Factory getFactory(Object scope);
}
