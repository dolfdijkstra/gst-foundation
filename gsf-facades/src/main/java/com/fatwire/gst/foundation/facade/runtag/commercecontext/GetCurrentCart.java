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
package com.fatwire.gst.foundation.facade.runtag.commercecontext;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * @author Tony Field
 * @since 2015-08-04 11:10 PM
 */
public class GetCurrentCart extends AbstractTagRunner {
    public GetCurrentCart() {
        super("COMMERCECONTEXT.GETCURRENTCART");
    }
    public GetCurrentCart(String varname) {
        this();
        setVarname(varname);
    }
    public void setVarname(String s) {
        set("VARNAME", s);
    }
}
