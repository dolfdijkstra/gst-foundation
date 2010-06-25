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

package com.fatwire.gst.foundation.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.gst.foundation.test.jndi.VerySimpleInitialContextFactory;

import COM.FutureTense.CS.Factory;
import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IPS;
import COM.FutureTense.Servlet.IPSRegistry;
import COM.FutureTense.Util.ftMessage;
import COM.FutureTense.Util.ftTimedHashtable;
import dd.DebugHelper;
import dd.service.ICSLocator;
import dd.service.ICSLocatorSupport;

public abstract class CSTest extends TestCase {
    static Log log = LogFactory.getLog(CSTest.class);

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        if (ds != null)
            ds.close();
        super.tearDown();
    }

    protected ICS ics;
    protected ICSLocator locator;
    private BasicDataSource ds;

    public CSTest() {
        super();
    }

    public CSTest(String name) {
        super(name);
    }

    private ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }

    String retrieveJndiName() throws IOException {

        Properties p;

        p = readProperties("futuretense.ini");
        String dsn = p.getProperty("cs.dsn");
        return p.getProperty("cs.dbconnpicture").replace("$dsn", dsn);

    }

    void setUpPool() throws Exception {

        Properties p = readProperties("datasource.properties");
        BasicDataSource ds = (BasicDataSource) BasicDataSourceFactory.createDataSource(p);
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, VerySimpleInitialContextFactory.class.getName());
        InitialContext c = new InitialContext();

        String dsn = this.retrieveJndiName();
        c.rebind(dsn, ds);
        this.ds = ds;

    }

    public Properties readProperties(final String name) throws IOException {
        Properties properties = null;
        InputStream in;

        in = this.getContextClassLoader().getResourceAsStream(name);

        if (in != null) {
            properties = new Properties();
            try {
                properties.load(in);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            throw new IllegalArgumentException(name + " could not be loaded.");
        }
        return properties;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setUpPool();
        if (System.getProperty("cs.installDir") == null) {
            throw new IllegalStateException("cs.installDir is not found as a property.");
        }
        if (!(System.getProperty("cs.installDir").endsWith("/") || System.getProperty("cs.installDir").endsWith("\\"))) {
            throw new IllegalStateException("cs.installDir property does not end with a slash or backslash. ("
                    + System.getProperty("cs.installDir") + ")");
        }
        if (!new File(System.getProperty("cs.installDir")).exists()) {
            throw new IllegalStateException("cs.installDir property does not exists. ("
                    + System.getProperty("cs.installDir") + ")");
        }

        // System.setProperty("cs.installDir",
        // "C:\\DATA\\CS\\zamak\\ContentServer\\");
        // NEEDS slash at the end

        long t = System.nanoTime();

        IPS ips = IPSRegistry.getInstance().get();
        ics = (ips != null) ? ips.GetICSObject() : null;
        if (ics == null) {
            long t0 = System.nanoTime();
            ics = Factory.newCS();
            DebugHelper.printTime(log, "newICS", t0);

            if (false) {
                FTValList cmds = new FTValList();
                cmds.put(ftMessage.verb, ftMessage.login);
                cmds.put(ftMessage.username, ftMessage.SiteReader);// "DefaultReader"
                cmds.put(ftMessage.password, ftMessage.SiteReaderPassword);// "SomeReader"
                // cmds.put(ftMessage.username, "firstsite");
                // cmds.put(ftMessage.password, "firstsite");
                if (!ics.CatalogManager(cmds) || ics.GetErrno() < 0)
                    throw new RuntimeException("Can't log in, errno " + ics.GetErrno());
                ics.RemoveVar("cshttp");
            }
        }

        DebugHelper.printTime(log, "booting ICS", t);
        locator = new ICSLocatorSupport(ics);

    }

    protected void dumpFTTH() {
        for (Object n : ftTimedHashtable.getAllCacheNames()) {
            ftTimedHashtable h = ftTimedHashtable.findHash(n.toString());
            System.out.println("name: " + h.getName());
            // for (Enumeration<String> k = h.keys(); k.hasMoreElements();) {
            // System.out.println("key: "+k.nextElement());
            //
            // }
        }
    }

}
