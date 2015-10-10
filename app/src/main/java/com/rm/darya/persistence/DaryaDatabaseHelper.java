package com.rm.darya.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rm.darya.model.Currency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.rm.darya.persistence.CurrencyTable.COLUMN_CODE;
import static com.rm.darya.persistence.CurrencyTable.COLUMN_NAME;
import static com.rm.darya.persistence.CurrencyTable.COLUMN_RATE;
import static com.rm.darya.persistence.CurrencyTable.COLUMN_SELECTED;
import static com.rm.darya.persistence.CurrencyTable.CREATE;
import static com.rm.darya.persistence.CurrencyTable.NAME;
import static com.rm.darya.persistence.CurrencyTable.PROJECTION;

/**
 * Created by alex
 */
public class DaryaDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "darya.db";
    private static final int DB_VERSION = 1;
    private static final String EMPTY_RATE = "0";
    private static final String CURRENCIES_LIST_FILE = "currencies.json";

    private static DaryaDatabaseHelper mInstance;
    private static List<Currency> sCurrencies;
    private final AssetManager sAssets;

    private DaryaDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        sAssets = context.getAssets();
    }

    public static DaryaDatabaseHelper getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new DaryaDatabaseHelper(context);
        }
        return mInstance;
    }

    public static ArrayList<Currency> getCurrencies(Context context, boolean selectedOnly) {
        return loadCurrencies(context, selectedOnly);
    }

    private static ArrayList<Currency> loadCurrencies(Context context, boolean selectedOnly) {
        Cursor data = DaryaDatabaseHelper.getCurrencyCursor(context);
        ArrayList<Currency> tmpCurrencies = new ArrayList<>(data.getCount());
        Log.d("DaryaDatabaseHelper", "loadCurrencies - data.getCount(): "
                        + data.getCount());

        if (data.getCount() == 0) return tmpCurrencies;

        do {
            final Currency currency = getCurrency(data);
            if (selectedOnly) {
                if (currency.isSelected()) tmpCurrencies.add(currency);
            } else {
                tmpCurrencies.add(currency);
            }
        } while (data.moveToNext());

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

        Log.d("DaryaDatabaseHelper", "getCurrency: "
                + c);
        return c;
    }

    private static Cursor getCurrencyCursor(Context context) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        Cursor data = readableDatabase.query(NAME, PROJECTION, null, null, null, null, null);
        data.moveToFirst();
        return data;
    }

    public static void updateCurrencyRate(Context context, Currency currency) {
        SQLiteDatabase writableDatabase = getWritableDatabase(context);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RATE, String.valueOf(currency.getRate()));

        writableDatabase.update(NAME, contentValues, COLUMN_CODE + "=?",
                new String[]{currency.getCode()});
    }

    public static void updateCurrencySelection(Context context, Currency currency) {
        SQLiteDatabase writableDatabase = getWritableDatabase(context);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SELECTED, currency.isSelected());

        writableDatabase.update(NAME, contentValues, COLUMN_CODE + "=?",
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
        values.put(COLUMN_NAME, currency.getString(JsonAttributes.NAME));
        values.put(COLUMN_CODE, currency.getString(JsonAttributes.CODE));
        values.put(COLUMN_RATE, EMPTY_RATE);
        values.put(COLUMN_SELECTED, false);
        db.insert(NAME, null, values);
    }

    private String readCategoriesFromResources() throws IOException {
        StringBuilder categoriesJson = new StringBuilder();
        InputStream rawCategories = sAssets.open(CURRENCIES_LIST_FILE);
        BufferedReader reader = new BufferedReader(new InputStreamReader(rawCategories));
        String line;

        while ((line = reader.readLine()) != null) {
            categoriesJson.append(line);
        }
        return categoriesJson.toString();
    }
}
