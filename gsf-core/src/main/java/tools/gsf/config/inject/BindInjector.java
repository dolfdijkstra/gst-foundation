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
package tools.gsf.config.inject;

import COM.FutureTense.Interfaces.ICS;
import com.fatwire.cs.core.db.Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * Helper to bind variables to an Object based on annotated fields.
 *
 * @author Dolf Dijkstra
 * @since 12 mei 2012
 */
public final class BindInjector implements Injector {

    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.config.inject.AnnotationBinder");

    private final ICS ics;

    public BindInjector(ICS ics) {
        this.ics = ics;
    }

    /**
     * Inject ICS runtime objects into the object. Objects flagged with the
     * {@link Bind} annotation will be populated by this method by
     * retrieving the value from ics context, request context, or session, as per the scope of the Bind annotation.
     *
     * @param dependent the object to inject into.
     */
    public void inject(final Object dependent) {
        if (dependent == null) {
            throw new IllegalArgumentException("Target cannot be null.");
        }
        Class<?> c = dependent.getClass();
        // all annotated fields.
        while (c != Object.class && c != null) {
            for (final Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(Bind.class)) {
                    bindToField(dependent, ics, field);
                }

            }

            c = c.getSuperclass();
        }
    }

    /**
     * @param object object to bind to
     * @param ics    the ics context
     * @param field  the field to inject to
     * @throws SecurityException security exception
     */
    private static void bindToField(final Object object, final ICS ics, final Field field) throws SecurityException {

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
                    if (!field.getType().isArray()) {
                        String var = ics.GetVar(name);
                        if (StringUtils.isBlank(var)) {
                            put(object, field, ics.GetObj(name));
                        } else {
                            put(object, field, var);
                        }
                    }
                    break;
                case request:
                	if (!field.getType().isArray()) {
                		put(object, field, ics.getAttribute(name));
                	}
                    break;
                case session:
                    @SuppressWarnings("deprecation")
                    HttpSession s = ics.getIServlet().getServletRequest().getSession(false);
                    if (s != null) {
                    	if (!field.getType().isArray()) {
                    		put(object, field, s.getAttribute(name));
                    	}
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
                LOG.debug("Can't set primitive field {} to {}", field.getName(), v);
            }
        } catch (final IllegalAccessException e) {
            throw new IllegalAccessException("IllegalAccessException binding " + v + " to field " + field.getName());
        }

    }

    private static void put(Object object, Field field, Object value) throws IllegalAccessException {
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            put(object, field, (String) value);
        } else if (field.getType().isPrimitive()) {
            putPrimitive(object, field, value);
        }

    }

    private static void put(Object object, Field field, String var) throws IllegalAccessException {
        if (StringUtils.isBlank(var)) {
            return;
        }
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
                value = var.charAt(0);
            } else if (field.getType() == Long.class) {
                value = new Long(var);
            } else {
            	if (LOG.isDebugEnabled()) {
            		LOG.debug("Annotated field {} has type {} but the object being bound has type {} which is not explicitly supported (yet)", field.getName(), field.getType(), var.getClass().getName());
            	}
            }
            LOG.debug("Binding {} to field {} for {}", value, field.getName(), object.getClass().getName());
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
        if (StringUtils.isBlank(s)) {
            return;
        }
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
                LOG.debug("Can't set primitive field {} to {}", field.getName(), s);
            }
        } catch (final IllegalAccessException e) {
            throw new IllegalAccessException("IllegalAccessException binding " + s + " to field " + field.getName());
        }
    }
}
