package com.hyd.dao;


import com.hyd.dao.database.commandbuilder.Command;
import com.hyd.dao.util.StringUtil;

import java.util.*;

/**
 * 生成 Command 的帮助类
 *
 * @author yiding.he
 */
public class SQL {

    //在这个类中本人坚持这种“不符合规范”的命名方式，因为考虑到
    //SQL 属于不同语种，即使是用 Java 语法来模拟 SQL，也应该保持这种感觉

    private SQL() {

    }

    private static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        String str = obj.toString();
        return str.length() == 0 || str.trim().length() == 0;
    }

    /////////////////////////////////////////////////////////

    public static Select Select(String columns) {  // NOSONAR
        return new Select(columns);
    }

    public static Select Select(String... columns) {  // NOSONAR
        return new Select(columns);
    }

    public static Update Update(String table) {  // NOSONAR
        return new Update(table);
    }

    public static Insert Insert(String table) {  // NOSONAR
        return new Insert(table);
    }

    public static Delete Delete(String table) {  // NOSONAR
        return new Delete(table);
    }

    /////////////////////////////////////////////////////////

    public static enum Pref {
        AND, OR
    }

    public static class Pair {

        private Pref pref = null;

        private String name;

        private Object value;

        public Pair(String name, Object value) {
            this.name = name;
            this.value = value;

            fixArrValue();
        }

        public Pair(Pref pref, String name, Object value) {
            this.pref = pref;
            this.name = name;
            this.value = value;

            fixArrValue();
        }

        private void fixArrValue() {
            if (value == null) {
                return;
            }

            if (value.getClass().isArray()) {
                value = Arrays.asList((Object[]) value);
            }
        }
    }

    public static class StatementPair extends Pair {

        public StatementPair(String statement) {
            super(statement, null);
        }

        public StatementPair(Pref pref, String statement) {
            super(pref, statement, null);
        }
    }

    /////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    public static abstract class Generatable<T extends Generatable> {

        protected String table;

        protected String statement;

        protected List<Object> params = new ArrayList<Object>();

        protected List<Pair> conditions = new ArrayList<Pair>();

        public abstract Command toCommand();

        protected String joinNames(List<Pair> pairs) {
            if (pairs.isEmpty()) {
                return "";
            } else {
                String result = "";
                for (Pair pair : pairs) {
                    result += pair.name + ",";
                }
                result = result.substring(0, result.length() - 1);
                return result;
            }
        }

        protected String joinQuestionMarks(List<Pair> pairs) {
            StringBuilder s = new StringBuilder();

            for (int size = pairs.size(), i = 0; i < size; i++) {
                Pair pair = pairs.get(i);

                if (pair.value == DAO.SYSDATE) {
                    s.append("sysdate");
                } else {
                    s.append("?");
                }

                s.append(i == size - 1 ? "" : ",");
            }
            return s.toString();
        }

        protected List<Object> joinValues(List<Pair> pairs) {
            if (pairs.isEmpty()) {
                return Collections.emptyList();
            }

            List<Object> result = new ArrayList<Object>();
            for (Pair pair : pairs) {

                if (pair.value == DAO.SYSDATE) {
                    continue;
                }

                result.add(pair.value);
            }

            return result;
        }

        public T Where(String statement) {  // NOSONAR
            if (this instanceof Insert) {
                throw new IllegalStateException("cannot use 'where' block in Insert");
            }
            this.conditions.add(new StatementPair(statement));
            return (T) this;
        }

        public T Where(String column, Object value) {  // NOSONAR
            if (this instanceof Insert) {
                throw new IllegalStateException("cannot use 'where' block in Insert");
            }
            this.conditions.add(new Pair(column, value));
            return (T) this;
        }

        public T Where(boolean exp, String statement) {  // NOSONAR
            if (this instanceof Insert) {
                throw new IllegalStateException("cannot use 'where' block in Insert");
            }
            if (exp) {
                this.conditions.add(new StatementPair(statement));
            }
            return (T) this;
        }

        public T Where(boolean exp, String column, Object value) {  // NOSONAR
            if (this instanceof Insert) {
                throw new IllegalStateException("cannot use 'where' block in Insert");
            }
            if (exp) {
                this.conditions.add(new Pair(column, value));
            }
            return (T) this;
        }

        public T And(String statement) {  // NOSONAR
            this.conditions.add(new StatementPair(Pref.AND, statement));
            return (T) this;
        }

        public T And(String column, Object value) {  // NOSONAR
            this.conditions.add(new Pair(Pref.AND, column, value));
            return (T) this;
        }

        public T And(boolean exp, String statement) {  // NOSONAR
            if (exp) {
                this.conditions.add(new StatementPair(Pref.AND, statement));
            }
            return (T) this;
        }

        public T And(boolean exp, String column, Object value) {  // NOSONAR
            if (exp) {
                this.conditions.add(new Pair(Pref.AND, column, value));
            }
            return (T) this;
        }

        public T AndIfNotEmpty(String column, Object value) {  // NOSONAR
            return And(!isEmpty(value), column, value);
        }

        public T Or(String statement) {  // NOSONAR
            this.conditions.add(new StatementPair(Pref.OR, statement));
            return (T) this;
        }

        public T Or(String column, Object value) {  // NOSONAR
            this.conditions.add(new Pair(Pref.OR, column, value));
            return (T) this;
        }

        public T Or(boolean exp, String statement) {  // NOSONAR
            if (exp) {
                this.conditions.add(new StatementPair(Pref.OR, statement));
            }
            return (T) this;
        }

        public T Or(boolean exp, String column, Object value) {  // NOSONAR
            if (exp) {
                this.conditions.add(new Pair(Pref.OR, column, value));
            }
            return (T) this;
        }

        public T OrIfNotEmpty(String column, Object value) {  // NOSONAR
            return Or(!isEmpty(value), column, value);
        }

        public T Append(String statement) {  // NOSONAR
            this.conditions.add(new StatementPair(statement));
            return (T) this;
        }

        public T Append(String column, Object value) {  // NOSONAR
            this.conditions.add(new Pair(column, value));
            return (T) this;
        }

        public T Append(boolean exp, String statement) {  // NOSONAR
            if (exp) {
                this.conditions.add(new StatementPair(statement));
            }
            return (T) this;
        }

        public T Append(boolean exp, String column, Object value) {  // NOSONAR
            if (exp) {
                this.conditions.add(new Pair(column, value));
            }
            return (T) this;
        }

        protected String generateWhereBlock() {
            String where = "";

            if (!this.conditions.isEmpty()) {
                where = "where ";

                for (int i = 0, conditionsSize = conditions.size(); i < conditionsSize; i++) {
                    Pair condition = conditions.get(i);
                    where = processCondition(i, where, condition);
                }

            }

            return " " + where;
        }

        private String processCondition(int index, String where, Pair condition) {

            where = where.trim();

            // 第一个条件不能加 and 和 or 前缀
            if (index > 0 && !where.endsWith("(")) {
                if (condition.pref == Pref.AND) {
                    where += " and ";
                } else if (condition.pref == Pref.OR) {
                    where += " or ";
                }
            }

            where += " ";

            if (condition instanceof StatementPair) {       // 不带参数的条件
                where += condition.name;

            } else if (condition.value instanceof List) {   // 参数为 List 的条件（即 in 条件）
                String marks = "(";

                for (Object o : (List) condition.value) {
                    marks += "?,";
                    this.params.add(o);
                }

                if (marks.endsWith(",")) {
                    marks = marks.substring(0, marks.length() - 1);
                }
                marks += ")";                                 // marks = "(?,?,?,...,?)"

                where += condition.name.replace("?", marks);  // "A in ?" -> "A in (?,?,?)"

            } else {
                where += condition.name;
                this.params.add(condition.value);
            }

            return where;
        }
    }

    /////////////////////////////////////////////////////////

    public static class Insert extends Generatable<Insert> {

        private String table;

        private List<Pair> pairs = new ArrayList<Pair>();

        public Insert(String table) {
            this.table = table;
        }

        public Insert Values(String column, Object value) {  // NOSONAR
            return Values(!isEmpty(value), column, value);
        }

        public Insert Values(boolean ifTrue, String column, Object value) {  // NOSONAR
            if (ifTrue) {
                pairs.add(new Pair(column, value));
            }
            return this;
        }

        public Insert Values(Map<String, Object> map) {  // NOSONAR
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Values(entry.getKey(), entry.getValue());
            }
            return this;
        }

        @Override
        public Command toCommand() {
            this.statement = "insert into " + table + "(" + joinNames(pairs) + ") values (" + joinQuestionMarks(pairs) + ")";
            this.params = joinValues(pairs);

            return new Command(statement, params);
        }
    }

    /////////////////////////////////////////////////////////

    /**
     * 用于生成 update 语句的帮助类
     */
    public static class Update extends Generatable<Update> {

        private List<Pair> updates = new ArrayList<Pair>();

        public Update(String table) {
            this.table = table;
        }

        public List<Pair> getUpdates() {
            return updates;
        }

        @Override
        public Command toCommand() {
            this.statement = "update " + table +
                    " set " + generateSetBlock() + " " + generateWhereBlock();

            return new Command(this.statement, this.params);
        }

        private String generateSetBlock() {
            String statement = "";

            for (int i = 0, updatesSize = updates.size(); i < updatesSize; i++) {
                Pair pair = updates.get(i);
                if (pair instanceof StatementPair) {
                    statement += pair.name;
                } else if (pair.name.contains("?")) {
                    this.params.add(pair.value);
                    statement += pair.name;
                } else {
                    this.params.add(pair.value);
                    statement += pair.name + "=?";
                }

                if (i < updatesSize - 1) {
                    statement += ",";
                }
            }

            return statement;
        }

        public Update Set(boolean exp, String column, Object value) {  // NOSONAR
            if (exp) {
                this.updates.add(new Pair(column, value));
            }
            return this;
        }

        public Update Set(String column, Object value) {  // NOSONAR
            this.updates.add(new Pair(column, value));
            return this;
        }

        public Update Set(String setStatement) {  // NOSONAR
            this.updates.add(new StatementPair(setStatement));
            return this;
        }

        public Update Set(boolean exp, String setStatement) {  // NOSONAR
            if (exp) {
                this.updates.add(new StatementPair(setStatement));
            }
            return this;
        }

        public Update SetIfNotNull(String column, Object value) {  // NOSONAR
            return Set(value != null, column, value);
        }

        public Update SetIfNotEmpty(String column, Object value) {  // NOSONAR
            return Set(!isEmpty(value), column, value);
        }
    }

    /////////////////////////////////////////////////////////

    /**
     * 用于生成 select 语句的帮助类
     */
    public static class Select extends Generatable<Select> {

        private String columns;

        private String from;

        private String orderBy;

        private String groupBy;

        public Select(String columns) {
            this.columns = columns;
        }

        public Select(String... columns) {
            this.columns = StringUtil.join(columns, ",");
        }

        public Select From(String from) {  // NOSONAR
            this.from = from;
            return this;
        }

        public Select From(String... from) {  // NOSONAR
            this.from = StringUtil.join(from, ",");
            return this;
        }

        public Select OrderBy(String orderBy) {  // NOSONAR
            this.orderBy = orderBy;
            return this;
        }

        public Select GroupBy(String groupBy) {  // NOSONAR
            this.groupBy = groupBy;
            return this;
        }

        @Override
        public Command toCommand() {
            this.statement = "select " + this.columns + " from " + this.from + " ";

            this.statement += generateWhereBlock();

            if (!isEmpty(this.groupBy)) {
                this.statement += " group by " + this.groupBy;
            }

            if (!isEmpty(this.orderBy)) {
                this.statement += " order by " + this.orderBy;
            }

            return new Command(this.statement, this.params);
        }

    }

    /////////////////////////////////////////////////////////

    public static class Delete extends Generatable<Delete> {

        public Delete(String table) {
            this.table = table;
        }

        @Override
        public Command toCommand() {
            this.statement = "delete from " + table + generateWhereBlock();
            return new Command(this.statement, this.params);
        }
    }
}


