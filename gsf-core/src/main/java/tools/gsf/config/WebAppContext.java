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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tony Field
 * @since 2016-07-20
 */
public class WebAppContext implements AppContext {
    protected static final Logger LOG = LoggerFactory.getLogger("tools.gsf.config.WebAppContext");

    public static final String WEB_CONTEXT_NAME = "gsf/AppContext";

    private final AppContext parent;

    private Map<String, Object> localScope = new HashMap<>();

    /**
     * This constructor was needed for the SimpleWebAppContextLoader (now deprecated).
     *
     * @param context servlet context
     * @param parent application context
     */
    public WebAppContext(ServletContext context, AppContext parent) {
        super();
        this.parent = parent;
    }

    @Override
    public void init() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T getBean(String name, Class<T> c) {
        if (StringUtils.isBlank(name))
            throw new IllegalArgumentException("name cannot be null or empty");

        if (c.isArray()) {
            throw new IllegalArgumentException("Arrays are not supported");
        }

        Object o = localScope.get(name);

        if (o == null) {
            LOG.debug("Asking for bean by name {} of type {}.",name,  c.getName());
            try {
                // TODO: medium: check for other method signatures

                o = createByMethod(this, c);
                if (o != null && c.isAssignableFrom(o.getClass())) {
                    localScope.put(name, o);
                }
            } catch (final NoSuchMethodException e) {

                try {
                    if (parent != null)
                        o = parent.getBean(name, c); // don't register locally if found
                    else {
                        LOG.debug("Could not create  a {} via a Template method, trying via constructor.",c.getName());
                        o = createByConstructor(c);
                        if (o != null && c.isAssignableFrom(o.getClass())) {
                            localScope.put(name, o);
                        }
                    }
                } catch (final RuntimeException e1) {
                    throw e1;
                } catch (final Exception e1) {
                    throw new RuntimeException(e1);
                }
            } catch (final RuntimeException e) {
                throw e;
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
        return (T) o;
    }

    private static final Class<?>[] NO_PARAMS = new Class[0];
    private static final Object[] NO_ARGS = new Object[0];

    @SuppressWarnings("unchecked")
    private static <T> T createByMethod(Object template, Class<T> c) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method m;
        m = template.getClass().getMethod("create" + c.getSimpleName(), NO_PARAMS);
        if (m != null) {
            Object o = m.invoke(template, NO_ARGS);
            if (o != null && c.isAssignableFrom(o.getClass())) {
                return (T) o;

            }
        }
        return null;
    }

    private static <T> T createByConstructor(Class<T> c) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException   {
        if(c.isInterface()) return null; //TODO medium test for abstract class
        final Constructor<T> constr = c.getConstructor(NO_PARAMS);
        return constr.newInstance();
    }
}
