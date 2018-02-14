package com.rachio.iro.ui.newschedulerulepath.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.rachio.iro.R;
import com.rachio.iro.ui.newschedulerulepath.views.CheckableTextRowView;

public class BaseSelectListFragment extends BaseScheduleRuleFragment {
    protected ListView list;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        this.list = (ListView) view.findViewById(R.id.list);
        final int choiceMode = getChoiceMode();
        this.list.setChoiceMode(choiceMode);
        this.list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (choiceMode == 1 && BaseSelectListFragment.this.moveToNextOnChoice()) {
                    BaseSelectListFragment.this.moveToNextStage();
                } else {
                    BaseSelectListFragment.this.onStateChanged();
                }
            }
        });
        return view;
    }

    int getChoiceMode() {
        return 2;
    }

    protected final CheckableTextRowView getRowView(View convertView) {
        if (convertView != null) {
            return (CheckableTextRowView) convertView;
        }
        CheckableTextRowView view = new CheckableTextRowView(getContext());
        view.setChoiceMode(getChoiceMode());
        return view;
    }

    protected int getLayout() {
        return R.layout.fragment_schedulerulewizard_selectlist;
    }

    protected boolean moveToNextOnChoice() {
        return true;
    }
}
