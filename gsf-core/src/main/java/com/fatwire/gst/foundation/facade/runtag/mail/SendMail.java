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

package com.fatwire.gst.foundation.facade.runtag.mail;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * Wrapper around the SENDMAIL xml tag
 * 
 * @author Mike Field
 * @since August 15, 2008
 */
public final class SendMail extends AbstractTagRunner {
    // Default Constructor
    public SendMail() {
        super("SENDMAIL");
    }

    /**
     * Sets "to" (the recipient) to the value of <code>s</code>
     * 
     * @param s The recipient's email address
     */
    public void setTo(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid TO string: " + s);
        }
        this.set("TO", s);
    }

    /**
     * Sets subject to the value of <code>s</code>
     * 
     * @param s The email's subject line
     */
    public void setSubject(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid subject string: " + s);
        }
        this.set("SUBJECT", s);
    }

    /**
     * Sets body to the value of <code>s</code>
     * 
     * @param s The body of the email message
     */
    public void setBody(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid body string: " + s);
        }
        this.set("BODY", s);
    }

}
