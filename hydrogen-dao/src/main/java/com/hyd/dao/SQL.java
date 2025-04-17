package com.hyd.dao;


import com.hyd.dao.command.Command;
import com.hyd.dao.database.type.NameConverter;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.StringUtils.uncapitalize;

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
        return str.isEmpty() || str.trim().isEmpty();
    }

    /// //////////////////////////////////////////////////////

    @FunctionalInterface
    public interface GetterRef<T, R> extends Serializable {

        @SuppressWarnings("unused")
        R apply(T t);
    }

    /**
     * 解析 lambda 表达式，得到原始的属性名
     */
    protected static <T, R> String parseFieldName(GetterRef<T, R> getterRef) {
        try {
            var lambdaClass = getterRef.getClass();
            var writeReplaceMethod = lambdaClass.getDeclaredMethod("writeReplace");
            writeReplaceMethod.setAccessible(true);
            var serializedForm = (SerializedLambda) writeReplaceMethod.invoke(getterRef);
            return extractPropFromGetter(serializedForm.getImplMethodName());
        } catch (Exception e) {
            throw DAOException.wrap(e);
        }
    }

    protected static String extractPropFromGetter(String getterMethodName) {
        if (getterMethodName.startsWith("is")) {
            return uncapitalize(getterMethodName.substring(2));
        } else if (getterMethodName.startsWith("get")) {
            return uncapitalize(getterMethodName.substring(3));
        } else {
            throw new IllegalArgumentException("Invalid getter method name: " + getterMethodName);
        }
    }

    /// //////////////////////////////////////////////////////

    public static Select Select(String columns) {
        return new Select(columns);
    }

    public static Select Select(String... columns) {
        return new Select(columns);
    }

    @SafeVarargs
    public static <T, R> Select Select(GetterRef<T, R>... getterRefs) {
        return new Select(getterRefs);
    }

    public static Select Select(NameConverter converter) {
        return new Select(converter);
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

    public static CteContext With(Cte... ctes) {
        return new CteContext(ctes);
    }

    /// //////////////////////////////////////////////////////

    public enum Joint {
        AND, OR
    }

    /**
     * SQL JOIN 类型，注意数据库是否支持
     */
    @Getter
    public enum JoinType {
        InnerJoin("INNER JOIN"), FullJoin("FULL JOIN"), LeftJoin("LEFT JOIN"), RightJoin("RIGHT JOIN");

        private final String code;

        JoinType(String code) {
            this.code = code;
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
            this.args = args == null ? Collections.emptyList() : Arrays.asList(args);
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

    public static class Cte extends Select {

        public Cte(NameConverter converter) {
            super(converter);
        }

        public Cte(String columns) {
            super(columns);
        }

        public Cte(String... columns) {
            super(columns);
        }

        public Cte(Select src) {
            super("");
            super.copy(src);
        }

        public <T, R> Cte(GetterRef<T, R>... getterRefs) {
            super(getterRefs);
        }

        @Getter
        private String alias;

        public Cte AsCTE(String alias) {
            this.alias = alias;
            return this;
        }
    }

    public static class CteContext {

        private final List<Cte> ctes = new ArrayList<>();

        public CteContext(Cte... ctes) {
            this.ctes.addAll(Arrays.asList(ctes));
        }

        public Select Select(String columns) {
            return new Select(columns).ctes(ctes);
        }

        public Select Select(String... columns) {
            return new Select(columns).ctes(ctes);
        }

        @SafeVarargs
        public final <T, R> Select Select(GetterRef<T, R>... getterRefs) {
            return new Select(getterRefs).ctes(ctes);
        }

        public Select Select(NameConverter converter) {
            return new Select(converter).ctes(ctes);
        }

        public Update Update(String table) {
            return new Update(table).ctes(ctes);
        }

        public Insert Insert(String table) {
            return new Insert(table).ctes(ctes);
        }

        public Delete Delete(String table) {
            return new Delete(table).ctes(ctes);
        }
    }


    /// //////////////////////////////////////////////////////

    @SuppressWarnings("rawtypes")
    public static abstract class Generatable<T extends Generatable> {

        @Setter
        protected NameConverter nameConverter = NameConverter.DEFAULT;

        @Getter
        protected String table;

        protected String statement;

        @Getter
        protected List<Object> params = new ArrayList<>();

        protected List<Pair> conditions = new ArrayList<>();

        protected List<Join> joins = new ArrayList<>();

        protected List<Cte> ctes = new ArrayList<>();

        protected void copy(Generatable<T> generatable) {
            this.nameConverter = generatable.nameConverter;
            this.table = generatable.table;
            this.statement = generatable.statement;
            this.params.addAll(generatable.params);
            this.conditions.addAll(generatable.conditions);
            this.joins.addAll(generatable.joins);
        }

        protected T ctes(List<Cte> ctes) {
            this.ctes.addAll(ctes);
            return (T) this;
        }

        public abstract Command toCommand();

        public boolean hasConditions() {
            return !conditions.isEmpty();
        }

        public boolean hasParams() {
            return !params.isEmpty();
        }

        public T converter(NameConverter nameConverter) {
            this.nameConverter = nameConverter;
            return (T) this;
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

        public T FullJoin(String statement, Object... params) {
            this.joins.add(new Join(JoinType.FullJoin, statement, params));
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

        protected Command generateJoinBlock() {
            StringBuilder joinBlock = new StringBuilder();
            var params = new ArrayList<>();
            for (Join join : joins) {
                joinBlock.append(join.type.getCode()).append(" ").append(join.statement);
                params.addAll(join.params);
            }
            return new Command(joinBlock.toString(), params);
        }

        protected Command generateWhereBlock() {
            var command = new Command();
            if (!this.conditions.isEmpty()) {
                command.append("WHERE");
                for (int i = 0, conditionsSize = conditions.size(); i < conditionsSize; i++) {
                    Pair condition = conditions.get(i);
                    command.append(processCondition(i, condition));
                }
            }
            return command;
        }

        protected Command generateCteBlock() {
            if (ctes.isEmpty()) {
                return new Command();
            }
            var command = new Command(" WITH ", new ArrayList<>());
            for (var cte : ctes) {
                command.append(cte.toCommand());
                command.append("AS " + cte.alias + ",");
            }
            command.removeSuffix(",");
            return command;
        }

        private Command processCondition(int index, Pair condition) {
            var where = "";
            var params = new ArrayList<>();

            // 第一个条件不能加 and 和 or 前缀
            if (index > 0) {
                where += (condition.joint == null ? "" : (condition.joint.name() + " "));
            }

            if (!condition.hasArg()) {       // 不带参数的条件
                where += condition.statement;

            } else if (condition.args.size() == 1 && condition.firstArg() instanceof List<?> objects) {   // 参数为 List 的条件（即 in 条件）

                // marks = "(?,?,?,...,?)"
                String marks = "(" +
                    objects.stream()
                        .map(o -> {
                            params.add(o);
                            return "?";
                        })
                        .collect(Collectors.joining(",")) +
                    ")";

                // "A in ?" -> "A in (?,?,?)"
                where += condition.statement.replace("?", marks);

            } else if (
                condition.statement.endsWith("IN ?") || condition.statement.endsWith("in ?")
            ) {

                // marks = "(?,?,?,...,?)"
                String marks = "(" +
                    condition.args.stream()
                        .map(o -> {
                            params.add(o);
                            return "?";
                        }).collect(Collectors.joining(",")) +
                    ")";

                // "A in ?" -> "A in (?,?,?)"
                where += condition.statement.replace("?", marks);

            } else {
                where += condition.statement;
                params.addAll(condition.args);
            }

            return new Command(where, params);
        }
    }

    /// //////////////////////////////////////////////////////

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
            var command = generateCteBlock();
            var insertBlock = new Command(
                "insert into " + table +
                    "(" + Pair.joinPairName(pairs) + ") values " +
                    "(" + Pair.joinPairHolder(pairs) + ")",
                Pair.joinPairValue(pairs)
            );
            command.append(insertBlock);
            return command;
        }
    }

    /////////////////////////////////////////////////////////

    /**
     * 用于生成 update 语句的帮助类
     */
    @Getter
    @SuppressWarnings({"StringConcatenationInLoop", "unused"})
    public static class Update extends Generatable<Update> {

        private final List<Pair> updates = new ArrayList<>();

        public Update(String table) {
            this.table = table;
        }

        @Override
        public Command toCommand() {
            var command = new Command("UPDATE " + table + " SET ");
            command.append(generateSetBlock());
            command.append(generateWhereBlock());
            return generateCteBlock().append(command);
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

        private String columns;

        private String from;

        private String orderBy;

        private String groupBy;

        private long skip = -1;

        private long limit = -1;

        protected void copy(Select other) {
            super.copy(other);
            this.columns = other.columns;
            this.from = other.from;
            this.orderBy = other.orderBy;
            this.groupBy = other.groupBy;
            this.skip = other.skip;
            this.limit = other.limit;
        }

        public Select(NameConverter converter) {
            this.nameConverter = converter;
        }

        public Select(String columns) {
            this.columns = columns;
        }

        public Select(String... columns) {
            this.columns = String.join(",", columns);
        }

        public <T, R> Select(GetterRef<T, R>... getterRefs) {
            this.Columns(getterRefs);
        }

        public Select Columns(String... columns) {
            this.columns = String.join(",", columns);
            return this;
        }

        @SafeVarargs
        public final <T, R> Select Columns(GetterRef<T, R>... getterRefs) {
            this.columns = Stream.of(getterRefs)
                .map(SQL::parseFieldName)
                .map(field -> this.nameConverter.field2Column(field))
                .collect(Collectors.joining(","));
            return this;
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

        public Cte AsCTE(String alias) {
            return new Cte(this).AsCTE(alias);
        }

        @Override
        public Command toCommand() {
            this.params.clear();
            var command = generateCteBlock();
            command.append("SELECT " + this.columns + " FROM " + this.from + " ");
            command.append(generateJoinBlock());
            command.append(generateWhereBlock());
            command.append(generateGroupBy());
            command.append(generateOrderBy());
            command.append(generateSkip());
            command.append(generateLimit());
            return command;
        }

        private Command generateGroupBy() {
            return new Command(isEmpty(this.groupBy) ? "" : (" GROUP BY " + this.groupBy));
        }

        private Command generateOrderBy() {
            return new Command(isEmpty(this.orderBy) ? "" : (" ORDER BY " + this.orderBy));
        }

        private Command generateSkip() {
            return new Command(this.skip > 0 ? (" SKIP " + this.skip + " ") : "");
        }

        private Command generateLimit() {
            return new Command(this.limit > 0 ? (" LIMIT " + this.limit + " ") : "");
        }
    }

    /// //////////////////////////////////////////////////////

    public static class Delete extends Generatable<Delete> {

        public Delete(String table) {
            this.table = table;
        }

        @Override
        public Command toCommand() {
            var command = generateCteBlock();
            command.append("DELETE FROM " + table);
            command.append(generateWhereBlock());
            return command;
        }
    }
}


