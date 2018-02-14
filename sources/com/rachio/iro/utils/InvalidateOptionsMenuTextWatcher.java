package com.rachio.iro.utils;

import android.text.Editable;
import android.text.TextWatcher;
import com.rachio.iro.ui.activity.BaseActivity;

public class InvalidateOptionsMenuTextWatcher implements TextWatcher {
    private final BaseActivity activity;

    public InvalidateOptionsMenuTextWatcher(BaseActivity activity) {
        this.activity = activity;
    }

    public void afterTextChanged(Editable s) {
        this.activity.supportInvalidateOptionsMenu();
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}
