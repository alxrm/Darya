package com.rm.darya.persistence;

import android.support.annotation.NonNull;

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
    private static final String STRING_START = " \"%";
    private static final String STRING_END = "%\" ";
    //endregion

    //region SQL keywords
    public static final String ALL = "*";
    public static final String PRIMARY_KEY = " PRIMARY KEY ";
    private static final String SPACE = " ";
    private static final String COMA = ", ";
    private static final String START_PARAMS = " ( ";
    private static final String END_PARAMS = " ) ";
    private static final String SELECT = " SELECT ";
    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    private static final String TABLE = " TABLE ";
    private static final String CREATE = " CREATE ";
    private static final String ORDER_BY = " ORDER BY ";
    private static final String INNER_JOIN = " INNER JOIN ";
    private static final String ON = " ON ";
    //endregion

    //region Data types
    public static final String TYPE_TEXT = " TEXT ";
    public static final String TYPE_INT = " INTEGER ";
    public static final String NULL = " NULL ";
    //endregion

    private static StringBuilder sBuilder;

    private SQLQueryBuilder() {
        sBuilder = new StringBuilder();
    }

    public static SQLQueryBuilder getInstance() {
        return new SQLQueryBuilder();
    }

    private static void removeLastComa() {
        sBuilder.deleteCharAt(sBuilder.length() - 2);
    }

    //region Create table
    public SQLQueryBuilder create(String tableName) {
        sBuilder.append(CREATE).append(TABLE).append(tableName);
        return this;
    }

    public SQLQueryBuilder columnsStart() {
        sBuilder.append(START_PARAMS);
        return this;
    }

    /**
     * @param columnParams { TYPE, NULL/NOT NULL, PRIMARY_KEY }
     */
    public SQLQueryBuilder column(@NonNull String name, String[] columnParams) {
        sBuilder.append(name);
        if (columnParams != null)
            for (String param : columnParams) sBuilder.append(param);
        sBuilder.append(COMA);
        return this;
    }

    public SQLQueryBuilder columnsEnd() {
        removeLastComa();
        sBuilder.append(END_PARAMS);
        return this;
    }
    //endregion

    //region Select
    public SQLQueryBuilder select(String criteria) {
        sBuilder.append(SELECT).append(criteria == null ? ALL : criteria);
        return this;
    }

    public SQLQueryBuilder from(@NonNull String table) {
        sBuilder.append(FROM).append(table);
        return this;
    }

    public SQLQueryBuilder where() {
        sBuilder.append(WHERE);
        return this;
    }

    public SQLQueryBuilder integerClause(@NonNull String column,
                                         @NonNull String operators,
                                         int operand) {
        sBuilder.append(column)
                .append(operators)
                .append(operand);
        return this;
    }

    public SQLQueryBuilder stringClause(@NonNull String column,
                                        @NonNull String operators,
                                        @NonNull String operand) {
        sBuilder.append(column)
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

    public SQLQueryBuilder orderBy(@NonNull String[] columns) {
        if (columns.length == 0)
            throw new IllegalArgumentException("Must be at least one column");

        sBuilder.append(ORDER_BY);
        for (String col : columns) sBuilder.append(col).append(COMA);
        removeLastComa();
        return this;
    }
    //endregion

    //region Inner join
    /**
     * SELECT Orders.OrderID, Customers.CustomerName, Orders.OrderDate
     * FROM Orders
     * INNER JOIN Customers
     * ON Orders.CustomerID=Customers.CustomerID;
     */
    public SQLQueryBuilder innerJoin(@NonNull String tableName) {
        sBuilder.append(INNER_JOIN).append(tableName);
        return this;
    }

    public SQLQueryBuilder on(@NonNull String lCol,
                              @NonNull String operator,
                              @NonNull String rCol) {
        sBuilder.append(ON).append(lCol).append(operator).append(rCol);
        return this;
    }
    //endregion

    public String build() {
//        String q = sBuilder.toString().trim().replace(" +", SPACE);
//        Log.d("SQLQueryBuilder", "query: " + q);
        return sBuilder.toString().trim().replace(" +", SPACE);
    }
}
