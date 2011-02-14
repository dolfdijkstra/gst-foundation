package com.fatwire.gst.foundation.facade.assetapi;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.query.Condition;
import com.fatwire.assetapi.query.ConditionFactory;
import com.fatwire.assetapi.query.OpTypeEnum;

public class ConditionParser {

    enum Operator {
        EQUALS("="), NOT_EQUALS("!="), LESS_THAN("<"), LESS_THAN_EQUALS("<="), GREATER_THAN(">"), GREATER_THAN_EQUALS(
                ">="), BETWEEN("={"), BETWEEN_EXCLUDING("=!{"), LIKE("~"), RICHTEXT("#");
        private final String op;

        Operator(String op) {
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
        private StringBuilder value = new StringBuilder();
        private boolean quoted = false;
        private final ParserState next;

        AttributeState(ParserState next) {
            this.next = next;
        }

        public ParserState parse(char c) {
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
        private StringBuilder value = new StringBuilder();
        private final ParserState next;

        OperatorState(ParserState next) {
            this.next = next;
        }

        public ParserState parse(char c) {
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
        private StringBuilder value = new StringBuilder();
        private boolean quoted = false;

        public ParserState parse(char c) {
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

    public Condition parse(String s) {
        final ValueState valueState = new ValueState();
        final OperatorState operatorState = new OperatorState(valueState);
        final AttributeState attributeState = new AttributeState(operatorState);
        ParserState state = attributeState;

        char[] c = s.trim().toCharArray();
        for (int i = 0; i < c.length; i++) {
            state = state.parse(c[i]);
        }
        String attName = attributeState.toValue();
        if (StringUtils.isBlank(attName))
            throw new IllegalArgumentException("No attribute name found in '" + s + "'.");

        OpTypeEnum opType;
        try {
            opType = toOpType(operatorState.toValue());
        } catch (Exception e) {
            IllegalArgumentException e2 = new IllegalArgumentException("No operator found in '" + s + "'. "
                    + e.getMessage());
            e2.initCause(e);
            throw e2;
        }

        String value = valueState.toValue();
        if (opType == OpTypeEnum.BETWEEN) {

            String[] parts = valueSplit(value);
            if (parts.length != 2)
                throw new IllegalArgumentException("Between condition does not two comma-seperated values in '" + s
                        + "'. ");
            try {
                return new ConditionFactory().createBetweenCondition(attName, parts[0], parts[1]);
            } catch (AssetAccessException e) {
                RuntimeAssetAccessException e1 = new RuntimeAssetAccessException(e.getMessage());
                e1.initCause(e);
                throw e1;
            }
        } else if (opType == OpTypeEnum.EQUALS && value.startsWith("[") && value.endsWith("]")) {

            String[] parts = valueSplit(value.substring(1, value.length() - 1));
            if (parts.length < 1)
                throw new IllegalArgumentException("Equals condition with multiple values does have any values: '" + s
                        + "'. ");

            return ConditionFactory.createCondition(attName, opType, Arrays.asList(parts));
            /*
             *             Condition condition = null;
            for (String part : parts) {
                System.out.println(part);
                Condition cc = ConditionFactory.createCondition(attName, opType, part);
                if (condition == null) {
                    condition = cc;
                } else {
                    condition = condition.or(cc);
                }
            }
            return condition;
            */
        } else if (opType == OpTypeEnum.NOT_EQUALS && value.startsWith("[") && value.endsWith("]")) {

            String[] parts = valueSplit(value.substring(1, value.length() - 1));
            if (parts.length < 1)
                throw new IllegalArgumentException("Equals condition with multiple values does have any values: '" + s
                        + "'. ");
            Condition condition = null;
            for (String part : parts) {
                Condition cc = ConditionFactory.createCondition(attName, opType, part);
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

    private String unquote(String value) {
        if (StringUtils.isBlank(value))
            return value;
        char c = value.charAt(0);
        if (c == '\'' || c == '"') {
            if (value.length() < 3)
                return "";
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    public String[] valueSplit(String s) {
        List<String> list = new LinkedList<String>();
        boolean quoted = false;
        char[] c = s.toCharArray();
        StringBuilder cur = new StringBuilder();
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

    public Condition parseOld(String s) {
        State state = State.ATTRIBUTE;
        StringBuilder attr = new StringBuilder();
        StringBuilder op = new StringBuilder();
        StringBuilder val = new StringBuilder();
        boolean quoted = false;

        char[] c = s.trim().toCharArray();
        for (int i = 0; i < c.length; i++) {

            if (Character.isWhitespace(c[i])) {
                switch (state) {
                    case ATTRIBUTE:
                        if (quoted) {
                            attr.append(c[i]);
                        } else {
                            state = State.OP;
                        }
                        break;
                    case OP:
                        state = State.VALUE;
                        break;
                    case VALUE:
                        if (quoted) {
                            val.append(c[i]);
                        } else if (val.length() > 0) {
                            val.append(c[i]);
                        }
                        break;

                }
            } else if (c[i] == '"' || c[i] == '\'') {
                switch (state) {
                    case ATTRIBUTE:
                        if (quoted) {
                            state = State.OP;
                            quoted = false;
                        } else {
                            quoted = true;
                        }
                        break;
                    case OP:
                        state = State.VALUE;
                        quoted = true;
                        break;
                    case VALUE:
                        quoted = !quoted;
                        break;

                }
            } else if ("=!<>~".indexOf(c[i]) != -1) {
                switch (state) {
                    case ATTRIBUTE:
                        if (quoted) {
                            attr.append(c[i]);
                        } else if (attr.length() > 0) {
                            state = State.OP;
                            op.append(c[i]);
                        }

                        break;
                    case OP:
                        op.append(c[i]);
                        break;
                    case VALUE:
                        val.append(c[i]);
                        break;

                }
            } else if ("{}".indexOf(c[i]) != -1) {
                switch (state) {
                    case ATTRIBUTE:
                        if (quoted) {
                            attr.append(c[i]);
                        } else if (attr.length() > 0) {
                            state = State.OP;
                            op.append(c[i]);
                        }

                        break;
                    case OP:
                        op.append(c[i]);
                        break;
                    case VALUE: // brackets must always be quoted in values
                        if (quoted) {
                            val.append(c[i]);
                        }

                        break;

                }
            } else {
                switch (state) {
                    case ATTRIBUTE:
                        attr.append(c[i]);
                        break;
                    case OP:
                        state = State.VALUE;
                        val.append(c[i]);
                        break;
                    case VALUE:
                        val.append(c[i]);
                        break;

                }

            }
        }

        String attName = attr.toString();
        if (StringUtils.isBlank(attName))
            throw new IllegalArgumentException("No attribute name found in '" + s + "'.");

        OpTypeEnum opType;
        try {
            opType = toOpType(op);
        } catch (Exception e) {
            IllegalArgumentException e2 = new IllegalArgumentException("No operator found in '" + s + "'. "
                    + e.getMessage());
            e2.initCause(e);
            throw e2;
        }

        String v = val.toString();
        if (opType == OpTypeEnum.BETWEEN) {
            String[] parts = v.split(",");
            if (parts.length != 2)
                throw new IllegalArgumentException("Between condition does not two comma-seperated values in '" + s
                        + "'. ");
            try {
                return new ConditionFactory().createBetweenCondition(attName, parts[0], parts[1]);
            } catch (AssetAccessException e) {
                RuntimeAssetAccessException e1 = new RuntimeAssetAccessException(e.getMessage());
                e1.initCause(e);
                throw e1;
            }
        } else if (opType == OpTypeEnum.EQUALS && v.startsWith("[") && v.endsWith("]")) {

            String[] parts = v.substring(1, v.length() - 2).split(",");
            if (parts.length < 1)
                throw new IllegalArgumentException("Equals condition with multiple values does have any values: '" + s
                        + "'. ");
            Condition condition = null;
            for (String part : parts) {
                System.out.println(part);
                Condition cc = ConditionFactory.createCondition(attName, opType, part);
                if (condition == null) {
                    condition = cc;
                } else {
                    condition = condition.or(cc);
                }
            }
            return condition;

        }

        return ConditionFactory.createCondition(attName, opType, v);

    }

    OpTypeEnum toOpType(StringBuilder op) {
        return toOpType(op.toString().toLowerCase());
    }

    OpTypeEnum toOpType(String op) {
        if (StringUtils.isBlank(op))
            throw new IllegalArgumentException("Operator can  not be blank.");
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
