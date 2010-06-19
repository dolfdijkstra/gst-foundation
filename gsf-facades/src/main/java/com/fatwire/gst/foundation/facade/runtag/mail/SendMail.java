/*
 * Copyright (c) 2008 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
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
