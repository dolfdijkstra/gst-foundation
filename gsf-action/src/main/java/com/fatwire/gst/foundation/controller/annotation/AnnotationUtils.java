/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.controller.annotation;

import java.lang.reflect.Field;

/**
 * Helper class to work with Annotations.
 * 
 * @author Dolf Dijkstra
 * @since May 27, 2011
 */
public final class AnnotationUtils {
    private AnnotationUtils() {
    }

    /**
     * 
     * 
     * @param <T> the class of the object that is returned .
     * @param object the object containing the object to find.
     * @param type the Class of the type that is searched for.
     * @return the object that is present on the field with the InjectForRequest
     *         annotation.
     */
    @SuppressWarnings("unchecked")
    public static <T> T findService(final Object object, final Class<T> type) {
        final Field field = findField(object, type);
        try {
            return field == null ? null : (T) field.get(object);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Searches the object for a field annotated with the InjectForRequest
     * annotation of the provided type.
     * <p/>
     * 
     * For instance <tt>@InjectForRequest Service service; </tt> is defined on
     * the class as a field, then <tt>findField(object,Service.class);</tt> will
     * return the Field <tt>service</tt>.
     * 
     * @param <T> the type of the field to look for.
     * @param a the object to search on for the typed field.
     * @param type
     * @return the class field with the InjectForRequest annotation of the Class
     *         type.
     */
    public static <T> Field findField(final Object a, final Class<T> type) {
        Class<?> klazz = a.getClass();
        while (klazz != null && klazz != Object.class) {
            for (final Field field : klazz.getDeclaredFields()) {
                if (field.getAnnotation(InjectForRequest.class) != null && type.isAssignableFrom(field.getType())) {
                    return field;
                }
            }
            klazz = klazz.getSuperclass();
        }
        return null;
    }

}
