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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.query.Condition;
import com.fatwire.assetapi.query.ConditionFactory;
import com.fatwire.assetapi.query.OpTypeEnum;

import org.apache.commons.lang.StringUtils;

/**
 * Builds up a Condition from a string.
 * <p/>
 * Sample query strings are:
 * <ul>
 * <li>name='foo'</li>
 * <li>name = 'foo'</li>
 * <li>name = foo</li>
 * <li>name= 'foo bar'</li>
 * <li>size=[1,2]</li>
 * <li>size{10,250}</li>
 * <li>name!='foo'</li>
 * </ul>
 * Whitespace is not significant outside single quotes.
 * 
 * 
 * @author Dolf Dijkstra
 * @since Mar 29, 2011
 */
public class ConditionParser {

    enum Operator {
        EQUALS("="), NOT_EQUALS("!="), LESS_THAN("<"), LESS_THAN_EQUALS("<="), GREATER_THAN(">"), GREATER_THAN_EQUALS(
                ">="), BETWEEN("={"), BETWEEN_EXCLUDING("=!{"), LIKE("~"), RICHTEXT("#");
        private final String op;

        Operator(final String op) {
            this.op = op;
        }
    };

    private enum State {
        ATTRIBUTE, OP, VALUE

    };

    interface ParserState {
        ParserState parse(char c);

        String toValue();
    }

    static class AttributeState implements ParserState {
        private final StringBuilder value = new StringBuilder();
        private boolean quoted = false;
        private final ParserState next;

        AttributeState(final ParserState next) {
            this.next = next;
        }

        public ParserState parse(final char c) {
            if (Character.isWhitespace(c)) {
                if (quoted) {
                    value.append(c);
                } else {
                    return next;
                }
            } else if (c == '"' || c == '\'') {
                if (quoted) {
                    quoted = false;
                    return next;
                } else {
                    quoted = true;
                }
            } else if ("=!<>~{}".indexOf(c) != -1) {
                if (quoted) {
                    value.append(c);
                } else if (value.length() > 0) {
                    next.parse(c);
                    return next;
                }
            } else {
                value.append(c);

            }
            return this;

        }

        public String toValue() {
            return value.toString();
        }
    }

    static class OperatorState implements ParserState {
        private final StringBuilder value = new StringBuilder();
        private final ParserState next;

        OperatorState(final ParserState next) {
            this.next = next;
        }

        public ParserState parse(final char c) {
            if ("=!<>~{".indexOf(c) != -1) {
                value.append(c);
            } else {
                next.parse(c);
                return next;
            }
            return this;
        }

        public String toValue() {
            return value.toString();
        }

    }

    static class ValueState implements ParserState {
        private final StringBuilder value = new StringBuilder();
        private boolean quoted = false;

        public ParserState parse(final char c) {
            if (Character.isWhitespace(c)) {
                if (quoted) {
                    value.append(c);
                } else if (value.length() > 0) {
                    value.append(c);
                }
            } else if (c == '"' || c == '\'') {
                quoted = !quoted;
                value.append(c);
            } else if ("{}".indexOf(c) != -1) {
                // brackets must always be quoted in values
                if (quoted) {
                    value.append(c);
                }
            } else {
                value.append(c);

            }
            return this;
        }

        public String toValue() {
            return value.toString();
        }

    }

    public Condition parse(final String s) {
        final ValueState valueState = new ValueState();
        final OperatorState operatorState = new OperatorState(valueState);
        final AttributeState attributeState = new AttributeState(operatorState);
        ParserState state = attributeState;

        final char[] c = s.trim().toCharArray();
        for (int i = 0; i < c.length; i++) {
            state = state.parse(c[i]);
        }
        final String attName = attributeState.toValue();
        if (StringUtils.isBlank(attName)) {
            throw new IllegalArgumentException("No attribute name found in '" + s + "'.");
        }

        OpTypeEnum opType;
        try {
            opType = toOpType(operatorState.toValue());
        } catch (final Exception e) {
            final IllegalArgumentException e2 = new IllegalArgumentException("No operator found in '" + s + "'. "
                    + e.getMessage());
            e2.initCause(e);
            throw e2;
        }

        final String value = valueState.toValue();
        if (opType == OpTypeEnum.BETWEEN) {

            final String[] parts = valueSplit(value);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Between condition does not two comma-seperated values in '" + s
                        + "'. ");
            }
            try {
                return new ConditionFactory().createBetweenCondition(attName, parts[0], parts[1]);
            } catch (final AssetAccessException e) {
                final RuntimeAssetAccessException e1 = new RuntimeAssetAccessException(e.getMessage());
                e1.initCause(e);
                throw e1;
            }
        } else if (opType == OpTypeEnum.EQUALS && value.startsWith("[") && value.endsWith("]")) {

            final String[] parts = valueSplit(value.substring(1, value.length() - 1));
            if (parts.length < 1) {
                throw new IllegalArgumentException("Equals condition with multiple values does have any values: '" + s
                        + "'. ");
            }

            return ConditionFactory.createCondition(attName, opType, Arrays.asList(parts));
        } else if (opType == OpTypeEnum.NOT_EQUALS && value.startsWith("[") && value.endsWith("]")) {

            final String[] parts = valueSplit(value.substring(1, value.length() - 1));
            if (parts.length < 1) {
                throw new IllegalArgumentException("Equals condition with multiple values does have any values: '" + s
                        + "'. ");
            }
            Condition condition = null;
            for (final String part : parts) {
                final Condition cc = ConditionFactory.createCondition(attName, opType, part);
                if (condition == null) {
                    condition = cc;
                } else {
                    condition = condition.and(cc);
                }
            }
            return condition;
        }

        return ConditionFactory.createCondition(attName, opType, unquote(value));

    }

    private String unquote(final String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        final char c = value.charAt(0);
        if (c == '\'' || c == '"') {
            if (value.length() < 3) {
                return "";
            }
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    public String[] valueSplit(final String s) {
        final List<String> list = new LinkedList<String>();
        boolean quoted = false;
        final char[] c = s.toCharArray();
        final StringBuilder cur = new StringBuilder();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ',') {
                if (quoted) {
                    cur.append(c[i]);
                } else {
                    list.add(cur.toString());
                    cur.setLength(0);
                }
            } else if (c[i] == '\'') {
                quoted = !quoted;
            } else {
                cur.append(c[i]);
            }

        }
        list.add(cur.toString());
        return list.toArray(new String[0]);
    }

    OpTypeEnum toOpType(final StringBuilder op) {
        return toOpType(op.toString().toLowerCase());
    }

    OpTypeEnum toOpType(final String op) {
        if (StringUtils.isBlank(op)) {
            throw new IllegalArgumentException("Operator can  not be blank.");
        }
        if ("=".equals(op)) {
            return OpTypeEnum.EQUALS;
        } else if ("!=".equals(op)) {
            return OpTypeEnum.NOT_EQUALS;
        } else if ("<".equals(op)) {
            return OpTypeEnum.LESS_THAN;
        } else if (">".equals(op)) {
            return OpTypeEnum.GREATER_THAN;
        } else if ("{".equals(op)) {
            return OpTypeEnum.BETWEEN;
        } else if ("~".equals(op)) {
            return OpTypeEnum.LIKE;
        } else if ("#".equals(op)) {
            return OpTypeEnum.RICHTEXT;

        }
        throw new IllegalArgumentException("Can't decode operator in " + op);
    }

}
