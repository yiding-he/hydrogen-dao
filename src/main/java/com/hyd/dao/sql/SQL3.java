package com.hyd.dao.sql;

public class SQL3 {

    public static Table Table(String name) {
        return new Table();
    }

    public static Select Select(Column... columns) {
        return new Select();
    }

    public static class Column {

        public Condition In(Object... values) {
            return new Condition();
        }

        public Condition LessOrEqual(Object value) {
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

        public Select WithJoining(Joining... joinings) {
            return this;
        }

        public Select WithAllMatch(Condition... conditions) {
            return this;
        }

        public Select WithAnyMatch(Condition... conditions) {
            return this;
        }
    }

    public static class Joining {

        public Joining Using(String column) {
            return this;
        }

        public Joining OnMatch(Column column1, Column column2) {
            return this;
        }
    }

    public static class Condition {

    }
}
