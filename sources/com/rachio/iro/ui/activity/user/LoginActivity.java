package com.rachio.iro.ui.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.rachio.iro.R;
import com.rachio.iro.utils.ValidationUtils;

public class LoginActivity extends BaseLoginActivity {
    private final String TAG = LoginActivity.class.getCanonicalName();
    private EditText emailView;
    private Button login;
    private EditText passwordView;
    private Button register;
    private final TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            LoginActivity.this.login.setEnabled(LoginActivity.access$000(LoginActivity.this));
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_login);
        this.emailView = (EditText) findViewById(R.id.email);
        this.passwordView = (EditText) findViewById(R.id.password);
        this.login = (Button) findViewById(R.id.sign_in_button);
        this.register = (Button) findViewById(R.id.register_button);
        TextView passwordReset = (TextView) findViewById(R.id.login_passwordreset);
        this.passwordView.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id != R.id.login && id != 0) {
                    return false;
                }
                if (LoginActivity.access$000(LoginActivity.this)) {
                    LoginActivity.access$200(LoginActivity.this);
                }
                return true;
            }
        });
        this.login.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LoginActivity.access$200(LoginActivity.this);
            }
        });
        this.register.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
        passwordReset.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class));
            }
        });
        this.emailView.addTextChangedListener(this.textWatcher);
        this.passwordView.addTextChangedListener(this.textWatcher);
    }

    private String getUsername() {
        return this.emailView.getText().toString();
    }

    private String getPassword() {
        return this.passwordView.getText().toString();
    }

    static /* synthetic */ boolean access$000(LoginActivity x0) {
        String username = x0.getUsername();
        return (ValidationUtils.isValidUsername(username) || ValidationUtils.isValidEmail(username)) && ValidationUtils.isValidPassword(x0.getPassword());
    }

    static /* synthetic */ void access$200(LoginActivity x0) {
        Intent intent = new Intent(x0, LoginProgressActivity.class);
        intent.putExtra(LoginProgressActivity.EXTRA_USERNAME, x0.getUsername());
        intent.putExtra(LoginProgressActivity.EXTRA_PASSWORD, x0.getPassword());
        x0.addExtrasToIntent(intent);
        x0.startActivity(intent);
        x0.finish();
    }
}
