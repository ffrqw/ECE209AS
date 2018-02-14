package com.google.android.gms.measurement;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbdm;
import com.google.android.gms.internal.zzcgl;
import com.google.android.gms.internal.zzchl;
import com.google.android.gms.internal.zzcjl;
import com.j256.ormlite.field.FieldType;
import java.util.List;
import java.util.Map;

@Keep
@Deprecated
public class AppMeasurement {
    private final zzcgl zzboe;

    public static class zzb {
        public String zzboj;
        public String zzbok;
        public long zzbol;

        public zzb(zzb zzb) {
            this.zzboj = zzb.zzboj;
            this.zzbok = zzb.zzbok;
            this.zzbol = zzb.zzbol;
        }
    }

    public static class ConditionalUserProperty {
        @Keep
        public boolean mActive;
        @Keep
        public String mAppId;
        @Keep
        public long mCreationTimestamp;
        @Keep
        public String mExpiredEventName;
        @Keep
        public Bundle mExpiredEventParams;
        @Keep
        public String mName;
        @Keep
        public String mOrigin;
        @Keep
        public long mTimeToLive;
        @Keep
        public String mTimedOutEventName;
        @Keep
        public Bundle mTimedOutEventParams;
        @Keep
        public String mTriggerEventName;
        @Keep
        public long mTriggerTimeout;
        @Keep
        public String mTriggeredEventName;
        @Keep
        public Bundle mTriggeredEventParams;
        @Keep
        public long mTriggeredTimestamp;
        @Keep
        public Object mValue;

        public ConditionalUserProperty(ConditionalUserProperty conditionalUserProperty) {
            zzbo.zzu(conditionalUserProperty);
            this.mAppId = conditionalUserProperty.mAppId;
            this.mOrigin = conditionalUserProperty.mOrigin;
            this.mCreationTimestamp = conditionalUserProperty.mCreationTimestamp;
            this.mName = conditionalUserProperty.mName;
            if (conditionalUserProperty.mValue != null) {
                this.mValue = zzcjl.zzD(conditionalUserProperty.mValue);
                if (this.mValue == null) {
                    this.mValue = conditionalUserProperty.mValue;
                }
            }
            this.mValue = conditionalUserProperty.mValue;
            this.mActive = conditionalUserProperty.mActive;
            this.mTriggerEventName = conditionalUserProperty.mTriggerEventName;
            this.mTriggerTimeout = conditionalUserProperty.mTriggerTimeout;
            this.mTimedOutEventName = conditionalUserProperty.mTimedOutEventName;
            if (conditionalUserProperty.mTimedOutEventParams != null) {
                this.mTimedOutEventParams = new Bundle(conditionalUserProperty.mTimedOutEventParams);
            }
            this.mTriggeredEventName = conditionalUserProperty.mTriggeredEventName;
            if (conditionalUserProperty.mTriggeredEventParams != null) {
                this.mTriggeredEventParams = new Bundle(conditionalUserProperty.mTriggeredEventParams);
            }
            this.mTriggeredTimestamp = conditionalUserProperty.mTriggeredTimestamp;
            this.mTimeToLive = conditionalUserProperty.mTimeToLive;
            this.mExpiredEventName = conditionalUserProperty.mExpiredEventName;
            if (conditionalUserProperty.mExpiredEventParams != null) {
                this.mExpiredEventParams = new Bundle(conditionalUserProperty.mExpiredEventParams);
            }
        }
    }

    public static final class Event extends com.google.firebase.analytics.FirebaseAnalytics.Event {
        public static final String[] zzbof = new String[]{"app_clear_data", "app_exception", "app_remove", "app_upgrade", "app_install", "app_update", "firebase_campaign", "error", "first_open", "first_visit", "in_app_purchase", "notification_dismiss", "notification_foreground", "notification_open", "notification_receive", "os_update", "session_start", "user_engagement", "ad_exposure", "adunit_exposure", "ad_query", "ad_activeview", "ad_impression", "ad_click", "screen_view", "firebase_extra_parameter"};
        public static final String[] zzbog = new String[]{"_cd", "_ae", "_ui", "_ug", "_in", "_au", "_cmp", "_err", "_f", "_v", "_iap", "_nd", "_nf", "_no", "_nr", "_ou", "_s", "_e", "_xa", "_xu", "_aq", "_aa", "_ai", "_ac", "_vs", "_ep"};

        public static String zzdF(String str) {
            return zzcjl.zza(str, zzbof, zzbog);
        }
    }

    public static final class Param extends com.google.firebase.analytics.FirebaseAnalytics.Param {
        public static final String[] zzboh = new String[]{"firebase_conversion", "engagement_time_msec", "exposure_time", "ad_event_id", "ad_unit_id", "firebase_error", "firebase_error_value", "firebase_error_length", "firebase_event_origin", "firebase_screen", "firebase_screen_class", "firebase_screen_id", "firebase_previous_screen", "firebase_previous_class", "firebase_previous_id", "message_device_time", "message_id", "message_name", "message_time", "previous_app_version", "previous_os_version", "topic", "update_with_analytics", "previous_first_open_count", "system_app", "system_app_update", "previous_install_count", "firebase_event_id", "firebase_extra_params_ct", "firebase_group_name", "firebase_list_length", "firebase_index", "firebase_event_name"};
        public static final String[] zzboi = new String[]{"_c", "_et", "_xt", "_aeid", "_ai", "_err", "_ev", "_el", "_o", "_sn", "_sc", "_si", "_pn", "_pc", "_pi", "_ndt", "_nmid", "_nmn", "_nmt", "_pv", "_po", "_nt", "_uwa", "_pfo", "_sys", "_sysu", "_pin", "_eid", "_epc", "_gn", "_ll", "_i", "_en"};

