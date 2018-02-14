package com.instabug.library.internal.module;

import android.content.Context;
import com.instabug.library.c;
import com.instabug.library.internal.a.b;
import com.instabug.library.model.f;
import com.instabug.library.u;
import com.instabug.library.util.d;
import com.instabug.library.w;
import java.lang.Thread.UncaughtExceptionHandler;

public final class a {
    private w a;

    public static b a(Context context) {
        return new b(context);
    }

    public final w b() {
        if (this.a == null) {
            this.a = new w();
        }
        return this.a;
    }

    public final c a(f fVar, com.instabug.library.internal.a.a aVar, u uVar) {
        final UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        return new c(fVar, uVar, aVar, new d(this) {
            final /* synthetic */ a b;

            public final void a(Thread thread, Throwable th) {
                defaultUncaughtExceptionHandler.uncaughtException(thread, th);
            }
        });
    }
}
