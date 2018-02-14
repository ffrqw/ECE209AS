package com.rachio.iro.ui.activity.device;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.widget.EditText;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.apionly.Collaborator;
import com.rachio.iro.model.apionly.DeviceResponse;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Device.Manager;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.fragment.ShareListFragment;
import com.rachio.iro.ui.fragment.ShareOverviewFragment;
import com.rachio.iro.utils.ProgressDialogAsyncTask;
import com.rachio.iro.utils.StringUtils;
import java.util.Iterator;

public class ShareActivity extends BaseActivity implements FetchDeviceListener {
    private Device device;
    private String deviceId;
    private FetchDeviceCommand fetchDeviceCommand;

    public static void addUser(final ShareActivity context, final User user, final String deviceId) {
        new Builder(context).setView(LayoutInflater.from(context).inflate(R.layout.dialog_share, null)).setPositiveButton((CharSequence) "SHARE", new OnClickListener() {
            public final void onClick(DialogInterface dialog, int which) {
                final String email = ((EditText) ((AlertDialog) dialog).findViewById(R.id.dialog_share_user)).getEditableText().toString();
                new ProgressDialogAsyncTask<Void, Void, DeviceResponse>(context) {
                    protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                        DeviceResponse deviceResponse = (DeviceResponse) obj;
                        super.onPostExecute(deviceResponse);
                        if (deviceResponse == null || deviceResponse.hasError()) {
                            new Builder(context).setMessage(deviceResponse != null ? deviceResponse.getError() : "Failed to share access with " + email).setPositiveButton(17039370, null).show();
                        } else {
                            context.onDeviceDataChanged(deviceResponse.id);
                        }
                    }

                    protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                        Database database = ((IroApplication) context.getApplication()).getDatabase();
                        database.lock();
                        HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
                        DeviceResponse deviceResponse = (DeviceResponse) ((IroApplication) context.getApplication()).getRestClient().postObject(DeviceResponse.class, new Collaborator(email, deviceId), httpResponseErrorHandler);
                        if (!(httpResponseErrorHandler.hasError || deviceResponse == null)) {
                            deviceResponse.user = user;
                            database.save(deviceResponse, true);
                        }
                        database.unlock();
                        return deviceResponse;
                    }
                }.execute(null);
            }
        }).show();
    }

    public static void deleteManager(final BaseActivity context, final String deviceId, final String email) {
        new ProgressDialogAsyncTask<Void, Void, Void>(context) {
            protected final /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                super.onPostExecute((Void) obj);
                context.onDeviceDataChanged(deviceId);
            }

            protected final /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                Database database = ((IroApplication) context.getApplication()).getDatabase();
                database.lock();
                HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
                ((IroApplication) context.getApplication()).getRestClient().removeCollaborator(email, deviceId, httpResponseErrorHandler);
                if (!httpResponseErrorHandler.hasError) {
                    Device device = (Device) database.find(Device.class, deviceId);
                    if (device != null) {
                        Iterator it = device.managers.iterator();
                        while (it.hasNext()) {
                            Manager manager = (Manager) it.next();
                            if (StringUtils.equals(manager.email, email)) {
                                device.managers.remove(manager);
                                break;
                            }
                        }
                    }
                    database.save(device, true);
                }
                database.unlock();
                return null;
            }
        }.execute(null);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_device_settings_share);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.deviceId = getDeviceIdFromExtras();
    }

    protected void onResume() {
        super.onResume();
        if (this.fetchDeviceCommand == null) {
            this.fetchDeviceCommand = new FetchDeviceCommand(this, this.deviceId);
            this.fetchDeviceCommand.execute();
        }
    }

    public final void onDeviceDataChanged(String deviceId) {
        super.onDeviceDataChanged(deviceId);
        if (this.device != null && StringUtils.equals(deviceId, this.deviceId)) {
            ((IroApplication) getApplication()).getDatabase().refresh(this.device);
            setupFragment(this.device);
        }
    }

    private void setupFragment(Device device) {
        Fragment f;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (device.managers == null || device.managers.size() == 0) {
            f = ShareOverviewFragment.newInstance(User.getLoggedInUser(this.database, this.prefsWrapper), device);
        } else {
            f = ShareListFragment.newInstance(device);
        }
        ft.replace(R.id.content_frame, f);
        ft.commit();
    }

    public final void onDeviceLoaded(Device device) {
        this.fetchDeviceCommand = null;
        this.device = device;
        if (!isFinishing()) {
            setupFragment(device);
        }
    }
}
