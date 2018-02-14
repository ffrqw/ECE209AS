package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.Param;
import com.google.android.gms.measurement.AppMeasurement.UserProperty;
import com.j256.ormlite.stmt.query.SimpleComparison;

public final class zzcfj extends zzchj {
    private static String[] zzbqI = new String[26];
    private static String[] zzbqJ = new String[33];
    private static String[] zzbqK = new String[6];

    static {
        String[] strArr = Event.zzbof;
        strArr = Param.zzboh;
        strArr = UserProperty.zzbom;
    }

    zzcfj(zzcgl zzcgl) {
        super(zzcgl);
    }

    private static String zza(String str, String[] strArr, String[] strArr2, String[] strArr3) {
        boolean z = true;
        int i = 0;
        zzbo.zzu(strArr);
        zzbo.zzu(strArr2);
        zzbo.zzu(strArr3);
        zzbo.zzaf(strArr.length == strArr2.length);
        if (strArr.length != strArr3.length) {
            z = false;
        }
        zzbo.zzaf(z);
        while (i < strArr.length) {
            if (zzcjl.zzR(str, strArr[i])) {
                synchronized (strArr3) {
                    if (strArr3[i] == null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(strArr2[i]);
                        stringBuilder.append("(");
                        stringBuilder.append(strArr[i]);
                        stringBuilder.append(")");
                        strArr3[i] = stringBuilder.toString();
                    }
                    str = strArr3[i];
                }
                return str;
            }
            i++;
        }
        return str;
    }

