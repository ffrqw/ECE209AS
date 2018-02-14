package com.instabug.library;

import android.content.Context;
import java.util.ArrayList;

public final class u {
    private static u a;

    private u(Context context) {
        s.a(context);
    }

    public static void a(Context context) {
        a = new u(context);
    }

    public static u a() {
        return a;
    }

    public static String H() {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList l = q.a().l();
        if (l != null && l.size() > 0) {
            int size = l.size();
            for (int i = 0; i < size; i++) {
                stringBuilder.append((String) l.get(i));
                if (i != size - 1) {
                    stringBuilder.append(", ");
                }
            }
        }
        return stringBuilder.toString();
    }
}
