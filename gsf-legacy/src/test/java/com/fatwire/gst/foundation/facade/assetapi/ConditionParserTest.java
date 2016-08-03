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

import com.fatwire.assetapi.query.Condition;
import com.fatwire.assetapi.query.ConditionExpression;
import com.fatwire.assetapi.query.OpTypeEnum;
import com.fatwire.assetapi.query.QueryProperties;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.List;

public class ConditionParserTest extends TestCase {

    public void testParseNumber() {
        ConditionParser parser = new ConditionParser();
        Condition c = parser.parse("name = 123");
        Assert.assertEquals("name", c.getExpression().getAttributeName());
        Assert.assertEquals(OpTypeEnum.EQUALS, c.getExpression().getOpType());
        List<?> vals = c.getExpression().getValues();
        Assert.assertEquals(1, vals.size());
        Object o = vals.get(0);

        Assert.assertEquals("123", o);
    }

    public void testParseLessThan() {
        ConditionParser parser = new ConditionParser();
        Condition c = parser.parse("name < 123");
        Assert.assertEquals("name", c.getExpression().getAttributeName());
        Assert.assertEquals(OpTypeEnum.LESS_THAN, c.getExpression().getOpType());
        List<?> vals = c.getExpression().getValues();
        Assert.assertEquals(1, vals.size());
        Object o = vals.get(0);

        Assert.assertEquals("123", o);
    }

    public void testParseBetween() {
        ConditionParser parser = new ConditionParser();
        Condition c = parser.parse("size{123,130}");
        Assert.assertEquals("size", c.getExpression().getAttributeName());
        Assert.assertEquals(OpTypeEnum.BETWEEN, c.getExpression().getOpType());
        List<?> vals = c.getExpression().getValues();
        Assert.assertEquals(2, vals.size());

        Assert.assertEquals("123", vals.get(0));
        Assert.assertEquals("130", vals.get(1));

    }

    public void testParseSimpleOr() {
        ConditionParser parser = new ConditionParser();
        Condition c = parser.parse("size=[123,130]");
        assertNotNull(c);
        List<?> vals = c.getExpression().getValues();
        assertEquals(2, vals.size());
        assertEquals("123", vals.get(0));
        assertEquals("130", vals.get(1));

    }

    public void testValueSplit() {
        ConditionParser parser = new ConditionParser();
        String[] c = parser.valueSplit("john,'mary','mary poppins','barker,john'");
        assertEquals(4, c.length);
        assertEquals("john", c[0]);
        assertEquals("mary", c[1]);
        assertEquals("mary poppins", c[2]);
        assertEquals("barker,john", c[3]);

    }

    public void testParseSimpleOr_with_quotes() {
        ConditionParser parser = new ConditionParser();
        Condition c = parser.parse("name=[john,'mary','mary poppins','barker,john']");
        assertNotNull(c);
        List<?> vals = c.getExpression().getValues();
        Assert.assertEquals(4, vals.size());

        Assert.assertEquals("john", vals.get(0));
        Assert.assertEquals("mary", vals.get(1));
        Assert.assertEquals("mary poppins", vals.get(2));
        assertEquals("barker,john", vals.get(3));

    }

    void print(Condition c) {
        if (c == null) {
            return;
        }
        System.out.println("JoinType: " + c.getJoinType());
        ConditionExpression e = c.getExpression();
        if (e != null) {
            QueryProperties p = e.getProperties();

            System.out.println("ConditionExpression: " + e.getAttributeName() + " " + e.getOpType() + " "
                    + e.getValues() + ": " + (p != null ? p.getIsBasicSearch() : ""));
        }
        if (c.getLeftCondition() != null) {
            System.out.println("left:");
            print(c.getLeftCondition());
        }
        if (c.getRightCondition() != null) {
            System.out.println("right:");
            print(c.getRightCondition());
        }
    }

