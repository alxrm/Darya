package com.rm.darya.persistence;

import static com.rm.darya.persistence.SQLQueryBuilder.NOT;
import static com.rm.darya.persistence.SQLQueryBuilder.NULL;
import static com.rm.darya.persistence.SQLQueryBuilder.PRIMARY_KEY;
import static com.rm.darya.persistence.SQLQueryBuilder.TYPE_TEXT;

/**
 * Created by alex
 */
public interface CurrencyTable {

    String TABLE_NAME = "currency";

    String COLUMN_NAME = "name";
    String COLUMN_CODE = "code";
    String COLUMN_RATE = "rate";
    String COLUMN_SELECTED = "selected";

    String CREATE = SQLQueryBuilder.getInstance()
            .create(TABLE_NAME)
            .columnsStart()
            .column(COLUMN_NAME, new String[]{TYPE_TEXT, NOT, NULL, PRIMARY_KEY})
            .column(COLUMN_CODE, new String[]{TYPE_TEXT, NOT, NULL})
            .column(COLUMN_RATE, new String[]{TYPE_TEXT, NOT, NULL})
            .column(COLUMN_SELECTED, null)
            .columnsEnd()
            .build();

}
