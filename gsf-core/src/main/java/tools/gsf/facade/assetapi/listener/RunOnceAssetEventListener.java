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
package tools.gsf.facade.assetapi.listener;

import COM.FutureTense.Interfaces.ICS;
import com.fatwire.assetapi.data.AssetId;
import com.openmarket.basic.event.AbstractAssetEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.gsf.facade.install.AssetListenerInstall;

import java.util.HashSet;
import java.util.Set;

//import oracle.core.ojdl.logging.ODLLogger;

/**
 * AssetEventListener that protects from multiple event fires for the same
 * asset. It does so by registering the assets on a list on the ICS scope.
 *
 * @author Dolf Dijkstra
 */

public abstract class RunOnceAssetEventListener extends AbstractAssetEventListener {

    protected final Logger LOG = LoggerFactory.getLogger(getClass().getName().replaceFirst("fatwire.gst", "function1.gsf"));
    private ICS ics;

    private static class RunOnceList {
        private final Set<String> assets = new HashSet<String>();

        boolean seenBefore(final AssetId id) {
            return !assets.add(id.toString());
        }

        static RunOnceList find(final ICS ics, final Class<?> z) {
            final String name = z.getName() + "-RunOnceList";
            Object o = ics.GetObj(name);
            if (o instanceof RunOnceList) {
                return (RunOnceList) o;
            } else {
                o = new RunOnceList();
                ics.SetObj(name, o);
                return (RunOnceList) o;
            }
        }

    }

    @Override
    public final void assetAdded(final AssetId id) {
        LOG.debug("************ Asset added event received for " + id);
        if (!seen(id)) {
            doAssetAdded(id);
        }
    }

    private boolean seen(final AssetId id) {
        final boolean s = RunOnceList.find(getICS(), getClass()).seenBefore(id);
        LOG.debug("************ An event for asset " + id + " was " + (s ? "" : " not ")
                + " executed before.");
        return s;
    }

    protected abstract void doAssetAdded(AssetId id);

    @Override
    public final void assetDeleted(final AssetId id) {
        LOG.debug("************ Asset deleted event received for " + id);
        if (!seen(id)) {
            doAssetDeleted(id);
        }
    }

    protected abstract void doAssetDeleted(AssetId id);

    @Override
    public final void assetUpdated(final AssetId id) {
        LOG.debug("************ Asset updated event received for " + id);
        if (!seen(id)) {
            doAssetUpdated(id);
        }
    }

    protected abstract void doAssetUpdated(AssetId id);

    /**
     * Install self into AssetListener_reg table
     *
     * @param ics Content Server context object
     */
    public final void install(final ICS ics) {
        AssetListenerInstall.register(ics, getClass().getName(), true);
    }

    protected ICS getICS() {
        return ics;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.openmarket.basic.event.AbstractAssetEventListener#init(COM.FutureTense
     * .Interfaces.ICS)
     */
    @Override
    public void init(ICS ics) {
        this.ics = ics;

    }

    public final boolean isInstalled(final ICS ics) {
        return AssetListenerInstall.isRegistered(ics, getClass().getName());
    }
}
