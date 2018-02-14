package rx.internal.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class PlatformDependent {
    private static final boolean IS_ANDROID = isAndroid0();

    public static boolean isAndroid() {
        return IS_ANDROID;
    }

    private static boolean isAndroid0() {
        try {
            ClassLoader systemClassLoader;
            String str = "android.app.Application";
            if (System.getSecurityManager() == null) {
                systemClassLoader = ClassLoader.getSystemClassLoader();
            } else {
                systemClassLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                    public final /* bridge */ /* synthetic */ Object run() {
                        return ClassLoader.getSystemClassLoader();
                    }
                });
            }
            Class.forName(str, false, systemClassLoader);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
