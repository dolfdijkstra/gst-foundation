package com.fatwire.gst.foundation.controller;

import COM.FutureTense.Interfaces.*;
import COM.FutureTense.XML.Template.Seed;
import com.fatwire.developernet.CSRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class AbstractController implements Seed
{
    protected static final Log LOG = LogFactory.getLog("com.fatwire.gst.foundation.controller");

    public final String Execute(FTValList vIn, FTValList vOut)
    {
        FTVAL obj = vIn.getVal("_ICS");
        final ICS ics = (ICS)obj.GetObj();

        try
        {
            execute(ics);
        }
        catch(CSRuntimeException e)
        {
            handleCSRuntimeException(ics, e);
        }

        return "";
    }

    /**
     * Executes the core business logic of the controller.
     *
     * @param ics
     * @throws CSRuntimeException may throw a CSRuntimeException which is handled by handleCSRuntimeException
     */
    abstract protected void execute(ICS ics);

    /**
     * Handles teh exception, doing what is required
     *
     * @param ics ics context
     * @param e exception
     */
    abstract protected void handleCSRuntimeException(ICS ics, CSRuntimeException e);
}