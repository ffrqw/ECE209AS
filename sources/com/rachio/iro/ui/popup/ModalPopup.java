package com.rachio.iro.ui.popup;

import android.view.View;
import android.widget.PopupWindow;

public class ModalPopup extends PopupWindow {
    public ModalPopup(View content) {
        super(content.getContext());
        setContentView(content);
        setFocusable(true);
        setOutsideTouchable(true);
        setWindowLayoutMode(-2, -2);
    }
}
