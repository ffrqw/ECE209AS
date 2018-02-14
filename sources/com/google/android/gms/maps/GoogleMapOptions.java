package com.google.android.gms.maps;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import com.google.android.gms.R;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;

public final class GoogleMapOptions extends zza implements ReflectedParcelable {
    public static final Creator<GoogleMapOptions> CREATOR = new zzz();
    private Boolean zzblZ;
    private Boolean zzbma;
    private int zzbmb = -1;
    private CameraPosition zzbmc;
    private Boolean zzbmd;
    private Boolean zzbme;
    private Boolean zzbmf;
    private Boolean zzbmg;
    private Boolean zzbmh;
    private Boolean zzbmi;
    private Boolean zzbmj;
    private Boolean zzbmk;
    private Boolean zzbml;
    private Float zzbmm = null;
    private Float zzbmn = null;
    private LatLngBounds zzbmo = null;

    GoogleMapOptions(byte b, byte b2, int i, CameraPosition cameraPosition, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8, byte b9, byte b10, byte b11, Float f, Float f2, LatLngBounds latLngBounds) {
        this.zzblZ = com.google.android.gms.maps.internal.zza.zza(b);
        this.zzbma = com.google.android.gms.maps.internal.zza.zza(b2);
        this.zzbmb = i;
        this.zzbmc = cameraPosition;
        this.zzbmd = com.google.android.gms.maps.internal.zza.zza(b3);
        this.zzbme = com.google.android.gms.maps.internal.zza.zza(b4);
        this.zzbmf = com.google.android.gms.maps.internal.zza.zza(b5);
        this.zzbmg = com.google.android.gms.maps.internal.zza.zza(b6);
        this.zzbmh = com.google.android.gms.maps.internal.zza.zza(b7);
        this.zzbmi = com.google.android.gms.maps.internal.zza.zza(b8);
        this.zzbmj = com.google.android.gms.maps.internal.zza.zza(b9);
        this.zzbmk = com.google.android.gms.maps.internal.zza.zza(b10);
        this.zzbml = com.google.android.gms.maps.internal.zza.zza(b11);
        this.zzbmm = f;
        this.zzbmn = f2;
        this.zzbmo = latLngBounds;
    }

