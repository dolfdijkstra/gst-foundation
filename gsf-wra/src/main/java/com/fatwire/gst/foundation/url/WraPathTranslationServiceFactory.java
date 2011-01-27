/*
 * Copyright 2010 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.url;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.ics.ICSFactory;
import com.fatwire.gst.foundation.url.db.UrlRegistry;

/**
 * Used to instantiate path translation services.  Probably should get replaced by DI at some point.
 *
 * @author Tony Field
 * @since Jul 21, 2010
 */
public final class WraPathTranslationServiceFactory
{

	/**
	 * Return a new instance of the WraPathTranslationService.
	 *
	 * @param ics context, if available. Null is allowed
	 * @return service
	 */
	public static WraPathTranslationService getService(ICS ics)
	{
		if (ics == null)
		{
			return new UrlRegistry(ICSFactory.newICS());
		}
		else
		{
			Object o = ics.GetObj(WraPathTranslationService.class.getName());
			if (o instanceof UrlRegistry)
				return (UrlRegistry) o;

			UrlRegistry x = new UrlRegistry(ics);
			ics.SetObj(WraPathTranslationService.class.getName(), x);
			return x;
		}

	}
}
