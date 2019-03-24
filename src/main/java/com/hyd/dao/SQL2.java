package com.hyd.dao;

public class SQL2 {

    public static Select Select(Selectable... columns) {
        return new Select();
    }

    public static Column Column(String... names) {
        return new Column(names);
    }

    public static Condition AllMatch(Condition... conditions) {
        return new Condition();
    }

    public static Condition AnyMatch(Condition... conditions) {
        return new Condition();
    }

    ////////////////////////////////////////////////////////////

    public static abstract class Selectable {

    }

    public static class Condition {

    }

    public static class Column extends Selectable {

        private String[] columnNames;

        private String tableName;

        public Column(String... columnNames) {
            this.columnNames = columnNames;
        }

        public Column From(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Column OfSelect(Column... columns) {
            return this;
        }

        public Column As(String tableAlias) {
            return this;
        }

        public Condition Between(Object value1, Object value2) {
            return new Condition();
        }

        public Condition Like(String pattern) {
            return new Condition();
        }

        public Condition Equals(Object value) {
            return new Condition();
        }
    }

    public static class Select extends Selectable {

        public Select As(String alias) {
            return this;
        }

        public Select WithJoining(Column column1, Column column2) {
            return this;
        }

        public Select AllMatch(Condition... conditions) {
            return this;
        }

        public Select AnyMatch(Condition... conditions) {
            return this;
        }
    }
}
