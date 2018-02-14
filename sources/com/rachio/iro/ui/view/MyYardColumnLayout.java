package com.rachio.iro.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.rachio.iro.R;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.ui.activity.zone.ZoneDetailsActivity;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyYardColumnLayout extends LinearLayout {
    private int mColumnCount;
    private List<LinearLayout> mColumns;
    private LinkedList<MyYardStampView> mStamps;

    public MyYardColumnLayout(Context context) {
        this(context, null);
    }

    public MyYardColumnLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyYardColumnLayout);
        this.mColumnCount = a.getInt(0, 2);
        a.recycle();
        this.mColumns = new ArrayList(8);
        for (int i = 0; i < this.mColumnCount; i++) {
            LinearLayout column = new LinearLayout(getContext());
            column.setOrientation(1);
            column.setLayoutParams(new LayoutParams(0, -2, 1.0f / ((float) this.mColumnCount)));
            this.mColumns.add(column);
            addView(column);
        }
        this.mStamps = new LinkedList();
    }

    private void setZoneCount(int count) {
        if (count != this.mStamps.size()) {
            int c;
            for (c = 0; c < this.mColumnCount; c++) {
                ((LinearLayout) this.mColumns.get(c)).removeAllViews();
            }
            while (this.mStamps.size() > count) {
                this.mStamps.pop();
            }
            while (this.mStamps.size() < count) {
                this.mStamps.add((MyYardStampView) LayoutInflater.from(getContext()).inflate(R.layout.view_stamp_my_yard, this, false));
            }
            c = 0;
            for (int s = 0; s < this.mStamps.size(); s++) {
                ((LinearLayout) this.mColumns.get(c)).addView((View) this.mStamps.get(s));
                c = (c + 1) % this.mColumnCount;
            }
        }
    }

    public final void setZones(List<Zone> zones, int activeZone, int activeZoneDuration) {
        int zoneCount = zones.size();
        if (zones == null || zoneCount == 0) {
            setZoneCount(0);
            return;
        }
        setZoneCount(zoneCount);
        int i = 0;
        while (i < zones.size()) {
            MyYardStampView stamp;
            final Zone zone = (Zone) zones.get(i);
            if (i < 0 || i >= this.mStamps.size()) {
                stamp = null;
            } else {
                stamp = (MyYardStampView) this.mStamps.get(i);
            }
            stamp.setZone(zone, activeZone, activeZoneDuration);
            stamp.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(MyYardColumnLayout.this.getContext(), ZoneDetailsActivity.class);
                    i.putExtra("ZONEID", zone.id);
                    MyYardColumnLayout.this.getContext().startActivity(i);
                }
            });
            i++;
        }
    }
}
