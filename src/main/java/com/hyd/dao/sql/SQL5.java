package com.hyd.dao.sql;

import com.hyd.dao.database.commandbuilder.Command;

import java.util.Arrays;
import java.util.List;

public class SQL5 {

    public static final String JOINT_AND = " and ";

    public static final String JOINT_OR = " or ";

    private Select select;

    private From from;

    private Conditions conditions;

    protected Select select(String... columns) {
        this.select = new Select(columns);
        return select;
    }

    protected Condition where(String statement, Object... args) {
        return new Condition(statement, args);
    }

    public Conditions matchAll(Condition... conditions) {
        SQL5.this.conditions = new Conditions(JOINT_AND, conditions);
        return SQL5.this.conditions;
    }

    public Conditions matchAny(Condition... conditions) {
        SQL5.this.conditions = new Conditions(JOINT_OR, conditions);
        return SQL5.this.conditions;
    }

    //////////////////////////////////////////////////////////////

    public class From {

        private List<String> tables;

        public From(String... tables) {
            this.tables = Arrays.asList(tables);
        }

        public Conditions matchAll(Condition... conditions) {
            SQL5.this.conditions = new Conditions(JOINT_AND, conditions);
            return SQL5.this.conditions;
        }
    }

    public class Condition {

        private String statement;

        private List<Object> args;

        public Condition(String statement, Object... args) {
            this.statement = statement;
            this.args = Arrays.asList(args);
        }
    }

    public class Conditions extends Condition {

        private String joint = JOINT_AND;

        private List<Condition> conditions;

        public Conditions(String joint, Condition... conditions) {
            super(null, null);
            this.conditions = Arrays.asList(conditions);
        }
    }

    public class Select {

        private List<String> columns;

        public Select(String[] columns) {
            this.columns = Arrays.asList(columns);
        }

        public From from(String... tables) {
            SQL5.this.from = new From();
            return SQL5.this.from;
        }
    }

    public Command toCommand() {
        return new Command();
    }
}
