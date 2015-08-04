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

import COM.FutureTense.Interfaces.IList;
import com.fatwire.assetapi.data.AssetId;

import java.util.List;

/**
 * Facade over the WCS Engage personalization infrastructure, particularly the
 * Commerce Context (COMMERCECONTEXT).
 *
 * Not all commercecontext functions are exposed. The following APIs are NOT yet
 * available and may be added in the future:
 * <pre>
 *     getabandonedcartsessions
 *     getsessioncart
 *     saveall
 *     inform
 * </pre>
 * @author Tony Field
 * @since 15-08-04 5:36 PM
 */
public interface CommerceContextService {

    /**
     * Set the rating used to determine whether or not a user should be inluded in a segment or not.
     * @param rating rating to be used as a cutoff. Must be between 1 and 100. The default value is 50.
     */
    void setSegmentCutoff(int rating);

    /**
     * Re-calculate segments
     */
    void calculateSegments();

    /**
     * Get the segments that apply to the current user
     * Automatically calls {@link #calculateSegments()}.
     * @return the IDs of the segments that apply to the user
     */
    List<AssetId> getSegments();

    /**
     * Return true if the visitor is in the segment specified
     * @param id segment asset id
     * @return true if the user is in the segment; false otherwise
     */
    boolean isUserInSegment(AssetId id);

    /**
     * Set the rating to be used to determine whether a promotion shall be in effect.
     * @param rating rating to be used as a cutoff. Must be between 1 and 100. The default value is 50.
     */
    void setPromotionCutoff(int rating);

    /**
     * Calculate promotions in effect for the user.
     * Automatically calls {@link #calculateSegments()}.
     */
    void calculatePromotions();

    /**
     * Get a list of the promotions in effect for the user.
     * Automatically calls {@link #calculatePromotions()}.
     * @return IList containing promotion information. Structure of the list is ....
     */
    IList getPromotions();


    /**
     * Discounts the items in the cart in accordance with
     * any promotions in effect.
     * Automatically calls {@link #calculatePromotions()} if
     * they have not already been calculated.
     */
    void discountCart();


    /**
     * Discount items in the specified temporary cart against any items
     * currently being promoted. This is typically used when trying to get the
     * discount amount applied to a particular item.
     * Automatically calls {@link #calculatePromotions()} if
     * they have not already been calculated.
     * @param cartName temp cart name
     */
    void discountTempCart(String cartName);

    /**
     * Extract a COPY of the current cart and make it available in the request context. This cart
     * is not attached to the visitor context. It's possible to alter this cart though, and then
     * set it back into the visitor context using {@link #setCurrentCart(String)}
     * @param cartName name to be used for the working copy of the cart
     */
    void getCurrentCart(String cartName);

    /**
     * Set the working copy of the cart back into the visitor context.
     * @param cartName name of the working copy of the cart
     */
    void setCurrentCart(String cartName);

    /**
     * Access the ratings associated with the assets specified. This method is used primarily for debug purposes.
     * @param assets list of asset ids to have their ratings checked
     * @param defaultRating rating to use for assets that don't have one.
     *                      The default is 50. Must be a number between 1 and 100.
     * @return IList containing 3 columns - assettype, assetid, rating,
     * all sorted according to the ratings of the assets.
     */
    IList getRatings(List<AssetId> assets, int defaultRating);

    /**
     * Return a single recommended asset from the recommendation specified.
     * This is a convenience version of the {@link #getRecommendations(AssetId,List,boolean,int,String,int,String,IList)} method.
     * @param recId recommendation asset id to be used. If a promotion is in
     *              effect, the promotion id is used instead.
     * @return recommended asset, or null if none found.
     */
    AssetId getRecommendation(AssetId recId);
    /**
     * Return a single recommended asset from the recommendation specified.
     * This is a convenience version of the {@link #getRecommendations(String,List,boolean,int,String,int,String,IList)} method.
     * @param recName recommendation asset name. If a promotion is in effect,
     *                the promotion name is used instead.
     * @return recommended asset, or null if none found.
     */
    AssetId getRecommendation(String recName);

    /**
     * Get recommended assets for the specified recommendation.
     * This is a convenience version of the {@link #getRecommendations(AssetId,List,boolean,int,String,int,String,IList)} method.
     *
     * @param recId recommendation asset id to be used, or if a promotion is in effect, the promotion name.
     * @param assetsForContext input asset list for context-specific (related-item) recommendations
     * @param doFilter true to exclude the assets for context-based (related-item) recommendations
     * @param maxCount maximum number of assets to return
     * @return recommended assets.
     */
    List<AssetId> getRecommendations(AssetId recId,
                                     List<AssetId> assetsForContext,
                                     boolean doFilter,
                                     int maxCount);

    /**
     * Get recommended assets for the specified recommendation.
     * @param recId recommendation asset id to be used, or if a promotion is in effect, the promotion name.
     * @param assetsForContext input asset list for context-specific (related-item) recommendations
     * @param doFilter true to exclude the assets for context-based (related-item) recommendations
     * @param maxCount maximum number of assets to return
     * @param doFilter true to exclude the assets for context-based recommendations
     *                 (related item recommendations)
     * @param depType dependency type to be used for this recommendation. Default value
     *                is "unknown". If no promotions that are allowed to override
     *                recommendations are in effect, then "exact" and "exist" values are
     *                allowed, but if promotions are allowed to override the recommendation,
     *                then this value is ignored and the value of "unknown" is used.
     * @param engine the name of the engine used to optimize the recommendation.
     *               Used in conjunction with the engineParams param - typically rtd if overridden
     *               at all. May be null.
     * @param engineParams a list of params that are passed to the engine. May be null if engine is null.
     * @return recommended assets.
     */
    List<AssetId> getRecommendations(AssetId recId,
                                     List<AssetId> assetsForContext,
                                     boolean doFilter,
                                     int maxCount,
                                     String depType,
                                     int defaultRating,
                                     String engine,
                                     IList engineParams);


    /**
     * Get recommended assets for the specified recommendation.
     * @param recName the name of the recommendation to use, or if a promotion is in effect that
     *                overrides this recommendation, the promotion name.
     * @param assetsForContext input asset list for context-specific (related-item) recommendations
     * @param doFilter true to exclude the assets for context-based (related-item) recommendations
     * @param maxCount maximum number of assets to return
     * @param doFilter true to exclude the assets for context-based recommendations
     *                 (related item recommendations)
     * @param depType dependency type to be used for this recommendation. Default value
     *                is "unknown". If no promotions that are allowed to override
     *                recommendations are in effect, then "exact" and "exist" values are
     *                allowed, but if promotions are allowed to override the recommendation,
     *                then this value is ignored and the value of "unknown" is used.
     * @param engine the name of the engine used to optimize the recommendation.
     *               Used in conjunction with the engineParams param - typically rtd if overridden
     *               at all.
     * @param engineParams a list of params that are passed to the engine.
     * @return recommended assets.
     */
    List<AssetId> getRecommendations(String recName,
                                     List<AssetId> assetsForContext,
                                     boolean doFilter,
                                     int maxCount,
                                     String depType,
                                     int defaultRating,
                                     String engine,
                                     IList engineParams);

    /**
     * Logs the user out of the visitor context, dissociating the user's id from the visitor context.
     */
    void logout();

}
