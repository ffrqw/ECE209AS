package com.rachio.iro.utils;

import android.graphics.drawable.LayerDrawable;
import android.widget.Spinner;
import com.rachio.iro.R;
import com.rachio.iro.ui.DrawableWithFixedChild;

public class SpinnerUtils {
    public static void fixChevronSpinner(Spinner spinner) {
        LayerDrawable background = (LayerDrawable) spinner.getBackground().getConstantState().newDrawable();
        background.setDrawableByLayerId(R.id.spinnericon, new DrawableWithFixedChild(background.findDrawableByLayerId(R.id.spinnericon)));
        spinner.setBackgroundDrawable(background);
    }
}
