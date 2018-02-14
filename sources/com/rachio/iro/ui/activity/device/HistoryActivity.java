package com.rachio.iro.ui.activity.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import com.github.yasevich.endlessrecyclerview.EndlessRecyclerView;
import com.github.yasevich.endlessrecyclerview.EndlessRecyclerView.Pager;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchEventsCommand;
import com.rachio.iro.async.command.FetchEventsCommand.FetchEventsListener;
import com.rachio.iro.model.Event;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.view.EventCardItem;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity implements Pager, FetchEventsListener {
    private Adapter<EventViewHolder> adapter = new Adapter<EventViewHolder>() {
        public /* bridge */ /* synthetic */ void onBindViewHolder(ViewHolder viewHolder, int i) {
            Event event = (Event) HistoryActivity.this.events.get(i);
            ((EventViewHolder) viewHolder).view.setEvent(event, false);
        }

        public int getItemCount() {
            return HistoryActivity.this.events.size();
        }

        public /* bridge */ /* synthetic */ ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new EventViewHolder(new EventCardItem(HistoryActivity.this));
        }
    };
    private String deviceId;
    private List<Event> events = new ArrayList();
    private FetchEventsCommand fetchEventsCommand;
    private String filter;
    private int first = 0;
    private int last = 30;
    private EndlessRecyclerView list;
    private boolean noMoreEvents = false;
    private SwipeRefreshLayout swipeRefreshLayout;

    private class EventViewHolder extends ViewHolder {
        private final EventCardItem view;

        public EventViewHolder(View itemView) {
            super(itemView);
            this.view = (EventCardItem) itemView;
        }
    }

    public final boolean shouldLoad() {
        return !this.noMoreEvents;
    }

    public final void loadNextPage() {
        this.first = this.last;
        this.last = this.first + 30;
        loadEvents();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_history);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        IroApplication.get(this).component().inject(this);
        this.deviceId = getDeviceIdFromExtras();
        this.filter = getIntent().getStringExtra("TOPIC");
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        this.list = (EndlessRecyclerView) findViewById(R.id.history_list);
        this.list.setAdapter(this.adapter);
        this.list.setPager(this);
        this.list.setLayoutManager(new LinearLayoutManager(this));
        this.list.setProgressView(R.layout.view_endlessrecyclerprogress);
        this.swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            public final void onRefresh() {
                HistoryActivity.this.events.clear();
                HistoryActivity.this.adapter.notifyDataSetChanged();
                HistoryActivity.this.first = 0;
                HistoryActivity.this.last = 30;
                HistoryActivity.this.loadEvents();
            }
        });
    }

    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        if (this.fetchEventsCommand == null) {
            this.fetchEventsCommand = new FetchEventsCommand(this, this.deviceId, this.filter, this.first, this.last);
            this.fetchEventsCommand.execute();
        }
    }

    public final void onEventsLoaded(String topic, List<Event> events) {
        this.swipeRefreshLayout.setRefreshing(false);
        this.list.setRefreshing(false);
        this.fetchEventsCommand = null;
        if (events == null || events.size() <= 0) {
            this.noMoreEvents = true;
            return;
        }
        this.events.addAll(events);
        this.adapter.notifyDataSetChanged();
    }

    public static final void showHistory(Context context, String deviceId, String topic) {
        if (deviceId == null) {
            throw new IllegalArgumentException("device id cannot be null");
        }
        Intent intent = new Intent(context, HistoryActivity.class);
        intent.putExtra("DEVICEID", deviceId);
        intent.putExtra("TOPIC", topic);
        context.startActivity(intent);
    }
}
