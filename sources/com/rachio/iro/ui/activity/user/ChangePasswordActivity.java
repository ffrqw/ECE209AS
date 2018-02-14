package com.rachio.iro.ui.activity.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.rachio.iro.R;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.apionly.ChangePasswordRequest;
import com.rachio.iro.model.apionly.ChangePasswordResponse;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import com.rachio.iro.utils.ValidationUtils;

public class ChangePasswordActivity extends BaseActivity {
    private EditText newPassword;
    private EditText newPasswordConfirm;
    private EditText oldPassword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_changepassword);
        wireupToolbarActionBar();
        this.oldPassword = (EditText) findViewById(R.id.changepassword_oldpassword);
        this.newPassword = (EditText) findViewById(R.id.changepassword_newpassword);
        this.newPasswordConfirm = (EditText) findViewById(R.id.changepassword_newpasswordconfirm);
        ((Button) findViewById(R.id.changepassword_changepassword)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String oldPassword = ChangePasswordActivity.this.oldPassword.getEditableText().toString().trim();
                String newPassword = ChangePasswordActivity.this.newPassword.getEditableText().toString().trim();
                String newPasswordConfirm = ChangePasswordActivity.this.newPasswordConfirm.getEditableText().toString().trim();
                boolean cancel = false;
                String errorText = null;
                if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(newPasswordConfirm)) {
                    errorText = "All data required";
                    cancel = true;
                }
                if (TextUtils.isEmpty(oldPassword)) {
                    errorText = "Current password required";
                    cancel = true;
                }
                if (!newPassword.equals(newPasswordConfirm)) {
                    errorText = "Passwords do not match";
                    cancel = true;
                }
                if (!ValidationUtils.isValidPassword(newPassword)) {
                    errorText = RegistrationActivity.PASSWORD_VALIDATION_MESSAGE;
                    cancel = true;
                }
                if (cancel) {
                    Toast.makeText(ChangePasswordActivity.this.getApplicationContext(), errorText, 1).show();
                } else {
                    ChangePasswordActivity.access$300(ChangePasswordActivity.this, oldPassword, newPassword, newPasswordConfirm);
                }
            }
        });
    }

    static /* synthetic */ void access$300(ChangePasswordActivity x0, String x1, String x2, String x3) {
        final String loggedInUserId = x0.prefsWrapper.getLoggedInUserId();
        final String str = x1;
        final String str2 = x2;
        final String str3 = x3;
        new RestClientProgressDialogAsyncTask<Void, Void, ChangePasswordResponse>(x0) {
            public final void onFailure(ErrorResponse errorResponse) {
            }

            public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                ChangePasswordActivity.this.finish();
            }

            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                ModelObject changePasswordRequest = new ChangePasswordRequest(loggedInUserId, str, str2, str3);
                HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
                ChangePasswordResponse changePasswordResponse = (ChangePasswordResponse) this.holder.restClient.postObject(ChangePasswordResponse.class, changePasswordRequest, httpResponseErrorHandler);
                return (changePasswordResponse != null || httpResponseErrorHandler.hasError) ? changePasswordResponse : new ChangePasswordResponse();
            }
        }.execute(new Void[]{null});
    }
}
