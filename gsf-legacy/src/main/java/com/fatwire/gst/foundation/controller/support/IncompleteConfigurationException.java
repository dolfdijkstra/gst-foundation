/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.controller.support;

/**
 * @author Dolf.Dijkstra
 * @since 4 April 2012
 * @deprecated see {@link tools.gsf.config.IncompleteConfigurationException}
 */
public class IncompleteConfigurationException extends tools.gsf.config.IncompleteConfigurationException {

    /**
     * 
     */
    private static final long serialVersionUID = -411240247677991694L;

    public IncompleteConfigurationException(String message) {
        super(message);

    }

    public IncompleteConfigurationException(Throwable cause) {
        super(cause);

    }

    public IncompleteConfigurationException(String message, Throwable cause) {
        super(message, cause);

    }

}