package com.rm.darya.persistence;

/**
 * Created by alex
 */
public interface CurrencyTable {

    String NAME = "currency";

    String COLUMN_NAME = "name";
    String COLUMN_CODE = "code";
    String COLUMN_RATE = "rate";

    String COLUMN_SELECTED = "selected";

    String[] PROJECTION = new String[]{COLUMN_NAME, COLUMN_CODE, COLUMN_RATE, COLUMN_SELECTED};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_NAME + " TEXT NOT NULL PRIMARY KEY, "
            + COLUMN_CODE + " TEXT NOT NULL, "
            + COLUMN_RATE + " TEXt NOT NULL, "
            + COLUMN_SELECTED + ");";
}
