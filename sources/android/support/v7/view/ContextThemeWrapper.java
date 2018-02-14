package android.support.v7.view;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources.Theme;
import android.view.LayoutInflater;
import com.rachio.iro.R;

public final class ContextThemeWrapper extends ContextWrapper {
    private LayoutInflater mInflater;
    private Theme mTheme;
    private int mThemeResource;

    public ContextThemeWrapper(Context base, int themeResId) {
        super(base);
        this.mThemeResource = themeResId;
    }

    public ContextThemeWrapper(Context base, Theme theme) {
        super(base);
        this.mTheme = theme;
    }

    public final void setTheme(int resid) {
        if (this.mThemeResource != resid) {
            this.mThemeResource = resid;
            initializeTheme();
        }
    }

    public final int getThemeResId() {
        return this.mThemeResource;
    }

    public final Theme getTheme() {
        if (this.mTheme != null) {
            return this.mTheme;
        }
        if (this.mThemeResource == 0) {
            this.mThemeResource = R.style.Theme.AppCompat.Light;
        }
        initializeTheme();
        return this.mTheme;
    }

    public final Object getSystemService(String name) {
        if (!"layout_inflater".equals(name)) {
            return getBaseContext().getSystemService(name);
        }
        if (this.mInflater == null) {
            this.mInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
        }
        return this.mInflater;
    }

    private void initializeTheme() {
        if (this.mTheme == null) {
            this.mTheme = getResources().newTheme();
            Theme theme = getBaseContext().getTheme();
            if (theme != null) {
                this.mTheme.setTo(theme);
            }
        }
        this.mTheme.applyStyle(this.mThemeResource, true);
    }

    public final AssetManager getAssets() {
        return getResources().getAssets();
    }
}
