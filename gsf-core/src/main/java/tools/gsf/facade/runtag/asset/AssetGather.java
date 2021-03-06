/*
 * Copyright 2008 Metastratus Web Solutions Limited. All Rights Reserved.
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
package tools.gsf.facade.runtag.asset;

import tools.gsf.facade.runtag.render.TagRunnerWithRenderArguments;

/**
 * @author David Chesebro
 * @since 3/16/12
 */
public class AssetGather extends TagRunnerWithRenderArguments {
    public AssetGather(final String name, final String prefix, final String fieldlist) {
        super("ASSET.GATHER");
        set("NAME", name);
        set("PREFIX", prefix);
        set("FIELDLIST", fieldlist);
    }

    public AssetGather(final String name, final String prefix, final boolean exclude) {
        super("ASSET.GATHER");
        set("NAME", name);
        set("PREFIX", prefix);
        set("EXCLUDE", exclude);
    }
}
