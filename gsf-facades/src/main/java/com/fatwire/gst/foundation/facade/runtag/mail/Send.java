package com.fatwire.gst.foundation.facade.runtag.mail;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftErrors;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * <mail.send
 * to="recipient,recipient"
 * <p/>
 * [from="your e-mail"]
 * [subject="subject of message"]
 * [body="message body"]
 * [replyto="your alternate e-mail address"]
 * [contenttype="content format"]
 * [charset="character set"] />
 *
 * @author Tony Field
 * @since 4-Nov-2008
 */
public final class Send extends AbstractTagRunner
{
    public Send() { super("mail.send"); }

    public void setTo(String commaSeparatedListOfEmailAddresses)
    {
        set("to", commaSeparatedListOfEmailAddresses);
    }

    public void setFrom(String from) { set("from", from); }

    public void setSubject(String subject) { set("subject", subject); }

    public void setBody(String body) { set("body", body); }

    public void setReplyto(String replyto) { set("replyto", replyto); }

    public void setContentType(String contentType) { set("contenttype", contentType); }

    public void setCharset(String charset) { set("charset", charset); }

    public static boolean sendMail(ICS ics, String to, String from, String subject, String body, String replyto, String contentType, String charset)
    {
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

    public static boolean sendMail(ICS ics, String to, String subject, String body)
    {
        return sendMail(ics, to, null, subject, body, null, null, null);
    }

}
