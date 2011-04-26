/*
 * Copyright 2009 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.test.jndi;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * 
 * @author Dolf Dijkstra
 * 
 */
public class VerySimpleInitialContextFactory implements javax.naming.spi.InitialContextFactory {
    private static Context initial;

    public VerySimpleInitialContextFactory() {
        super();

    }

    @SuppressWarnings("unchecked")
    public Context getInitialContext(final Hashtable<?, ?> environment) throws NamingException {
        // System.out.println("getInitialContext" + environment);
        if (initial == null)
            initial = new Context() {
                @SuppressWarnings("rawtypes")
                Hashtable env = new Hashtable(environment);
                Map<String, Object> store = new HashMap<String, Object>();

                public Object addToEnvironment(String propName, Object propVal) throws NamingException {
                    return env.put(propName, propVal);
                }

                public void bind(Name name, Object obj) throws NamingException {
                    store.put(name.toString(), obj);

                }

                public void bind(String name, Object obj) throws NamingException {
                    store.put(name.toString(), obj);

                }

                public void close() throws NamingException {
                    store.clear();

                }

                public Name composeName(Name name, Name prefix) throws NamingException {

                    return null;
                }

                public String composeName(String name, String prefix) throws NamingException {

                    return null;
                }

                public Context createSubcontext(Name name) throws NamingException {

                    return null;
                }

                public Context createSubcontext(String name) throws NamingException {

                    return null;
                }

                public void destroySubcontext(Name name) throws NamingException {

                }

                public void destroySubcontext(String name) throws NamingException {

                }

                public Hashtable<?, ?> getEnvironment() throws NamingException {
                    return env;
                }

                public String getNameInNamespace() throws NamingException {

                    return null;
                }

                public NameParser getNameParser(Name name) throws NamingException {

                    return null;
                }

                public NameParser getNameParser(String name) throws NamingException {

                    return null;
                }

                public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {

                    return null;
                }

                public NamingEnumeration<NameClassPair> list(String name) throws NamingException {

                    return null;
                }

                public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {

                    return null;
                }

                public NamingEnumeration<Binding> listBindings(String name) throws NamingException {

                    return null;
                }

                public Object lookup(Name name) throws NamingException {
                    return store.get(name.toString());
                }

                public Object lookup(String name) throws NamingException {
                    return store.get(name);
                }

                public Object lookupLink(Name name) throws NamingException {

                    return null;
                }

                public Object lookupLink(String name) throws NamingException {

                    return null;
                }

                public void rebind(Name name, Object obj) throws NamingException {
                    store.put(name.toString(), obj);

                }

                public void rebind(String name, Object obj) throws NamingException {
                    store.put(name, obj);

                }

                public Object removeFromEnvironment(String propName) throws NamingException {

                    return null;
                }

                public void rename(Name oldName, Name newName) throws NamingException {

                }

                public void rename(String oldName, String newName) throws NamingException {

                }

                public void unbind(Name name) throws NamingException {
                    store.remove(name.toString());

                }

                public void unbind(String name) throws NamingException {
                    store.remove(name);

                }
            };
        return initial;
    }
}
