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

package com.fatwire.gst.foundation.facade.cm;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.FTValListFacade;

public abstract class FTCmdTemplate extends FTValListFacade {

    public static final String FTCMD = "ftcmd";

    private final String ftcmd;

    protected FTCmdTemplate(final String ftcmd, final String table) {
        super();
        list.setValString(FTCMD, ftcmd);
        list.setValString("tablename", table);
        this.ftcmd = ftcmd;
    }

    /**
     * We can do preliminary check on the values for list to see if they are
     * complete
     * 
     * @return true to continue with ics.CatalogManager call
     */
    protected boolean preExcecuteAssert(final ICS ics) {
        return true;
    }

    protected void postExcecuteCheck(final ICS ics) {

    }

    final public void execute(final ICS ics) {
        if (this.preExcecuteAssert(ics)) {
            ics.ClearErrno();
            if (!ics.CatalogManager(list)) {
                throw new RuntimeException("CatalogManager said no to " + ftcmd + "with errno: " + ics.GetErrno());
            }
            this.postExcecuteCheck(ics);
        }
    }

}
