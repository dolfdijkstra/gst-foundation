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

package com.fatwire.gst.foundation.facade.assetapi.asset;

import java.util.Date;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.cs.core.db.Util;

/**
 * @author Dolf.Dijkstra
 * @since Apr 21, 2011
 */
public class PreviewContext {

    /**
     * Returns the value of the preview date based on the session variable and
     * the setting for the xcelerate property <tt>cs.sitepreview</tt>.
     * 
     * @param ics Content Server context object
     * @param ssvarName server session variable name
     * @return the preview date
     */
    public static Date getPreviewDateFromSession(final ICS ics, final String ssvarName) {
        final Date cutoff = ics.GetSSVar(ssvarName) != null ? Util.parseJdbcDate(ics.GetSSVar(ssvarName)) : null;
        return getPreviewDate(ics, cutoff);
    }

    /**
     * Returns the value of the preview date based on the cs variable and the
     * setting for the xcelerate property <tt>cs.sitepreview</tt>.
     * 
     * @param ics Content Server context object
     * @param varName variable name
     * @return the date to preview
     */
    public static Date getPreviewDateFromCSVar(final ICS ics, final String varName) {
        final Date cutoff = ics.GetVar(varName) != null ? Util.parseJdbcDate(ics.GetVar(varName)) : null;
        return getPreviewDate(ics, cutoff);
    }

    /**
     * Returns the value of the preview date based on the provided date and the
     * setting for the xcelerate property <tt>cs.sitepreview</tt>.
     * 
     * @param ics Content Server context object
     * @param cutoff cut-off date
     * @return the date to preview
     */
    public static Date getPreviewDate(final ICS ics, final Date cutoff) {
        if (ics.LoadProperty("futuretense.ini;futuretense_xcel.ini")) {
            if (ftMessage.cm.equals(ics.GetProperty(ftMessage.cssitepreview))) {
                // We disable caching if and ONLY if cs.sitepreview is
                // contentmanagement. Check for that property in the ini files
                ics.DisableFragmentCache();

                // Insite Editing is enabled
                if (null == cutoff)
                    return new Date();
                else
                    return cutoff;

            } else if (ftMessage.disabled.equals(ics.GetProperty(ftMessage.cssitepreview)))
                return null;
            else
                return new Date(); // site preview disabled or delivery,
            // implies production install, use
            // server date

        } else
            // Cannot read from property file, use server date
            // TODO: isn't ignoring cutoff a better option when prop can't be
            // read??
            return new Date();
    }

    /**
     * Checks if start/enddate checking is enabled.
     * 
     * @param ics Content Server context object
     * @return true if 'cs.sitepreview' xcelerate property is either
     *         'contentmanagement' or 'delivery', false if set to 'disabled'.
     */
    public static boolean isSitePreviewEnabled(final ICS ics) {
        boolean ret = false;
        if (ics.LoadProperty("futuretense.ini;futuretense_xcel.ini")) {

            if (!ftMessage.disabled.equals(ics.GetProperty(ftMessage.cssitepreview))) {
                ret = true;
            }

        }
        return ret;

    }

    /**
     * @param ics Content Server context object
     * @return true if 'cs.sitepreview' xcelerate property is set to delivery
     */
    public static boolean isSitePreviewDelivery(final ICS ics) {
        boolean ret = false;

        if (ics.LoadProperty("futuretense.ini;futuretense_xcel.ini")) {

            if (ftMessage.delivery.equals(ics.GetProperty(ftMessage.cssitepreview))) {
                ret = true;
            }
        }
        return ret;

    }
}
