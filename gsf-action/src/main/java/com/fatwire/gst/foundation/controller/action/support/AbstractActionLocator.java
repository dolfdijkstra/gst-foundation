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

package com.fatwire.gst.foundation.controller.action.support;

import COM.FutureTense.Cache.CacheManager;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.ActionLocator;
import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.Injector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * ActionLocator with support for {@link Injector} and a fall back
 * {@link ActionLocator} in case this ActionLocator is not designed or
 * configured to create an action for that name.
 * <p/>
 * Objects are created via a {@link Factory}, that can be obtained via the
 * <tt>FactoryFactory</tt>.
 * 
 * @author Dolf Dijkstra
 * @since Apr 27, 2011
 */
public abstract class AbstractActionLocator implements ActionLocator {

    protected static final Log LOG = LogFactory.getLog(AbstractActionLocator.class.getPackage().getName());
    /**
     * The default fallbackActionLocator in case no action is found.
     */
    private ActionLocator fallbackActionLocator;
    private Injector injector;

    public AbstractActionLocator() {
        super();
    }

    public AbstractActionLocator(Injector injector) {
        super();
        this.injector = injector;
    }

    public AbstractActionLocator(ActionLocator fallbackActionLocator, Injector injector) {
        super();
        this.fallbackActionLocator = fallbackActionLocator;
        this.injector = injector;
    }

    /**
     * This method selects the Action by calling the
     * {@link #doFindAction(ICS, String)} method. If that method returns an
     * Action, that Action will be dependency injected with the
     * {@link #getInjector()}, otherwise the {@link #getFallbackActionLocator()}
     * is called.
     * <p/>
     * It is the fall back ActionLocator that should inject its Action with
     * services.
     * <p/>
     * Contract is that either the {@link #doFindAction(ICS, String)} method or
     * the {@link #getFallbackActionLocator()} returns an Action, so that this
     * method never returns null.
     * 
     * @see com.fatwire.gst.foundation.controller.action.ActionLocator#getAction(COM.FutureTense.Interfaces.ICS,
     *      java.lang.String)
     */
    public final Action getAction(final ICS ics, final String name) {
        Action action = null;
        action = doFindAction(ics, name);
        /*
         * the if/else construct might be confusing here. The contract is that
         * only if doFindAction() is returning an Action (aka THIS locator)
         * Dependency Injection is happening, in other words, each locator
         * itself is responsible for Dependency Injection. For all locators that 
         * subclass AbstractActionLocator this contract is met.
         */
        if (action == null) {
            ActionLocator fabal = getFallbackActionLocator();
            if (fabal == null)
                throw new IllegalStateException(
                        "The doFindAction() method returned null and there is no fallback ActionLocator defined. This is an incorrect setup.");
            LOG.trace("No Action found. Trying with fallback action locator: " + fabal.getClass().getName());
            action = fabal.getAction(ics, name);
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Action '" + name + "' maps to action " + action.getClass().getName());
            }
            injectDependencies(ics, action);
            if (StringUtils.isNotBlank(name)) {
                CacheManager.RecordItem(ics, "gsf-action:" + name);
            } else if (action != null) {
                CacheManager.RecordItem(ics, "gsf-action:" + action.getClass().getName());
            }
        }
        return action;

    }

    /**
     * Template Method for finding the Action for the custom ActionLocator. In
     * case the Action is created through this method, it is expected to be
     * fully injected and ready to use.
     * 
     * @param ics the Content Server context
     * @param name the name of the action
     * @return the Action if found, null is valid.
     */
    protected abstract Action doFindAction(final ICS ics, final String name);

    /**
     * @param ics
     * @param action
     */
    protected final void injectDependencies(final ICS ics, final Action action) {

        Injector injector = getInjector();
        injector.inject(ics, action);

    }

    public Injector getInjector() {

        return injector;
    }

    /**
     * @return the fallbackActionLocator
     */
    public ActionLocator getFallbackActionLocator() {
        return fallbackActionLocator;
    }

    /**
     * @param fallbackActionLocator the fallbackActionLocator to set
     */
    public void setFallbackActionLocator(final ActionLocator fallbackActionLocator) {
        Assert.notNull(fallbackActionLocator);
        this.fallbackActionLocator = fallbackActionLocator;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }

}
