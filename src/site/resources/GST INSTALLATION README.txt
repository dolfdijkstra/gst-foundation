1) Compile jars in this order, with "mvn clean package install":
  jar name (gst/gsf dependencies)
  -------------------------------
  gst-parent-3 (just a pom.xml file; no jar)
  gsf-facades-2.0-SNAPSHOT.jar (gst-parent, also requires all the com.fatwire.cs 7.5.3 jars)
  gsf-wra-1.0-SNAPSHOT.jar (gsf-facades, gst-parent)
  gsf-taglib-1.0-SNAPSHOT.jar (gsf-wra, gst-parent)
  [IN PROGRESS?] gsf-tagging-1.0-SNAPSHOT.jar (gsf-facades, gst-parent)

2) In futuretense_xcel.ini, set the following property:
  xcelerate.pageref=com.fatwire.gst.foundation.url.WraPageReference

3) In ServletRequest.properties, set the following properties:
# set to the name of your wrapper element. If not set, it defaults to "GST/Dispatcher"
com.fatwire.gst.foundation.url.wrapathassembler.dispatcher=Wrapper

uri.assembler.1.shortform=wrapath
uri.assembler.1.classname=com.fatwire.gst.foundation.url.WraPathAssembler
uri.assembler.2.shortform=query
uri.assembler.2.classname=com.fatwire.cs.core.uri.QueryAssembler

4) For each CS environment, set the system property com.fatwire.gst.foundation.env-name (i.e. in a CS startup script). For example,
  -Dcom.fatwire.gst.foundation.env-name=fatwire-dev

5) deploy the jars to your CS webserver

6) In ContentServer, create a Virtual Webroot (GSTVirtualWebroot) asset for each site. The 'Environment Name' attribute must match the com.fatwire.gst.foundation.env-name property from step #4.

7) In each Web-Referencable Asset, set the path (OOB) field to a fully qualified URL using the Master Virtual Webroot.

For more information, please refer to the GST Site Foundation documentation (http://www.nl.fatwire.com/svn/dta/contrib/gst-foundation/trunk/gsf-site/src/site/resources/)