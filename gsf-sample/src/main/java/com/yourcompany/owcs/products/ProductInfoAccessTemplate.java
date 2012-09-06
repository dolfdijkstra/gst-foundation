/*
 * Copyright 2012 Oracle Corporation. All Rights Reserved.
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
package com.yourcompany.owcs.products;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.gst.foundation.facade.assetapi.AssetMapper;
import com.fatwire.gst.foundation.facade.assetapi.asset.MappedAssetAccessTemplate;

/**
 * A sample AssetAccessTemplate to create a ProductInfo bean.
 * <p/>
 * The full creation of the bean is not demonstrated here.
 * 
 * @author Dolf Dijkstra
 * @since 6 sep. 2012
 * 
 */
public class ProductInfoAccessTemplate extends MappedAssetAccessTemplate<ProductInfo> {

    private static final AssetMapper<ProductInfo> mapper_ = new AssetMapper<ProductInfo>() {

        @Override
        public ProductInfo map(AssetData assetData) {
            // do the hard work to create and populate the ProductInfo bean
            // here...

            return new ProductInfo();
        }
    };

    public ProductInfoAccessTemplate(final ICS ics) {
        super(ics, mapper_);

    }
}
