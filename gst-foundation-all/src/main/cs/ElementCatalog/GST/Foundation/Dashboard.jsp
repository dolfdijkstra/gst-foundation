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
<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%>
<%@ taglib prefix="ics" uri="futuretense_cs/ics.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="satellite" uri="futuretense_cs/satellite.tld"%>
<%@ taglib prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%>
<%@ taglib prefix="gsf-install"
	uri="http://gst.fatwire.com/foundation/tags/gsf-install"%>
<cs:ftcs>
	<gsf:root>
		<!DOCTYPE html>
		<html>
<head>
<title>GST Site Foundation Dashbaord</title>
<meta name="title" content="GST Site Foundation Dashbaord" />
<meta name="description"
	content="GST Site Foundation dashboard and control panel, showing the status of the application." />
<meta http-equiv="Pragma" content="No-cache">
<link href="/cs/Xcelerate/data/css/en_US/common.css" rel="styleSheet"
	type="text/css">
<link href="/cs/Xcelerate/data/css/en_US/content.css" rel="styleSheet"
	type="text/css">
<link href="/cs/Xcelerate/data/css/en_US/wemAdvancedUI.css"
	rel="stylesheet" type="text/css">
<link href="/cs/Xcelerate/data/css/en_US/toolbar.css" rel="styleSheet"
	type="text/css">
<style type="text/css">
.logoGSF {
	margin-top: 3px;
	font-size: 18px;
	color: #EFEFEF;
}

.menuItem a {
	color: #EFEFEF;
}

body a:visited {
	color: #EFEFEF;
}

table.install-status {
	border: 0;
	border-spacing: 0;
	border-collapse: collapse;
	padding: 0;
}

table.inner {
	border: 0;
	border-spacing: 0;
	border-collapse: collapse;
	padding: 3;
}
</style>
</head>
<body>
	<div class="topMenuBar">
		<div class="topMenuBarLeft">
			<div class="logoGSF">GST Site Foundation</div>
			<div class="topMenuBarMenu">
				<div class="menuItem">
					<satellite:link pagename="GST/Foundation/Dashboard"
						satellite="false" />
					<a href="${cs.referURL}">Dashboard</a>
				</div>
				<div class="menuItem">
					<a href="http://www.gst-foundation.org/">GSF Website</a>
				</div>
			</div>
		</div>
		<div class="topMenuBarRight"></div>
	</div>
	<div class="title-value-text">
		<h1>GST Site Foundation Dashboard</h1>
	</div>
	<%-- ----------- process forms first --------------- --%>
	<ics:if condition='${cs.cmd == "install"}'>
		<ics:then>
			<h2>Installing GSF Components...</h2>
			<c:forEach var="c" items="${paramValues.component}">
				Installing <c:out value="${c}"></c:out><br/>
			</c:forEach>
			<gsf-install:install output="gsfInstallStatus"
				components="${paramValues.component}" />
		</ics:then>
	</ics:if>

	<%-- --------------- Display dashboard -------------- --%>
	<gsf-install:install-status output="gsfStatus" />
	<c:if test="${not gsfStatusComplete}">
		<table class="install-status">
			<tr>
				<td class="message-bg-color">
					<table class="inner">
						<tr>
							<td valign="top"><img
								src="/cs/Xcelerate/graphics/common/msg/error.gif" width="25"
								height="22"></td>
							<td>
								<h3>GSF Installation Incomplete</h3>
								<p>The GST Site Foundation is not completely installed.
									Please select the components you would like to install.</p> <satellite:form
									satellite="false" method="post">
									<input type="hidden" name="pagename"
										value="GST/Foundation/Dashboard" />
									<input type="hidden" name="cmd" value="install" />

									<ul>
										<c:forEach var="c" items="${gsfStatus}">
											<c:choose>
												<c:when test="${c.value}">
													<li><c:out value="${c.key.description}" /> is already
														installed.</li>
												</c:when>
												<c:when test="${not c.value}">
													<li><input type="checkbox" name="component"
														value="${c.key.class.simpleName}" /> <c:out
															value="${c.key.description}" /></li>
												</c:when>

											</c:choose>
										</c:forEach>
									</ul>
									<input type="submit" value="Install/reinstall GSF components">
								</satellite:form>
							</td>
						</tr>
					</table>
			</td>
			</tr>
		</table>
		<p>&nbsp;</p>
	</c:if>

	<c:if test="${gsfStatusComplete}">
		<h3>GSF is properly installed and is healthy.</h3>
	</c:if>

	<ics:if
		condition='<%=System
								.getProperty("com.fatwire.gst.foundation.env-name") == null%>'>
		<ics:then>
			<table class="install-status">
				<tr>
					<td class="message-bg-color">
						<table class="inner">
							<tr>
								<td valign="top"><img
									src="/cs/Xcelerate/graphics/common/msg/warning.gif" width="25"
									height="22"></td>
								<td>
									<h3>Pretty URL Environment Not Identified</h3>

									<p>
										The pretty URL environment property name has not been set. In
										order for URL rewriting to occur, the system property
										identifying this particular environment must be set. This is
										usually done in the
										<code>catalina.sh</code>
										script for Tomcat (other places for other application
										servers). Typically, adding
										<code>-Dcom.fatwire.gst.foundation.env-name=YOURNAME</code>
										where
										<code>YOURNAME</code>
										is the environment name is sufficient. It should of course
										match the appropriate entry in the Virtual Webroot assets.
										Consult the GSF documentation, wiki, or mailing list for more
										information.
									</p>
									<p>The GSF sites will continue to render without this set,
										but they pages will not have pretty URLs.</p>
								</td>
							</tr>
						</table>
				</td>
				</tr>
			</table>
		</ics:then>
	        <ics:else>
		  <h3>Pretty URL Environment Identified</h3>
		  <p>System property <tt>com.fatwire.gst.foundation.env-name</tt>:  <%= System.getProperty("com.fatwire.gst.foundation.env-name") %></p>
	        </ics:else>
	</ics:if>

	<h2>GSF Control Panel</h2>
	<ul>
		<li><satellite:link pagename="GST/Foundation/Dashboard"
				satellite="false" /><a href="${cs.referURL}">Refresh GSF
				Dashboard</a></li>
		<%--
		<li><satellite:form satellite="false">
				<input type="hidden" name="pagename"
					value="GST/Foundation/Dashboard" /> Add WRA attributes to flex family
                <select name="families">
					<option>coming soon...</option>
				</select>
				<input type="submit" name="cmd" value="install" />
			</satellite:form></li>
			 --%>
		<li><satellite:link pagename="GST/Foundation/RebuildUrlRegistry"
				satellite="false" /> <a href="${cs.referURL}">Rebuild URL
				Registry</a> (requires loading/saving all WRA assets and can be long)</li>
		<li><satellite:link pagename="GST/Foundation/RewriteGenerator"
				satellite="false" /> <a href="${cs.referURL}">Generate
				mod_rewrite rules</a> for Virtual Webroots</li>
	</ul>
</body>
		</html>
	</gsf:root>
</cs:ftcs>
