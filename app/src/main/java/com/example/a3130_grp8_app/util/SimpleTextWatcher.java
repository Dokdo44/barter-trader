package com.example.a3130_grp8_app.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Removes empty TextWatcher overrides from Activities when only afterTextChanged is needed.
 */
public abstract class SimpleTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public abstract void afterTextChanged(Editable s);
}