    public static GoogleMapOptions createFromAttributes(Context context, AttributeSet attributeSet) {
        if (attributeSet == null) {
            return null;
        }
        TypedArray obtainAttributes = context.getResources().obtainAttributes(attributeSet, R.styleable.MapAttrs);
        GoogleMapOptions googleMapOptions = new GoogleMapOptions();
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_mapType)) {
            googleMapOptions.zzbmb = obtainAttributes.getInt(R.styleable.MapAttrs_mapType, -1);
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_zOrderOnTop)) {
            googleMapOptions.zzblZ = Boolean.valueOf(obtainAttributes.getBoolean(R.styleable.MapAttrs_zOrderOnTop, false));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_useViewLifecycle)) {
            googleMapOptions.zzbma = Boolean.valueOf(obtainAttributes.getBoolean(R.styleable.MapAttrs_useViewLifecycle, false));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_uiCompass)) {
            googleMapOptions.zzbme = Boolean.valueOf(obtainAttributes.getBoolean(R.styleable.MapAttrs_uiCompass, true));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_uiRotateGestures)) {
            googleMapOptions.zzbmi = Boolean.valueOf(obtainAttributes.getBoolean(R.styleable.MapAttrs_uiRotateGestures, true));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_uiScrollGestures)) {
            googleMapOptions.zzbmf = Boolean.valueOf(obtainAttributes.getBoolean(R.styleable.MapAttrs_uiScrollGestures, true));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_uiTiltGestures)) {
            googleMapOptions.zzbmh = Boolean.valueOf(obtainAttributes.getBoolean(R.styleable.MapAttrs_uiTiltGestures, true));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_uiZoomGestures)) {
            googleMapOptions.zzbmg = Boolean.valueOf(obtainAttributes.getBoolean(R.styleable.MapAttrs_uiZoomGestures, true));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_uiZoomControls)) {
            googleMapOptions.zzbmd = Boolean.valueOf(obtainAttributes.getBoolean(R.styleable.MapAttrs_uiZoomControls, true));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_liteMode)) {
            googleMapOptions.zzbmj = Boolean.valueOf(obtainAttributes.getBoolean(R.styleable.MapAttrs_liteMode, false));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_uiMapToolbar)) {
            googleMapOptions.zzbmk = Boolean.valueOf(obtainAttributes.getBoolean(R.styleable.MapAttrs_uiMapToolbar, true));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_ambientEnabled)) {
            googleMapOptions.zzbml = Boolean.valueOf(obtainAttributes.getBoolean(R.styleable.MapAttrs_ambientEnabled, false));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_cameraMinZoomPreference)) {
            googleMapOptions.zzbmm = Float.valueOf(obtainAttributes.getFloat(R.styleable.MapAttrs_cameraMinZoomPreference, Float.NEGATIVE_INFINITY));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_cameraMinZoomPreference)) {
            googleMapOptions.zzbmn = Float.valueOf(obtainAttributes.getFloat(R.styleable.MapAttrs_cameraMaxZoomPreference, Float.POSITIVE_INFINITY));
        }
        googleMapOptions.zzbmo = LatLngBounds.createFromAttributes(context, attributeSet);
        googleMapOptions.zzbmc = CameraPosition.createFromAttributes(context, attributeSet);
        obtainAttributes.recycle();
        return googleMapOptions;
    }

    public final String toString() {
        return zzbe.zzt(this).zzg("MapType", Integer.valueOf(this.zzbmb)).zzg("LiteMode", this.zzbmj).zzg("Camera", this.zzbmc).zzg("CompassEnabled", this.zzbme).zzg("ZoomControlsEnabled", this.zzbmd).zzg("ScrollGesturesEnabled", this.zzbmf).zzg("ZoomGesturesEnabled", this.zzbmg).zzg("TiltGesturesEnabled", this.zzbmh).zzg("RotateGesturesEnabled", this.zzbmi).zzg("MapToolbarEnabled", this.zzbmk).zzg("AmbientEnabled", this.zzbml).zzg("MinZoomPreference", this.zzbmm).zzg("MaxZoomPreference", this.zzbmn).zzg("LatLngBoundsForCameraTarget", this.zzbmo).zzg("ZOrderOnTop", this.zzblZ).zzg("UseViewLifecycleInFragment", this.zzbma).toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, com.google.android.gms.maps.internal.zza.zzb(this.zzblZ));
        zzd.zza(parcel, 3, com.google.android.gms.maps.internal.zza.zzb(this.zzbma));
        zzd.zzc(parcel, 4, this.zzbmb);
        zzd.zza(parcel, 5, this.zzbmc, i, false);
        zzd.zza(parcel, 6, com.google.android.gms.maps.internal.zza.zzb(this.zzbmd));
        zzd.zza(parcel, 7, com.google.android.gms.maps.internal.zza.zzb(this.zzbme));
        zzd.zza(parcel, 8, com.google.android.gms.maps.internal.zza.zzb(this.zzbmf));
        zzd.zza(parcel, 9, com.google.android.gms.maps.internal.zza.zzb(this.zzbmg));
        zzd.zza(parcel, 10, com.google.android.gms.maps.internal.zza.zzb(this.zzbmh));
        zzd.zza(parcel, 11, com.google.android.gms.maps.internal.zza.zzb(this.zzbmi));
        zzd.zza(parcel, 12, com.google.android.gms.maps.internal.zza.zzb(this.zzbmj));
        zzd.zza(parcel, 14, com.google.android.gms.maps.internal.zza.zzb(this.zzbmk));
        zzd.zza(parcel, 15, com.google.android.gms.maps.internal.zza.zzb(this.zzbml));
        zzd.zza$796a1efa(parcel, 16, this.zzbmm);
        zzd.zza$796a1efa(parcel, 17, this.zzbmn);
        zzd.zza(parcel, 18, this.zzbmo, i, false);
        zzd.zzI(parcel, zze);
    }
}
