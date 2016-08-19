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

import COM.FutureTense.Util.ftErrors;
import com.fatwire.assetapi.data.AssetId;
import com.openmarket.xcelerate.asset.AssetIdImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.gsf.facade.assetapi.AssetIdWithSite;
import tools.gsf.mapping.AssetName;
import tools.gsf.mapping.AssetNameImpl;
import tools.gsf.mapping.Mapping;
import tools.gsf.mapping.MappingService;
import tools.gsf.mapping.MappingValue;
import tools.gsf.runtime.CSRuntimeException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Tony Field
 * @since 2016-07-21
 */
public final class MappingInjector {
    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.config.inject.MappingInjector");

    private final MappingService mappingService;

    public MappingInjector(MappingService mappingService) {
        this.mappingService = mappingService;
    }

    public void inject(final Object target, final String pagename) {
        if (target == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }
        final Field[] fields = findFieldsWithAnnotation(target, Mapping.class);
        if (fields.length > 0) {
            AssetIdWithSite id = mappingService.resolveMapped(pagename);
            if (id != null) {
	            final Map<String, MappingValue> map = mappingService.readMapping(id);
	            for (final Field field : fields) {
	                injectIntoField(target, map, field, id);
	            }
            } else {
            	LOG.warn("Cannot determine eid / tid for current code element (CSElement / Template) based on pagename '" + pagename + "', @Mapping annotations will be ignored.");
            }
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
            throw new CSRuntimeException("Can't find a value for mapping " + name + " for asset " + id, ftErrors.badparams);
        }
        Object injectionValue;
        // Handle MappingVulue and AssetId special
        if (MappingValue.class.isAssignableFrom(field.getType())) {
            injectionValue = value;
        } else if (AssetId.class.isAssignableFrom(field.getType()) && value.getType() == MappingValue.Type.asset) {
            injectionValue = new AssetIdImpl(value.getLeft(), Long.parseLong(value.getRight()));
        } else if (AssetName.class.isAssignableFrom(field.getType()) && value.getType() == MappingValue.Type.assetname) {
            injectionValue = new AssetNameImpl(value.getLeft(), value.getRight());
        } else {
            injectionValue = value.getValue();
            final Mapping.Match what = ifr.match();
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
        LOG.debug("Injecting {} into field {} of type {} for {}", injectionValue.getClass().getName(), field.getName(), field.getType().getName(), object.getClass().getName());
        try {
            field.set(object, injectionValue);
        } catch (final IllegalArgumentException e) {
            throw new CSRuntimeException("IllegalArgumentException injecting " + injectionValue + " into field " + field.getName(), ftErrors.exceptionerr, e);
        } catch (final IllegalAccessException e) {
            throw new CSRuntimeException("IllegalAccessException injecting " + injectionValue + " into field " + field.getName(), ftErrors.exceptionerr, e);
        }
    }

    /**
     * Finds the fields in the class or super class that are annotated with the
     * <tt>annnotationClass</tt> annotation.
     *
     * @param object           the object to inspect.
     * @param annnotationClass the annotation to find.
     * @return the array of fields with the annotation, never null.
     */
    private static Field[] findFieldsWithAnnotation(final Object object,
                                                    final Class<? extends Annotation> annnotationClass) {
        if (object == null) {
            throw new IllegalArgumentException("object must not be null.");
        }
        if (annnotationClass == null) {
            throw new IllegalArgumentException("clazz must not be null.");
        }
        final List<Field> x = new ArrayList<>();
        Class<?> c = object.getClass();
        while (c != Object.class && c != null) {
            for (final Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(annnotationClass)) {
                    x.add(field);
                }
            }
            c = c.getSuperclass();
        }
        return x.toArray(new Field[x.size()]);
    }
}
