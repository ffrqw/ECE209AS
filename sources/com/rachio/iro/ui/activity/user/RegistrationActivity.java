package com.rachio.iro.ui.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.apionly.RegistrationRequest;
import com.rachio.iro.model.user.User;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import com.rachio.iro.utils.ValidationUtils;

public class RegistrationActivity extends BaseLoginActivity {
    private static String EMAIL_VALIDATION_MESSAGE = "Invalid email address";
    public static String PASSWORD_VALIDATION_MESSAGE = "Password must be between 8 and 40 characters long";
    private static String USERNAME_VALIDATION_MESSAGE = "Username must be at least 3 characters and a maximum length of 50. Username must match characters and symbols a-z, A-Z, 0-9, _, -, .";
    private EditText mEmail;
    private EditText mFullname;
    private EditText mPassword;
    private EditText mUsername;
    protected RestClient restClient;

    static /* synthetic */ void access$000(RegistrationActivity x0) {
        int i;
        final Object obj = x0.mFullname.getEditableText().toString();
        final String obj2 = x0.mEmail.getEditableText().toString();
        final String obj3 = x0.mUsername.getEditableText().toString();
        final String obj4 = x0.mPassword.getEditableText().toString();
        if (TextUtils.isEmpty(obj) || TextUtils.isEmpty(obj2) || TextUtils.isEmpty(obj3) || TextUtils.isEmpty(obj4)) {
            CharSequence charSequence = "All data required";
            i = 1;
        } else {
            i = 0;
            Object obj5 = null;
        }
        if (!ValidationUtils.isValidUsername(obj3)) {
            charSequence = USERNAME_VALIDATION_MESSAGE;
            i = 1;
        }
        if (!ValidationUtils.isValidEmail(obj2)) {
            charSequence = EMAIL_VALIDATION_MESSAGE;
            i = 1;
        }
        if (!ValidationUtils.isValidPassword(obj4)) {
            charSequence = PASSWORD_VALIDATION_MESSAGE;
            i = 1;
        }
        if (i != 0) {
            Toast.makeText(x0.getApplicationContext(), charSequence, 1).show();
            return;
        }
        new RestClientProgressDialogAsyncTask<Void, Void, User>(x0) {
            public final void onFailure(ErrorResponse error) {
            }

            public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                Intent intent = new Intent(RegistrationActivity.this, LoginProgressActivity.class);
                intent.putExtra(LoginProgressActivity.EXTRA_USERNAME, RegistrationActivity.this.mUsername.getEditableText().toString());
                intent.putExtra(LoginProgressActivity.EXTRA_PASSWORD, RegistrationActivity.this.mPassword.getEditableText().toString());
                RegistrationActivity.this.addExtrasToIntent(intent);
                RegistrationActivity.this.startActivity(intent);
                RegistrationActivity.this.finish();
            }

            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                return (User) RegistrationActivity.this.restClient.postObject(User.class, new RegistrationRequest(obj, obj2, obj3, obj4), this.errorHandler);
            }
        }.execute(null);
        x0.restClient.onLogout();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IroApplication.get(this).component().inject(this);
        setContentView((int) R.layout.activity_registration);
        this.mFullname = (EditText) findViewById(R.id.registration_fullname);
        this.mEmail = (EditText) findViewById(R.id.registration_email);
        this.mUsername = (EditText) findViewById(R.id.registration_username);
        this.mPassword = (EditText) findViewById(R.id.registration_password);
        this.mPassword.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id != R.id.register && id != 0) {
                    return false;
                }
                RegistrationActivity.access$000(RegistrationActivity.this);
                return true;
            }
        });
        ((Button) findViewById(R.id.registration_register_button)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                RegistrationActivity.access$000(RegistrationActivity.this);
            }
        });
    }
}
