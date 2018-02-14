package android.support.v7.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.TintContextWrapper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.view.View.OnClickListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

final class AppCompatViewInflater {
    private static final String[] sClassPrefixList = new String[]{"android.widget.", "android.view.", "android.webkit."};
    private static final Map<String, Constructor<? extends View>> sConstructorMap = new ArrayMap();
    private static final Class<?>[] sConstructorSignature = new Class[]{Context.class, AttributeSet.class};
    private static final int[] sOnClickAttrs = new int[]{16843375};
    private final Object[] mConstructorArgs = new Object[2];

    private static class DeclaredOnClickListener implements OnClickListener {
        private final View mHostView;
        private final String mMethodName;
        private Context mResolvedContext;
        private Method mResolvedMethod;

        public DeclaredOnClickListener(View hostView, String methodName) {
            this.mHostView = hostView;
            this.mMethodName = methodName;
        }

        public final void onClick(View v) {
            if (this.mResolvedMethod == null) {
                String str;
                Context context = this.mHostView.getContext();
                while (context != null) {
                    try {
                        if (!context.isRestricted()) {
                            Method method = context.getClass().getMethod(this.mMethodName, new Class[]{View.class});
                            if (method != null) {
                                this.mResolvedMethod = method;
                                this.mResolvedContext = context;
                            }
                        }
                    } catch (NoSuchMethodException e) {
                    }
                    if (context instanceof ContextWrapper) {
                        context = ((ContextWrapper) context).getBaseContext();
                    } else {
                        context = null;
                    }
                }
                int id = this.mHostView.getId();
                if (id == -1) {
                    str = "";
                } else {
                    str = " with id '" + this.mHostView.getContext().getResources().getResourceEntryName(id) + "'";
                }
                throw new IllegalStateException("Could not find method " + this.mMethodName + "(View) in a parent or ancestor Context for android:onClick attribute defined on view " + this.mHostView.getClass() + str);
            }
            try {
                this.mResolvedMethod.invoke(this.mResolvedContext, new Object[]{v});
            } catch (IllegalAccessException e2) {
                throw new IllegalStateException("Could not execute non-public method for android:onClick", e2);
            } catch (InvocationTargetException e3) {
                throw new IllegalStateException("Could not execute method for android:onClick", e3);
            }
        }
    }

    AppCompatViewInflater() {
    }

