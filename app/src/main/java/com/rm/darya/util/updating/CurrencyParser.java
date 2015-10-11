package com.rm.darya.util.updating;

import android.util.Log;
import android.util.Xml;

import com.rm.darya.model.Currency;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by alex
 */
public class CurrencyParser {

    private static final String TAG_QUERY = "query";
    private static final String TAG_RESULTS = "results";
    private static final String TAG_RATE = "rate";
    private static final String TAG_SUB_RATE = "Rate";
    private static final String TAG_SUB_NAME = "Name";
    private static final String NOT_AVAILABLE = "N/A";

    private static ArrayList<Currency> sResultCurrencies;

    public static ArrayList<Currency> parseResponse(InputStream stream)
            throws XmlPullParserException, IOException {

        sResultCurrencies = new ArrayList<>();

        XmlPullParser xmlParser = Xml.newPullParser();
        xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        xmlParser.setInput(stream, null);
        xmlParser.nextTag();

        xmlParser.require(XmlPullParser.START_TAG, null, TAG_QUERY);

        while (xmlParser.next() != XmlPullParser.END_TAG) {
            processReading(xmlParser);
        }

        return sResultCurrencies;
    }

    private static void processReading(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, TAG_RESULTS);

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            String name = parser.getName();

            if (name.equals(TAG_RATE)) {

                Currency c = parseCurrency(parser);
                if (null != c) sResultCurrencies.add(c);

            } else {
                skipTag(parser);
            }
        }
//        try {
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        }
    }

    private static Currency parseCurrency(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        Currency item = new Currency();
        parser.require(XmlPullParser.START_TAG, null, TAG_RATE);

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            String name = parser.getName();

            switch (name) {
                case TAG_SUB_NAME:
                    String code = getCode(parser).substring(0, 3);
                    Log.d("CurrencyParser", "parseCurrency - code: "
                            + code);
                    if (code.equals(NOT_AVAILABLE)) {
                        return null;
                    }
                    else {
                        item.setCode(code);
                        break;
                    }
                case TAG_SUB_RATE:
                    String rate = getRate(parser);
                    Log.d("CurrencyParser", "parseCurrency - rate: "
                            + rate);
                    if (rate.equals(NOT_AVAILABLE)) {
                        return null;
                    }
                    else {
                        item.setRate(Float.parseFloat(rate));
                        break;
                    }
                default:
                    skipTag(parser);
                    break;
            }
        }
        return item;
    }

    private static String getRate(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, TAG_SUB_RATE);
        String rate = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, TAG_SUB_RATE);

//        Log.d("CurrencyParser", "getRate - rate: "
//                + rate);

        return rate;
    }

    private static String getCode(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, TAG_SUB_NAME);
        String code = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, TAG_SUB_NAME);

//        Log.d("CurrencyParser", "getCode - code: "
//                + code);
        return code;
    }

    private static void skipTag(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        int depth = 1;

        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }

        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private static String readText(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        String result = "";

        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }

        return result;
    }

}
