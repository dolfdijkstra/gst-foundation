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
package tools.gsf.navigation;

import java.util.List;

/**
 * Navigation structure node. Knows how to return parent nodes, child nodes, sibling nodes, and the id of the object
 * represented by this node.
 *
 * Designed to be extended to support more sophisticated node types.
 *
 * @author Tony Field
 * @since 2016-07-02.
 */
public interface Node<NODE extends Node, ID> {

    /**
     * Get the id of the object represented by this node.
     * @return object id
     */
    ID getId();

    /**
     * Get the parent node
     * @return parent node
     */
    NODE getParent();

    /**
     * Get the siblings of this node. All siblings are returned in ranked order, including this node.
     * @return sibling nodes
     */
    List<NODE> getSiblings();

    /**
     * Return this node's children, if any, in ranked order
     * @return this node's children, never null.
     */
    List<NODE> getChildren();

}