    public final View createView(View parent, String name, Context context, AttributeSet attrs, boolean inheritContext, boolean readAndroidTheme, boolean readAppTheme, boolean wrapContext) {
        int resourceId;
        Context originalContext = context;
        if (inheritContext && parent != null) {
            context = parent.getContext();
        }
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.View, 0, 0);
        if (readAndroidTheme) {
            resourceId = obtainStyledAttributes.getResourceId(R.styleable.View_android_theme, 0);
        } else {
            resourceId = 0;
        }
        if (resourceId == 0) {
            resourceId = obtainStyledAttributes.getResourceId(R.styleable.View_theme, 0);
            if (resourceId != 0) {
                Log.i("AppCompatViewInflater", "app:theme is now deprecated. Please move to using android:theme instead.");
            }
        }
        int i = resourceId;
        obtainStyledAttributes.recycle();
        if (!(i == 0 || ((context instanceof ContextThemeWrapper) && ((ContextThemeWrapper) context).getThemeResId() == i))) {
            context = new ContextThemeWrapper(context, i);
        }
        if (wrapContext) {
            context = TintContextWrapper.wrap(context);
        }
        View view = null;
        resourceId = -1;
        switch (name.hashCode()) {
            case -1946472170:
                if (name.equals("RatingBar")) {
                    resourceId = 11;
                    break;
                }
                break;
            case -1455429095:
                if (name.equals("CheckedTextView")) {
                    resourceId = 8;
                    break;
                }
                break;
            case -1346021293:
                if (name.equals("MultiAutoCompleteTextView")) {
                    resourceId = 10;
                    break;
                }
                break;
            case -938935918:
                if (name.equals("TextView")) {
                    resourceId = 0;
                    break;
                }
                break;
            case -937446323:
                if (name.equals("ImageButton")) {
                    resourceId = 5;
                    break;
                }
                break;
            case -658531749:
                if (name.equals("SeekBar")) {
                    resourceId = 12;
                    break;
                }
                break;
            case -339785223:
                if (name.equals("Spinner")) {
                    resourceId = 4;
                    break;
                }
                break;
            case 776382189:
                if (name.equals("RadioButton")) {
                    resourceId = 7;
                    break;
                }
                break;
            case 1125864064:
                if (name.equals("ImageView")) {
                    resourceId = 1;
                    break;
                }
                break;
            case 1413872058:
                if (name.equals("AutoCompleteTextView")) {
                    resourceId = 9;
                    break;
                }
                break;
            case 1601505219:
                if (name.equals("CheckBox")) {
                    resourceId = 6;
                    break;
                }
                break;
            case 1666676343:
                if (name.equals("EditText")) {
                    resourceId = 3;
                    break;
                }
                break;
            case 2001146706:
                if (name.equals("Button")) {
                    resourceId = 2;
                    break;
                }
                break;
        }
        switch (resourceId) {
            case 0:
                view = new AppCompatTextView(context, attrs);
                break;
            case 1:
                view = new AppCompatImageView(context, attrs);
                break;
            case 2:
                view = new AppCompatButton(context, attrs);
                break;
            case 3:
                view = new AppCompatEditText(context, attrs);
                break;
            case 4:
                view = new AppCompatSpinner(context, attrs);
                break;
            case 5:
                view = new AppCompatImageButton(context, attrs);
                break;
            case 6:
                view = new AppCompatCheckBox(context, attrs);
                break;
            case 7:
                view = new AppCompatRadioButton(context, attrs);
                break;
            case 8:
                view = new AppCompatCheckedTextView(context, attrs);
                break;
            case 9:
                view = new AppCompatAutoCompleteTextView(context, attrs);
                break;
            case 10:
                view = new AppCompatMultiAutoCompleteTextView(context, attrs);
                break;
            case 11:
                view = new AppCompatRatingBar(context, attrs);
                break;
            case 12:
                view = new AppCompatSeekBar(context, attrs);
                break;
        }
        if (view == null && originalContext != context) {
            view = createViewFromTag(context, name, attrs);
        }
        if (view != null) {
            Context context2 = view.getContext();
            if ((context2 instanceof ContextWrapper) && (VERSION.SDK_INT < 15 || ViewCompat.hasOnClickListeners(view))) {
                TypedArray obtainStyledAttributes2 = context2.obtainStyledAttributes(attrs, sOnClickAttrs);
                String string = obtainStyledAttributes2.getString(0);
                if (string != null) {
                    view.setOnClickListener(new DeclaredOnClickListener(view, string));
                }
                obtainStyledAttributes2.recycle();
            }
        }
        return view;
    }

    private View createViewFromTag(Context context, String name, AttributeSet attrs) {
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }
        try {
            this.mConstructorArgs[0] = context;
            this.mConstructorArgs[1] = attrs;
            View view;
            if (-1 == name.indexOf(46)) {
                for (int i = 0; i < 3; i++) {
                    view = createView(context, name, sClassPrefixList[i]);
                    if (view != null) {
                        return view;
                    }
                }
                this.mConstructorArgs[0] = null;
                this.mConstructorArgs[1] = null;
                return null;
            }
            view = createView(context, name, null);
            this.mConstructorArgs[0] = null;
            this.mConstructorArgs[1] = null;
            return view;
        } catch (Exception e) {
            return null;
        } finally {
            this.mConstructorArgs[0] = null;
            this.mConstructorArgs[1] = null;
        }
    }

    private View createView(Context context, String name, String prefix) throws ClassNotFoundException, InflateException {
        Constructor<? extends View> constructor = (Constructor) sConstructorMap.get(name);
        if (constructor == null) {
            try {
                constructor = context.getClassLoader().loadClass(prefix != null ? prefix + name : name).asSubclass(View.class).getConstructor(sConstructorSignature);
                sConstructorMap.put(name, constructor);
            } catch (Exception e) {
                return null;
            }
        }
        constructor.setAccessible(true);
        return (View) constructor.newInstance(this.mConstructorArgs);
    }
}
