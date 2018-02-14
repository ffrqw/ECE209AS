package android.support.v7.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.Window;
import android.view.Window.Callback;
import java.util.List;

@TargetApi(24)
final class AppCompatDelegateImplN extends AppCompatDelegateImplV23 {

    class AppCompatWindowCallbackN extends AppCompatWindowCallbackV23 {
        AppCompatWindowCallbackN(Callback callback) {
            super(callback);
        }

        public final void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, Menu menu, int deviceId) {
            PanelFeatureState panel = AppCompatDelegateImplN.this.getPanelState$20476cf5(0);
            if (panel == null || panel.menu == null) {
                super.onProvideKeyboardShortcuts(data, menu, deviceId);
            } else {
                super.onProvideKeyboardShortcuts(data, panel.menu, deviceId);
            }
        }
    }

    AppCompatDelegateImplN(Context context, Window window, AppCompatCallback callback) {
        super(context, window, callback);
    }

    final Callback wrapWindowCallback(Callback callback) {
        return new AppCompatWindowCallbackN(callback);
    }
}
