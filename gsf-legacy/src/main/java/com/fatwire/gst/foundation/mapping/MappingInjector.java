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
package com.fatwire.gst.foundation.mapping;

import COM.FutureTense.Util.ftErrors;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.controller.action.AnnotationInjector;
import com.fatwire.gst.foundation.controller.annotation.Mapping;
import com.fatwire.gst.foundation.controller.annotation.Mapping.Match;
import com.fatwire.gst.foundation.mapping.MappingValue.Type;
import com.openmarket.xcelerate.asset.AssetIdImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.gsf.config.Factory;
import tools.gsf.time.Stopwatch;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 * @deprecated - class due for rewriting
 */
public final class MappingInjector {
    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.mapping.MappingInjector");

    public static void inject(final Object object, final Factory factory, final AssetIdWithSite id) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }
        if (factory == null) {
            throw new IllegalArgumentException("factory cannot be null.");
        }
        Stopwatch stopwatch = factory.getObject("stopwatch", Stopwatch.class);
        try {
            final Field[] fields = AnnotationInjector.findFieldsWithAnnotation(object, Mapping.class);

            if (fields.length > 0) {
                final MappingService mappingService = factory.getObject("mappingService", MappingService.class);
                if (mappingService == null) {
                    throw new IllegalStateException("MappingService can not be retrieved from "
                            + factory.getClass().getName());
                }
                final Map<String, MappingValue> map = mappingService.readMapping(id);
                for (final Field field : fields) {
                    injectIntoField(object, map, field, id);
                }
            }
        } finally {
            stopwatch.elapsed("inject mapping for {}", object.getClass().getName());
        }
    }

    private static void injectIntoField(final Object object, final Map<String, MappingValue> map, final Field field,
                                        final AssetIdWithSite id) throws SecurityException {

        final Mapping ifr = field.getAnnotation(Mapping.class);

        String name = ifr.value();
        if (StringUtils.isBlank(name)) {
            name = field.getName();
        }

        final MappingValue value = map.get(name);
        if (value == null) {
            throw new CSRuntimeException("Can't find a value for mapping " + name + " for asset " + id,
                    ftErrors.badparams);
        }
        Object injectionValue;
        // Handle MappingVulue and AssetId special
        if (MappingValue.class.isAssignableFrom(field.getType())) {
            injectionValue = value;
        } else if (AssetId.class.isAssignableFrom(field.getType()) && value.getType() == Type.asset) {
            injectionValue = new AssetIdImpl(value.getLeft(), Long.parseLong(value.getRight()));
        } else if (AssetName.class.isAssignableFrom(field.getType()) && value.getType() == Type.assetname) {
            injectionValue = new AssetName(value.getLeft(), value.getRight());
        } else {
            injectionValue = value.getValue();
            final Match what = ifr.match();
            switch (what) {
                case left:
                    injectionValue = value.getLeft();
                    break;
                case right:
                    injectionValue = value.getRight();
                    break;
                case all:
                    break;
                default:
                    break;

            }
        }
        if (injectionValue == null) {
            throw new CSRuntimeException("No value found to map  '" + field.getType().getName() + "' into the field '"
                    + field.getName() + "' for an action " + object.getClass().getName(), ftErrors.badparams);
        }
        field.setAccessible(true); // make private fields accessible
        if (LOG.isDebugEnabled()) {
            LOG.debug("Injecting " + injectionValue.getClass().getName() + " into field " + field.getName()
                    + " of type " + field.getType().getName() + " for " + object.getClass().getName());
        }
        try {
            field.set(object, injectionValue);
        } catch (final IllegalArgumentException e) {
            throw new CSRuntimeException("IllegalArgumentException injecting " + injectionValue + " into field "
                    + field.getName(), ftErrors.exceptionerr, e);
        } catch (final IllegalAccessException e) {
            throw new CSRuntimeException("IllegalAccessException injecting " + injectionValue + " into field "
                    + field.getName(), ftErrors.exceptionerr, e);
        }
    }

}
