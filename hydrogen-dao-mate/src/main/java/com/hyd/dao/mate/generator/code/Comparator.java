package com.hyd.dao.mate.generator.code;

/**
 * (description)
 * created at 2018/4/18
 *
 * @author yidin
 */
public enum Comparator {

    Equals("="),
    NotEquals("<>"),
    LessThan("<"),
    LessOrEqual("<="),
    GreaterThan(">"),
    GreaterOrEqual(">="),
    In("in"),
    Like("like")

    //////////////////////////////////////////////////////////////

    ;

    private final String symbol;

    Comparator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
