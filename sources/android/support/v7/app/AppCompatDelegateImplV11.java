package android.support.v7.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;

@TargetApi(11)
class AppCompatDelegateImplV11 extends AppCompatDelegateImplV9 {
    AppCompatDelegateImplV11(Context context, Window window, AppCompatCallback callback) {
        super(context, window, callback);
    }

    final View callActivityOnCreateView$1fef4371(String name, Context context, AttributeSet attrs) {
        return null;
    }
}
