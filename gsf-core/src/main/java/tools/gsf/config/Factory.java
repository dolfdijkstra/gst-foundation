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
 * Factory clas used to get and/or create objects. This can be used to
 * configure an application, and is used internally by other components
 * like the {@link tools.gsf.config.inject.InjectForRequestInjector}.
 *
 * @author Tony Field
 * @since 2016-08-05
 */
public interface Factory {

    /**
     * Return an object with the type specified. If a name is provided,
     * more than one variant of the specified type could be returned.
     *
     * @param name the name used to identify the variant of the class
     *             to be returned. If no name is specified, then the
     *             class name of the type specified is assumed.
     * @param type the type of the oblject to return
     * @param <T>  the type of the object to return
     * @return the matching object, or null if the factory does not
     * know how to create the type specified.
     */
    <T> T getObject(String name, Class<T> type);

}
