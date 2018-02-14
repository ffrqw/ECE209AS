package com.instabug.library.internal.a;

import android.os.Build;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

class c {
    c() {
    }

    private static boolean c() {
        try {
            return new File("/system/app/Superuser.apk").exists();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean d() {
        ArrayList arrayList = new ArrayList();
        try {
            Process exec = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(exec.getOutputStream()));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            while (true) {
                try {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    InstabugSDKLogger.d(c.class, "SHELL --> Line received: " + readLine);
                    arrayList.add(readLine);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            InstabugSDKLogger.d(c.class, "SHELL --> Full response was: " + arrayList);
            if (arrayList.size() != 0) {
                return true;
            }
            return false;
        } catch (Exception e2) {
            return false;
        }
    }

    public static boolean a() {
        boolean z;
        String str = Build.TAGS;
        if (str == null || !str.contains("test-keys")) {
            z = false;
        } else {
            z = true;
        }
        if (z || c() || d()) {
            return true;
        }
        return false;
    }
}
