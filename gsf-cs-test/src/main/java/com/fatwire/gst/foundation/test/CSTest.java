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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.TestCase;
import COM.FutureTense.CS.Factory;
import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.gst.foundation.test.jndi.VerySimpleInitialContextFactory;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * NOTE July 6, 2010: The following instructions are not rigorously tested but
 * the class works.
 * <p/>
 * JUnit test base class that allows AssetAPI and limited ICS usage in the
 * absence of the ContentServer web container.
 * <p/>
 * Using this class, it is possible to test the DAO layer without requiring
 * deployment to the web container.
 * <p/>
 * To set up, follow the following instructions:
 * <ol>
 * <li>mount the shared filesystem on your local machine in the same path that
 * it is mounted on on the application server</li>
 * <li>mount (or copy) the Content Server home folder onto your local file
 * system. It is probably not a bad idea to mount it in the same place that it
 * is mounted on the application server. TODO: verify</li>
 * <li>add the path to futuretense.ini to your classpath (this is the home
 * folder described above)</li>
 * <li>add a system property for cs.installDir, and set it to the of the Content
 * Server home folder</li>
 * <li>add a property file called "datasource.properties" to your classpath that
 * contains the following properties, set to the appropriate values for the
 * purposes of setting up a JDBCDataSource (you can probably get these from your
 * application server administrator: username, password, driverClassName, url,
 * maxActive, maxIdle)</li>
 * </ol>
 * This effectively sets up a local copy of Content Server without a servlet
 * context. Some operations that require the execution of a JSP element and
 * related items will fail when using ICS, but core DB operations should
 * succeed. The ICS object's cache is not reliable in this configuration,
 * however, and writes to the database will not be noticed on the main server.
 * An ICS object is available, protected, and as well
 * <code>SessionFactory.getSession(ics)</code> operates per usual.
 */
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
    //protected ICSLocator locator;
    private BasicDataSource ds;
    private boolean login;

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
        if (in == null) {
            File f = new File(System.getProperty("cs.installDir"), name);
            if (f.exists()) {
                in = new FileInputStream(f);
            }
        }

        if (in != null) {
            properties = new Properties();
            try {
                properties.load(in);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e, e);
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
        setUpPool();
        // System.setProperty("cs.installDir",
        // "C:\\DATA\\CS\\zamak\\ContentServer\\");
        // NEEDS slash at the end

        //long t = System.nanoTime();

        // IPS ips = IPSRegistry.getInstance().get();
        // ics = (ips != null) ? ips.GetICSObject() : null;
        if (ics == null) {
            //long t0 = System.nanoTime();
            ics = Factory.newCS();
            //DebugHelper.printTime(log, "newICS", t0);

            if (login) {
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

        //DebugHelper.printTime(log, "booting ICS", t);
        //locator = new ICSLocatorSupport(ics);

    }

}
