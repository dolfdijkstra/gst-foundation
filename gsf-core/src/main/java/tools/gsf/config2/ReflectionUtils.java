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
package tools.gsf.config2;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Convenience class for working with reflection. Intended for internal GSF use only.
 *
 * @author Tony Field
 * @since 2016-08-06
 */
final class ReflectionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);

    private ReflectionUtils() {
    }

    /**
     * Locate a single resource in the classpath of the classloader specified. If more than one matching
     * resource is found, an IllegalStateException is thrown
     *
     * @param classLoader  the classloader to search
     * @param resourceName the name of the resource to find
     * @return a URL to the resource
     */
    static URL getSingleResource(ClassLoader classLoader, String resourceName) {
        try {
            Enumeration<URL> resources = classLoader.getResources(resourceName);
            URL result = null;
            boolean bFound = false;
            while (resources.hasMoreElements()) {
                if (bFound) {
                    throw new IllegalStateException("Too many resources found matching name: " + resourceName);
                }
                result = resources.nextElement();
                bFound = true;
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to locate resource: " + resourceName, e);
        }
    }

    /**
     * Locate a resource or resources in the classpath of the specified classloader, and read them.
     * <p>
     * More than one resource of the name specified is allowed.
     * <p>
     * The configuration resource file format ignores blank lines and lines starting with a #.
     * <p>
     * Each line of the configuration file will have its spaces normalized using
     * org.apache.commons.lang3.StringUtils.normalizeSpace(String)
     * <p>
     * The configuration file names will be added to a set. If two matching configuration lines are
     * found (after normalization), and IllegalStateException will be thrown.
     *
     * @param classLoader  the classloader to search
     * @param resourceName the name of the resource to find
     * @return a set of the strings from the configuration resources. If no matching resources are
     * found, or if the configuration resources are empty, an empty set will be returned.
     * @throws IllegalStateException if more than one matching configuration line is found
     * @throws RuntimeException      if an error occurs reading the resources
     */
    static Set<String> readConfigurationResource(ClassLoader classLoader, String resourceName) {
        Set<String> lines = new HashSet<>();
        try {
            Enumeration<URL> resources = classLoader.getResources(resourceName);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (InputStream in = url.openStream(); BufferedReader r = new BufferedReader(new InputStreamReader(in, "utf-8"))) {
                    String line;
                    while ((line = r.readLine()) != null) {
                        line = StringUtils.normalizeSpace(line);
                        if (StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, "#")) {
                            if (lines.contains(line)) {
                                throw new IllegalStateException("Duplicate configuration information found in resource named " + resourceName + ". The following information was found more than once: " + line);
                            }
                            lines.add(line);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error reading configuration resource: " + resourceName, e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading configuration resource: " + resourceName, e);
        }
        return lines;
    }

    /**
     * @param <T>           created method type
     * @param name          name of the object
     * @param typeTocreate  the type of the object to create
     * @param factoryMethod the method to use to create the object
     * @param factory       The factory on which the factory method will be invoked
     * @return created object
     * @throws InvocationTargetException exception from invoking specified method from class name
     */
    @SuppressWarnings("unchecked")
    static <T> T createFromMethod(String name, Class<T> typeTocreate, Object factory, Method factoryMethod, Object... params) throws InvocationTargetException {
        Object o = null;
        LOG.trace("Trying to create a {} object with name {} from method {} from factory {}", typeTocreate.getName(), name, factoryMethod.toGenericString(), factory.getClass().getName());

        if (typeTocreate.isAssignableFrom(factoryMethod.getReturnType())) {
            try {
                o = factoryMethod.invoke(factory, params);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Access exception creating object " + typeTocreate.getName() + ": " + e, e);
            }
        }
        return (T) o;
    }
}
