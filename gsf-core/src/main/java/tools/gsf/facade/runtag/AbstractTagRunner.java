/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
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

package tools.gsf.facade.runtag;

import COM.FutureTense.Interfaces.ICS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.gsf.facade.FTValListFacade;

import java.util.Enumeration;

/**
 * @author Dolf Dijkstra
 */

public abstract class AbstractTagRunner extends FTValListFacade implements TagRunner {

    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.facade.runtag.AbstractTagRunner");

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
     * <p>
     * Implementation in this class does nothing, subclasses can override.
     *
     * @param ics Content Server context object
     */
    protected void bind(ICS ics) {

    }

    /**
     * Executes the tag via ics.runtag
     * <p>
     * order is
     * <ul>
     * <li>bind(ics);</li>
     * <li>preExecute();</li>
     * <li>ics.runTag();</li>
     * <li>postExceute();</li>
     * <li>handleError() if runTag or postExecute set errno to anything else
     * then zero.</li>
     * </ul>
     *
     * @see TagRunner#execute(ICS)
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
                sb.append("\n\t").append(key).append("=")
                        .append(isPW(key) ? "<password suppressed>" : list.getValString(key));
            }
            sb.append("\nVariables:");
            Enumeration<?> e = ics.GetVars();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                sb.append("\n\t").append(key).append("=").append(isPW(key) ? "<password suppressed>" : ics.GetVar(key));
            }
            sb.append("\nSession Variables:");
            e = ics.GetSSVars();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                sb.append("\n\t").append(key).append("=")
                        .append(isPW(key) ? "<password suppressed>" : ics.GetSSVar(key));
            }
            LOG.trace(sb.toString());
        }
        String s = ics.runTag(tagName, list);
        if (LOG.isTraceEnabled()) {
            StringBuffer sb = new StringBuffer("Just completed execution of runTag for tag: " + tagName + ".  ");
            sb.append("\nInput param list:");
            for (Object k : list.keySet()) {
                String key = (String) k;
                sb.append("\n\t").append(key).append("=")
                        .append(isPW(key) ? "<password suppressed>" : list.getValString(key));
            }
            sb.append("\nVariables:");
            Enumeration<?> e = ics.GetVars();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                sb.append("\n\t").append(key).append("=").append(isPW(key) ? "<password suppressed>" : ics.GetVar(key));
            }
            sb.append("\nSession Variables:");
            e = ics.GetSSVars();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                sb.append("\n\t").append(key).append("=")
                        .append(isPW(key) ? "<password suppressed>" : ics.GetSSVar(key));
            }
            LOG.trace(sb.toString());
        }
        postExecute(ics);
        if (ics.GetErrno() < 0 && ics.GetErrno()!=-101) {
            this.handleError(ics);
        }
        return s;
    }

    private static final boolean isPW(String key) {
        return key != null && key.toLowerCase().contains("password");
    }

    /**
     * Template method that is called before ics.runTag
     * <p>
     * subclasses can override
     *
     * @param ics Content Server context object
     */

    protected void preExecute(ICS ics) {

    }

    /**
     * Template method that is called after ics.runTag and before handleError
     * <p>
     * subclasses can override
     *
     * @param ics Content Server context object
     */
    protected void postExecute(ICS ics) {

    }

    /**
     * default error handling method. This implemetation throws an
     * TagRunnerRuntimeException.
     * <p>
     * subclasses can override
     *
     * @param ics Content Server context object
     * @see TagRunnerRuntimeException
     */
    protected void handleError(ICS ics) {
        throw new TagRunnerRuntimeException("ics.runTag(" + tagName + ") returned an errno.", ics.GetErrno(), list,
                ics.getComplexError(), ics.GetVar("pagename"), ics.ResolveVariables("CS.elementname"));

    }
}
