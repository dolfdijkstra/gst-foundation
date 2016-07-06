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

package com.fatwire.gst.foundation.facade.logging;

import org.apache.commons.logging.LogFactory;

/**
 * Utility class for Loggers.
 * 
 * @author Dolf Dijkstra
 * 
 * 
 * @deprecated as of release 12.x, replaced with SLF4J which is natively used by (and shipped along) WCS
 * 
 */
public class LogUtil {

    /**
     * Returns the Log for the provided class based on the package name of the
     * class.
     * 
     * @param clazz class
     * @return the logger with the name of the package of the clazz.
     */
    public static Log getLog(Class<?> clazz) {
        org.apache.commons.logging.Log f = LogFactory.getLog(clazz.getPackage().getName());
        return new LogEnhancer(f);
    }

   

}
