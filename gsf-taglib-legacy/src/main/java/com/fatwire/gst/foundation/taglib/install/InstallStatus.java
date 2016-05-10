/*
 * Copyright 2010 Metastratus Web Solutions Limited. All Rights Reserved.
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

package com.fatwire.gst.foundation.taglib.install;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import com.fatwire.gst.foundation.taglib.GsfSimpleTag;

import org.apache.commons.lang.StringUtils;

/**
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Mar 23, 2012
 */
public class InstallStatus extends GsfSimpleTag {

    private String output;
    private String families = null;

    public void doTag() throws JspException, IOException {

        InstallerEngine ie = new InstallerEngine(getICS(), getPageContext().getServletContext(),
                getTargetFlexFamilies());
        Map<GSFComponent, Boolean> installStatus = ie.getInstallStatus();
        
        boolean complete = true;
        for (Boolean b : installStatus.values()) {
            if (!b) {
                complete = false;
                break;
            }
        }
        getJspContext().setAttribute(output, installStatus);
        getJspContext().setAttribute(output + "Complete", complete);
        super.doTag();
    }

    public final void setOutput(String output) {
        this.output = output;
    }

    public void setFamilies(String families) {
        this.families = families;
    }

    protected final List<String> getTargetFlexFamilies() {
        if (StringUtils.isBlank(families)) {
            return Arrays.asList("GSTAttribute");
        }
        return Arrays.asList(families.split(","));
    }

}
