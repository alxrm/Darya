package com.rm.darya.model;

import android.support.annotation.NonNull;

/**
 * Created by alex
 */
public class Currency implements Comparable<Currency> {

    private String mCode;
    private String mName;
    private float mRate;
    private String mValue;
    private boolean mSelected;

    public static Currency dummy() {
        return new Currency() {{
            setName("Dollar");
            setCode("USD");
            setRate(1.2F);
        }};
    }

    public void setCode(String name) {
        mCode = name;
    }

    public String getCode() {
        return mCode;
    }

    public float getRate() {
        return mRate;
    }

    public void setRate(float rate) {
        mRate = rate;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    @Override
    public int compareTo(@NonNull Currency another) {
        return getName().compareTo(another.getName());
    }

    public boolean filter(String criteria) {
        // TODO implement better search
        return getName().toLowerCase().contains(criteria.toLowerCase())
                || getCode().toLowerCase().contains(criteria.toLowerCase());
    }
}
