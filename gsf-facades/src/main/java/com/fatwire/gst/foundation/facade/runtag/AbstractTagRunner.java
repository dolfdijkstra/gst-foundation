package com.fatwire.gst.foundation.facade.runtag;

import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.FTValListFacade;

/**
 * 
 * 
 * @author Dolf Dijkstra
 * 
 */

public abstract class AbstractTagRunner extends FTValListFacade implements TagRunner {

    private static final Log LOG = LogFactory.getLog(AbstractTagRunner.class);

    private final String tagName;

    /**
     * @param tagName the name of the tag to be invoked.
     */
    protected AbstractTagRunner(String tagName) {
        this.tagName = tagName;
    }

    /**
     * Template method to bind variables on ics (or any object space) to the
     * current object.
     * <p/>
     * Implementation in this class does nothing, subclasses can override.
     * 
     * @param ics
     */
    protected void bind(ICS ics) {

    }

    /*
     * Executes the tag via ics.runtag
     * 
     * order is bind(ics); preExecute(); ics.runTag(); postExceute();
     * handleError() is runTag or postExecute set errno to anything else the
     * zero.
     * 
     * 
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.developernet.facade.TagRunner#execute(COM.FutureTense.Interfaces
     * .ICS)
     */
    public String execute(ICS ics) {
        bind(ics);
        if (ics.GetErrno() != 0) {
            ics.ClearErrno();
        }
        preExecute(ics);

        if (LOG.isTraceEnabled()) {
            StringBuffer sb = new StringBuffer("About to execute runTag for tag: " + tagName + ".  ");
            sb.append("\nInput param list:");
            for (Object k : list.keySet()) {
                String key = (String) k;
                sb.append("\n\t").append(key).append("=").append(
                        isPW(key) ? "<password suppressed>" : list.getValString(key));
            }
            sb.append("\nVariables:");
            Enumeration e = ics.GetVars();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                sb.append("\n\t").append(key).append("=").append(isPW(key) ? "<password suppressed>" : ics.GetVar(key));
            }
            sb.append("\nSession Variables:");
            e = ics.GetSSVars();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                sb.append("\n\t").append(key).append("=").append(
                        isPW(key) ? "<password suppressed>" : ics.GetSSVar(key));
            }
            LOG.trace(sb);
        }
        String s = ics.runTag(tagName, list);
        if (LOG.isTraceEnabled()) {
            StringBuffer sb = new StringBuffer("Just completed execution of runTag for tag: " + tagName + ".  ");
            sb.append("\nInput param list:");
            for (Object k : list.keySet()) {
                String key = (String) k;
                sb.append("\n\t").append(key).append("=").append(
                        isPW(key) ? "<password suppressed>" : list.getValString(key));
            }
            sb.append("\nVariables:");
            Enumeration e = ics.GetVars();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                sb.append("\n\t").append(key).append("=").append(isPW(key) ? "<password suppressed>" : ics.GetVar(key));
            }
            sb.append("\nSession Variables:");
            e = ics.GetSSVars();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                sb.append("\n\t").append(key).append("=").append(
                        isPW(key) ? "<password suppressed>" : ics.GetSSVar(key));
            }
            LOG.trace(sb);
        }
        postExecute(ics);
        if (ics.GetErrno() != 0) {
            this.handleError(ics);
        }
        return s;
    }

    private static final boolean isPW(String key) {
        return key != null && key.toLowerCase().contains("password");
    }

    /**
     * Template method that is called before ics.runTag
     * <p/>
     * subclasses can override
     * 
     * @param ics
     */

    protected void preExecute(ICS ics) {

    }

    /**
     * Template method that is called after ics.runTag and before handleError
     * <p/>
     * subclasses can override
     * 
     * @param ics
     */
    protected void postExecute(ICS ics) {

    }

    /**
     * default error handling method. This implemetation throws an
     * TagRunnerRuntimeException.
     * <p/>
     * subclasses can override
     * 
     * @param ics
     * @see TagRunnerRuntimeException
     */
    protected void handleError(ICS ics) {
        throw new TagRunnerRuntimeException("ics.runTag(" + tagName + ") returned an errno.", ics.GetErrno(), list, ics
                .getComplexError(), ics.GetVar("pagename"), ics.ResolveVariables("CS.elementname"));

    }
}
