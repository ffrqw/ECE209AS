package com.instabug.library;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.instabug.library.IBGCustomTextPlaceHolder.Key;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.c;
import com.instabug.library.util.f;
import com.instabug.library.util.l;
import com.rachio.iro.R;

public final class p extends h {
    private a a;

    public interface a {
        void e();
    }

    public final void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Key key = Key.REPORT_SUCCESSFULLY_SENT;
        String a = new f(getActivity().getApplicationContext()).a();
        if (a == null) {
            InstabugSDKLogger.w(this, "It seems app:name isn't defined in your manifest. Using a generic name instead");
        }
        Object[] objArr = new Object[1];
        if (a == null) {
            a = "App";
        }
        objArr[0] = a;
        ((TextView) view.findViewById(R.id.instabug_txt_success_note)).setText(l.a(key, getString(R.string.instabug_str_thank_you, objArr)));
        c.a((ImageView) view.findViewById(R.id.instabug_img_success));
        new Handler().postDelayed(new Runnable(this) {
            final /* synthetic */ p a;

            {
                this.a = r1;
            }

            public final void run() {
                if (this.a.a != null) {
                    this.a.a.e();
                }
            }
        }, 3000);
    }

    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.a = (a) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement InstabugSuccessFragment.Callbacks");
        }
    }

    public final void onDetach() {
        super.onDetach();
        this.a = null;
    }

    protected final void a(Bundle bundle) {
    }

    protected final void b(Bundle bundle) {
    }

    protected final int b() {
        return R.layout.instabug_lyt_success;
    }

    protected final String c() {
        return getString(R.string.instabug_str_empty);
    }

    protected final void a() {
    }
}
