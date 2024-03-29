package com.hyd.dao;


import com.hyd.dao.command.Command;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 生成 Command 的帮助类
 *
 * @author yiding.he
 */
@SuppressWarnings({
    "unused", "BooleanMethodIsAlwaysInverted", "unchecked", "UnusedReturnValue"
})
public class SQL {

    //在这个类中本人坚持这种“不符合规范”的命名方式，因为考虑到
    //SQL 属于不同语种，即使是用 Java 语法来模拟 SQL，也应该保持这种感觉

    private SQL() {

    }

    private static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }

        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }

        String str = obj.toString();
        return str.length() == 0 || str.trim().length() == 0;
    }

    /////////////////////////////////////////////////////////

    public static Select Select(String columns) {
        return new Select(columns);
    }

    public static Select Select(String... columns) {
        return new Select(columns);
    }

    public static Update Update(String table) {
        return new Update(table);
    }

    public static Insert Insert(String table) {
        return new Insert(table);
    }

    public static Delete Delete(String table) {
        return new Delete(table);
    }

    /////////////////////////////////////////////////////////

    public enum Joint {
        AND, OR
    }

    public enum JoinType {
        InnerJoin(" INNER JOIN "), OuterJoin(" OUTER JOIN "), LeftJoin(" LEFT JOIN "), RightJoin(" RIGHT JOIN ");

        private final String code;

        JoinType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public static class Pair {

        private Joint joint = Joint.AND;  // AND/OR/null

        private final String statement;

        private List<Object> args;

        public Pair(String statement) {
            this.statement = statement;
        }

        public Pair(Joint joint, String statement) {
            this(joint, statement, (Object[]) null);
        }

        public Pair(String statement, Object... args) {
            this(null, statement, args);
        }

        public Pair(Joint joint, String statement, Object... args) {
            this.joint = joint;
            this.statement = statement.trim();
            this.args = args == null? Collections.emptyList(): Arrays.asList(args);
        }

        public Pair(Joint joint, String statement, List<Object> args) {
            this.joint = joint;
            this.statement = statement.trim();
            this.args = args;
        }

        public Object firstArg() {
            return this.args == null || this.args.isEmpty() ? null : this.args.get(0);
        }

        public boolean hasArg() {
            return this.args != null && !this.args.isEmpty();
        }

        protected static String joinPairName(List<Pair> pairs) {
            if (pairs.isEmpty()) {
                return "";
            } else {
                StringBuilder result = new StringBuilder();
                for (Pair pair : pairs) {
                    result.append(pair.statement).append(",");
                }
                result = new StringBuilder(result.substring(0, result.length() - 1));
                return result.toString();
            }
        }

        protected static String joinPairHolder(List<Pair> pairs) {
            StringBuilder s = new StringBuilder();

            for (int size = pairs.size(), i = 0; i < size; i++) {
                Pair pair = pairs.get(i);

                if (DAO.SYSDATE == pair.firstArg()) {
                    s.append("sysdate");
                } else {
                    s.append("?");
                }

                s.append(i == size - 1 ? "" : ",");
            }
            return s.toString();
        }

        protected static List<Object> joinPairValue(List<Pair> pairs) {
            if (pairs.isEmpty()) {
                return Collections.emptyList();
            }

            List<Object> result = new ArrayList<>();
            for (Pair pair : pairs) {

                if (DAO.SYSDATE == pair.firstArg()) {
                    continue;
                }

                result.addAll(pair.args);
            }

            return result;
        }
    }

    public static class Join {

        public final JoinType type;

        public final String statement;

        public final List<Object> params;

        public Join(JoinType type, String statement) {
            this(type, statement, Collections.emptyList());
        }

        public Join(JoinType type, String statement, Object... params) {
            this.type = type;
            this.statement = statement;
            this.params = Arrays.asList(params);
        }
    }

    /////////////////////////////////////////////////////////

    @SuppressWarnings("rawtypes")
    public static abstract class Generatable<T extends Generatable> {

        protected String table;

        protected String statement;

        protected List<Object> params = new ArrayList<>();

        protected List<Pair> conditions = new ArrayList<>();

        protected List<Join> joins = new ArrayList<>();

        public abstract Command toCommand();

        public String getTable() {
            return table;
        }

        public List<Object> getParams() {
            return params;
        }

        public boolean hasConditions() {
            return !conditions.isEmpty();
        }

        public boolean hasParams() {
            return !params.isEmpty();
        }

        public T LeftJoin(String statement, Object... params) {
            this.joins.add(new Join(JoinType.LeftJoin, statement, params));
            return (T) this;
        }

        public T RightJoin(String statement, Object... params) {
            this.joins.add(new Join(JoinType.RightJoin, statement, params));
            return (T) this;
        }

        public T InnerJoin(String statement, Object... params) {
            this.joins.add(new Join(JoinType.InnerJoin, statement, params));
            return (T) this;
        }

        public T OuterJoin(String statement, Object... params) {
            this.joins.add(new Join(JoinType.OuterJoin, statement, params));
            return (T) this;
        }

        public T Where(String statement) {
            if (this instanceof Insert) {
                throw new IllegalStateException("cannot use 'where' block in Insert");
            }
            this.conditions.add(new Pair(Joint.AND, statement));
            return (T) this;
        }

        public T Where(String statement, Object... args) {
            if (this instanceof Insert) {
                throw new IllegalStateException("cannot use 'where' block in Insert");
            }
            this.conditions.add(new Pair(Joint.AND, statement, args));
            return (T) this;
        }

        public T Where(String statement, Generatable<T> child) {
            return Where(true, statement, child);
        }

        public T Where(boolean exp, String statement) {
            if (this instanceof Insert) {
                throw new IllegalStateException("cannot use 'where' block in Insert");
            }
            if (exp) {
                this.conditions.add(new Pair(Joint.AND, statement));
            }
            return (T) this;
        }

        public T Where(boolean exp, String statement, Object... args) {
            if (this instanceof Insert) {
                throw new IllegalStateException("cannot use 'where' block in Insert");
            }
            if (exp) {
                this.conditions.add(new Pair(Joint.AND, statement, args));
            }
            return (T) this;
        }

        public T Where(boolean exp, String statement, Generatable<T> child) {
            if (this instanceof Insert) {
                throw new IllegalStateException("cannot use 'where' block in Insert");
            }
            if (exp) {
                Command childCmd = child.toCommand();
                this.conditions.add(new Pair(Joint.AND, statement + "(" + childCmd.getStatement() + ")", childCmd.getParams()));
            }
            return (T) this;
        }

        public T And(String statement) {
            this.conditions.add(new Pair(Joint.AND, statement));
            return (T) this;
        }

        public T And(String statement, Object... args) {
            this.conditions.add(new Pair(Joint.AND, statement, args));
            return (T) this;
        }

        public T And(String statement, Generatable<T> child) {
            return And(true, statement, child);
        }

        public T And(boolean exp, String statement) {
            if (exp) {
                this.conditions.add(new Pair(Joint.AND, statement));
            }
            return (T) this;
        }

        public T And(boolean exp, String statement, Object... args) {
            if (exp) {
                this.conditions.add(new Pair(Joint.AND, statement, args));
            }
            return (T) this;
        }

        public T And(boolean exp, String statement, Generatable<T> child) {
            if (exp) {
                Command childCmd = child.toCommand();
                this.conditions.add(new Pair(Joint.AND, statement + "(" + childCmd.getStatement() + ")", childCmd.getParams()));
            }
            return (T) this;
        }

        public T AndIfNotEmpty(String statement, Object value) {
            return And(!isEmpty(value), statement, value);
        }

        public <V> T IfNotEmpty(V value, Consumer<T> consumer) {
            if (!isEmpty(value) && consumer != null) {
                consumer.accept((T) this);
            }
            return (T) this;
        }

        public <V> T IfNotEmpty(V value, BiConsumer<T, V> consumer) {
            if (!isEmpty(value) && consumer != null) {
                consumer.accept((T) this, value);
            }
            return (T) this;
        }

        public T Or(String statement) {
            this.conditions.add(new Pair(Joint.OR, statement));
            return (T) this;
        }

        public T Or(String statement, Object... args) {
            this.conditions.add(new Pair(Joint.OR, statement, args));
            return (T) this;
        }

        public T Or(String statement, Generatable<T> child) {
            return Or(true, statement, child);
        }

        public T Or(boolean exp, String statement) {
            if (exp) {
                this.conditions.add(new Pair(Joint.OR, statement));
            }
            return (T) this;
        }

        public T Or(boolean exp, String statement, Object... args) {
            if (exp) {
                this.conditions.add(new Pair(Joint.OR, statement, args));
            }
            return (T) this;
        }

        public T Or(boolean exp, String statement, Generatable<T> child) {
            if (exp) {
                Command childCmd = child.toCommand();
                this.conditions.add(new Pair(Joint.OR, statement + "(" + childCmd.getStatement() + ")", childCmd.getParams()));
            }
            return (T) this;
        }

        public T OrIfNotEmpty(String column, Object value) {
            return Or(!isEmpty(value), column, value);
        }

        public T Append(String statement) {
            this.conditions.add(new Pair(statement));
            return (T) this;
        }

        public T Append(String column, Object... args) {
            this.conditions.add(new Pair(column, args));
            return (T) this;
        }

        public T Append(boolean exp, String statement) {
            if (exp) {
                this.conditions.add(new Pair(statement));
            }
            return (T) this;
        }

        public T Append(boolean exp, String statement, Object... args) {
            if (exp) {
                this.conditions.add(new Pair(statement, args));
            }
            return (T) this;
        }

        protected String generateJoinBlock() {
            StringBuilder joinBlock = new StringBuilder();
            for (Join join : joins) {
                joinBlock.append(join.type.getCode()).append(join.statement);
                this.params.addAll(join.params);
            }
            return joinBlock.toString();
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
                where += (condition.joint == null ? "" : (" " + condition.joint.name() + " "));
            }

            where += " ";

            if (!condition.hasArg()) {       // 不带参数的条件
                where += condition.statement;

            } else if (condition.args.size() == 1 && condition.firstArg() instanceof List) {   // 参数为 List 的条件（即 in 条件）
                List<?> objects = (List<?>) condition.firstArg();

                // marks = "(?,?,?,...,?)"
                String marks = "(" +
                    objects.stream()
                        .map(o -> {
                            this.params.add(o);
                            return "?";
                        })
                        .collect(Collectors.joining(",")) +
                    ")";

                // "A in ?" -> "A in (?,?,?)"
                where += condition.statement.replace("?", marks);

            } else if (condition.statement.endsWith("in ?")) {

                // marks = "(?,?,?,...,?)"
                String marks = "(" +
                    condition.args.stream()
                        .map(o -> {
                            this.params.add(o);
                            return "?";
                        }).collect(Collectors.joining(",")) +
                    ")";

                // "A in ?" -> "A in (?,?,?)"
                where += condition.statement.replace("?", marks);

            } else {
                where += condition.statement;
                this.params.addAll(condition.args);
            }

            return where;
        }
    }

    /////////////////////////////////////////////////////////

    public static class Insert extends Generatable<Insert> {

        private final List<Pair> pairs = new ArrayList<>();

        public Insert(String table) {
            this.table = table;
        }

        public Insert Values(String column, Object value) {
            return Values(!isEmpty(value), column, value);
        }

        public Insert Values(boolean ifTrue, String column, Object value) {
            if (ifTrue) {
                pairs.add(new Pair(column, value));
            }
            return this;
        }

        public Insert Values(Map<String, Object> map) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Values(entry.getKey(), entry.getValue());
            }
            return this;
        }

        @Override
        public Command toCommand() {
            this.statement = "insert into " + table +
                "(" + Pair.joinPairName(pairs) + ") values " +
                "(" + Pair.joinPairHolder(pairs) + ")";
            this.params = Pair.joinPairValue(pairs);

            return new Command(statement, params);
        }
    }

    /////////////////////////////////////////////////////////

    /**
     * 用于生成 update 语句的帮助类
     */
    @SuppressWarnings({"StringConcatenationInLoop", "unused"})
    public static class Update extends Generatable<Update> {

        private final List<Pair> updates = new ArrayList<>();

        public Update(String table) {
            this.table = table;
        }

        public List<Pair> getUpdates() {
            return updates;
        }

        @Override
        public Command toCommand() {
            this.params.clear();
            this.statement = "update " + table +
                " set " + generateSetBlock() + " " + generateWhereBlock();

            return new Command(this.statement, this.params);
        }

        private String generateSetBlock() {
            String statement = "";

            for (int i = 0, updatesSize = updates.size(); i < updatesSize; i++) {
                Pair pair = updates.get(i);
                if (!pair.hasArg()) {
                    statement += pair.statement;
                } else if (pair.statement.contains("?")) {
                    this.params.addAll(pair.args);
                    statement += pair.statement;
                } else {
                    this.params.addAll(pair.args);
                    statement += pair.statement + "=?";
                }

                if (i < updatesSize - 1) {
                    statement += ",";
                }
            }

            return statement;
        }

        public Update Set(boolean exp, String column, Object value) {
            if (exp) {
                this.updates.add(new Pair(column, value));
            }
            return this;
        }

        public Update Set(String column, Object value) {
            this.updates.add(new Pair(column, value));
            return this;
        }

        public Update Set(String setStatement) {
            this.updates.add(new Pair(setStatement));
            return this;
        }

        public Update Set(boolean exp, String setStatement) {
            if (exp) {
                this.updates.add(new Pair(setStatement));
            }
            return this;
        }

        public Update SetIfNotNull(String column, Object value) {
            return Set(value != null, column, value);
        }

        public Update SetIfNotEmpty(String column, Object value) {
            return Set(!isEmpty(value), column, value);
        }
    }

    /////////////////////////////////////////////////////////

    /**
     * 用于生成 select 语句的帮助类
     */
    public static class Select extends Generatable<Select> {

        private final String columns;

        private String from;

        private String orderBy;

        private String groupBy;

        private long skip = -1;

        private long limit = -1;

        public Select(String columns) {
            this.columns = columns;
        }

        public Select(String... columns) {
            this.columns = String.join(",", columns);
        }

        public Select From(String from) {
            this.from = from;
            return this;
        }

        public Select From(String... from) {
            this.from = String.join(",", from);
            return this;
        }

        public Select OrderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public Select GroupBy(String groupBy) {
            this.groupBy = groupBy;
            return this;
        }

        public Select Skip(long skip) {
            this.skip = skip;
            return this;
        }

        public Select Limit(long limit) {
            this.limit = limit;
            return this;
        }

        @Override
        public Command toCommand() {
            this.params.clear();
            this.statement = "select " + this.columns + " from " + this.from + " ";
            this.statement += generateJoinBlock();
            this.statement += generateWhereBlock();
            this.statement += generateGroupBy();
            this.statement += generateOrderBy();
            this.statement += this.skip > 0 ? (" skip " + this.skip + " ") : "";
            this.statement += this.limit > 0 ? (" limit " + this.limit + " ") : "";
            return new Command(this.statement, this.params);
        }

        private String generateGroupBy() {
            return isEmpty(this.groupBy) ? "" : (" group by " + this.groupBy);
        }

        private String generateOrderBy() {
            return isEmpty(this.orderBy) ? "" : (" order by " + this.orderBy);
        }
    }

    /////////////////////////////////////////////////////////

    public static class Delete extends Generatable<Delete> {

        public Delete(String table) {
            this.table = table;
        }

        @Override
        public Command toCommand() {
            this.params.clear();
            this.statement = "delete from " + table + generateWhereBlock();
            return new Command(this.statement, this.params);
        }
    }
}


