package com.fatwire.gst.foundation.controller.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import COM.FutureTense.Util.ftErrors;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.controller.AbstractController;
import com.fatwire.gst.foundation.facade.RenderUtils;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;

import static COM.FutureTense.Interfaces.Utilities.goodString;

public abstract class AnnotationController extends AbstractController {

    protected void doExecute() {

        // record seid and eid and any other deps required
        RenderUtils.recordBaseCompositionalDependencies(ics);
        ControllerMappingResolver resolver = new ControllerMappingResolver();
        Object target = getTarget();
        Method m = resolver.findControllerMethod(ics, target);
        if (m != null) {
            executeDefault();
        }
        invokeControllerMethod(m, target);
    }

    /**
     * @param m
     * @param target
     */
    protected void invokeControllerMethod(Method m, Object target) {
        try {
            m.invoke(target, new Object[] { ics });
        } catch (IllegalArgumentException e) {
            handleException(e);
        } catch (IllegalAccessException e) {
            handleException(e);
        } catch (InvocationTargetException e) {
            handleException(e);
        }

    }

    /**
     * @return
     */
    protected Object getTarget() {
        return this;
    }

    /**
     * Default operation to execute if no matching annotated method can be
     * found.
     */
    abstract void executeDefault();

    @Override
    protected void handleException(final Exception e) {
        if (e instanceof CSRuntimeException) {
            handleCSRuntimeException((CSRuntimeException) e);
        } else {
            sendError(500, e);
        }
    }

    /**
     * This method transforms errno values into http status codes and sets them
     * using the X-Fatwire-Status header.
     * <p/>
     * Only some errnos are handled by this base class.
     * <p/>
     * More info coming soon
     * 
     * @param e exception
     */
    protected void handleCSRuntimeException(final CSRuntimeException e) {
        switch (e.getErrno()) {
            case 400:
            case ftErrors.badparams:
                sendError(400, e);
                break;
            case 404:
            case ftErrors.pagenotfound:
                sendError(404, e);
                break;
            case 403:
            case ftErrors.noprivs:
                sendError(403, e);
                break;
            default:
                sendError(500, e);
                break;
        }
    }

    /**
     * Record compositional dependencies that are required for the controller
     */
    protected void recordCompositionalDependencies() {
        if (ics.isCacheable(ics.GetVar(ftMessage.PageName))) {
            if (goodString(ics.GetVar("seid"))) {
                LogDep.logDep(ics, "SiteEntry", ics.GetVar("seid"));
            }
            if (goodString(ics.GetVar("eid"))) {
                LogDep.logDep(ics, "CSElement", ics.GetVar("eid"));
            }
        }
    }

}
