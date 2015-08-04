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

/**
 * Facade over the WCS Engage personalization infrastructure, particularly the
 * Cart API (CART).
 *
 * Note: This service will be expanded on an as-needed basis. Currently the following CART API
 * functions are NOT yet implemented:
 * <pre>
 * cart:additem
 * cart:clear
 * cart:cleardiscounts
 * cart:commit
 * cart:create
 * cart:deleteitem
 * cart:fromstring
 * cart:getcartdiscounts
 * cart:getcartdiscounttotal
 * cart:geterrors
 * cart:getfinaltotal
 * cart:getitemdiscounts
 * cart:getitemdiscounttotal
 * cart:getitemerrors
 * cart:getitemlegalvalues
 * cart:getitemparameter
 * cart:getitemparameters
 * cart:getitems
 * cart:getitemtotal
 * cart:getlegalvalues
 * cart:getparameter
 * cart:getparameters
 * cart:getpreliminarytotal
 * cart:getshippingtotal
 * cart:getstores
 * cart:gettaxtotal
 * cart:gettransactionid
 * cart:gettransactionids
 * cart:merge
 * </pre>
 * @author Tony Field
 * @since 2015-08-04 5:36 PM
 */
public interface CartService {
}
