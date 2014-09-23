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

package com.fatwire.gst.foundation.facade.assetapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.query.Condition;
import com.fatwire.assetapi.query.ConditionFactory;
import com.fatwire.assetapi.query.OpTypeEnum;
import com.fatwire.assetapi.query.Query;
import com.fatwire.assetapi.query.QueryProperties;
import com.fatwire.assetapi.query.SimpleQuery;
import com.fatwire.assetapi.query.SortOrder;

/**
 * Builder for a SimpleQuery object.
 * 
 * @author Dolf Dijkstra
 * @see SimpleQuery
 */

public class QueryBuilder {

    private static final String INITIALIZE_FIRST_MSG = "Condition not initialized.  Please set the condition first with QueryBuilder.condition(...).";
            
    private SimpleQuery query;

    private QueryProperties props() {
        return query.getProperties();
    }

    public QueryBuilder(String assetType, String subType) {
        query = new SimpleQuery(assetType, subType);
    }

    public QueryBuilder(String assetType) {
        query = new SimpleQuery(assetType, null);
    }

    public Query toQuery() {
        return query;
    }

    /**
     * @param attributeName
     * @see com.fatwire.assetapi.query.SimpleQuery#setAttributes(java.util.List)
     */
    public QueryBuilder attribute(String attributeName) {
        List<String> old = new ArrayList<String>();
        old.addAll(query.getAttributeNames());
        old.add(attributeName);
        query.setAttributes(old);
        return this;
    }

    /**
     * @param attributeNames
     * @see com.fatwire.assetapi.query.SimpleQuery#setAttributes(java.util.List)
     */
    public QueryBuilder attributes(String... attributeNames) {
        List<String> old = new ArrayList<String>();
        old.addAll(query.getAttributeNames());
        old.addAll(Arrays.asList(attributeNames));
        query.setAttributes(old);
        return this;

    }

    /**
     * @param sort
     * @see com.fatwire.assetapi.query.SimpleQuery#setSortOrder(java.util.List)
     */
    public QueryBuilder setSortOrder(List<SortOrder> sort) {
        query.setSortOrder(sort);
        return this;
    }

    /**
     * @param fConfidence
     * @see com.fatwire.assetapi.query.QueryProperties#setConfidence(float)
     */
    public QueryBuilder setConfidence(float fConfidence) {
        props().setConfidence(fConfidence);
        return this;
    }

    /**
     * @param fixedList
     * @see com.fatwire.assetapi.query.QueryProperties#setFixedList(boolean)
     */
    public QueryBuilder setFixedList(boolean fixedList) {
        props().setFixedList(fixedList);
        return this;
    }

    /**
     * @param basicSearch
     * @see com.fatwire.assetapi.query.QueryProperties#setIsBasicSearch(boolean)
     */
    public QueryBuilder setBasicSearch(boolean basicSearch) {
        props().setIsBasicSearch(basicSearch);
        return this;
    }

    /**
     * @param caseSensitive
     * @see com.fatwire.assetapi.query.QueryProperties#setIsCaseSensitive(boolean)
     */
    public QueryBuilder setCaseSensitive(boolean caseSensitive) {
        props().setIsCaseSensitive(caseSensitive);
        return this;
    }

    /**
     * @param immediateOnly
     * @see com.fatwire.assetapi.query.QueryProperties#setIsImmediateOnly(boolean)
     */
    public QueryBuilder setImmediateOnly(boolean immediateOnly) {
        props().setIsImmediateOnly(immediateOnly);
        return this;
    }

    /**
     * @param lowerEqual
     * @see com.fatwire.assetapi.query.QueryProperties#setIsLowerEqual(boolean)
     */
    public QueryBuilder setLowerEqual(boolean lowerEqual) {
        props().setIsLowerEqual(lowerEqual);
        return this;
    }

    /**
     * @param upperEqual
     * @see com.fatwire.assetapi.query.QueryProperties#setIsUpperEqual(boolean)
     */
    public QueryBuilder setUpperEqual(boolean upperEqual) {
        props().setIsUpperEqual(upperEqual);
        return this;
    }

    /**
     * @param loadDependency
     * @see com.fatwire.assetapi.query.QueryProperties#setLoadDependency(int)
     */
    public QueryBuilder setLoadDependency(int loadDependency) {
        props().setLoadDependency(loadDependency);
        return this;
    }

