package com.rm.darya.persistence;

/**
 * Created by alex
 */
public interface CurrencyTable {

    String TABLE_NAME = "currency";

    String COLUMN_NAME = "name";
    String COLUMN_CODE = "code";
    String COLUMN_RATE = "rate";
    String COLUMN_SELECTED = "selected";

    String CREATE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_NAME + " TEXT NOT NULL PRIMARY KEY, "
            + COLUMN_CODE + " TEXT NOT NULL, "
            + COLUMN_RATE + " TEXt NOT NULL, "
            + COLUMN_SELECTED + ");";
}
