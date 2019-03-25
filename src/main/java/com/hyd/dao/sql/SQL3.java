package com.hyd.dao.sql;

public class SQL3 {

    protected Table Table(String name) {
        return new Table();
    }

    protected Select Select(Selectable... selectables) {
        return new Select();
    }

    protected Select Select(String... columns) {
        return new Select();
    }

    protected Select Any(Selectable selectable) {
        return new Select();
    }

    protected Select All(Selectable selectable) {
        return new Select();
    }

    protected Expression Expr(String expression) {
        return new Expression();
    }

    protected Column Column(String column) {
        return new Column();
    }

    protected Condition Where(String expression, Object... args) {
        if (args.length == 1 && args[0].getClass().isArray()) {
            return Where(expression, (Object[]) args[0]);
        }
        return new Condition();
    }

    protected JoiningContext LeftJoin(String table1) {
        return new JoiningContext();
    }

    //////////////////////////////////////////////////////////////

    public static abstract class Selectable {

    }

    public static class Expression extends Selectable {

    }

    public static class Column extends Selectable {

        public Condition In(Object... values) {
            return new Condition();
        }

        public Condition LessOrEqual(Object value) {
            return new Condition();
        }

        public Condition Equals(Object value) {
            return new Condition();
        }
    }

    public static class Table {

        public Table As(String alias) {
            return this;
        }

        public Column Column(String... names) {
            return new Column();
        }

        public Joining LeftJoin(Table table) {
            return new Joining();
        }
    }

    public static class Select {

        public Select Joins(Joining... joinings) {
            return this;
        }

        public Select Match(Condition condition) {
            return this;
        }

        public Select MatchAll(Condition... conditions) {
            return this;
        }

        public Select MatchAny(Condition... conditions) {
            return this;
        }
    }

    public static class Joining {

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

    public static class JoiningContext {

        public Joining With(String table2) {
            return new Joining();
        }
    }

    public static class Condition {

    }
}
