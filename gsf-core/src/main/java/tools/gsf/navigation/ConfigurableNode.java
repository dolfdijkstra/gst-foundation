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

/**
 * @author Freddy Villalba
 * 
 *
 */
public interface ConfigurableNode<NODE extends Node<NODE>> {
	
    
    /**
     * Add a child at the end of this node's list of children.
     * 
     * If children must be ordered in any particular way, it's the caller's responsibility to add them in the right order.
     * 
     * This method must be invoked BEFORE setParent. Calling it AFTER setParent will throw a runtime exception.
     *  
     * @param node
     */
    void addChild(NODE node);
    
    /**
     * Can only be called once per instance. Otherwise, a runtime exception will be thrown.     
     * @param node
     */
    void setParent(NODE node);
    
    /**
     * Breaks the relationship between this node and all of its children, meaning:
     * 
     * - This node is not the parent of any of its children anymore.
     * - The removed nodes are not this node's children anymore.
     * 
     */
    void removeChildren();
    
    /**
     * Breaks the relationship between this node and the specified child, meaning:
     * 
     * - This node is not the parent of the specified child anymore.
     * - The removed node is not this node's child anymore.
     * 
     * The method returns true if the specified child was removed, false otherwise. 
     * 
     * @param child The child node we want to remove
     * @return true if the node was removed, false otherwise
     */
    boolean removeChild(NODE child);
	
}