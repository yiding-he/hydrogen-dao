package com.hyd.dao.sql;

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

    public static Joining LeftJoin(String table1, String table2) {
        return new Joining(table1, table2);
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

        public Condition Equals(Selectable selectable) {
            return new Condition();
        }
    }

    public static class Joining {

        private String table1, table2;

        private Condition joinCondition;

        public Joining(String table1, String table2) {
            this.table1 = table1;
            this.table2 = table2;
        }

        public Joining Using(String column) {
            return this;
        }

        public Joining On(Condition condition) {
            return this;
        }
    }

    public static class Select extends Selectable {

        public Select As(String alias) {
            return this;
        }

        public Select WithJoining(Joining... joinings) {
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
