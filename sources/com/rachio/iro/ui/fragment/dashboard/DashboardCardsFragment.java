package com.rachio.iro.ui.fragment.dashboard;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rachio.iro.R;
import com.rachio.iro.adapters.ModelObjectAdapter;
import com.rachio.iro.binder.CardInfo;
import com.rachio.iro.ui.activity.DashboardActivity;
import java.util.List;

public abstract class DashboardCardsFragment extends BaseDeviceDashboardFragment {
    protected ModelObjectAdapter mAdapter;
    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        this.swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        this.recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        ((SimpleItemAnimator) this.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        return rootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            public final void onRefresh() {
                ((DashboardActivity) DashboardCardsFragment.this.getActivity()).refreshSelectedDevice();
                DashboardCardsFragment.this.swipeRefreshLayout.setRefreshing(false);
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(getResources().getInteger(R.integer.device_dashboard_columns), 1);
        layoutManager.setGapStrategy(2);
        recyclerView.setLayoutManager(layoutManager);
        this.mAdapter = new ModelObjectAdapter();
        recyclerView.setAdapter(this.mAdapter);
    }

    protected final void setCards(List<CardInfo> data) {
        this.mAdapter.setData(data);
    }

    protected final void addCard(CardInfo cardInfo) {
        this.mAdapter.addCard(cardInfo);
    }

    protected final void removeCard(CardInfo cardInfo) {
        this.mAdapter.removeCard(cardInfo);
    }

    protected final void clearCards() {
        this.mAdapter.clear();
    }
}
