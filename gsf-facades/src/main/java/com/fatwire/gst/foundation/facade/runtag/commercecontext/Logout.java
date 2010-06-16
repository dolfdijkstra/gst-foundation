/*
 * Copyright (c) 2009 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.runtag.commercecontext;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 *When visitors access a site implemented with CS-Engage, they are automatically
 * assigned a visitor ID. This tag clears the current visitor ID and assigns a
 * new one. Typical use for this method is to use it to create a logout button
 * so a new visitor can interact with the site without having to end the session.

If the current visitor is logged into Transact or any other external database
 through Commerce Connector, this method automatically clears this visitor's
 commerce ID or access ID as well, which disconnects the visitor from Commerce
 Connector.
 *
 * @author Tony Field
 * @since Sep 17, 2009
 */
public final class Logout extends AbstractTagRunner
{
    public Logout() { super("COMMERCECONTEXT.LOGOUT"); }
}
