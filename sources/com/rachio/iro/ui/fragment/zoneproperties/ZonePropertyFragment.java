package com.rachio.iro.ui.fragment.zoneproperties;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.zoneproperties.ZonePropertyCommon;
import com.rachio.iro.ui.activity.HelpActivity;
import com.rachio.iro.ui.activity.zone.ZonePropertyActivity;
import com.rachio.iro.ui.fragment.BaseFragment;
import com.rachio.iro.utils.ProgressDialogAsyncTask;
import com.rachio.iro.utils.StringUtils;
import com.squareup.picasso.Picasso;

public abstract class ZonePropertyFragment extends BaseFragment {
    private GridView grid;
    private ProgressDialogAsyncTask<Void, Void, ZonePropertyCommon[]> propsLoader;
    private String selectedId;

    private final class DummyProp extends ZonePropertyCommon {
        private DummyProp() {
        }

        public final String getDescriptiveString(User user) {
            return null;
        }
    }

    public abstract ZonePropertyCommon[] getProps();

    public abstract String getSupportLink();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_zoneproperty, null);
        TextView description = (TextView) v.findViewById(R.id.nozzlelist_description);
        ImageView info = (ImageView) v.findViewById(R.id.zoneproperty_info);
        this.grid = (GridView) v.findViewById(R.id.nozzlelist_grid);
        String descriptionText = getDescription();
        if (TextUtils.isEmpty(descriptionText)) {
            description.setVisibility(8);
        } else {
            description.setText(descriptionText);
        }
        info.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ZonePropertyFragment.this.getActivity(), HelpActivity.class);
                i.putExtra("article", ZonePropertyFragment.this.getSupportLink());
                ZonePropertyFragment.this.startActivity(i);
            }
        });
        this.grid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ZonePropertyFragment.this.canAdd() && position == ZonePropertyFragment.this.grid.getCount() - 1) {
                    ZonePropertyFragment.this.onAdd();
                    return;
                }
                String itemid = ((ZonePropertyCommon) parent.getItemAtPosition(position)).id;
                if (ZonePropertyFragment.this.getActivity() instanceof ZonePropertyActivity) {
                    ZonePropertyActivity activity = (ZonePropertyActivity) ZonePropertyFragment.this.getActivity();
                    Intent intent = new Intent();
                    intent.putExtra("selectedid", itemid);
                    activity.setResult(-1, intent);
                    activity.finish();
                    return;
                }
                ZonePropertyFragment.this.selectedId = itemid;
                ZonePropertyFragment.this.grid.invalidateViews();
            }
        });
        int selectorId = getSelectorId();
        if (selectorId != -1) {
            this.grid.setSelector(selectorId);
        }
        return v;
    }

    public void onResume() {
        super.onResume();
        this.propsLoader = new ProgressDialogAsyncTask<Void, Void, ZonePropertyCommon[]>(getActivity()) {

            /* renamed from: com.rachio.iro.ui.fragment.zoneproperties.ZonePropertyFragment$3$1 */
            class AnonymousClass1 extends ArrayAdapter<ZonePropertyCommon> {
                final /* synthetic */ User val$user;

                AnonymousClass1(Context x0, int x1, ZonePropertyCommon[] x2, User user) {
                    this.val$user = user;
                    super(x0, R.layout.view_zoneproperty, x2);
                }

                public View getView(int position, View convertView, ViewGroup parent) {
                    LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_zoneproperty, null);
                    ZonePropertyCommon item = (ZonePropertyCommon) getItem(position);
                    if (item instanceof DummyProp) {
                        ((ImageView) view.findViewById(R.id.zoneproperty_icon)).setImageResource(R.drawable.more);
                        ((TextView) view.findViewById(R.id.zoneproperty_label)).setText("Add Nozzle");
                    } else {
                        Picasso.with().load(item.imageUrl).into((ImageView) view.findViewById(R.id.zoneproperty_icon));
                        ((TextView) view.findViewById(R.id.zoneproperty_label)).setText(item.name);
                        ((TextView) view.findViewById(R.id.zoneproperty_description)).setText(item.getDescriptiveString(this.val$user));
                        if (StringUtils.equals(item.id, ZonePropertyFragment.this.selectedId)) {
                            view.setBackgroundResource(ZonePropertyFragment.this.getSelectedColourId());
                        } else {
                            view.setBackgroundDrawable(null);
                        }
                    }
                    return view;
                }
            }

            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                Object props = ZonePropertyFragment.this.getProps();
                if (!ZonePropertyFragment.this.canAdd()) {
                    return props;
                }
                Object obj = new ZonePropertyCommon[(props.length + 1)];
                for (int i = 0; i < props.length; i++) {
                    obj[i] = props[i];
                }
                obj[props.length] = new DummyProp();
                return obj;
            }

            protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                ZonePropertyCommon[] zonePropertyCommonArr = (ZonePropertyCommon[]) obj;
                super.onPostExecute(zonePropertyCommonArr);
                if (!isCancelled()) {
                    User loggedInUser = User.getLoggedInUser(ZonePropertyFragment.this.database, ZonePropertyFragment.this.prefsWrapper);
                    ZonePropertyFragment.this.grid.setAdapter(new AnonymousClass1(ZonePropertyFragment.this.getActivity(), R.layout.view_zoneproperty, zonePropertyCommonArr, loggedInUser));
                }
            }
        };
        this.propsLoader.execute(null);
    }

    public void onPause() {
        super.onPause();
        if (this.propsLoader != null) {
            this.propsLoader.cancel(true);
        }
    }

    public String getDescription() {
        return null;
    }

    public boolean canAdd() {
        return false;
    }

    public void onAdd() {
    }

    public int getSelectorId() {
        return -1;
    }

    public int getSelectedColourId() {
        return -1;
    }

    public final void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }

    public final String getSelectedId() {
        return this.selectedId;
    }
}
