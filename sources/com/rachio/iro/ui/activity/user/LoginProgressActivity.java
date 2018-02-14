package com.rachio.iro.ui.activity.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.cloud.PushPull;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.fcm.IDService;
import com.rachio.iro.model.ResponseCacheItem;
import com.rachio.iro.model.apionly.LoginRequest;
import com.rachio.iro.model.apionly.LoginResponse;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.ShallowDevice;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.activity.DashboardActivity;
import com.rachio.iro.ui.activity.device.NoDeviceActivity;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import java.util.ArrayList;
import java.util.Iterator;

public class LoginProgressActivity extends BaseLoginActivity {
    public static String EXTRA_PASSWORD = "password";
    public static String EXTRA_USERNAME = "username";
    private static final String TAG = LoginProgressActivity.class.getCanonicalName();
    private AsyncTask<Void, Void, LoginResponse> loginTask;
    private TextView message;
    private String password;
    protected RestClient restClient;
    private LoginState state = LoginState.NOTLOGGEDIN;
    private AsyncTask<String, Void, User> syncTask;
    private String username;

    private enum LoginState {
        NOTLOGGEDIN,
        LOGGINGIN,
        SYNCINGUSERSTATE,
        DONE,
        FAILED
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IroApplication.get(this).component().inject(this);
        setContentView((int) R.layout.activity_loginprogress);
        this.message = (TextView) findViewById(R.id.loginprogress_message);
        this.username = getIntent().getStringExtra(EXTRA_USERNAME);
        this.password = getIntent().getStringExtra(EXTRA_PASSWORD);
    }

    protected void onResume() {
        super.onResume();
        if (this.state != LoginState.NOTLOGGEDIN) {
            return;
        }
        if (this.prefsWrapper.isUserLoggedIn()) {
            this.restClient.setUserHeaders(this.prefsWrapper.getLoggedInUserCredentials().getSessionKeys());
            moveToState(LoginState.SYNCINGUSERSTATE);
        } else if (this.username == null || this.password == null) {
            moveToState(LoginState.FAILED);
        } else {
            moveToState(LoginState.LOGGINGIN);
        }
    }

    private void moveToState(LoginState newState) {
        switch (newState) {
            case LOGGINGIN:
                this.message.setText("Validating user credentials...");
                if (this.username == null || this.password == null) {
                    throw new IllegalStateException();
                }
                ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                this.loginTask = new AsyncTask<Void, Void, LoginResponse>() {
                    protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                        LoginResponse loginResponse = (LoginResponse) obj;
                        super.onPostExecute(loginResponse);
                        if (!isCancelled()) {
                            if (loginResponse == null || loginResponse.hasError()) {
                                if (loginResponse != null) {
                                    LoginProgressActivity.this.toastError(loginResponse);
                                } else {
                                    LoginProgressActivity.this.toastGenericError();
                                }
                                LoginProgressActivity.this.moveToState(LoginState.FAILED);
                                return;
                            }
                            LoginProgressActivity.this.moveToState(LoginState.SYNCINGUSERSTATE);
                        }
                    }

                    protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                        ResponseCacheItem.clean(LoginProgressActivity.this.database);
                        LoginProgressActivity.this.restClient.onLogout();
                        LoginResponse loginResponse = (LoginResponse) LoginProgressActivity.this.restClient.postObject(LoginResponse.class, new LoginRequest(LoginProgressActivity.this.username, LoginProgressActivity.this.password), new HttpResponseErrorHandler());
                        if (loginResponse != null && loginResponse.loggedIn) {
                            Crashlytics.setUserIdentifier(loginResponse.userId);
                            Crashlytics.setUserName(loginResponse.username);
                            LoginProgressActivity.this.prefsWrapper.storeLoggedInUserInfo(loginResponse);
                            LoginProgressActivity.this.restClient.setUserHeaders(loginResponse.getUserCredentials().getSessionKeys());
                        }
                        return loginResponse;
                    }
                };
                this.loginTask.execute(null);
                return;
            case SYNCINGUSERSTATE:
                this.message.setText("Synchronizing user data...");
                this.syncTask = new AsyncTask<String, Void, User>() {
                    protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                        User user = (User) PushPull.pullEntityAndSave(LoginProgressActivity.this.database, LoginProgressActivity.this.restClient, User.class, ((String[]) objArr)[0]);
                        if (user != null) {
                            ArrayList arrayList;
                            Iterator it;
                            if (user.devices != null && user.devices.size() > 0 && user.devices.size() < 5) {
                                arrayList = new ArrayList();
                                it = user.devices.iterator();
                                while (it.hasNext()) {
                                    arrayList.add(((ShallowDevice) it.next()).id);
                                }
                                PushPull.backgroundPullAndSaveEntities(LoginProgressActivity.this, Device.class, arrayList, user.id, false);
                            }
                            if (user.managedDevices != null && user.managedDevices.size() > 0 && user.managedDevices.size() < 5) {
                                arrayList = new ArrayList();
                                it = user.managedDevices.iterator();
                                while (it.hasNext()) {
                                    arrayList.add(((ShallowDevice) it.next()).id);
                                }
                                PushPull.backgroundPullAndSaveEntities(LoginProgressActivity.this, Device.class, arrayList, user.id, true);
                            }
                        } else {
                            User loggedInUser = User.getLoggedInUser(LoginProgressActivity.this.database, LoginProgressActivity.this.prefsWrapper);
                            if (LoginProgressActivity.access$800(LoginProgressActivity.this, loggedInUser)) {
                                user = loggedInUser;
                            }
                        }
                        if (user != null) {
                            IDService.sendToken(LoginProgressActivity.this.restClient, user.id);
                        }
                        return user;
                    }

                    protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                        User user = (User) obj;
                        super.onPostExecute(user);
                        if (!isCancelled()) {
                            if (user != null) {
                                RestClientProgressDialogAsyncTask.READONLY = user.hasReadOnlyRole();
                                if (!user.haveDevices()) {
                                    LoginProgressActivity.this.startActivity(new Intent(LoginProgressActivity.this, NoDeviceActivity.class));
                                } else if (LoginProgressActivity.this.goToHistory) {
                                    DashboardActivity.goToDashboard(LoginProgressActivity.this, LoginProgressActivity.this.prefsWrapper, user.id, user.haveDevices(), user.hasReadOnlyRole(), "Activity", LoginProgressActivity.this.deviceId, false, false);
                                } else {
                                    DashboardActivity.goToDashboard(LoginProgressActivity.this, LoginProgressActivity.this.prefsWrapper, user.id, user.haveDevices(), user.hasReadOnlyRole(), null, null, false, false);
                                }
                                LoginProgressActivity.this.moveToState(LoginState.DONE);
                                return;
                            }
                            LoginProgressActivity.this.moveToState(LoginState.FAILED);
                        }
                    }
                };
                this.syncTask.execute(new String[]{this.prefsWrapper.getLoggedInUserId()});
                return;
            case DONE:
                finish();
                return;
            case FAILED:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            default:
                return;
        }
    }

    protected void onPause() {
        super.onPause();
        if (this.loginTask != null) {
            this.loginTask.cancel(true);
        }
        if (this.syncTask != null) {
            this.syncTask.cancel(true);
        }
    }

    static /* synthetic */ boolean access$800(LoginProgressActivity x0, User x1) {
        if (x1 != null) {
            return x1.haveDevices();
        }
        return false;
    }
}
