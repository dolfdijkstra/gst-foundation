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
package com.fatwire.gst.foundation.groovy.spring;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.IcsBackedObjectFactoryTemplate;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.facade.assetapi.AssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAssetAccessTemplate;
import com.fatwire.gst.foundation.facade.mda.DefaultLocaleService;
import com.fatwire.gst.foundation.facade.mda.LocaleService;
import com.fatwire.gst.foundation.include.IncludeService;
import com.fatwire.gst.foundation.include.JspIncludeService;
import com.fatwire.gst.foundation.mapping.IcsMappingService;
import com.fatwire.gst.foundation.mapping.MappingService;

/**
 * @author Dolf.Dijkstra
 * @since Apr 19, 2011
 */
public class ExtendedIcsBackedObjectFactory extends IcsBackedObjectFactoryTemplate {

    public ExtendedIcsBackedObjectFactory(final ICS ics) {
        super(ics);
    }

    public IncludeService createIncludeService(final ICS ics) {
        return new JspIncludeService(ics);
    }

    public ScatteredAssetAccessTemplate createScatteredAssetAccessTemplate(final ICS ics) {
        return new ScatteredAssetAccessTemplate(ics);
    }

    public AssetAccessTemplate createAssetAccessTemplate(final ICS ics) {
        return new AssetAccessTemplate(ics);
    }

    public MappingService createMappingService(final ICS ics) {
        return new IcsMappingService(ics);
    }

    public LocaleService createLocaleService(final ICS ics) {
        return new DefaultLocaleService(ics);
    }

    public Model createModel(final ICS ics) {
        return new Model();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.controller.action.IcsBackedObjectFactoryTemplate
     * #shouldCache(java.lang.Class)
     */
    @Override
    public boolean shouldCache(final Class<?> c) {
        // don't cache the mode as this is bound to the jsp page context and not
        // to ICS. It would leak variables into other elements if we allowed it
        // to cache.
        // TODO:medium, figure out if this should be done more elegantly. If
        // seems that scoping logic is
        // brough into the factory
        if (Model.class.isAssignableFrom(c)) {
            return false;
        }
        return super.shouldCache(c);
    }

}
