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

package com.fatwire.gst.foundation.controller.action;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.cs.core.db.Util;
import com.fatwire.gst.foundation.DebugHelper;
import com.fatwire.gst.foundation.controller.annotation.Bind;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;

/**
 * Helper to bind variables to an Object based on annotated fields.
 * 
 * @author Dolf Dijkstra
 * @since 12 mei 2012
 */
public final class AnnotationBinder {
    protected static final Log LOG = LogFactory.getLog(AnnotationBinder.class.getPackage().getName());
    protected static final Log LOG_TIME = LogFactory.getLog(AnnotationBinder.class.getPackage().getName() + ".time");

    /**
     * Inject ICS runtime objects into the object. Objects flagged with the
     * {@link InjectForRequest} annotation will be populated by this method by
     * retrieving the value from the {@link Factory#getObject(String,Class)}
     * method.
     * 
     * @param object the object to inject into.
     * @param ics the ics context.
     */
    public static final void bind(final Object object, ICS ics) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null.");
        }
        if (ics == null) {
            throw new IllegalArgumentException("CS cannot be null.");
        }
        final long start = LOG_TIME.isDebugEnabled() ? System.nanoTime() : 0L;
        try {
            Class<?> c = object.getClass();
            // all annotated fields.
            while (c != Object.class && c != null) {
                for (final Field field : c.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Bind.class)) {
                        bindToField(object, ics, field);
                    }

                }

                c = c.getSuperclass();
            }
        } finally {
            DebugHelper.printTime(LOG_TIME, "inject model for " + object.getClass().getName(), start);
        }
    }

    /**
     * @param object object to bind to
     * @param ics the ics context
     * @param field the field to inject to
     * @throws SecurityException
     */
    public static void bindToField(final Object object, final ICS ics, final Field field) throws SecurityException {

        if (!field.isAccessible()) {
            field.setAccessible(true); // make private fields accessible
        }
        final Bind ifr = field.getAnnotation(Bind.class);

        String name = ifr.value();
        if (StringUtils.isBlank(name)) {
            name = field.getName();
        }
        try {

            switch (ifr.scope()) {
                case ics:
                    if (field.getType().isArray()) {

                    } else {
                        String var = ics.GetVar(name);
                        if (StringUtils.isBlank(var)) {
                            put(object, field, ics.GetObj(name));
                        } else {
                            put(object, field, var);
                        }
                    }
                    break;
                case request:
                    put(object, field, ics.getAttribute(name));

                    break;
                case session:
                    @SuppressWarnings("deprecation")
                    HttpSession s = ics.getIServlet().getServletRequest().getSession(false);
                    if (s != null) {
                        Object obj = s.getAttribute(name);
                        if (obj == null) {
                            try {
                                Method m = object.getClass().getMethod("create" + field.getType().getSimpleName(),
                                        ICS.class);
                                obj = m.invoke(object, ics);
                            } catch (NoSuchMethodException e) {
                                // ignore
                            } catch (IllegalArgumentException e) {
                                LOG.debug(e);
                            } catch (InvocationTargetException e) {
                                LOG.warn(e.getMessage());
                            }
                        }
                        put(object, field, obj);
                    }
                    break;

            }
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private static void putPrimitive(Object object, Field field, Object v) throws IllegalAccessException {
        try {
            if (field.getType() == Byte.TYPE) {
                field.setByte(object, (Byte) v);
            } else if (field.getType() == Integer.TYPE) {
                field.setInt(object, (Integer) v);
            } else if (field.getType() == Double.TYPE) {
                field.setDouble(object, (Double) (v));
            } else if (field.getType() == Float.TYPE) {
                field.setFloat(object, (Float) (v));
            } else if (field.getType() == Long.TYPE) {
                field.setLong(object, (Long) (v));
            } else if (field.getType() == Short.TYPE) {
                field.setShort(object, (Short) (v));
            } else if (field.getType() == Boolean.TYPE) {
                field.setBoolean(object, (Boolean) (v));
            } else {
                LOG.debug("Can't set primitive field " + field.getName() + " to " + v);
            }
        } catch (final IllegalAccessException e) {
            throw new IllegalAccessException("IllegalAccessException binding " + v + " to field " + field.getName());
        }

    }

    private static void put(Object object, Field field, Object value) throws IllegalAccessException {
        if (value == null)
            return;
        if (value instanceof String) {
            put(object, field, (String) value);
        } else if (field.getType().isPrimitive()) {
            putPrimitive(object, field, value);

        } else {

        }

    }

    private static void put(Object object, Field field, String var) throws IllegalAccessException {
        if (StringUtils.isBlank(var))
            return;
        if (field.getType().isPrimitive()) {
            putPrimitive(object, field, var);
        } else {

            Object value = null;
            if (field.getType() == String.class) {
                value = var;
            } else if (field.getType() == Date.class) {
                value = Util.parseJdbcDate(var);
            } else if (field.getType() == Integer.class) {
                value = new Integer(var);
            } else if (field.getType() == Double.class) {
                value = new Double(var);
            } else if (field.getType() == Character.class) {
                value = new Character(var.charAt(0));
            } else if (field.getType() == Long.class) {
                value = new Long(var);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Binding " + value + " to field " + field.getName() + " for " + object.getClass().getName());
            }
            try {
                field.set(object, value);
            } catch (final IllegalArgumentException e) {
                throw new IllegalArgumentException("IllegalArgumentException binding " + value + " to field "
                        + field.getName(), e);
            } catch (final IllegalAccessException e) {
                throw new IllegalAccessException("IllegalAccessException binding " + value + " to field "
                        + field.getName());
            }
        }

    }

    private static void putPrimitive(Object object, Field field, String s) throws IllegalAccessException {
        if (StringUtils.isBlank(s))
            return;
        try {
            if (field.getType() == Byte.TYPE) {
                field.setByte(object, Byte.parseByte(s));
            } else if (field.getType() == Integer.TYPE) {
                field.setInt(object, Integer.parseInt(s));
            } else if (field.getType() == Double.TYPE) {
                field.setDouble(object, Double.parseDouble(s));
            } else if (field.getType() == Float.TYPE) {
                field.setFloat(object, Float.parseFloat(s));
            } else if (field.getType() == Long.TYPE) {
                field.setLong(object, Long.parseLong(s));
            } else if (field.getType() == Short.TYPE) {
                field.setShort(object, Short.parseShort(s));
            } else if (field.getType() == Boolean.TYPE) {
                field.setBoolean(object, Boolean.parseBoolean(s));
            } else {
                LOG.debug("Can't set primitive field " + field.getName() + " to " + s);
            }
        } catch (final IllegalAccessException e) {
            throw new IllegalAccessException("IllegalAccessException binding " + s + " to field " + field.getName());
        }

    }
}
