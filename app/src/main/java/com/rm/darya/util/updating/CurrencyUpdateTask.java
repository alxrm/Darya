package com.rm.darya.util.updating;

import android.os.AsyncTask;

import com.rm.darya.events.OnParseResultListener;
import com.rm.darya.model.Currency;
import com.rm.darya.util.CurrencyUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import static com.rm.darya.util.CurrencyUtils.ExceptedCurrencies.isExceptedCode;

/**
 * Created by alex
 */
public class CurrencyUpdateTask extends AsyncTask<Void, Void, ArrayList<Currency>> {

    private static final String PAIR_START = "%22";
    private static final String PAIR_END = "USD%22";
    private static final String SEPARATOR = ",";

    private static final String REQUEST_BASE_URL
            = "http://query.yahooapis.com/v1/public/yql?";
    private static final String REQUEST_DB_QUERY
            = "q=select+*+from%20yahoo.finance.xchange%20where%20pair%20in%20";
    private static final String REQUEST_DB_SOURCE
            = "&env=store://datatables.org/alltableswithkeys";
    private static final String BRACKET_OPEN = "(";
    private static final String BRACKET_CLOSE = ")";

    private static CurrencyUpdateTask sTask;

    private ArrayList<Currency> mProjection;
    private OnParseResultListener mListener;
    private StringBuilder mLinkBuilder, mPairsBuilder;

    public static void updateAll(OnParseResultListener listener) {
        sTask = new CurrencyUpdateTask(listener, false);
        sTask.execute();
    }

    public static void updateSelected(OnParseResultListener listener) {
        sTask = new CurrencyUpdateTask(listener, true);
        sTask.execute();
    }

    private CurrencyUpdateTask(OnParseResultListener listener, boolean selected) {
        mProjection = selected
                ?
                CurrencyUtils.getSelectedCurrencies()
                :
                CurrencyUtils.getAllCurrencies();
        mListener = listener;
    }

    @Override
    protected ArrayList<Currency> doInBackground(Void... params) {

        try {
            InputStream data = getRawData();
            return data != null ? CurrencyParser.parseResponse(getRawData()) : null;
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Currency> currencies) {
        super.onPostExecute(currencies);

        if (currencies != null) {
            CurrencyUtils.pushRates(currencies);
            mListener.onParseSuccessful();
        } else {
            mListener.onError();
        }
    }

    private InputStream getRawData() throws IOException {
        URL url = getUrl();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        if (HttpsURLConnection.HTTP_OK != conn.getResponseCode())
            return null;

        return conn.getInputStream();
    }

    private URL getUrl() throws UnsupportedEncodingException, MalformedURLException {
        String pairs;
        mLinkBuilder = new StringBuilder();
        mPairsBuilder = new StringBuilder();

        for (int i = 0; i < mProjection.size(); i++) {
            String code = mProjection.get(i).getCode();

            if (!isExceptedCode(code)) {
                mPairsBuilder.append(PAIR_START)
                        .append(code)
                        .append(PAIR_END)
                        .append(SEPARATOR);
            }
        }

        pairs = BRACKET_OPEN
                + mPairsBuilder.substring(0, mPairsBuilder.length() - 1)
                + BRACKET_CLOSE;

        mLinkBuilder.append(REQUEST_BASE_URL)
                .append(REQUEST_DB_QUERY)
                .append(pairs)
                .append(REQUEST_DB_SOURCE);

        return new URL(mLinkBuilder.toString());
    }
}