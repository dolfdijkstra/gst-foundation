/*
 * Copyright (c) 2015 Function1 Inc. All rights reserved.
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
package com.fatwire.gst.foundation.facade.engage;

import java.util.Date;

/**
 * Facade over the WCS Engage personalization infrastructure, particularly the
 * Visitor Data Manager (VDM).
 *
 * Note this facade does not implement all methods of the VDM API. The following methods
 * are not yet implemented and may be added in the future:
 * <pre>
 *     getaccessid
 *     getcommerceid
 *     gethistorycount
 *     gethistoryearliest
 *     gethistorylatest
 *     gethistorysum
 *     setaccessid
 *     sercommerceid
 * </pre>
 *
 * @author Tony Field
 * @since 15-08-04 5:36 PM
 */
public interface VisitorDataManagerService {

    /**
     * Set the alias string for the visitor. Does not re-set if no
     * change in the alias value (checks first).
     * @param name alias name (e.g. userid, tracker-cookie)
     * @param value alias value
     */
    void setAlias(String name, String value);

    /**
     * Retrieve the value for the specified alias.
     * @param name alias name
     * @return value if set
     */
    String getAlias(String name);

    /**
     * Set a visitor attribute into the visitor context. Does not re-set if no change
     * in the attribute value (checks first).
     * @param name attribute name
     * @param value attribute value
     */
    void setScalar(String name, String value);

    /**
     * Get the attribute value corresponding to the name sepcified.
     * @param name the attribute name
     * @return the attribute value
     */
    String getScalar(String name);

    /**
     * Save the object in the visitor repository. Useful for saving shopping carts.
     * @param name name of the object
     * @param value object value - must implement either Serializable or IStorableObject
     */
    void saveScalarObject(String name, Object value);

    /**
     * Retrieve the visitor object. Useful for retrieving shopping carts
     * @param name object name
     * @return visitor object value
     */
    Object loadScalarObject(String name);


    /**
     * Save a time-stamped attribute to the visitor context
     * @param definition the name of the history definition to be used
     * @param name name of the history attribute to be stored
     * @param value value of the history attribute to be stored
     */
    void recordHistory(String definition, String name, Object value);

    void flushInactive(Date cutoff);
    void mergeInactive(Date cutoff);

}
