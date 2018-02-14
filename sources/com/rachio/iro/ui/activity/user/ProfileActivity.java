package com.rachio.iro.ui.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.view.settings.RowWithChevronView;
import com.rachio.iro.utils.StringUtils;
import com.rachio.iro.utils.ValidationUtils;

public class ProfileActivity extends ActivityThatSavesUser {
    private EditText email;
    private EditText fullname;
    private User user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_profile);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.fullname = (EditText) findViewById(R.id.profile_fullname);
        this.email = (EditText) findViewById(R.id.profile_email);
        TextView username = (TextView) findViewById(R.id.profile_username);
        RowWithChevronView changePassword = (RowWithChevronView) findViewById(R.id.profile_changepassword);
        this.user = User.getLoggedInUser(this.database, ((IroApplication) getApplication()).getPrefsWrapper());
        this.fullname.setText(this.user.fullName);
        this.email.setText(this.user.email);
        username.setText(this.user.username);
        changePassword.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ProfileActivity.this.startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
            }
        });
    }

    public final void save() {
        String fullname = this.fullname.getEditableText().toString();
        String email = this.email.getEditableText().toString();
        String validationError = null;
        boolean cancel = false;
        if (TextUtils.isEmpty(fullname)) {
            validationError = "You must enter your name";
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            validationError = "You must enter your email address";
            cancel = true;
        } else {
            ValidationUtils.isValidFullname$552c4dfd();
            if (!ValidationUtils.isValidEmail(email)) {
                validationError = "Your email address is not valid";
                cancel = true;
            }
        }
        if (cancel) {
            Toast.makeText(this, validationError, 0).show();
            return;
        }
        this.user.fullName = fullname;
        this.user.email = email;
        saveUser(this.user, true);
    }

    public final boolean hasChanges() {
        return (StringUtils.equals(this.user.fullName, this.fullname.getEditableText().toString()) && StringUtils.equals(this.user.email, this.email.getEditableText().toString())) ? false : true;
    }

    public final boolean isBusy() {
        return false;
    }

    public final boolean isValid() {
        return true;
    }
}
