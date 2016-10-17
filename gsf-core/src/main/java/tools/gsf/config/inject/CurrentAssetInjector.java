/*
 * Copyright 2016 Function1. All Rights Reserved.
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

package tools.gsf.config.inject;

import COM.FutureTense.Interfaces.ICS;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.gsf.facade.assetapi.AssetAccessTemplate;
import tools.gsf.facade.assetapi.AssetIdUtils;
import tools.gsf.facade.assetapi.asset.CurrentAsset;
import tools.gsf.facade.assetapi.asset.ScatteredAsset;
import tools.gsf.facade.assetapi.asset.ScatteredAssetAccessTemplate;
import tools.gsf.facade.assetapi.asset.TemplateAsset;
import tools.gsf.facade.assetapi.asset.TemplateAssetAccess;

import java.lang.reflect.Field;

/**
 * @author Tony Field
 * @since 2016-08-24
 */
public class CurrentAssetInjector implements Injector {
    private static final Logger LOG = LoggerFactory.getLogger(CurrentAssetInjector.class);

    private final ICS ics;
    private final TemplateAssetAccess taa;
    private final ScatteredAssetAccessTemplate saa;
    private final AssetAccessTemplate aat;

    public CurrentAssetInjector(ICS ics, TemplateAssetAccess taa, ScatteredAssetAccessTemplate saa, AssetAccessTemplate aat) {
        this.ics = ics;
        this.taa = taa;
        this.saa = saa;
        this.aat = aat;
    }

    @Override
    public void inject(Object dependent) {
        if (dependent == null) {
            throw new IllegalArgumentException("dependent cannot be null.");
        }
        Class<?> c = dependent.getClass();
        while (c != Object.class && c != null) {
            for (final Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(CurrentAsset.class)) {
                    String[] attributes = field.getAnnotation(CurrentAsset.class).attributes();
                    AssetId id = _getAssetId(field);
                    Object dependency = readAsset(id, attributes, field.getType());
                    _injectIntoField(field, dependent, dependency, id);
                }
            }
            c = c.getSuperclass();
        }
    }

    private AssetId _getAssetId(Field field) {
        AssetId id;
        try {
            id = AssetIdUtils.currentId(ics);
        } catch (IllegalArgumentException e) {
            throw new InjectionException("Could not inject current asset into field " + field.getName() + " because current asset could not be found", e);
        }
        return id;
    }


    protected Object readAsset(AssetId id, String[] attributes, Class assetDataType) {
        Object dependency;
        if (TemplateAsset.class.equals(assetDataType)) {
            dependency = taa.read(id, attributes);
        } else if (ScatteredAsset.class.equals(assetDataType)) {
            dependency = saa.read(id, attributes);
        } else if (AssetData.class.equals(assetDataType)) {
            dependency = aat.readAsset(id, attributes);
        } else {
            throw new InjectionException("Unsupported field type " + assetDataType);
        }
        return dependency;
    }

    private void _injectIntoField(Field field, Object dependent, Object dependency, AssetId id) {
        try {
            LOG.debug("Injecting current asset {} into field {} for {}", id, field.getName(), dependent.getClass().getName());
            field.setAccessible(true);
            field.set(dependent, dependency);
        } catch (final IllegalArgumentException | IllegalAccessException e) {
            throw new InjectionException("Exception injecting current asset" + id + " into field " + field.getName(), e);
        }
    }
}
