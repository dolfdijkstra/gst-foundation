package com.fatwire.gst.foundation.facade.assetapi;

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
 * @author Dolf Dijkstra
 * 
 */

public class QueryBuilder {

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
        query.getAttributeNames().add(attributeName);
        return this;
    }

    /**
     * @param attributeNames
     * @see com.fatwire.assetapi.query.SimpleQuery#setAttributes(java.util.List)
     */
    public QueryBuilder attributes(String... attributeNames) {
        query.getAttributeNames().addAll(Arrays.asList(attributeNames));
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
     * @param sParser
     * @see com.fatwire.assetapi.query.QueryProperties#setParser(java.lang.String)
     */
    public QueryBuilder setParser(String sParser) {
        props().setParser(sParser);
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
        query.getCondition().or(cond);
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Date value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.getCondition().or(cond);
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Integer value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.getCondition().or(cond);
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Float value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.getCondition().or(cond);
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Long value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.getCondition().or(cond);
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, List<String> value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.getCondition().or(cond);
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, List<String> value, boolean containsAll) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.getCondition().or(cond);
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Date value, boolean caseSensiive, boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.getCondition().or(cond);
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Integer value, boolean caseSensiive, boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.getCondition().or(cond);
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Float value, boolean caseSensiive, boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.getCondition().or(cond);
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, Long value, boolean caseSensiive, boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.getCondition().or(cond);
        return this;
    }

    public QueryBuilder or(String attName, OpTypeEnum opType, List<String> value, boolean caseSensiive,
            boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.getCondition().or(cond);
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, String value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.getCondition().and(cond);
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Date value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.getCondition().and(cond);
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Integer value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.getCondition().and(cond);
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Float value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.getCondition().and(cond);
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Long value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.getCondition().and(cond);
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, List<String> value) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.getCondition().and(cond);
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, List<String> value, boolean containsAll) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value);
        query.getCondition().and(cond);
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Date value, boolean caseSensiive, boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.getCondition().and(cond);
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Integer value, boolean caseSensiive,
            boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.getCondition().and(cond);
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Float value, boolean caseSensiive, boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.getCondition().and(cond);
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, Long value, boolean caseSensiive, boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.getCondition().and(cond);
        return this;
    }

    public QueryBuilder and(String attName, OpTypeEnum opType, List<String> value, boolean caseSensiive,
            boolean immediateOnly) {
        Condition cond = ConditionFactory.createCondition(attName, opType, value, caseSensiive, immediateOnly);
        query.getCondition().and(cond);

        return this;

    }
}
