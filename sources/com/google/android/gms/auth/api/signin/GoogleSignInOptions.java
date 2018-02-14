package com.google.android.gms.auth.api.signin;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.internal.zzn;
import com.google.android.gms.auth.api.signin.internal.zzo;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleSignInOptions extends zza implements ReflectedParcelable {
    public static final Creator<GoogleSignInOptions> CREATOR = new zzd();
    public static final GoogleSignInOptions DEFAULT_GAMES_SIGN_IN = new Builder().requestScopes(SCOPE_GAMES, new Scope[0]).build();
    public static final GoogleSignInOptions DEFAULT_SIGN_IN = new Builder().requestId().requestProfile().build();
    private static Scope SCOPE_GAMES = new Scope("https://www.googleapis.com/auth/games");
    private static Comparator<Scope> zzalU = new zzc();
    public static final Scope zzalV = new Scope("profile");
    public static final Scope zzalW = new Scope("email");
    public static final Scope zzalX = new Scope("openid");
    private int versionCode;
    private Account zzajb;
    private final ArrayList<Scope> zzalY;
    private final boolean zzalZ;
    private boolean zzalh;
    private String zzali;
    private final boolean zzama;
    private String zzamb;
    private ArrayList<zzn> zzamc;
    private Map<Integer, zzn> zzamd;

    public static final class Builder {
        private Set<Scope> zzame = new HashSet();
        private Map<Integer, zzn> zzamf = new HashMap();

        public final GoogleSignInOptions build() {
            return new GoogleSignInOptions(new ArrayList(this.zzame), null, false, false, false, null, null, this.zzamf);
        }

        public final Builder requestId() {
            this.zzame.add(GoogleSignInOptions.zzalX);
            return this;
        }

        public final Builder requestProfile() {
            this.zzame.add(GoogleSignInOptions.zzalV);
            return this;
        }

        public final Builder requestScopes(Scope scope, Scope... scopeArr) {
            this.zzame.add(scope);
            this.zzame.addAll(Arrays.asList(scopeArr));
            return this;
        }
    }

    GoogleSignInOptions(int i, ArrayList<Scope> arrayList, Account account, boolean z, boolean z2, boolean z3, String str, String str2, ArrayList<zzn> arrayList2) {
        this(i, (ArrayList) arrayList, account, z, z2, z3, str, str2, zzw(arrayList2));
    }

    private GoogleSignInOptions(int i, ArrayList<Scope> arrayList, Account account, boolean z, boolean z2, boolean z3, String str, String str2, Map<Integer, zzn> map) {
        this.versionCode = i;
        this.zzalY = arrayList;
        this.zzajb = account;
        this.zzalh = z;
        this.zzalZ = z2;
        this.zzama = z3;
        this.zzali = str;
        this.zzamb = str2;
        this.zzamc = new ArrayList(map.values());
        this.zzamd = map;
    }

    public static GoogleSignInOptions zzbQ(String str) throws JSONException {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        JSONObject jSONObject = new JSONObject(str);
        Collection hashSet = new HashSet();
        JSONArray jSONArray = jSONObject.getJSONArray("scopes");
        int length = jSONArray.length();
        for (int i = 0; i < length; i++) {
            hashSet.add(new Scope(jSONArray.getString(i)));
        }
        Object optString = jSONObject.optString("accountName", null);
        return new GoogleSignInOptions(3, new ArrayList(hashSet), !TextUtils.isEmpty(optString) ? new Account(optString, "com.google") : null, jSONObject.getBoolean("idTokenRequested"), jSONObject.getBoolean("serverAuthRequested"), jSONObject.getBoolean("forceCodeForRefreshToken"), jSONObject.optString("serverClientId", null), jSONObject.optString("hostedDomain", null), new HashMap());
    }

    private static Map<Integer, zzn> zzw(List<zzn> list) {
        Map<Integer, zzn> hashMap = new HashMap();
        if (list == null) {
            return hashMap;
        }
        for (zzn zzn : list) {
            hashMap.put(Integer.valueOf(zzn.getType()), zzn);
        }
        return hashMap;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            GoogleSignInOptions googleSignInOptions = (GoogleSignInOptions) obj;
            if (this.zzamc.size() > 0 || googleSignInOptions.zzamc.size() > 0 || this.zzalY.size() != googleSignInOptions.zzmA().size() || !this.zzalY.containsAll(googleSignInOptions.zzmA())) {
                return false;
            }
            if (this.zzajb == null) {
                if (googleSignInOptions.zzajb != null) {
                    return false;
                }
            } else if (!this.zzajb.equals(googleSignInOptions.zzajb)) {
                return false;
            }
            if (TextUtils.isEmpty(this.zzali)) {
                if (!TextUtils.isEmpty(googleSignInOptions.zzali)) {
                    return false;
                }
            } else if (!this.zzali.equals(googleSignInOptions.zzali)) {
                return false;
            }
            return this.zzama == googleSignInOptions.zzama && this.zzalh == googleSignInOptions.zzalh && this.zzalZ == googleSignInOptions.zzalZ;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        List arrayList = new ArrayList();
        ArrayList arrayList2 = this.zzalY;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList2.get(i);
            i++;
            arrayList.add(((Scope) obj).zzpp());
        }
        Collections.sort(arrayList);
        return new zzo().zzo(arrayList).zzo(this.zzajb).zzo(this.zzali).zzP(this.zzama).zzP(this.zzalh).zzP(this.zzalZ).zzmJ();
    }

    public void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.versionCode);
        zzd.zzc(parcel, 2, zzmA(), false);
        zzd.zza(parcel, 3, this.zzajb, i, false);
        zzd.zza(parcel, 4, this.zzalh);
        zzd.zza(parcel, 5, this.zzalZ);
        zzd.zza(parcel, 6, this.zzama);
        zzd.zza(parcel, 7, this.zzali, false);
        zzd.zza(parcel, 8, this.zzamb, false);
        zzd.zzc(parcel, 9, this.zzamc, false);
        zzd.zzI(parcel, zze);
    }

    public final ArrayList<Scope> zzmA() {
        return new ArrayList(this.zzalY);
    }
}
