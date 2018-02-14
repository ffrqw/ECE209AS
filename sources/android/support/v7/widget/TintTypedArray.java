package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.util.TypedValue;

public final class TintTypedArray {
    private final Context mContext;
    private TypedValue mTypedValue;
    private final TypedArray mWrapped;

    public static TintTypedArray obtainStyledAttributes(Context context, AttributeSet set, int[] attrs) {
        return new TintTypedArray(context, context.obtainStyledAttributes(set, attrs));
    }

    public static TintTypedArray obtainStyledAttributes(Context context, AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes) {
        return new TintTypedArray(context, context.obtainStyledAttributes(set, attrs, defStyleAttr, defStyleRes));
    }

    public static TintTypedArray obtainStyledAttributes(Context context, int resid, int[] attrs) {
        return new TintTypedArray(context, context.obtainStyledAttributes(resid, attrs));
    }

    private TintTypedArray(Context context, TypedArray array) {
        this.mContext = context;
        this.mWrapped = array;
    }

    public final Drawable getDrawable(int index) {
        if (this.mWrapped.hasValue(index)) {
            int resourceId = this.mWrapped.getResourceId(index, 0);
            if (resourceId != 0) {
                return AppCompatResources.getDrawable(this.mContext, resourceId);
            }
        }
        return this.mWrapped.getDrawable(index);
    }

    public final Drawable getDrawableIfKnown(int index) {
        if (this.mWrapped.hasValue(index)) {
            int resourceId = this.mWrapped.getResourceId(index, 0);
            if (resourceId != 0) {
                return AppCompatDrawableManager.get().getDrawable(this.mContext, resourceId, true);
            }
        }
        return null;
    }

    public final int length() {
        return this.mWrapped.length();
    }

    public final int getIndexCount() {
        return this.mWrapped.getIndexCount();
    }

    public final int getIndex(int at) {
        return this.mWrapped.getIndex(at);
    }

    public final Resources getResources() {
        return this.mWrapped.getResources();
    }

    public final CharSequence getText(int index) {
        return this.mWrapped.getText(index);
    }

    public final String getString(int index) {
        return this.mWrapped.getString(index);
    }

    public final String getNonResourceString(int index) {
        return this.mWrapped.getNonResourceString(index);
    }

    public final boolean getBoolean(int index, boolean defValue) {
        return this.mWrapped.getBoolean(index, defValue);
    }

    public final int getInt(int index, int defValue) {
        return this.mWrapped.getInt(index, defValue);
    }

    public final float getFloat(int index, float defValue) {
        return this.mWrapped.getFloat(index, defValue);
    }

    public final int getColor(int index, int defValue) {
        return this.mWrapped.getColor(index, defValue);
    }

    public final ColorStateList getColorStateList(int index) {
        if (this.mWrapped.hasValue(index)) {
            int resourceId = this.mWrapped.getResourceId(index, 0);
            if (resourceId != 0) {
                ColorStateList value = AppCompatResources.getColorStateList(this.mContext, resourceId);
                if (value != null) {
                    return value;
                }
            }
        }
        return this.mWrapped.getColorStateList(index);
    }

    public final int getInteger(int index, int defValue) {
        return this.mWrapped.getInteger(index, defValue);
    }

    public final float getDimension(int index, float defValue) {
        return this.mWrapped.getDimension(index, defValue);
    }

    public final int getDimensionPixelOffset(int index, int defValue) {
        return this.mWrapped.getDimensionPixelOffset(index, defValue);
    }

    public final int getDimensionPixelSize(int index, int defValue) {
        return this.mWrapped.getDimensionPixelSize(index, defValue);
    }

    public final int getLayoutDimension(int index, String name) {
        return this.mWrapped.getLayoutDimension(index, name);
    }

    public final int getLayoutDimension(int index, int defValue) {
        return this.mWrapped.getLayoutDimension(index, defValue);
    }

    public final float getFraction(int index, int base, int pbase, float defValue) {
        return this.mWrapped.getFraction(index, base, pbase, defValue);
    }

    public final int getResourceId(int index, int defValue) {
        return this.mWrapped.getResourceId(index, defValue);
    }

    public final CharSequence[] getTextArray(int index) {
        return this.mWrapped.getTextArray(index);
    }

    public final boolean getValue(int index, TypedValue outValue) {
        return this.mWrapped.getValue(index, outValue);
    }

    public final int getType(int index) {
        if (VERSION.SDK_INT >= 21) {
            return this.mWrapped.getType(index);
        }
        if (this.mTypedValue == null) {
            this.mTypedValue = new TypedValue();
        }
        this.mWrapped.getValue(index, this.mTypedValue);
        return this.mTypedValue.type;
    }

    public final boolean hasValue(int index) {
        return this.mWrapped.hasValue(index);
    }

    public final TypedValue peekValue(int index) {
        return this.mWrapped.peekValue(index);
    }

    public final String getPositionDescription() {
        return this.mWrapped.getPositionDescription();
    }

    public final void recycle() {
        this.mWrapped.recycle();
    }

    public final int getChangingConfigurations() {
        return this.mWrapped.getChangingConfigurations();
    }
}
