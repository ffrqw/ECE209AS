package com.rachio.iro.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import java.util.ArrayList;
import java.util.List;

public class SuperDuperRadioGroup extends LinearLayout {
    private final OnCheckedChangeListener checkListener = new OnCheckedChangeListener() {
        int lastCheckedPos = -1;

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                SuperDuperRadioGroup.this.check(buttonView.getId());
            }
            if (!(SuperDuperRadioGroup.this.onCheckedChangeListener == null || this.lastCheckedPos == SuperDuperRadioGroup.this.getCheckPosition())) {
                SuperDuperRadioGroup.this.onCheckedChangeListener.onCheckedChanged(null, 0);
            }
            this.lastCheckedPos = SuperDuperRadioGroup.this.getCheckPosition();
        }
    };
    private List<CompoundButton> checks;
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener;

    public SuperDuperRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperDuperRadioGroup(Context context) {
        super(context);
    }

    private ArrayList<View> getAllChildren(View v) {
        if (v instanceof ViewGroup) {
            ArrayList<View> result = new ArrayList();
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                ArrayList<View> viewArrayList = new ArrayList();
                viewArrayList.add(v);
                viewArrayList.addAll(getAllChildren(child));
                result.addAll(viewArrayList);
            }
            return result;
        }
        viewArrayList = new ArrayList();
        viewArrayList.add(v);
        return viewArrayList;
    }

    public final void fixup() {
        List<View> allChildren = getAllChildren(this);
        this.checks = new ArrayList();
        for (View v : allChildren) {
            if (v instanceof CompoundButton) {
                this.checks.add((CompoundButton) v);
                ((CompoundButton) v).setOnCheckedChangeListener(this.checkListener);
            }
        }
        System.out.println("managing " + this.checks.size() + " checkables");
    }

    public final int getCheckPosition() {
        for (int i = 0; i < this.checks.size(); i++) {
            if (((CompoundButton) this.checks.get(i)).isChecked()) {
                return i;
            }
        }
        return -1;
    }

    public final void check(int id) {
        for (CompoundButton c : this.checks) {
            if (c.getId() == id) {
                c.setChecked(true);
            } else {
                c.setChecked(false);
            }
        }
    }

    public final void checkPosition(int pos) {
        int i = 0;
        while (i < this.checks.size()) {
            ((CompoundButton) this.checks.get(i)).setChecked(i == pos);
            i++;
        }
    }

    public final void setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public final int getCheckCount() {
        return this.checks.size();
    }
}
