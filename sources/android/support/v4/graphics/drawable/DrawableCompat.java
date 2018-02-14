package android.support.v4.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class DrawableCompat {
    static final DrawableImpl IMPL;

    interface DrawableImpl {
        void applyTheme(Drawable drawable, Theme theme);

        boolean canApplyTheme(Drawable drawable);

        int getAlpha(Drawable drawable);

        ColorFilter getColorFilter(Drawable drawable);

        int getLayoutDirection(Drawable drawable);

        void inflate(Drawable drawable, Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Theme theme) throws IOException, XmlPullParserException;

        boolean isAutoMirrored(Drawable drawable);

        void jumpToCurrentState(Drawable drawable);

        void setAutoMirrored(Drawable drawable, boolean z);

        void setHotspot(Drawable drawable, float f, float f2);

        void setHotspotBounds(Drawable drawable, int i, int i2, int i3, int i4);

        boolean setLayoutDirection(Drawable drawable, int i);

        void setTint(Drawable drawable, int i);

        void setTintList(Drawable drawable, ColorStateList colorStateList);

        void setTintMode(Drawable drawable, Mode mode);

        Drawable wrap(Drawable drawable);
    }

    static class BaseDrawableImpl implements DrawableImpl {
        BaseDrawableImpl() {
        }

        public void jumpToCurrentState(Drawable drawable) {
        }

        public void setAutoMirrored(Drawable drawable, boolean mirrored) {
        }

        public boolean isAutoMirrored(Drawable drawable) {
            return false;
        }

        public void setHotspot(Drawable drawable, float x, float y) {
        }

        public void setHotspotBounds(Drawable drawable, int left, int top, int right, int bottom) {
        }

        public boolean setLayoutDirection(Drawable drawable, int layoutDirection) {
            return false;
        }

        public int getLayoutDirection(Drawable drawable) {
            return 0;
        }

        public int getAlpha(Drawable drawable) {
            return 0;
        }

        public void applyTheme(Drawable drawable, Theme t) {
        }

        public boolean canApplyTheme(Drawable drawable) {
            return false;
        }

        public ColorFilter getColorFilter(Drawable drawable) {
            return null;
        }

        public void setTint(Drawable drawable, int tint) {
            if (drawable instanceof TintAwareDrawable) {
                ((TintAwareDrawable) drawable).setTint(tint);
            }
        }

        public void setTintList(Drawable drawable, ColorStateList tint) {
            if (drawable instanceof TintAwareDrawable) {
                ((TintAwareDrawable) drawable).setTintList(tint);
            }
        }

        public void setTintMode(Drawable drawable, Mode tintMode) {
            if (drawable instanceof TintAwareDrawable) {
                ((TintAwareDrawable) drawable).setTintMode(tintMode);
            }
        }

        public Drawable wrap(Drawable drawable) {
            return !(drawable instanceof TintAwareDrawable) ? new DrawableWrapperGingerbread(drawable) : drawable;
        }

        public void inflate(Drawable drawable, Resources res, XmlPullParser parser, AttributeSet attrs, Theme t) throws IOException, XmlPullParserException {
            drawable.inflate(res, parser, attrs);
        }
    }

    static class HoneycombDrawableImpl extends BaseDrawableImpl {
        HoneycombDrawableImpl() {
        }

        public final void jumpToCurrentState(Drawable drawable) {
            drawable.jumpToCurrentState();
        }

        public Drawable wrap(Drawable drawable) {
            return !(drawable instanceof TintAwareDrawable) ? new DrawableWrapperHoneycomb(drawable) : drawable;
        }
    }

    static class JellybeanMr1DrawableImpl extends HoneycombDrawableImpl {
        JellybeanMr1DrawableImpl() {
        }

        public boolean setLayoutDirection(Drawable drawable, int layoutDirection) {
            return DrawableCompatJellybeanMr1.setLayoutDirection(drawable, layoutDirection);
        }

        public int getLayoutDirection(Drawable drawable) {
            int dir = DrawableCompatJellybeanMr1.getLayoutDirection(drawable);
            return dir >= 0 ? dir : 0;
        }
    }

    static class KitKatDrawableImpl extends JellybeanMr1DrawableImpl {
        KitKatDrawableImpl() {
        }

        public final void setAutoMirrored(Drawable drawable, boolean mirrored) {
            drawable.setAutoMirrored(mirrored);
        }

        public final boolean isAutoMirrored(Drawable drawable) {
            return drawable.isAutoMirrored();
        }

        public Drawable wrap(Drawable drawable) {
            return !(drawable instanceof TintAwareDrawable) ? new DrawableWrapperKitKat(drawable) : drawable;
        }

        public final int getAlpha(Drawable drawable) {
            return drawable.getAlpha();
        }
    }

    static class LollipopDrawableImpl extends KitKatDrawableImpl {
        LollipopDrawableImpl() {
        }

        public final void setHotspot(Drawable drawable, float x, float y) {
            drawable.setHotspot(x, y);
        }

        public final void setHotspotBounds(Drawable drawable, int left, int top, int right, int bottom) {
            drawable.setHotspotBounds(left, top, right, bottom);
        }

        public final void setTint(Drawable drawable, int tint) {
            drawable.setTint(tint);
        }

        public final void setTintList(Drawable drawable, ColorStateList tint) {
            drawable.setTintList(tint);
        }

        public final void setTintMode(Drawable drawable, Mode tintMode) {
            drawable.setTintMode(tintMode);
        }

        public Drawable wrap(Drawable drawable) {
            return !(drawable instanceof TintAwareDrawable) ? new DrawableWrapperLollipop(drawable) : drawable;
        }

        public final void applyTheme(Drawable drawable, Theme t) {
            drawable.applyTheme(t);
        }

        public final boolean canApplyTheme(Drawable drawable) {
            return drawable.canApplyTheme();
        }

        public final ColorFilter getColorFilter(Drawable drawable) {
            return drawable.getColorFilter();
        }

        public final void inflate(Drawable drawable, Resources res, XmlPullParser parser, AttributeSet attrs, Theme t) throws IOException, XmlPullParserException {
            drawable.inflate(res, parser, attrs, t);
        }
    }

    static class MDrawableImpl extends LollipopDrawableImpl {
        MDrawableImpl() {
        }

        public final Drawable wrap(Drawable drawable) {
            return drawable;
        }

        public final boolean setLayoutDirection(Drawable drawable, int layoutDirection) {
            return drawable.setLayoutDirection(layoutDirection);
        }

        public final int getLayoutDirection(Drawable drawable) {
            return drawable.getLayoutDirection();
        }
    }

    static {
        int version = VERSION.SDK_INT;
        if (version >= 23) {
            IMPL = new MDrawableImpl();
        } else if (version >= 21) {
            IMPL = new LollipopDrawableImpl();
        } else if (version >= 19) {
            IMPL = new KitKatDrawableImpl();
        } else if (version >= 17) {
            IMPL = new JellybeanMr1DrawableImpl();
        } else if (version >= 11) {
            IMPL = new HoneycombDrawableImpl();
        } else {
            IMPL = new BaseDrawableImpl();
        }
    }

    public static void jumpToCurrentState(Drawable drawable) {
        IMPL.jumpToCurrentState(drawable);
    }

    public static void setAutoMirrored(Drawable drawable, boolean mirrored) {
        IMPL.setAutoMirrored(drawable, mirrored);
    }

    public static boolean isAutoMirrored(Drawable drawable) {
        return IMPL.isAutoMirrored(drawable);
    }

    public static void setHotspot(Drawable drawable, float x, float y) {
        IMPL.setHotspot(drawable, x, y);
    }

    public static void setHotspotBounds(Drawable drawable, int left, int top, int right, int bottom) {
        IMPL.setHotspotBounds(drawable, left, top, right, bottom);
    }

    public static void setTint(Drawable drawable, int tint) {
        IMPL.setTint(drawable, tint);
    }

    public static void setTintList(Drawable drawable, ColorStateList tint) {
        IMPL.setTintList(drawable, tint);
    }

    public static void setTintMode(Drawable drawable, Mode tintMode) {
        IMPL.setTintMode(drawable, tintMode);
    }

    public static int getAlpha(Drawable drawable) {
        return IMPL.getAlpha(drawable);
    }

    public static void applyTheme(Drawable drawable, Theme t) {
        IMPL.applyTheme(drawable, t);
    }

    public static boolean canApplyTheme(Drawable drawable) {
        return IMPL.canApplyTheme(drawable);
    }

    public static ColorFilter getColorFilter(Drawable drawable) {
        return IMPL.getColorFilter(drawable);
    }

    public static void inflate(Drawable drawable, Resources res, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        IMPL.inflate(drawable, res, parser, attrs, theme);
    }

    public static Drawable wrap(Drawable drawable) {
        return IMPL.wrap(drawable);
    }

    public static <T extends Drawable> T unwrap(Drawable drawable) {
        if (drawable instanceof DrawableWrapper) {
            return ((DrawableWrapper) drawable).getWrappedDrawable();
        }
        return drawable;
    }

    public static boolean setLayoutDirection(Drawable drawable, int layoutDirection) {
        return IMPL.setLayoutDirection(drawable, layoutDirection);
    }

    public static int getLayoutDirection(Drawable drawable) {
        return IMPL.getLayoutDirection(drawable);
    }
}
