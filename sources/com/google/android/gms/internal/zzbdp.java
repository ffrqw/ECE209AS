package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public interface zzbdp {
    void connect();

    void disconnect();

    void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    boolean isConnected();

    <A extends zzb, T extends zzbay<? extends Result, A>> T zze(T t);
}