    /**
     * @param maxAnswers
     * @see com.fatwire.assetapi.query.QueryProperties#setMaxAnswers(int)
     */
    public QueryBuilder setMaxAnswers(int maxAnswers) {
        props().setMaxAnswers(maxAnswers);
        return this;
    }

    /**
     * @param maxRows
     * @see com.fatwire.assetapi.query.QueryProperties#setMaxRows(int)
     */
    public QueryBuilder setMaxRows(int maxRows) {
        props().setMaxRows(maxRows);
        return this;
    }

    /**
     * @param parser
     * @see com.fatwire.assetapi.query.QueryProperties#setParser(java.lang.String)
     */
    public QueryBuilder setParser(String parser) {
        props().setParser(parser);
        return this;
    }

    /**
     * @param readAll
     * @see com.fatwire.assetapi.query.QueryProperties#setReadAll(boolean)
     */
    public QueryBuilder setReadAll(boolean readAll) {
        props().setReadAll(readAll);
        return this;
    }

    /**
     * @param site
     * @see com.fatwire.assetapi.query.QueryProperties#setSite(java.lang.Long)
     */
    public QueryBuilder setSite(long site) {
        props().setSite(site);
        return this;
    }

    /**
     * @param startIndex
     * @see com.fatwire.assetapi.query.QueryProperties#setStartIndex(int)
     */
    public QueryBuilder setStartIndex(int startIndex) {
        props().setStartIndex(startIndex);
        return this;
    }

    public QueryBuilder condition(String condition) {

        Condition cond = new ConditionParser().parse(condition);
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder condition(String attName, OpTypeEnum opType, String value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder condition(String attName, OpTypeEnum opType, Date value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder condition(String attName, OpTypeEnum opType, Integer value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder condition(String attName, OpTypeEnum opType, Float value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder condition(String attName, OpTypeEnum opType, Long value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder condition(String attName, OpTypeEnum opType, List<String> value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder condition(String attName, OpTypeEnum opType, List<String> value, boolean containsAll) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder condition(String attName, OpTypeEnum opType, Date value, boolean caseSensiive,
            boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder condition(String attName, OpTypeEnum opType, Integer value, boolean caseSensiive,
            boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder condition(String attName, OpTypeEnum opType, Float value, boolean caseSensiive,
            boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder condition(String attName, OpTypeEnum opType, Long value, boolean caseSensiive,
            boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder condition(String attName, OpTypeEnum opType, List<String> value, boolean caseSensiive,
            boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder between(String attName, Object lower, Object upper) {
        Condition cond;
        try {
            cond = new ConditionFactory().createBetweenCondition(attName, lower, upper);
        } catch (AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder between(String attName, Object lower, Object upper, boolean lowerEqual, boolean upperEqual) {
        Condition cond;
        try {
            cond = new ConditionFactory().createBetweenCondition(attName, lower, upper, lowerEqual, upperEqual);
        } catch (AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }
        query.setCondition(cond);
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, String value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.or(cond));
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Date value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.or(cond));
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Integer value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.or(cond));
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Float value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.or(cond));
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Long value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.or(cond));
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, List<String> value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.or(cond));
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, List<String> value, boolean containsAll) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.or(cond));
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Date value, boolean caseSensiive, boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.or(cond));
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Integer value, boolean caseSensiive, boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.or(cond));
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Float value, boolean caseSensiive, boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.or(cond));
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Long value, boolean caseSensiive, boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.or(cond));
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, List<String> value, boolean caseSensiive,
            boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.or(cond));
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, String value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.and(cond));
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Date value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.and(cond));
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Integer value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.and(cond));
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Float value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.and(cond));
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Long value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.and(cond));
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, List<String> value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.and(cond));
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, List<String> value, boolean containsAll) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.and(cond));
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Date value, boolean caseSensiive, boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.and(cond));
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Integer value, boolean caseSensiive,
            boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.and(cond));
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Float value, boolean caseSensiive, boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.and(cond));
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Long value, boolean caseSensiive, boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.and(cond));
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, List<String> value, boolean caseSensiive,
            boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        Condition qc = query.getCondition();
        if (qc == null) throw new RuntimeAssetAccessException(INITIALIZE_FIRST_MSG);
        query.setCondition(qc.and(cond));
        return this;
    }
}
