package com.rm.darya.events.typing;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by alex
 */
public abstract class TextWatcherAdapter implements TextWatcher {

    protected final int mPosition;

    public TextWatcherAdapter(int position) {
        mPosition = position;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextInput(s.toString(), mPosition);
    }

    @Override
    public void afterTextChanged(Editable s) {}

    protected abstract void onTextInput(String s, int position);
}
