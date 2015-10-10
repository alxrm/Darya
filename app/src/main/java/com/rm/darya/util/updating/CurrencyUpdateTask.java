package com.rm.darya.util.updating;

import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by alex
 */
public class CurrencyUpdateTask extends AsyncTask<Void, Void, ArrayList<Currency>> {

    private static final String REQUEST_BASE_URL
            = "http://query.yahooapis.com/v1/public/yql?";
    private static final String REQUEST_DB_QUERY
            = "q=select+*+from%20yahoo.finance.xchange%20where%20pair%20in%20";
    private static final String REQUEST_DB_SOURCE
            = "&env=store://datatables.org/alltableswithkeys";

    private ArrayList<Currency> mProjection;
    private OnParseResultListener mListener;

    public CurrencyUpdateTask(OnParseResultListener listener) {
        mProjection = CurrencyUtils.getAllCurrencies();
        mListener = listener;
    }

    @Override
    protected ArrayList<Currency> doInBackground(Void... params) {

        try {
            return CurrencyParser.parseResponse(getRawData());
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

        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        return conn.getInputStream();
    }

    private URL getUrl() throws UnsupportedEncodingException, MalformedURLException {

        final String rawLink;
        final StringBuilder currencyPairs = new StringBuilder();

        for (int i = 0; i < mProjection.size(); i++) {
            currencyPairs.append("%22")
                    .append(mProjection.get(i).getCode())
                    .append("USD%22")
                    .append((i == (mProjection.size() - 1)) ? "" : ",");
        }

        rawLink =
                REQUEST_BASE_URL +
                REQUEST_DB_QUERY +
                "(" + currencyPairs.toString() + ")" +
                REQUEST_DB_SOURCE;
        Log.d("CurrencyUpdateTask", "getUrl - rawLink: "
                + rawLink);

        return new URL(rawLink);
    }
}