    void checkValue(Condition c, String expectedName, OpTypeEnum op, String expected) {
        assertNotNull("expression is null", c.getExpression());
        Assert.assertEquals(expectedName, c.getExpression().getAttributeName());
        Assert.assertEquals(op, c.getExpression().getOpType());
        List<?> vals = c.getExpression().getValues();
        Assert.assertEquals(1, vals.size());
        Assert.assertEquals(expected, vals.get(0));
    }

    public void testParseLike() {
        ConditionParser parser = new ConditionParser();
        Condition c = parser.parse("name~foo");
        Assert.assertEquals("name", c.getExpression().getAttributeName());
        Assert.assertEquals(OpTypeEnum.LIKE, c.getExpression().getOpType());
        List<?> vals = c.getExpression().getValues();
        Assert.assertEquals(1, vals.size());

        Assert.assertEquals("foo", vals.get(0));

    }

    public void testParseFloat() {
        ConditionParser parser = new ConditionParser();
        Condition c = parser.parse("name = 123.0");
        Assert.assertEquals("name", c.getExpression().getAttributeName());
        Assert.assertEquals(OpTypeEnum.EQUALS, c.getExpression().getOpType());
        List<?> vals = c.getExpression().getValues();
        Assert.assertEquals(1, vals.size());
        Object o = vals.get(0);

        Assert.assertEquals(new Float(123.0).toString(), o);
    }

    public void testParseNoSpace() {
        ConditionParser parser = new ConditionParser();
        Condition c = parser.parse("name=123.0");
        Assert.assertEquals("name", c.getExpression().getAttributeName());
        Assert.assertEquals(OpTypeEnum.EQUALS, c.getExpression().getOpType());
        List<?> vals = c.getExpression().getValues();
        Assert.assertEquals(1, vals.size());
        Object o = vals.get(0);

        Assert.assertEquals("123.0", o);
    }

    public void testParseDoubleSpace() {
        ConditionParser parser = new ConditionParser();
        Condition c = parser.parse("name=  123.0");
        Assert.assertEquals("name", c.getExpression().getAttributeName());
        Assert.assertEquals(OpTypeEnum.EQUALS, c.getExpression().getOpType());
        List<?> vals = c.getExpression().getValues();
        Assert.assertEquals(1, vals.size());
        Object o = vals.get(0);

        Assert.assertEquals("123.0", o);
    }

    public void testParseQuote() {
        ConditionParser parser = new ConditionParser();
        Condition c = parser.parse("name='foo'");
        Assert.assertEquals("name", c.getExpression().getAttributeName());
        Assert.assertEquals(OpTypeEnum.EQUALS, c.getExpression().getOpType());
        List<?> vals = c.getExpression().getValues();
        Assert.assertEquals(1, vals.size());
        Object o = vals.get(0);

        Assert.assertEquals("foo", o);
    }

    public void testParseQuoteWithSpace() {
        ConditionParser parser = new ConditionParser();
        Condition c = parser.parse("name='foo bar'");
        Assert.assertEquals("name", c.getExpression().getAttributeName());
        Assert.assertEquals(OpTypeEnum.EQUALS, c.getExpression().getOpType());
        List<?> vals = c.getExpression().getValues();
        Assert.assertEquals(1, vals.size());
        Object o = vals.get(0);

        Assert.assertEquals("foo bar", o);
    }

    public void testParseQuoteStartWithSpace() {
        ConditionParser parser = new ConditionParser();
        Condition c = parser.parse("name=' foo bar'");
        Assert.assertEquals("name", c.getExpression().getAttributeName());
        Assert.assertEquals(OpTypeEnum.EQUALS, c.getExpression().getOpType());
        List<?> vals = c.getExpression().getValues();
        Assert.assertEquals(1, vals.size());
        Object o = vals.get(0);

        Assert.assertEquals(" foo bar", o);
    }

    public void testToOpType() {
        ConditionParser parser = new ConditionParser();
        OpTypeEnum e = parser.toOpType("=");
        Assert.assertEquals(OpTypeEnum.EQUALS, e);
    }

}