    private static void zza(StringBuilder stringBuilder, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            stringBuilder.append("  ");
        }
    }

    private final void zza(StringBuilder stringBuilder, int i, zzcjo zzcjo) {
        if (zzcjo != null) {
            zza(stringBuilder, i);
            stringBuilder.append("filter {\n");
            zza(stringBuilder, i, "complement", zzcjo.zzbuU);
            zza(stringBuilder, i, "param_name", zzdX(zzcjo.zzbuV));
            int i2 = i + 1;
            String str = "string_filter";
            zzcjr zzcjr = zzcjo.zzbuS;
            if (zzcjr != null) {
                zza(stringBuilder, i2);
                stringBuilder.append(str);
                stringBuilder.append(" {\n");
                if (zzcjr.zzbve != null) {
                    Object obj = "UNKNOWN_MATCH_TYPE";
                    switch (zzcjr.zzbve.intValue()) {
                        case 1:
                            obj = "REGEXP";
                            break;
                        case 2:
                            obj = "BEGINS_WITH";
                            break;
                        case 3:
                            obj = "ENDS_WITH";
                            break;
                        case 4:
                            obj = "PARTIAL";
                            break;
                        case 5:
                            obj = "EXACT";
                            break;
                        case 6:
                            obj = "IN_LIST";
                            break;
                    }
                    zza(stringBuilder, i2, "match_type", obj);
                }
                zza(stringBuilder, i2, "expression", zzcjr.zzbvf);
                zza(stringBuilder, i2, "case_sensitive", zzcjr.zzbvg);
                if (zzcjr.zzbvh.length > 0) {
                    zza(stringBuilder, i2 + 1);
                    stringBuilder.append("expression_list {\n");
                    for (String str2 : zzcjr.zzbvh) {
                        zza(stringBuilder, i2 + 2);
                        stringBuilder.append(str2);
                        stringBuilder.append("\n");
                    }
                    stringBuilder.append("}\n");
                }
                zza(stringBuilder, i2);
                stringBuilder.append("}\n");
            }
            zza(stringBuilder, i + 1, "number_filter", zzcjo.zzbuT);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, zzcjp zzcjp) {
        if (zzcjp != null) {
            zza(stringBuilder, i);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (zzcjp.zzbuW != null) {
                Object obj = "UNKNOWN_COMPARISON_TYPE";
                switch (zzcjp.zzbuW.intValue()) {
                    case 1:
                        obj = "LESS_THAN";
                        break;
                    case 2:
                        obj = "GREATER_THAN";
                        break;
                    case 3:
                        obj = "EQUAL";
                        break;
                    case 4:
                        obj = "BETWEEN";
                        break;
                }
                zza(stringBuilder, i, "comparison_type", obj);
            }
            zza(stringBuilder, i, "match_as_float", zzcjp.zzbuX);
            zza(stringBuilder, i, "comparison_value", zzcjp.zzbuY);
            zza(stringBuilder, i, "min_comparison_value", zzcjp.zzbuZ);
            zza(stringBuilder, i, "max_comparison_value", zzcjp.zzbva);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, zzcka zzcka) {
        int i2 = 0;
        if (zzcka != null) {
            int i3;
            int i4;
            zza(stringBuilder, 3);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (zzcka.zzbwf != null) {
                zza(stringBuilder, 4);
                stringBuilder.append("results: ");
                long[] jArr = zzcka.zzbwf;
                int length = jArr.length;
                i3 = 0;
                i4 = 0;
                while (i3 < length) {
                    Long valueOf = Long.valueOf(jArr[i3]);
                    int i5 = i4 + 1;
                    if (i4 != 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(valueOf);
                    i3++;
                    i4 = i5;
                }
                stringBuilder.append('\n');
            }
            if (zzcka.zzbwe != null) {
                zza(stringBuilder, 4);
                stringBuilder.append("status: ");
                long[] jArr2 = zzcka.zzbwe;
                int length2 = jArr2.length;
                i3 = 0;
                while (i2 < length2) {
                    Long valueOf2 = Long.valueOf(jArr2[i2]);
                    i4 = i3 + 1;
                    if (i3 != 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(valueOf2);
                    i2++;
                    i3 = i4;
                }
                stringBuilder.append('\n');
            }
            zza(stringBuilder, 3);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, Object obj) {
        if (obj != null) {
            zza(stringBuilder, i + 1);
            stringBuilder.append(str);
            stringBuilder.append(": ");
            stringBuilder.append(obj);
            stringBuilder.append('\n');
        }
    }

    private static void zza$583a27d2(StringBuilder stringBuilder, zzcjv[] zzcjvArr) {
        if (zzcjvArr != null) {
            for (zzcjv zzcjv : zzcjvArr) {
                if (zzcjv != null) {
                    zza(stringBuilder, 2);
                    stringBuilder.append("audience_membership {\n");
                    zza(stringBuilder, 2, "audience_id", zzcjv.zzbuI);
                    zza(stringBuilder, 2, "new_audience", zzcjv.zzbvu);
                    zza(stringBuilder, 2, "current_data", zzcjv.zzbvs);
                    zza(stringBuilder, 2, "previous_data", zzcjv.zzbvt);
                    zza(stringBuilder, 2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private final void zza$583a9c31(StringBuilder stringBuilder, zzcjw[] zzcjwArr) {
        if (zzcjwArr != null) {
            for (zzcjw zzcjw : zzcjwArr) {
                if (zzcjw != null) {
                    zza(stringBuilder, 2);
                    stringBuilder.append("event {\n");
                    zza(stringBuilder, 2, "name", zzdW(zzcjw.name));
                    zza(stringBuilder, 2, "timestamp_millis", zzcjw.zzbvx);
                    zza(stringBuilder, 2, "previous_timestamp_millis", zzcjw.zzbvy);
                    zza(stringBuilder, 2, "count", zzcjw.count);
                    zzcjx[] zzcjxArr = zzcjw.zzbvw;
                    if (zzcjxArr != null) {
                        for (zzcjx zzcjx : zzcjxArr) {
                            if (zzcjx != null) {
                                zza(stringBuilder, 3);
                                stringBuilder.append("param {\n");
                                zza(stringBuilder, 3, "name", zzdX(zzcjx.name));
                                zza(stringBuilder, 3, "string_value", zzcjx.zzaIF);
                                zza(stringBuilder, 3, "int_value", zzcjx.zzbvA);
                                zza(stringBuilder, 3, "double_value", zzcjx.zzbuB);
                                zza(stringBuilder, 3);
                                stringBuilder.append("}\n");
                            }
                        }
                    }
                    zza(stringBuilder, 2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private final void zza$583f27e7(StringBuilder stringBuilder, zzckb[] zzckbArr) {
        if (zzckbArr != null) {
            for (zzckb zzckb : zzckbArr) {
                if (zzckb != null) {
                    zza(stringBuilder, 2);
                    stringBuilder.append("user_property {\n");
                    zza(stringBuilder, 2, "set_timestamp_millis", zzckb.zzbwh);
                    zza(stringBuilder, 2, "name", zzdY(zzckb.name));
                    zza(stringBuilder, 2, "string_value", zzckb.zzaIF);
                    zza(stringBuilder, 2, "int_value", zzckb.zzbvA);
                    zza(stringBuilder, 2, "double_value", zzckb.zzbuB);
                    zza(stringBuilder, 2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private final String zzb(zzcew zzcew) {
        return zzcew == null ? null : !zzyw() ? zzcew.toString() : zzA(zzcew.zzyt());
    }

    private final boolean zzyw() {
        return this.zzboe.zzwF().zzz(3);
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    protected final String zzA(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        if (!zzyw()) {
            return bundle.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : bundle.keySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(", ");
            } else {
                stringBuilder.append("Bundle[{");
            }
            stringBuilder.append(zzdX(str));
            stringBuilder.append(SimpleComparison.EQUAL_TO_OPERATION);
            stringBuilder.append(bundle.get(str));
        }
        stringBuilder.append("}]");
        return stringBuilder.toString();
    }

    protected final String zza(zzceu zzceu) {
        if (zzceu == null) {
            return null;
        }
        if (!zzyw()) {
            return zzceu.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Event{appId='");
        stringBuilder.append(zzceu.mAppId);
        stringBuilder.append("', name='");
        stringBuilder.append(zzdW(zzceu.mName));
        stringBuilder.append("', params=");
        stringBuilder.append(zzb(zzceu.zzbpF));
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    protected final String zza(zzcjn zzcjn) {
        int i = 0;
        if (zzcjn == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nevent_filter {\n");
        zza(stringBuilder, 0, "filter_id", zzcjn.zzbuM);
        zza(stringBuilder, 0, "event_name", zzdW(zzcjn.zzbuN));
        zza(stringBuilder, 1, "event_count_filter", zzcjn.zzbuQ);
        stringBuilder.append("  filters {\n");
        zzcjo[] zzcjoArr = zzcjn.zzbuO;
        int length = zzcjoArr.length;
        while (i < length) {
            zza(stringBuilder, 2, zzcjoArr[i]);
            i++;
        }
        zza(stringBuilder, 1);
        stringBuilder.append("}\n}\n");
        return stringBuilder.toString();
    }

    protected final String zza(zzcjq zzcjq) {
        if (zzcjq == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nproperty_filter {\n");
        zza(stringBuilder, 0, "filter_id", zzcjq.zzbuM);
        zza(stringBuilder, 0, "property_name", zzdY(zzcjq.zzbvc));
        zza(stringBuilder, 1, zzcjq.zzbvd);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    protected final String zza(zzcjy zzcjy) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nbatch {\n");
        if (zzcjy.zzbvB != null) {
            for (zzcjz zzcjz : zzcjy.zzbvB) {
                if (!(zzcjz == null || zzcjz == null)) {
                    zza(stringBuilder, 1);
                    stringBuilder.append("bundle {\n");
                    zza(stringBuilder, 1, "protocol_version", zzcjz.zzbvD);
                    zza(stringBuilder, 1, "platform", zzcjz.zzbvL);
                    zza(stringBuilder, 1, "gmp_version", zzcjz.zzbvP);
                    zza(stringBuilder, 1, "uploading_gmp_version", zzcjz.zzbvQ);
                    zza(stringBuilder, 1, "config_version", zzcjz.zzbwb);
                    zza(stringBuilder, 1, "gmp_app_id", zzcjz.zzboQ);
                    zza(stringBuilder, 1, "app_id", zzcjz.zzaH);
                    zza(stringBuilder, 1, "app_version", zzcjz.zzbgW);
                    zza(stringBuilder, 1, "app_version_major", zzcjz.zzbvY);
                    zza(stringBuilder, 1, "firebase_instance_id", zzcjz.zzboY);
                    zza(stringBuilder, 1, "dev_cert_hash", zzcjz.zzbvU);
                    zza(stringBuilder, 1, "app_store", zzcjz.zzboR);
                    zza(stringBuilder, 1, "upload_timestamp_millis", zzcjz.zzbvG);
                    zza(stringBuilder, 1, "start_timestamp_millis", zzcjz.zzbvH);
                    zza(stringBuilder, 1, "end_timestamp_millis", zzcjz.zzbvI);
                    zza(stringBuilder, 1, "previous_bundle_start_timestamp_millis", zzcjz.zzbvJ);
                    zza(stringBuilder, 1, "previous_bundle_end_timestamp_millis", zzcjz.zzbvK);
                    zza(stringBuilder, 1, "app_instance_id", zzcjz.zzbvT);
                    zza(stringBuilder, 1, "resettable_device_id", zzcjz.zzbvR);
                    zza(stringBuilder, 1, "limited_ad_tracking", zzcjz.zzbvS);
                    zza(stringBuilder, 1, "os_version", zzcjz.zzaY);
                    zza(stringBuilder, 1, "device_model", zzcjz.zzbvM);
                    zza(stringBuilder, 1, "user_default_language", zzcjz.zzbvN);
                    zza(stringBuilder, 1, "time_zone_offset_minutes", zzcjz.zzbvO);
                    zza(stringBuilder, 1, "bundle_sequential_index", zzcjz.zzbvV);
                    zza(stringBuilder, 1, "service_upload", zzcjz.zzbvW);
                    zza(stringBuilder, 1, "health_monitor", zzcjz.zzboU);
                    if (zzcjz.zzbwc.longValue() != 0) {
                        zza(stringBuilder, 1, "android_id", zzcjz.zzbwc);
                    }
                    zza$583f27e7(stringBuilder, zzcjz.zzbvF);
                    zza$583a27d2(stringBuilder, zzcjz.zzbvX);
                    zza$583a9c31(stringBuilder, zzcjz.zzbvE);
                    zza(stringBuilder, 1);
                    stringBuilder.append("}\n");
                }
            }
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    protected final String zzb(zzcez zzcez) {
        if (zzcez == null) {
            return null;
        }
        if (!zzyw()) {
            return zzcez.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("origin=");
        stringBuilder.append(zzcez.zzbpc);
        stringBuilder.append(",name=");
        stringBuilder.append(zzdW(zzcez.name));
        stringBuilder.append(",params=");
        stringBuilder.append(zzb(zzcez.zzbpM));
        return stringBuilder.toString();
    }

    protected final String zzdW(String str) {
        return str == null ? null : zzyw() ? zza(str, Event.zzbog, Event.zzbof, zzbqI) : str;
    }

    protected final String zzdX(String str) {
        return str == null ? null : zzyw() ? zza(str, Param.zzboi, Param.zzboh, zzbqJ) : str;
    }

    protected final String zzdY(String str) {
        if (str == null) {
            return null;
        }
        if (!zzyw()) {
            return str;
        }
        if (!str.startsWith("_exp_")) {
            return zza(str, UserProperty.zzbon, UserProperty.zzbom, zzbqK);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("experiment_id");
        stringBuilder.append("(");
        stringBuilder.append(str);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    public final /* bridge */ /* synthetic */ zzcfj zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjl zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzcja zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgg zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfl zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfw zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwH() {
        return super.zzwH();
    }

    public final /* bridge */ /* synthetic */ void zzwo() {
        super.zzwo();
    }

    public final /* bridge */ /* synthetic */ void zzwp() {
        super.zzwp();
    }

    public final /* bridge */ /* synthetic */ void zzwq() {
        super.zzwq();
    }

    public final /* bridge */ /* synthetic */ zzcec zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcej zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchl zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzcet zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcid zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchz zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfh zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcen zzwz() {
        return super.zzwz();
    }
}
