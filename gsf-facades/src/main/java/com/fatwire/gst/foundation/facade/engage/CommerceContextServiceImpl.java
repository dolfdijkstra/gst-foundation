/*
 * Copyright (c) 2015 Function1 Inc. All rights reserved.
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
package com.fatwire.gst.foundation.facade.engage;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import com.fatwire.assetapi.data.AssetId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * @author Tony Field
 * @since 15-08-04 6:06 PM
 */
public class CommerceContextServiceImpl implements CommerceContextService {

    private static final Log LOG = LogFactory.getLog(CommerceContextService.class);
    private final ICS ics;

    public CommerceContextServiceImpl(ICS ics) {
        this.ics = ics;
        LOG.trace("Created CommerceContextServiceImpl");
        LOG.warn("Implementation is not complete");
    }

    @Override
    public void setSegmentCutoff(int rating) {
        // todo: implement
    }

    @Override
    public void calculateSegments() {
        // todo: implement
    }

    @Override
    public List<AssetId> getSegments() {
        // todo: implement
        return null;
    }

    @Override
    public boolean isUserInSegment(AssetId id) {
        // todo: implement
        return false;
    }

    @Override
    public void setPromotionCutoff(int rating) {
        // todo: implement
    }

    @Override
    public void calculatePromotions() {
        // todo: implement
    }

    @Override
    public IList getPromotions() {
        // todo: implement
        return null;
    }

    @Override
    public void discountCart() {
        // todo: implement
    }

    @Override
    public void discountTempCart(String cartName) {
        // todo: implement
    }

    @Override
    public void getCurrentCart(String cartName) {
        // todo: implement
    }

    @Override
    public void setCurrentCart(String cartName) {
        // todo: implement
    }

    @Override
    public IList getRatings(List<AssetId> assets, int defaultRating) {
        // todo: implement
        return null;
    }

    @Override
    public AssetId getRecommendation(AssetId recId) {
        // todo: implement
        return null;
    }

    @Override
    public AssetId getRecommendation(String recName) {
        // todo: implement
        return null;
    }

    @Override
    public List<AssetId> getRecommendations(AssetId recId, List<AssetId> assetsForContext, boolean doFilter, int maxCount) {
        // todo: implement
        return null;
    }

    @Override
    public List<AssetId> getRecommendations(AssetId recId, List<AssetId> assetsForContext, boolean doFilter, int maxCount, String depType, int defaultRating, String engine, IList engineParams) {
        // todo: implement
        return null;
    }

    @Override
    public List<AssetId> getRecommendations(String recName, List<AssetId> assetsForContext, boolean doFilter, int maxCount, String depType, int defaultRating, String engine, IList engineParams) {
        // todo: implement
        return null;
    }

    @Override
    public void logout() {
        // todo: implement
    }
}
