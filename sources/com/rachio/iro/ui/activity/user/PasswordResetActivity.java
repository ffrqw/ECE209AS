package com.rachio.iro.ui.activity.user;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.apionly.PasswordResetRequest;
import com.rachio.iro.model.apionly.PasswordResetResponse;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.utils.ProgressDialogAsyncTask;
import com.rachio.iro.utils.ValidationUtils;

public class PasswordResetActivity extends BaseActivity {
    private Button reset;
    protected RestClient restClient;
    private final TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            String username = PasswordResetActivity.this.username.getEditableText().toString();
            Button access$100 = PasswordResetActivity.this.reset;
            boolean z = ValidationUtils.isValidUsername(username) || ValidationUtils.isValidEmail(username);
            access$100.setEnabled(z);
        }
    };
    private EditText username;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IroApplication.get(this).component().inject(this);
        setContentView((int) R.layout.activity_passwordreset);
        this.username = (EditText) findViewById(R.id.passwordreset_username);
        this.reset = (Button) findViewById(R.id.passwordreset_reset);
        this.username.addTextChangedListener(this.textWatcher);
        this.reset.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                new ProgressDialogAsyncTask<Void, Void, PasswordResetResponse>(PasswordResetActivity.this, PasswordResetActivity.this.username.getEditableText().toString()) {
                    protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                        PasswordResetResponse passwordResetResponse = (PasswordResetResponse) obj;
                        super.onPostExecute(passwordResetResponse);
                        if (passwordResetResponse == null) {
                            Toast.makeText(PasswordResetActivity.this, "Password reset sent", 0).show();
                            PasswordResetActivity.this.finish();
                        } else if (passwordResetResponse != null) {
                            PasswordResetActivity.this.toastError(passwordResetResponse);
                        }
                    }

                    protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                        return (PasswordResetResponse) PasswordResetActivity.this.restClient.postObject(PasswordResetResponse.class, new PasswordResetRequest(x1), new HttpResponseErrorHandler());
                    }
                }.execute(null);
            }
        });
    }
}
