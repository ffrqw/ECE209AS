package com.rachio.iro.ui.view.settings;

import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class SwitchWithHelp extends LinearLayout implements Checkable {

    /* renamed from: com.rachio.iro.ui.view.settings.SwitchWithHelp$1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ SwitchWithHelp this$0;

        public void onClick(View v) {
            this.this$0.toggle();
        }
    }

    /* renamed from: com.rachio.iro.ui.view.settings.SwitchWithHelp$2 */
    class AnonymousClass2 implements OnClickListener {
        final /* synthetic */ SwitchWithHelp this$0;

        public void onClick(View v) {
            new Builder(this.this$0.getContext()).setMessage(null).show();
        }
    }

    public void setChecked(boolean checked) {
        SwitchCompat switchCompat = null;
        switchCompat.setChecked(checked);
    }

    public boolean isChecked() {
        SwitchCompat switchCompat = null;
        return switchCompat.isChecked();
    }

    public void toggle() {
        SwitchCompat switchCompat = null;
        switchCompat.toggle();
    }

    public void setOnClickListener(OnClickListener l) {
        throw new UnsupportedOperationException();
    }
}
