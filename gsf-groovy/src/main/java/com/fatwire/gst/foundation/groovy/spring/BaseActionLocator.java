package com.fatwire.gst.foundation.groovy.spring;

import java.lang.reflect.Constructor;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.ActionLocator;
import com.fatwire.gst.foundation.controller.action.AnnotationInjector;
import com.fatwire.gst.foundation.controller.action.AnnotationInjector.Factory;
import com.fatwire.gst.foundation.controller.action.IcsBackedObjectFactory;
import com.fatwire.gst.foundation.controller.action.RenderPage;
import com.fatwire.gst.foundation.mapping.MappingInjector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BaseActionLocator {

    protected static final Log LOG = LogFactory.getLog(BaseActionLocator.class.getPackage().getName());
    private ActionLocator fallbackActionLocator = new ActionLocator() {
    
            public Action getAction(final ICS ics) {
    
                return new RenderPage();
            }
    
            public Action getAction(final ICS ics, final String name) {
                return null;
            }
    
        };
    private Constructor<Factory> constructor;

    public BaseActionLocator() {
        super();
    }

    /**
     * @param ics
     * @param action
     */
    protected void injectDependencies(final ICS ics, Action action) {
        final Factory factory = getFactory(ics);
        AnnotationInjector.inject(action, factory);
        final AssetIdWithSite id = figureOutTemplateOrCSElementId(ics);
        if (id != null) {
            MappingInjector.inject(action, factory, id);
        }
    
    }

    private AssetIdWithSite figureOutTemplateOrCSElementId(final ICS ics) {
        String eid = ics.GetVar("eid");
        if (eid != null) {
            return new AssetIdWithSite("CSElement", Long.parseLong(eid), ics.GetVar("site"));
        }
        eid = ics.GetVar("tid");
        if (eid != null) {
            return new AssetIdWithSite("Template", Long.parseLong(eid), ics.GetVar("site"));
        }
        return null;
    }

    protected Factory getFactory(final ICS ics) {
        final Object o = ics.GetObj(Factory.class.getName());
        if (o instanceof Factory) {
            return (Factory) o;
        }
        Factory factory = null;
        try {
            factory = getInjectionFactory(ics);
        } catch (final Exception e) {
            LOG.warn(e);
        }
        if (factory == null) {
            factory = new IcsBackedObjectFactory(ics);
        }
        ics.SetObj(Factory.class.getName(), factory);
        return factory;
    }

    public final Factory getInjectionFactory(final ICS ics) throws Exception {
        Factory factory = null;
        if (constructor != null) {
            factory = constructor.newInstance(new Object[] { ics });
        }
        return factory;
    }

    @SuppressWarnings("unchecked")
    private void findConstructor(final String factoryClassname) throws ClassNotFoundException, NoSuchMethodException,
            SecurityException {
                if (factoryClassname != null) {
                    final Class<Factory> c = (Class<Factory>) Class.forName(factoryClassname);
                    constructor = c.getConstructor(ICS.class);
            
                }
            }

    /**
     * @param factoryClassname the factoryClassname to set
     */
    public void setFactoryClassname(final String factoryClassname) {
        try {
            findConstructor(factoryClassname);
        } catch (final Exception e) {
            throw new IllegalArgumentException("factoryClassname: " + factoryClassname + " is illegal. "
                    + e.getMessage(), e);
        }
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
        this.fallbackActionLocator = fallbackActionLocator;
    }

}
