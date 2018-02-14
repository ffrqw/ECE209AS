package com.rachio.iro.ui.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchNozzlesCommand;
import com.rachio.iro.async.command.FetchNozzlesCommand.Listener;
import com.rachio.iro.async.command.FetchNozzlesCommand.NozzleDataHolder;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.PostId;
import com.rachio.iro.model.zoneproperties.Nozzle;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.activity.zone.NozzleConfigurationActivity;
import com.rachio.iro.utils.ProgressDialogAsyncTask;
import com.rachio.iro.utils.StringUtils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class MyNozzlesActivity extends BaseActivity implements Listener {
    private static final String TAG = MyNozzlesActivity.class.getCanonicalName();
    private ActionMode actionMode;
    private FetchNozzlesCommand fetchNozzlesCommand;
    private ListView list;
    private Callback mActionModeCallback = new Callback() {
        public final boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.snippet_delete, menu);
            return true;
        }

        public final boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public final boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    List<Nozzle> selectedNozzles = new ArrayList();
                    SparseBooleanArray checkedItems = MyNozzlesActivity.this.list.getCheckedItemPositions();
                    for (int i = 0; i < MyNozzlesActivity.this.list.getCount(); i++) {
                        if (checkedItems.get(i)) {
                            selectedNozzles.add((Nozzle) MyNozzlesActivity.this.list.getItemAtPosition(i));
                        }
                    }
                    for (Nozzle nz : selectedNozzles) {
                        if (MyNozzlesActivity.this.nozzleData.zonesAssociatedToNozzles.containsKey(nz.id)) {
                            List<String> activeZoneNames = (List) MyNozzlesActivity.this.nozzleData.zonesAssociatedToNozzles.get(nz.id);
                            Log.d(MyNozzlesActivity.TAG, "nozzle " + nz.id + " is active");
                            new Builder(MyNozzlesActivity.this).setMessage(String.format("The nozzle named %s cannot be deleted because it is associated with the following zones: %s.\nPlease change the nozzle for these zones and try again.", new Object[]{nz.name, StringUtils.arrayToCommaList(activeZoneNames.toArray(new String[activeZoneNames.size()]))})).setNeutralButton(17039370, null).show();
                            mode.finish();
                            return true;
                        }
                    }
                    new ProgressDialogAsyncTask<Nozzle, HttpResponseErrorHandler, List<Nozzle>>(MyNozzlesActivity.this) {
                        protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                            Nozzle[] nozzleArr = (Nozzle[]) objArr;
                            List arrayList = new ArrayList();
                            for (Nozzle nozzle : nozzleArr) {
                                HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
                                MyNozzlesActivity.this.restClient.deleteObjectById(Nozzle.class, nozzle.id, httpResponseErrorHandler);
                                if (httpResponseErrorHandler.hasError) {
                                    publishProgress(new HttpResponseErrorHandler[]{httpResponseErrorHandler});
                                    break;
                                }
                                MyNozzlesActivity.this.nozzleData.allNozzles.remove(nozzle);
                                arrayList.add(nozzle);
                            }
                            MyNozzlesActivity.this.nozzleData.user.nozzles = (Nozzle[]) MyNozzlesActivity.this.nozzleData.allNozzles.toArray(new Nozzle[0]);
                            MyNozzlesActivity.this.database.save(MyNozzlesActivity.this.nozzleData.user);
                            return arrayList;
                        }

                        protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                            List list = (List) obj;
                            mode.finish();
                            MyNozzlesActivity.this.nozzleData.editableNozzles.removeAll(list);
                            ((ArrayAdapter) MyNozzlesActivity.this.list.getAdapter()).notifyDataSetChanged();
                            super.onPostExecute(list);
                        }

                        protected /* bridge */ /* synthetic */ void onProgressUpdate(Object[] objArr) {
                            super.onProgressUpdate((HttpResponseErrorHandler[]) objArr);
                            MyNozzlesActivity.this.toastGenericError();
                        }
                    }.execute(selectedNozzles.toArray(new Nozzle[0]));
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        public final void onDestroyActionMode(ActionMode mode) {
            MyNozzlesActivity.this.list.clearChoices();
            ((ArrayAdapter) MyNozzlesActivity.this.list.getAdapter()).notifyDataSetChanged();
            MyNozzlesActivity.this.actionMode = null;
        }
    };
    private NozzleDataHolder nozzleData;
    RestClient restClient;
    private String userId;

    private static class NozzleAdapter extends ArrayAdapter<Nozzle> {

        class ViewHolder {
            final ImageView icon;
            final TextView name;

            public ViewHolder(View view) {
                this.icon = (ImageView) view.findViewById(R.id.usernozzle_icon);
                this.name = (TextView) view.findViewById(R.id.usernozzle_name);
            }
        }

        public NozzleAdapter(Context context, int resource, List<Nozzle> objects) {
            super(context, -1, objects);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                View view = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.list_usernozzle, null);
                view.setTag(new ViewHolder(view));
                convertView = view;
            }
            Nozzle n = (Nozzle) getItem(position);
            ViewHolder holder = (ViewHolder) convertView.getTag();
            Picasso.with().load(n.imageUrl).into(holder.icon);
            holder.name.setText(n.name);
            return convertView;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_customnozzles);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        IroApplication.get(this).component().inject(this);
        this.userId = getUserIdFromExtras();
        this.list = (ListView) findViewById(R.id.mynozzles_list);
        this.list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (MyNozzlesActivity.this.actionMode != null) {
                    MyNozzlesActivity.this.actionMode.finish();
                }
                MyNozzlesActivity.this.editNozzle((Nozzle) parent.getItemAtPosition(position));
            }
        });
        this.list.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (MyNozzlesActivity.this.actionMode != null) {
                    return false;
                }
                MyNozzlesActivity.this.actionMode = MyNozzlesActivity.this.startSupportActionMode(MyNozzlesActivity.this.mActionModeCallback);
                MyNozzlesActivity.this.list.setItemChecked(position, true);
                return true;
            }
        });
    }

    protected void onResume() {
        super.onResume();
        if (this.fetchNozzlesCommand == null) {
            this.fetchNozzlesCommand = new FetchNozzlesCommand(this, this.userId);
            this.fetchNozzlesCommand.execute();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.snippet_add, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                if (this.nozzleData != null) {
                    Nozzle n = new Nozzle();
                    n.person = new PostId(this.nozzleData.user.id);
                    editNozzle(n);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editNozzle(Nozzle n) {
        Intent i = new Intent(this, NozzleConfigurationActivity.class);
        i.putExtra("newNozzle", n);
        startActivity(i);
    }

    public final void onNozzlesLoaded(NozzleDataHolder nozzleData) {
        this.fetchNozzlesCommand = null;
        this.nozzleData = nozzleData;
        if (nozzleData != null) {
            this.list.setAdapter(new NozzleAdapter(this, -1, nozzleData.editableNozzles));
        }
    }
}
