package android.support.v7.app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.rachio.iro.R;

public class AppCompatDialog extends Dialog implements AppCompatCallback {
    private AppCompatDelegate mDelegate;

    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        super.onCreate(savedInstanceState);
        getDelegate().onCreate(savedInstanceState);
    }

    public void setContentView(int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    public void setContentView(View view, LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    public View findViewById(int id) {
        return getDelegate().findViewById(id);
    }

    public void setTitle(CharSequence title) {
        super.setTitle(title);
        getDelegate().setTitle(title);
    }

    public void setTitle(int titleId) {
        super.setTitle(titleId);
        getDelegate().setTitle(getContext().getString(titleId));
    }

    public void addContentView(View view, LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    public final AppCompatDelegate getDelegate() {
        if (this.mDelegate == null) {
            this.mDelegate = AppCompatDelegate.create((Dialog) this, (AppCompatCallback) this);
        }
        return this.mDelegate;
    }

    public AppCompatDialog(Context context, int theme) {
        if (theme == 0) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.dialogTheme, typedValue, true);
            theme = typedValue.resourceId;
        }
        super(context, theme);
        getDelegate().onCreate(null);
        getDelegate().applyDayNight();
    }
}
