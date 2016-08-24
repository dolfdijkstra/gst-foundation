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
package com.fatwire.gst.foundation.facade.runtag.workflowaction;

import com.fatwire.gst.foundation.facade.runtag.render.TagRunnerWithRenderArguments;

/**
 * @author David Chesebro
 * @since 3/13/12
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */
public class GetId extends TagRunnerWithRenderArguments
{
    public GetId()
    {
        super("WORKFLOWACTION.GETID");
    }

    public void setName(String name)
    {
        this.set("NAME", name);
    }
    
    public void setVarName(String varName)
    {
        this.set("VARNAME", varName);
    }
}
