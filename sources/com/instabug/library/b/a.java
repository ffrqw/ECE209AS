package com.instabug.library.b;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.text.Html;
import android.widget.TextView;
import com.instabug.library.internal.view.AnimatedImageView;
import com.rachio.iro.R;

public abstract class a extends Dialog implements OnShowListener {
    private AnimatedImageView a;
    private String b;
    private Runnable c;
    private boolean d = false;

    public abstract com.instabug.library.internal.view.AnimatedImageView.a[] a$78293e4();

    public a(Context context, String str) {
        super(context, R.style.InstabugBorderlessDialog);
        this.b = str;
        requestWindowFeature(1);
        setContentView(R.layout.instabug_lyt_dialog_animation);
        setOnShowListener(this);
        getContext().getResources();
        getContext().getPackageName();
        this.a = (AnimatedImageView) findViewById(R.id.animation_frame);
        this.a.setFrames(a$78293e4());
        ((TextView) findViewById(R.id.animation_description)).setText(Html.fromHtml(this.b));
    }

    public void dismiss() {
        super.dismiss();
        try {
            this.a.b();
            this.a.getDrawable().setCallback(null);
            this.a = null;
        } catch (Exception e) {
            this.a = null;
        }
    }

    public void onDetachedFromWindow() {
        this.d = false;
        findViewById(R.id.animation_description).removeCallbacks(this.c);
        super.onDetachedFromWindow();
    }

    public void onAttachedToWindow() {
        this.d = true;
        super.onAttachedToWindow();
    }

    protected void onStop() {
        super.onStop();
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
    }

    public void onShow(DialogInterface dialogInterface) {
        if (this.a != null) {
            this.a.a();
            this.c = new Runnable(this) {
                final /* synthetic */ a a;

                {
                    this.a = r1;
                }

                public final void run() {
                    if (this.a.d) {
                        this.a.dismiss();
                    }
                }
            };
            findViewById(R.id.animation_description).postDelayed(this.c, 4000);
        }
    }
}
