package rx.internal.util.unsafe;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

public final class UnsafeAccess {
    public static final Unsafe UNSAFE;

    static {
        Unsafe u = null;
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            u = (Unsafe) field.get(null);
        } catch (Throwable th) {
        }
        UNSAFE = u;
    }

    public static final boolean isUnsafeAvailable() {
        return UNSAFE != null;
    }
}
