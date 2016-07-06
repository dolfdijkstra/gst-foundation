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
package com.fatwire.gst.foundation.navigation;

import java.util.List;

/**
 * @author Tony Field
 * @since 2016-07-02.
 */
public interface Node<NODE extends Node, ID> {
    ID getId();
    NODE getParent();
    List<NODE> getSiblings();
    List<NODE> getChildren();
    List<NODE> getBreadcrumb();
}
