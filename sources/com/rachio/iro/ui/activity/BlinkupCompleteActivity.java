package com.rachio.iro.ui.activity;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.electricimp.blinkup.BlinkupController;
import com.electricimp.blinkup.BlinkupController.TokenStatusCallback;
import com.rachio.iro.R;
import com.rachio.iro.ui.fragment.blinkup.BlinkUpFragment;
import com.rachio.iro.utils.WifiUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class BlinkupCompleteActivity extends BlinkUpActivity {
    private static final String TAG = BlinkupCompleteActivity.class.getCanonicalName();
    private BlinkupController blinkup;
    private TokenStatusCallback callback = new TokenStatusCallback() {
        public final void onSuccess(JSONObject json) {
            Editor editor = PreferenceManager.getDefaultSharedPreferences(BlinkupCompleteActivity.this).edit();
            try {
                String agentUrl = json.getString("agent_url");
                String impeeId = json.getString("impee_id");
                String planId = json.getString("plan_id");
                if (impeeId != null) {
                    impeeId = impeeId.trim();
                }
                editor.putString("agentUrl", agentUrl);
                editor.putString("impeeId", impeeId);
                editor.putString("planId", planId);
                Log.d(BlinkupCompleteActivity.TAG, "agentUrl " + agentUrl);
                Log.d(BlinkupCompleteActivity.TAG, "impeeId " + impeeId);
                Log.d(BlinkupCompleteActivity.TAG, "planId " + planId);
                editor.commit();
                BlinkupCompleteActivity.this.finish();
            } catch (JSONException e) {
                onError(e.getMessage());
            }
        }

        public final void onError(String errorMsg) {
            BlinkupCompleteActivity.access$100(BlinkupCompleteActivity.this);
            Log.d(BlinkupCompleteActivity.TAG, "onError BlinkupCompleteActivity " + errorMsg);
        }

        public final void onTimeout() {
            BlinkupCompleteActivity.access$100(BlinkupCompleteActivity.this);
            Log.d(BlinkupCompleteActivity.TAG, "onTimeout BlinkupCompleteActivity");
        }
    };
    private BlinkUpFragment statusFragment = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.blinkup = BlinkupController.getInstance();
        setContentView((int) R.layout.activity_blink_up);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            this.statusFragment = BlinkUpFragment.newInstance(this.prefsWrapper.getLoggedInUserId());
            ft.add(R.id.blinkup_activity_fragment, this.statusFragment, "statusfragment");
            ft.setTransition(4099);
            ft.commit();
            return;
        }
        this.statusFragment = (BlinkUpFragment) getSupportFragmentManager().findFragmentByTag("statusfragment");
    }

    protected void onResume() {
        super.onResume();
        this.statusFragment.setStatusMessage("Trying to connect Iro to " + WifiUtils.getNetworkName(this), true);
        this.blinkup.getTokenStatus(this.callback);
    }

    protected void onPause() {
        super.onPause();
        this.blinkup.cancelTokenStatusPolling();
    }

    static /* synthetic */ void access$100(BlinkupCompleteActivity x0) {
        x0.statusFragment.setStatusMessage("Your Iro was not able to connect to your WiFi network.", false);
        x0.statusFragment.onFailure();
    }
}
