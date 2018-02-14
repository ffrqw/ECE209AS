package com.rachio.iro.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.rachio.iro.R;

public class DemoDialog {
    public static Dialog show(Context context) {
        View content = LayoutInflater.from(context).inflate(R.layout.dialog_demo, null);
        Button continueButton = (Button) content.findViewById(R.id.demo_continue);
        final Dialog dialog = new Dialog(context, 16973840);
        dialog.setContentView(content);
        dialog.setCancelable(false);
        continueButton.setOnClickListener(new OnClickListener() {
            public final void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }
}
