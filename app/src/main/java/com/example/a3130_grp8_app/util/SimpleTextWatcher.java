package com.example.a3130_grp8_app.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Removes empty TextWatcher overrides from Activities when only afterTextChanged is needed.
 */
public abstract class SimpleTextWatcher implements TextWatcher {

    /**
     * No-op default for callers that only need afterTextChanged.
     *
     * @param s text before the change
     * @param start start index of the change
     * @param count number of characters affected
     * @param after number of characters added
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    /**
     * No-op default for callers that only need afterTextChanged.
     *
     * @param s text after the change
     * @param start start index of the change
     * @param before number of characters replaced
     * @param count number of characters added
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    /**
     * Handles the text state after a watched field changes.
     *
     * @param s edited text
     */
    @Override
    public abstract void afterTextChanged(Editable s);
}
