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
import javax.servlet.jsp.JspException;

/**
 * Perform all installation tasks that remain un-done so far.
 * Once completed, return the installation status.
 *
 * @author Tony Field
 * @since Mar, 2012
 */
public class Install extends InstallStatus {

    public void doTag() throws JspException, IOException {
        InstallerEngine ie = new InstallerEngine(getICS(), getTargetFlexFamilies());
        int bInstallStatus = ie.getInstallStatus();
        ie.doInstall(bInstallStatus);
        super.doTag();
    }

}
