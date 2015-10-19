package com.rm.darya.persistence;

/**
 * Created by alex
 */
public class SQLQueryBuilder {

    //region Integer operations
    public static final String BIGGER = " > ";
    public static final String LESS = " < ";
    public static final String LESS_OR_EQUALS = " <= ";
    public static final String BIGGER_OR_EQUALS = " >= ";
    public static final String EQUALS = " = ";
    public static final String NOT_EQUALS = " != ";
    //endregion

    //region Boolean operations
    public static final String NOT = " NOT ";
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    //endregion

    //region String operations
    public static final String LIKE = " LIKE ";
    private static final String STRING_START = "\"%";
    private static final String STRING_END = "%\"";
    //endregion

    //region SQL keywords
    public static final String ALL = " * ";
    public static final String TEXT = " TEXT ";
    private static final String SPACE = " ";
    private static final String SELECT = " SELECT ";
    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    //endregion

    private static StringBuilder sBuilder;

    private SQLQueryBuilder() {
        sBuilder = new StringBuilder();
    }

    public static SQLQueryBuilder getInstance() {
        return new SQLQueryBuilder();
    }

    public SQLQueryBuilder select(String criteria) {
        sBuilder.append(SELECT).append(criteria);
        return this;
    }

    public SQLQueryBuilder from(String table) {
        sBuilder.append(FROM).append(table);
        return this;
    }

    public SQLQueryBuilder where() {
        sBuilder.append(WHERE);
        return this;
    }

    public SQLQueryBuilder integerClause(String column, String operators, int operand) {
        sBuilder.append(SPACE)
                .append(column)
                .append(operators)
                .append(operand);
        return this;
    }

    public SQLQueryBuilder stringClause(String column, String operators, String operand) {
        sBuilder.append(SPACE)
                .append(column)
                .append(operators)
                .append(STRING_START)
                .append(operand)
                .append(STRING_END);
        return this;
    }

    public SQLQueryBuilder and() {
        sBuilder.append(AND);
        return this;
    }

    public SQLQueryBuilder or() {
        sBuilder.append(OR);
        return this;
    }

    public String build() {
        return sBuilder.toString().trim();
    }
}
