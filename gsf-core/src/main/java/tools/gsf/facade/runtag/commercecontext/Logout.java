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

package tools.gsf.facade.runtag.commercecontext;

import tools.gsf.facade.runtag.AbstractTagRunner;

/**
 * When visitors access a site implemented with CS-Engage, they are
 * automatically assigned a visitor ID. This tag clears the current visitor ID
 * and assigns a new one. Typical use for this method is to use it to create a
 * logout button so a new visitor can interact with the site without having to
 * end the session.
 * <p>
 * If the current visitor is logged into Transact or any other external database
 * through Commerce Connector, this method automatically clears this visitor's
 * commerce ID or access ID as well, which disconnects the visitor from Commerce
 * Connector.
 *
 * @author Tony Field
 * @since Sep 17, 2009
 */
public final class Logout extends AbstractTagRunner {
    public Logout() {
        super("COMMERCECONTEXT.LOGOUT");
    }
}
