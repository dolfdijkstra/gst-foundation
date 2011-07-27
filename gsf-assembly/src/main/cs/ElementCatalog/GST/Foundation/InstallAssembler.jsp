<%--

    Copyright 2010 FatWire Corporation. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld" %>
<%@ taglib prefix="ics" uri="futuretense_cs/ics.tld" %>
<%//
// GST/Foundation/InstallAssembler
//
// INPUT
//
// OUTPUT
//%>
<%@ page import="COM.FutureTense.Interfaces.ICS" %>
<%@ page import="COM.FutureTense.Interfaces.IList" %>
<%@ page import="COM.FutureTense.Interfaces.Utilities" %>
<%@ page import="COM.FutureTense.Util.ftErrors" %>
<%@ page import="COM.FutureTense.Util.ftMessage"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<cs:ftcs>

<%


        String srProps = getServletConfig().getServletContext().getRealPath("/WEB-INF/classes/ServletRequest.properties");
        InputStream in = new FileInputStream(srProps);
        Properties prop = new Properties();
        prop.load(in);
        in.close();
        int i = 1;
        boolean hasGSFAssembler = false;
        for (;;) {

            String classname = prop.getProperty("uri.assembler." + i + ".classname");
            //out.write(classname +"<br/>");
            if (classname == null || classname.trim().length()==0) {
								i--;
                break;
            }

            if ("com.fatwire.gst.foundation.url.WraPathAssembler".equals(classname)) {
                hasGSFAssembler = true;
            }

            i++;
        }

        int max = i;
        //out.write("max:"+i +"<br/>");
        if (!hasGSFAssembler) {
            out.write("Registring the GSF assembler in ServletRequest.properties.<br/>");
            for (i = max + 1; i > 1; i--) {
                String classname = prop.getProperty("uri.assembler." + (i - 1) + ".classname");
                String shortform = prop.getProperty("uri.assembler." + (i - 1) + ".shortform");
                out.write("" + classname + "<br/>");
                out.write("" + shortform + "<br/>");
                prop.setProperty("uri.assembler." + (i) + ".classname", classname);
                prop.setProperty("uri.assembler." + (i) + ".shortform", shortform);
            }
            prop.setProperty("com.fatwire.gst.foundation.url.wrapathassembler.dispatcher", "GST/Dispatcher");
            prop.setProperty("uri.assembler.1.shortform", "wrapath");
            prop.setProperty("uri.assembler.1.classname", "com.fatwire.gst.foundation.url.WraPathAssembler");
            File orig = new File(srProps);
            File bk = new File(srProps + "." + System.currentTimeMillis() + ".bk");
            if (orig.exists()) {
                orig.renameTo(bk);
                StringWriter sw = new StringWriter();
                FileOutputStream fout = new FileOutputStream(orig);
                prop.store(fout, "Modified by GSF installer.");
                fout.close();

            }

        }else {
            out.write("GSF assembler is already registered in ServletRequest.properties.No changes made.<br/>");
        }



%>

</cs:ftcs>