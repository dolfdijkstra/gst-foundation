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
package tools.gsf.config;

import com.fatwire.assetapi.data.AssetDataManager;
import com.fatwire.assetapi.site.SiteManager;
import com.fatwire.system.Session;
import com.fatwire.system.SessionFactory;

import COM.FutureTense.Interfaces.ICS;

import tools.gsf.config.inject.AnnotationInjector;
import tools.gsf.config.inject.BindInjector;
import tools.gsf.config.inject.CurrentAssetInjector;
import tools.gsf.config.inject.InjectForRequestInjector;
import tools.gsf.config.inject.Injector;
import tools.gsf.config.inject.MappingInjector;
import tools.gsf.facade.assetapi.AssetAccessTemplate;
import tools.gsf.facade.assetapi.asset.ScatteredAssetAccessTemplate;
import tools.gsf.facade.assetapi.asset.TemplateAsset;
import tools.gsf.facade.assetapi.asset.TemplateAssetAccess;
import tools.gsf.mapping.IcsMappingService;
import tools.gsf.mapping.MappingService;
import tools.gsf.time.LoggerStopwatch;
import tools.gsf.time.Stopwatch;
import tools.gsf.properties.AssetApiPropertyDao;
import tools.gsf.properties.PropertyDao;
import tools.gsf.facade.mda.DefaultLocaleService;
import tools.gsf.facade.mda.LocaleService;

/**
 * @author Tony Field
 * @since 2016-08-05
 */
public class IcsBackedFactory extends AbstractDelegatingFactory<ICS> {

    private final ICS ics;

    @ServiceProducer(cache = true, name="ics")
    public ICS getICS() {
    	return this.ics;
    }

    public IcsBackedFactory(ICS ics, Factory delegate) {
        super(ics, delegate);
        this.ics = ics;
    }

    @ServiceProducer
    public Stopwatch newStopwatch() {
        return LoggerStopwatch.getInstance();
    }

    @ServiceProducer(cache = true, name="bindInjector")
    public Injector createBindInjector() {
        return new BindInjector(ics);
    }

    @ServiceProducer(cache = true, name="injectForRequestInjector")
    public Injector createInjectForRequestInjector() {
        Factory factory = FactoryLocator.locateFactory(ics);
        return new InjectForRequestInjector(factory);
    }

    @ServiceProducer(cache = true)
    public MappingService createMappingService() {
        AssetAccessTemplate aat = getObject("assetAccessTemplate", AssetAccessTemplate.class);
        return new IcsMappingService(ics, aat);
    }

    @ServiceProducer(cache = true, name="mappingInjector")
    public Injector createMappingInjector(final ICS ics) {
        MappingService mappingService = getObject("mappingService", MappingService.class);
        return new MappingInjector(ics, mappingService);
    }

    @ServiceProducer(cache = true, name="currentAssetInjector")
    public Injector createCurrentAssetInjector(final ICS ics) {
        AssetAccessTemplate aat = getObject("assetAccessTemplate", AssetAccessTemplate.class);
        TemplateAssetAccess taa = getObject("templateAssetAccess", TemplateAssetAccess.class);
        ScatteredAssetAccessTemplate saa = getObject("scatteredAssetAccessTemplate", ScatteredAssetAccessTemplate.class);
        return new CurrentAssetInjector(ics, taa, saa, aat);
    }

    @ServiceProducer(cache = true, name="compositeInjector")
    public Injector createCompositeInjector() {
        Injector bind = getObject("bindInjector", Injector.class);
        Injector map = getObject("mappingInjector", Injector.class);
        Injector ifr = getObject("injectForRequestInjector", Injector.class);
        Injector currentAsset = getObject("currentAssetInjector", Injector.class);
        Stopwatch stopwatch = getObject("stopwatch", Stopwatch.class);
        return new AnnotationInjector(stopwatch, bind, map, ifr, currentAsset);
    }

    @ServiceProducer(cache = true)
    public PropertyDao createPropertyDao(final ICS ics) {
    	Session session = SessionFactory.getSession(ics);
    	AssetDataManager adm = (AssetDataManager) session.getManager(AssetDataManager.class.getName());
    	SiteManager sm = (SiteManager) session.getManager(SiteManager.class.getName());
    	String type = "GSTProperty";
    	String flexDefName = "GSTProperty";
    	String propNameAttr = "name";
    	String propDescAttr = "description";
    	String propValueAttr = "value";
    	return new AssetApiPropertyDao(adm, sm, type, flexDefName, propNameAttr, propDescAttr, propValueAttr, ics);
    }

    @ServiceProducer(cache = true)
    public AssetAccessTemplate createAssetAccessTemplate() {
        return new AssetAccessTemplate(this.ics);
    }
    
    @ServiceProducer(cache = true)
    public ScatteredAssetAccessTemplate createScatteredAssetAccessTemplate() {
        return new ScatteredAssetAccessTemplate(this.ics);
    }

    @ServiceProducer(cache = true)
    public TemplateAssetAccess createTemplateAssetAccess() {
        return new TemplateAssetAccess(this.ics);
    }
    
    @ServiceProducer(cache = true)
    public LocaleService createLocaleService(final ICS ics) {
        return new DefaultLocaleService(ics);
    }
    
}