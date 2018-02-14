package com.instabug.library;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.instabug.library.util.InstabugSDKLogger;
import com.rachio.iro.R;

public abstract class h extends Fragment {
    private View a;
    private Activity b;
    private boolean c;

    protected abstract void a();

    protected abstract void a(Bundle bundle);

    protected abstract int b();

    protected abstract void b(Bundle bundle);

    protected abstract String c();

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.b = activity;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        InstabugSDKLogger.v(this, "onCreateView called");
        if (getArguments() != null) {
            InstabugSDKLogger.v(this, "Arguments found, calling consumeNewInstanceSavedArguments with " + getArguments());
            a();
        }
        this.c = false;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        InstabugSDKLogger.v(this, "onCreateView called");
        this.a = layoutInflater.inflate(b(), viewGroup, false);
        a(c());
        return this.a;
    }

    public void onViewCreated(View view, Bundle bundle) {
        InstabugSDKLogger.v(this, "onViewCreated called");
        super.onViewCreated(view, bundle);
        if (bundle != null) {
            InstabugSDKLogger.v(this, "savedInstanceState found, calling restoreState");
            b(bundle);
            this.c = true;
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        InstabugSDKLogger.v(this, "onSaveInstanceState called, calling saveState");
        a(bundle);
    }

    public void onPause() {
        super.onPause();
        InstabugSDKLogger.v(this, "onPause called, calling saveState");
    }

    public void onResume() {
        super.onResume();
        InstabugSDKLogger.v(this, "onResume called, calling saveState");
    }

    public final boolean d() {
        return this.c;
    }

    public final Activity e() {
        InstabugSDKLogger.v(this, "Returning preserved activity " + this.b);
        return this.b;
    }

    public final void a(String str) {
        if (this.a == null) {
            InstabugSDKLogger.v(this, "Calling setTitle before inflating the view! Ignoring call");
            return;
        }
        TextView textView = (TextView) this.a.findViewById(R.id.instabug_fragment_title);
        if (textView != null) {
            InstabugSDKLogger.v(this, "Setting fragment title to \"" + str + "\"");
            textView.setText(str);
            return;
        }
        InstabugSDKLogger.v(this, "instabug_fragment_title wasn't found, make sure your layout.xml contains it");
    }
}
