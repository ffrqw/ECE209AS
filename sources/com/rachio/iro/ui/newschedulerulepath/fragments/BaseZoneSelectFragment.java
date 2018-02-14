package com.rachio.iro.ui.newschedulerulepath.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.newschedulerulepath.DividerItemDecoration;
import com.rachio.iro.ui.newschedulerulepath.adapters.SelectableZonesAdapter;
import com.rachio.iro.ui.newschedulerulepath.adapters.SelectableZonesAdapter.OnCheckChangedListener;

public abstract class BaseZoneSelectFragment extends BaseScheduleRuleFragment {
    private RecyclerView list;

    /* renamed from: com.rachio.iro.ui.newschedulerulepath.fragments.BaseZoneSelectFragment$1 */
    class AnonymousClass1 extends SimpleCallback {
        AnonymousClass1(int x0, int x1) {
            super(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
            ((SelectableZonesAdapter) recyclerView.getAdapter()).onMove(viewHolder, target);
            return true;
        }

        public void onSwiped(ViewHolder viewHolder, int direction) {
        }
    }

    public abstract int getLayout();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        this.list = (RecyclerView) view.findViewById(R.id.schedulerulewizard_zoneselect_list);
        this.list.setLayoutManager(new LinearLayoutManager(getContext()));
        this.list.addItemDecoration(new DividerItemDecoration(getActivity(), 1));
        new ItemTouchHelper(new AnonymousClass1(3, 0)).attachToRecyclerView(this.list);
        return view;
    }

    public final void updateState(ScheduleRule state) {
        boolean isFlex = state.isFlex();
        SelectableZonesAdapter adapter = new SelectableZonesAdapter(state.device.getZonesAsList(isFlex, state.id), state.device.getEnabledZones(isFlex, state.id), state.zones, isFlex);
        this.list.setAdapter(adapter);
        adapter.setOnCheckChangedListener(new OnCheckChangedListener() {
            public final void onCheckChanged() {
                BaseZoneSelectFragment.this.onStateChanged();
            }
        });
        super.updateState(state);
    }

    public final boolean validate() {
        if (this.list == null) {
            return false;
        }
        SelectableZonesAdapter adapter = (SelectableZonesAdapter) this.list.getAdapter();
        if (adapter == null || adapter.getCheckedAndEnabledItemCount() <= 0) {
            return false;
        }
        return true;
    }

    public void commitState(ScheduleRule state) {
        super.commitState(state);
        if (this.list != null) {
            SelectableZonesAdapter adapter = (SelectableZonesAdapter) this.list.getAdapter();
            if (adapter != null) {
                state.zones = adapter.getNewZoneInfos();
            }
        }
    }
}
