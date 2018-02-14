package com.instabug.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.instabug.library.Feature.State;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.HashMap;

public class InstabugFeaturesManager {
    private static final String AVAILABILITY_PREFIX = "AVAIL";
    private static final boolean DEFAULT_FEATURE_AVAILABILITY = true;
    public static final State DEFAULT_FEATURE_STATE = State.ENABLED;
    private static InstabugFeaturesManager INSTANCE = null;
    private static final String STATE_PREFIX = "STATE";
    private HashMap<Feature, Boolean> featuresAvailability = new HashMap();
    private HashMap<Feature, State> featuresState = new HashMap();

    public static InstabugFeaturesManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InstabugFeaturesManager();
        }
        return INSTANCE;
    }

    private InstabugFeaturesManager() {
    }

    void updateFeatureAvailability(Feature feature, boolean z) {
        if (this.featuresAvailability.containsKey(feature) && ((Boolean) this.featuresAvailability.get(feature)).booleanValue() == z) {
            InstabugSDKLogger.d(this, "Feature " + feature + " availability is already " + z + " ignoring");
            return;
        }
        InstabugSDKLogger.d(this, "Setting feature " + feature + " availability to " + z);
        this.featuresAvailability.put(feature, Boolean.valueOf(z));
    }

    public boolean isFeatureAvailable(Feature feature) {
        if (this.featuresAvailability.containsKey(feature)) {
            InstabugSDKLogger.d(this, "Feature " + feature + " availability is " + this.featuresAvailability.get(feature));
            return ((Boolean) this.featuresAvailability.get(feature)).booleanValue();
        }
        InstabugSDKLogger.d(this, "Feature " + feature + " availability not found, returning true");
        return true;
    }

    public void setFeatureState(Feature feature, State state) {
        if (this.featuresState.containsKey(feature) && this.featuresState.get(feature) == state) {
            InstabugSDKLogger.d(this, "Feature " + feature + " state is already " + state + " ignoring");
            return;
        }
        InstabugSDKLogger.d(this, "Setting " + feature + " state to " + state);
        this.featuresState.put(feature, state);
    }

    public State getFeatureState(Feature feature) {
        boolean z = isFeatureAvailable(feature) && isFeatureAvailable(Feature.INSTABUG);
        InstabugSDKLogger.d(this, "Feature " + feature + " isAvailable = " + z + ", and it's state is " + this.featuresState.get(feature));
        if (!z) {
            InstabugSDKLogger.d(this, "Feature " + feature + " isn't available, returning " + State.DISABLED);
            return State.DISABLED;
        } else if (this.featuresState.containsKey(feature)) {
            return (State) this.featuresState.get(feature);
        } else {
            InstabugSDKLogger.d(this, "Feature " + feature + " is available, but no specific state is set. Returning " + DEFAULT_FEATURE_STATE);
            return DEFAULT_FEATURE_STATE;
        }
    }

    void saveFeaturesToSharedPreferences(Context context) {
        Editor edit = context.getSharedPreferences("instabug", 0).edit();
        for (Feature feature : this.featuresAvailability.keySet()) {
            edit.putBoolean(feature.name() + AVAILABILITY_PREFIX, ((Boolean) this.featuresAvailability.get(feature)).booleanValue());
            InstabugSDKLogger.d(this, "Saved feature " + feature + " availability " + this.featuresAvailability.get(feature) + " to shared preferences");
        }
        for (Feature feature2 : this.featuresState.keySet()) {
            edit.putString(feature2.name() + STATE_PREFIX, ((State) this.featuresState.get(feature2)).name());
            InstabugSDKLogger.d(this, "Saved feature " + feature2 + " state " + this.featuresState.get(feature2) + " to shared preferences");
        }
        edit.apply();
    }

    void restoreFeaturesFromSharedPreferences(Context context) {
        int i = 0;
        SharedPreferences sharedPreferences = context.getSharedPreferences("instabug", 0);
        Feature[] values = Feature.values();
        int length = values.length;
        while (i < length) {
            Feature feature = values[i];
            String str = feature.name() + AVAILABILITY_PREFIX;
            boolean z = sharedPreferences.getBoolean(feature.name() + AVAILABILITY_PREFIX, true);
            if (sharedPreferences.contains(str)) {
                this.featuresAvailability.put(feature, Boolean.valueOf(z));
                InstabugSDKLogger.d(this, "Feature " + feature + " saved availability " + z + " restored from shared preferences");
            } else if (this.featuresAvailability.containsKey(feature)) {
                InstabugSDKLogger.d(this, "Not restoring feature " + feature + " availability as it's already set to " + this.featuresAvailability.get(feature));
            } else {
                this.featuresAvailability.put(feature, Boolean.valueOf(z));
                InstabugSDKLogger.d(this, "Restored feature " + feature + " availability " + z + " from shared preferences");
            }
            if (this.featuresState.containsKey(feature)) {
                InstabugSDKLogger.d(this, "Not restoring feature " + feature + " state as it's already set to " + this.featuresState.get(feature));
            } else {
                State valueOf = State.valueOf(sharedPreferences.getString(feature.name() + STATE_PREFIX, DEFAULT_FEATURE_STATE.name()));
                this.featuresState.put(feature, valueOf);
                InstabugSDKLogger.d(this, "Restored feature " + feature + " state " + valueOf + " from shared preferences");
            }
            i++;
        }
    }
}
