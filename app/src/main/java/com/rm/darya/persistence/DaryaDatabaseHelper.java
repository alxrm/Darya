package com.rm.darya.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rm.darya.model.Currency;
import com.rm.darya.util.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import static com.rm.darya.Darya.app;
import static com.rm.darya.persistence.CurrencyTable.COLUMN_CODE;
import static com.rm.darya.persistence.CurrencyTable.COLUMN_NAME;
import static com.rm.darya.persistence.CurrencyTable.COLUMN_RATE;
import static com.rm.darya.persistence.CurrencyTable.COLUMN_SELECTED;
import static com.rm.darya.persistence.CurrencyTable.CREATE;
import static com.rm.darya.persistence.CurrencyTable.TABLE_NAME;
import static com.rm.darya.persistence.SQLQueryBuilder.ALL;
import static com.rm.darya.persistence.SQLQueryBuilder.EQUALS;
import static com.rm.darya.persistence.SQLQueryBuilder.LIKE;

/**
 * Created by alex
 */
public class DaryaDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "darya.db";
    private static final int DB_VERSION = 1;
    private static final String CURRENCIES_LIST_FILE = "currencies.json";

    private static DaryaDatabaseHelper sInstance;
    private final AssetManager mAssets;

    private DaryaDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mAssets = context.getAssets();
    }

    public static DaryaDatabaseHelper getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new DaryaDatabaseHelper(context);
        }
        return sInstance;
    }

    public static ArrayList<Currency> getCurrencies(Context context) {
        return loadCurrencies(getCurrencyCursor(context));
    }

    public static ArrayList<Currency> findCurrencies(Context context, String query) {
        return loadCurrencies(getSearchCursor(context, query));
    }

    public static ArrayList<Currency> getSelectedCurrencies(Context context) {
        return loadCurrencies(getSelectionCursor(context));
    }

    public static void updateCurrencySelection(Context context, Currency currency) {
        SQLiteDatabase writableDatabase = getWritableDatabase(context);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SELECTED, currency.isSelected());

        writableDatabase.update(TABLE_NAME, contentValues, COLUMN_CODE + "=?",
                new String[]{currency.getCode()});
    }

    public static void updateAllRates(Context c, ArrayList<Currency> currencies) {
        SQLiteDatabase db = getReadableDatabase(c);

        db.beginTransaction();
        try {
            for (Currency currency : currencies)
                DaryaDatabaseHelper.updateCurrencyRate(app(), currency);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private static ArrayList<Currency> loadCurrencies(Cursor data) {
        ArrayList<Currency> tmpCurrencies = new ArrayList<>(data.getCount());

        if (data.getCount() == 0) return tmpCurrencies;

        do {
            final Currency currency = getCurrency(data);
            tmpCurrencies.add(currency);
        } while (data.moveToNext());

        Collections.sort(tmpCurrencies);
        return tmpCurrencies;
    }

    private static Currency getCurrency(Cursor data) {
        Currency c = new Currency();
        final String name = data.getString(0);
        final String code = data.getString(1);
        final float rate = getFloatFromDatabase(data.getString(2));
        final boolean isSelected = getBooleanFromDatabase(data.getInt(3));

        c.setName(name);
        c.setCode(code);
        c.setRate(rate);
        c.setSelected(isSelected);

        return c;
    }

    private static Cursor getSearchCursor(Context c, String searchQuery) {
        SQLiteDatabase readableDatabase = getReadableDatabase(c);
        String selectQuery = SQLQueryBuilder.getInstance()
                .select(ALL)
                .from(TABLE_NAME)
                .where()
                .stringClause(COLUMN_NAME, LIKE, searchQuery)
                .or()
                .stringClause(COLUMN_CODE, LIKE, searchQuery)
                .build();
        Cursor data = readableDatabase.rawQuery(selectQuery, null);
        data.moveToFirst();
        return data;
    }

    private static Cursor getSelectionCursor(Context context) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        String selectQuery = SQLQueryBuilder.getInstance()
                .select(ALL)
                .from(TABLE_NAME)
                .where()
                .integerClause(COLUMN_SELECTED, EQUALS, 1)
                .build();
        Cursor data = readableDatabase.rawQuery(selectQuery, null);
        data.moveToFirst();
        return data;
    }

    private static Cursor getCurrencyCursor(Context context) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        String selectQuery = SQLQueryBuilder.getInstance()
                .select(ALL)
                .from(TABLE_NAME)
                .build();
        Cursor data = readableDatabase.rawQuery(selectQuery, null);
        data.moveToFirst();
        return data;
    }

    private static void updateCurrencyRate(Context context, Currency currency) {
        SQLiteDatabase writableDatabase = getWritableDatabase(context);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RATE, String.valueOf(currency.getRate()));

        writableDatabase.update(TABLE_NAME, contentValues, COLUMN_CODE + "=?",
                new String[]{currency.getCode()});
    }

    private static SQLiteDatabase getWritableDatabase(Context context) {
        return getInstance(context).getWritableDatabase();
    }

    private static SQLiteDatabase getReadableDatabase(Context context) {
        return getInstance(context).getReadableDatabase();
    }

    private static float getFloatFromDatabase(String rate) {
        return Float.valueOf(rate);
    }

    private static boolean getBooleanFromDatabase(int isSolved) {
        return isSolved == 1;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Prefs.saveToday();
        Prefs.saveLastUpdateAll();
        db.execSQL(CREATE);
        preFillDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void preFillDatabase(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            try {
                fillAllCurrencies(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (IOException | JSONException e) {
            Log.e("DaryaDatabaseHelper", "preFillDatabase: " + e);
        }
    }

    private void fillAllCurrencies(SQLiteDatabase db) throws JSONException, IOException {
        ContentValues values = new ContentValues(); // reduce, reuse
        JSONArray jsonArray = new JSONArray(readCategoriesFromResources());
        JSONObject currency;
        for (int i = 0; i < jsonArray.length(); i++) {
            currency = jsonArray.getJSONObject(i);
            fillCurrency(db, values, currency);
        }
    }

    private void fillCurrency(SQLiteDatabase db, ContentValues values, JSONObject currency)
            throws JSONException {
        values.clear();

        String name = currency.getString(JsonAttributes.NAME);
        String code = currency.getString(JsonAttributes.CODE);
        float rate = Float.parseFloat(currency.getString(JsonAttributes.RATE));

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CODE, code);
        values.put(COLUMN_RATE, rate);
        values.put(COLUMN_SELECTED, false);
        db.insert(TABLE_NAME, null, values);
    }

    private String readCategoriesFromResources() throws IOException {
        StringBuilder categoriesJson = new StringBuilder();
        InputStream rawCategories = mAssets.open(CURRENCIES_LIST_FILE);
        BufferedReader reader = new BufferedReader(new InputStreamReader(rawCategories));
        String line;

        while ((line = reader.readLine()) != null) {
            categoriesJson.append(line);
        }
        return categoriesJson.toString();
    }
}
