package com.hyd.dao.sql;

import com.hyd.dao.database.commandbuilder.Command;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SQL3 {

    private Map<String, Table> tableMap = new HashMap<>();

    protected Table Table(String name) {
        return tableMap.computeIfAbsent(name, Table::new);
    }

    protected Select Select(Selectable... selectables) {
        return new Select(selectables);
    }

    protected Select Select(String... columns) {
        Column[] columnArr = toColumns(columns);
        return new Select(columnArr);
    }

    protected Select Any(Selectable selectable) {
        return new Select();
    }

    protected Select All(Selectable selectable) {
        return new Select();
    }

    protected Conditions MatchAll(Condition... conditions) {
        return new Conditions(ConditionJoint.And, conditions);
    }

    protected Conditions MatchAny(Condition... conditions) {
        return new Conditions(ConditionJoint.Or, conditions);
    }

    protected Expression Expr(String expression) {
        return new Expression(expression);
    }

    protected Column Column(String column) {
        int split = column.lastIndexOf(".");
        if (split == -1) {
            throw new IllegalArgumentException("Column name must be with table name");
        }
        return new Column(
                Table(column.substring(0, split)),
                column.substring(split + 1)
        );
    }

    protected Condition Where(String expression, Object... args) {
        if (args.length == 1 && args[0].getClass().isArray()) {
            return Where(expression, (Object[]) args[0]);
        }
        return new Condition(expression, args);
    }

    protected JoiningContext LeftJoin(String table1) {
        return new JoiningContext();
    }

    public Column[] toColumns(String tableName, String[] columnNames) {
        Column[] columns = new Column[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            String name = columnNames[i];
            columns[i] = new Column(Table(tableName), name);
        }
        return columns;
    }

    public Column[] toColumns(String[] columnNames) {
        Column[] columns = new Column[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            columns[i] = Column(columnNames[i]);
        }
        return columns;
    }

    //////////////////////////////////////////////////////////////

    public abstract class Selectable {

        public abstract String toCode();
    }

    public class Expression extends Selectable {

        private String value;

        public Expression(String value) {
            this.value = value;
        }

        @Override
        public String toCode() {
            return value;
        }
    }

    public class Column extends Selectable {

        private Table table;

        private String columnName;

        public Column(Table table, String columnName) {
            this.table = table;
            this.columnName = columnName;
        }

        private String fqn() {
            if (this instanceof Columns) {
                throw new UnsupportedOperationException();
            }
            return table.name + "." + columnName;
        }

        @Override
        public String toCode() {
            return fqn();
        }

        public Condition In(Object... values) {
            String questionMarks = Stream.of(values)
                    .map(o -> "?").collect(Collectors.joining(","));
            return new Condition(
                    fqn() + " in (" + questionMarks + ")", values);
        }

        public Condition LessOrEqual(Object value) {
            return new Condition(fqn() + "<=?", value);
        }

        public Condition Equals(Object value) {
            return new Condition(fqn() + "=?", value);
        }
    }

    public class Table {

        private String name;

        private String alias;

        public Table(String name) {
            this.name = name;
        }

        public Table As(String alias) {
            this.alias = alias;
            return this;
        }

        public Column Column(String... names) {
            if (names.length == 1) {
                return new Column(this, names[0]);
            } else {
                Column[] columns = toColumns(this.name, names);
                return new Columns(columns);
            }
        }

        public Joining LeftJoin(Table table) {
            return new Joining();
        }
    }

    public class Select {

        private Selectables selectables;

        private Conditions conditions;

        private Joinings joinings;

        private Select(Selectable... selectables) {
            this.selectables = new Selectables(selectables);
        }

        public Select Joins(Joining... joinings) {
            this.joinings = new Joinings(joinings);
            return this;
        }

        public Select Match(Condition condition) {
            return MatchAll(condition);
        }

        public Select MatchAll(Condition... conditions) {
            if (this.conditions != null) {
                throw new IllegalStateException("Conditions already been set.");
            }
            this.conditions = new Conditions(ConditionJoint.And, conditions);
            return this;
        }

        public Select MatchAny(Condition... conditions) {
            if (this.conditions != null) {
                throw new IllegalStateException("Conditions already been set.");
            }
            this.conditions = new Conditions(ConditionJoint.Or, conditions);
            return this;
        }

        public Command toCommand() {
            return SQLBuilder.build(this);
        }
    }

    public class Joining {

        public Joining Using(String column) {
            return this;
        }

        public Joining On(Condition condition) {
            return this;
        }

        public Joining On(String expression) {
            return this;
        }
    }

    public class JoiningContext {

        public Joining With(String table2) {
            return new Joining();
        }
    }

    public static class Condition {

        private String statement;

        private List<Object> args;

        public Condition(String statement, Object... args) {
            this.statement = statement;
            if (args != null) {
                this.args = Arrays.asList(args);
            }
        }
    }

    //////////////////////////////////////////////////////////////

    public class Columns extends Column {

        private List<Column> columns = new ArrayList<>();

        public Columns(Column... columns) {
            super(null, null);
            this.columns.addAll(Arrays.asList(columns));
        }
    }

    public class Selectables {

        private List<Selectable> selectables = new ArrayList<>();

        public Selectables(Selectable... selectables) {
            this.selectables.addAll(Arrays.asList(selectables));
        }
    }

    public class Joinings {

        private List<Joining> joinings = new ArrayList<>();

        public Joinings(Joining... joinings) {
            this.joinings.addAll(Arrays.asList(joinings));
        }
    }

    public class Conditions extends Condition {

        private List<Condition> conditionList = new ArrayList<>();

        private ConditionJoint joint;

        public Conditions(ConditionJoint joint, Condition... conditions) {
            super(null, null);
            this.joint = joint;
            this.conditionList.addAll(Arrays.asList(conditions));
        }
    }

    public enum ConditionJoint {
        And, Or
    }

    //////////////////////////////////////////////////////////////

    public static class SQLBuilder {

        public static Command build(Select select) {
            String statement = "select ";
            List<Object> args = new ArrayList<>();

            List<String> selectableFqns = new ArrayList<>();
            List<Selectable> selectables = select.selectables.selectables;

            selectables.forEach(selectable -> {
                if (selectable instanceof Columns) {
                    ((Columns) selectable).columns.forEach(column -> {
                        selectableFqns.add(column.toCode());
                    });
                } else {
                    selectableFqns.add(selectable.toCode());
                }
            });

            statement += String.join(", ", selectableFqns);

            return new Command(statement, args);
        }
    }
}
