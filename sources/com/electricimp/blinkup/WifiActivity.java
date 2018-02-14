package com.electricimp.blinkup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import com.rachio.iro.R;
import java.util.List;
import org.json.JSONArray;

public class WifiActivity extends PreBlinkUpActivity {
    private String apiKey;
    private MCrypt mcrypt = new MCrypt(null, null);
    private String oldSSID;
    private EditText passwordView;
    private TextWatcher passwordWatcher = new TextWatcher() {
        public final void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                WifiActivity.this.showCheckBox.setEnabled(true);
            }
        }

        public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public final void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };
    private CheckBox rememberCheckBox;
    private SharedPreferences sharedPreferences;
    private CheckBox showCheckBox;
    private String siteids;
    private EditText ssidView;
    private TextWatcher ssidWatcher = new TextWatcher() {
        public final void afterTextChanged(Editable s) {
            WifiActivity.this.blinkupButton.setEnabled(s.length() > 0);
        }

        public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public final void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };
    private String token;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.__bu_wifi);
        init();
        this.sharedPreferences = getSharedPreferences(getIntent().getExtras().getString("preferenceFile"), 0);
        this.ssidView = (EditText) findViewById(R.id.__bu_wifi_ssid);
        BlinkupController.setHint(this.ssidView, null, R.string.__bu_ssid);
        this.passwordView = (EditText) findViewById(R.id.__bu_wifi_password);
        BlinkupController.setHint(this.passwordView, null, R.string.__bu_password);
        this.rememberCheckBox = (CheckBox) findViewById(R.id.__bu_remember_password);
        BlinkupController.setText(this.rememberCheckBox, null, R.string.__bu_remember_password);
        this.showCheckBox = (CheckBox) findViewById(R.id.__bu_show_password);
        BlinkupController.setText(this.showCheckBox, null, R.string.__bu_show_password);
        this.showCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public final void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int start = WifiActivity.this.passwordView.getSelectionStart();
                int end = WifiActivity.this.passwordView.getSelectionEnd();
                if (isChecked) {
                    WifiActivity.this.passwordView.setInputType(145);
                } else {
                    WifiActivity.this.passwordView.setInputType(129);
                    WifiActivity.this.passwordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                WifiActivity.this.passwordView.setSelection(start, end);
                WifiActivity.this.passwordView.setTypeface(Typeface.DEFAULT);
            }
        });
        this.showCheckBox.setVisibility(this.blinkup.showPassword ? 0 : 8);
        this.blinkupButton.setEnabled(false);
        this.ssidView.addTextChangedListener(this.ssidWatcher);
        this.passwordView.setTypeface(Typeface.DEFAULT);
        this.passwordView.addTextChangedListener(this.passwordWatcher);
        BlinkupController.setText((TextView) findViewById(R.id.__bu_blinkup_desc), null, R.string.__bu_blinkup_desc);
        Bundle bundle = getIntent().getExtras();
        this.token = bundle.getString("token");
        this.siteids = bundle.getString("siteid");
        this.apiKey = bundle.getString("apiKey");
        String ssid = bundle.getString("ssid");
        this.oldSSID = ssid;
        if (ssid != null) {
            this.ssidView.setText(ssid);
            this.rememberCheckBox.setChecked(true);
        }
        String encrypted_pwd = this.sharedPreferences.getString("eimp:w:" + ssid, "");
        if (!TextUtils.isEmpty(encrypted_pwd)) {
            this.passwordView.setText(decryptPassword(encrypted_pwd));
            getWindow().setSoftInputMode(2);
        } else if (ssid != null) {
            this.passwordView.requestFocus();
        }
        this.showCheckBox.setEnabled(TextUtils.isEmpty(this.passwordView.getText()));
    }

    public void onResume() {
        super.onResume();
        this.blinkupButton.setEnabled(this.ssidView.getText().length() > 0);
    }

    public void onPause() {
        super.onPause();
        String obj = this.ssidView.getText().toString();
        String obj2 = this.passwordView.getText().toString();
        Object stringArrayListExtra = getIntent().getStringArrayListExtra("savedNetworks");
        if (this.rememberCheckBox.isChecked()) {
            if (!(this.oldSSID == null || this.oldSSID.equals(obj))) {
                removeSavedNetwork(stringArrayListExtra, this.oldSSID);
            }
            if (obj.length() != 0) {
                Editor edit = this.sharedPreferences.edit();
                if (!stringArrayListExtra.contains(obj)) {
                    stringArrayListExtra.add(obj.replaceAll("\"", ""));
                    edit.putString("eimp:savedNetworks", new JSONArray(stringArrayListExtra).toString());
                }
                edit.putString("eimp:w:" + obj, encryptPassword(obj2));
                edit.commit();
                return;
            }
            return;
        }
        removeSavedNetwork(stringArrayListExtra, this.oldSSID);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && requestCode == 4 && this.blinkup.intentBlinkupComplete != null) {
            setResult(-1);
            finish();
        }
    }

    protected final Intent createSendBlinkupIntent() {
        String ssid = this.ssidView.getText().toString();
        if (ssid.length() == 0) {
            return null;
        }
        String pwd = this.passwordView.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("mode", "wifi");
        intent.putExtra("ssid", ssid);
        intent.putExtra("pwd", pwd);
        intent.putExtra("token", this.token);
        intent.putExtra("siteid", this.siteids);
        intent.putExtra("apiKey", this.apiKey);
        return intent;
    }

    protected final void addCreateTokenIntentFields(Intent data) {
        String ssid = this.ssidView.getText().toString();
        String pwd = this.passwordView.getText().toString();
        data.putExtra("ssid", ssid);
        data.putExtra("pwd", pwd);
    }

    private void removeSavedNetwork(List<String> savedNetworks, String ssid) {
        if (savedNetworks.contains(ssid)) {
            savedNetworks.remove(ssid);
            JSONArray savedNetworksJSON = new JSONArray(savedNetworks);
            Editor editor = this.sharedPreferences.edit();
            editor.putString("eimp:savedNetworks", savedNetworksJSON.toString());
            editor.remove("eimp:w:" + ssid);
            editor.commit();
        }
    }

    private String encryptPassword(String password) {
        String encrypted = null;
        if (!(password == null || password.length() == 0)) {
            try {
                byte[] encrypt = this.mcrypt.encrypt(password);
                if (encrypt != null) {
                    int length = encrypt.length;
                    String str = "";
                    for (int i = 0; i < length; i++) {
                        if ((encrypt[i] & 255) < 16) {
                            str = new StringBuilder(String.valueOf(str)).append("0").append(Integer.toHexString(encrypt[i] & 255)).toString();
                        } else {
                            str = new StringBuilder(String.valueOf(str)).append(Integer.toHexString(encrypt[i] & 255)).toString();
                        }
                    }
                    encrypted = str;
                }
            } catch (Exception e) {
                Log.e("BlinkUp", e.toString());
            }
        }
        return encrypted;
    }

    private String decryptPassword(String encrypted) {
        if (encrypted == null || encrypted.length() == 0) {
            return null;
        }
        try {
            String decrypted = new String(this.mcrypt.decrypt(encrypted));
            int indexOf = decrypted.indexOf(0);
            if (indexOf > 0) {
                decrypted = decrypted.substring(0, indexOf);
            }
            return decrypted;
        } catch (Exception e) {
            Log.e("BlinkUp", e.toString());
            return null;
        }
    }
}
