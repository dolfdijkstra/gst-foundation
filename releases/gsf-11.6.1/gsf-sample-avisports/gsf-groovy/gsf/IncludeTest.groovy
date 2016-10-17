package gsf

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.include.IncludeService

public class IncludeTest implements Action {
    
    @InjectForRequest
    public IncludeService include

    @InjectForRequest
    public Model model;
    static Log LOG = LogFactory.getLog("com.fatwire.gst.foundation.customactions");

    @Override
    public void handleRequest(ICS ics) {
       model.add("title","a test page"); 

       include.element("sample", "GSFInclude")
    }
}

