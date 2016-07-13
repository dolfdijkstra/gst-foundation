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

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftErrors;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * {@code<mail.send to="recipient,recipient"
 * 
 * [from="your e-mail"] [subject="subject of message"] [body="message body"]
 * [replyto="your alternate e-mail address"] [contenttype="content format"]
 * [charset="character set"] />}
 * 
 * @author Tony Field
 * @since 4-Nov-2008
 */
public final class Send extends AbstractTagRunner {
    public Send() {
        super("mail.send");
    }

    public void setTo(String commaSeparatedListOfEmailAddresses) {
        set("to", commaSeparatedListOfEmailAddresses);
    }

    public void setFrom(String from) {
        set("from", from);
    }

    public void setSubject(String subject) {
        set("subject", subject);
    }

    public void setBody(String body) {
        set("body", body);
    }

    public void setReplyto(String replyto) {
        set("replyto", replyto);
    }

    public void setContentType(String contentType) {
        set("contenttype", contentType);
    }

    public void setCharset(String charset) {
        set("charset", charset);
    }

    public static boolean sendMail(ICS ics, String to, String from, String subject, String body, String replyto,
            String contentType, String charset) {
        Send send = new Send();
        send.setTo(to);
        send.setFrom(from);
        send.setSubject(subject);
        send.setBody(body);
        send.setReplyto(replyto);
        send.setContentType(contentType);
        send.setCharset(charset);
        send.execute(ics);
        return ics.GetErrno() != ftErrors.emailexception;
    }

    public static boolean sendMail(ICS ics, String to, String subject, String body) {
        return sendMail(ics, to, null, subject, body, null, null, null);
    }

}
