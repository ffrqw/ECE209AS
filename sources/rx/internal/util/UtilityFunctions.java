package rx.internal.util;

import rx.functions.Func0;
import rx.functions.Func1;

public final class UtilityFunctions {
    private static final NullFunction NULL_FUNCTION = new NullFunction();

    private static final class NullFunction<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R> implements Func0<R>, Func1<T0, R> {
        private NullFunction() {
        }

        public final R call() {
            return null;
        }

        public final R call(T0 t0) {
            return null;
        }
    }

    public static <T> Func1<T, T> identity() {
        return new Func1<T, T>() {
            public final T call(T o) {
                return o;
            }
        };
    }
}