        public static String zzdF(String str) {
            return zzcjl.zza(str, zzboh, zzboi);
        }
    }

    public static final class UserProperty extends com.google.firebase.analytics.FirebaseAnalytics.UserProperty {
        public static final String[] zzbom = new String[]{"firebase_last_notification", "first_open_time", "first_visit_time", "last_deep_link_referrer", "user_id", "first_open_after_install"};
        public static final String[] zzbon = new String[]{"_ln", "_fot", "_fvt", "_ldl", FieldType.FOREIGN_ID_FIELD_SUFFIX, "_fi"};

        public static String zzdF(String str) {
            return zzcjl.zza(str, zzbom, zzbon);
        }
    }

    public interface zza {
        boolean zza$5a299da7();
    }

    public AppMeasurement(zzcgl zzcgl) {
        zzbo.zzu(zzcgl);
        this.zzboe = zzcgl;
    }

    @Keep
    @Deprecated
    public static AppMeasurement getInstance(Context context) {
        return zzcgl.zzbj(context).zzyS();
    }

    @Keep
    public void beginAdUnitExposure(String str) {
        this.zzboe.zzwr().beginAdUnitExposure(str);
    }

    @Keep
    protected void clearConditionalUserProperty(String str, String str2, Bundle bundle) {
        this.zzboe.zzwt().clearConditionalUserProperty(str, str2, bundle);
    }

    @Keep
    protected void clearConditionalUserPropertyAs(String str, String str2, String str3, Bundle bundle) {
        this.zzboe.zzwt().clearConditionalUserPropertyAs(str, str2, str3, bundle);
    }

    @Keep
    public void endAdUnitExposure(String str) {
        this.zzboe.zzwr().endAdUnitExposure(str);
    }

    @Keep
    public long generateEventId() {
        return this.zzboe.zzwB().zzzs();
    }

    @Keep
    public String getAppInstanceId() {
        return this.zzboe.zzwt().zzyH();
    }

    @Keep
    protected List<ConditionalUserProperty> getConditionalUserProperties(String str, String str2) {
        return this.zzboe.zzwt().getConditionalUserProperties(str, str2);
    }

    @Keep
    protected List<ConditionalUserProperty> getConditionalUserPropertiesAs(String str, String str2, String str3) {
        return this.zzboe.zzwt().getConditionalUserPropertiesAs(str, str2, str3);
    }

    @Keep
    public String getCurrentScreenClass() {
        zzb zzzi = this.zzboe.zzwx().zzzi();
        return zzzi != null ? zzzi.zzbok : null;
    }

    @Keep
    public String getCurrentScreenName() {
        zzb zzzi = this.zzboe.zzwx().zzzi();
        return zzzi != null ? zzzi.zzboj : null;
    }

    @Keep
    public String getGmpAppId() {
        try {
            return zzbdm.zzqA();
        } catch (IllegalStateException e) {
            this.zzboe.zzwF().zzyx().zzj("getGoogleAppId failed with exception", e);
            return null;
        }
    }

    @Keep
    protected int getMaxUserProperties(String str) {
        this.zzboe.zzwt();
        return zzchl.getMaxUserProperties(str);
    }

    @Keep
    protected Map<String, Object> getUserProperties(String str, String str2, boolean z) {
        return this.zzboe.zzwt().getUserProperties(str, str2, z);
    }

    @Keep
    protected Map<String, Object> getUserPropertiesAs(String str, String str2, String str3, boolean z) {
        return this.zzboe.zzwt().getUserPropertiesAs(str, str2, str3, z);
    }

    @Keep
    public void logEventInternal(String str, String str2, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        this.zzboe.zzwt().zzd(str, str2, bundle);
    }

    @Keep
    public void registerOnScreenChangeCallback(zza zza) {
        this.zzboe.zzwx().registerOnScreenChangeCallback(zza);
    }

    @Keep
    protected void setConditionalUserProperty(ConditionalUserProperty conditionalUserProperty) {
        this.zzboe.zzwt().setConditionalUserProperty(conditionalUserProperty);
    }

    @Keep
    protected void setConditionalUserPropertyAs(ConditionalUserProperty conditionalUserProperty) {
        this.zzboe.zzwt().setConditionalUserPropertyAs(conditionalUserProperty);
    }

    public final void setUserPropertyInternal(String str, String str2, Object obj) {
        this.zzboe.zzwt().zzb(str, str2, obj);
    }

    @Keep
    public void unregisterOnScreenChangeCallback(zza zza) {
        this.zzboe.zzwx().unregisterOnScreenChangeCallback(zza);
    }
}
