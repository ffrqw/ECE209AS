package com.rachio.iro.ui.newschedulerulepath.fragments.wizard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.newschedulerulepath.activity.BaseScheduleRuleActivity;
import com.rachio.iro.ui.newschedulerulepath.activity.ScheduleRuleWizardActivity;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment.Listener;
import com.rachio.iro.ui.newschedulerulepath.views.Indicator;
import com.rachio.iro.ui.newschedulerulepath.views.LockableViewPager;
import java.util.ArrayList;

public class WizardFragment extends BaseScheduleRuleFragment implements Listener {
    private static final String TAG = WizardFragment.class.getName();
    private FragmentPagerAdapter adapter;
    private Button back;
    private Indicator[] circles;
    private BaseScheduleRuleFragment[] fragments;
    private int lastPos = 0;
    private Button next;
    private LockableViewPager pager;
    private ViewGroup progressContainer;
    private ScheduleRule state;

    private void updateButtons() {
        boolean valid;
        boolean z;
        boolean z2 = true;
        int position = this.pager.getCurrentItem();
        if (this.fragments[position].isAdded()) {
            valid = this.fragments[position].validate();
        } else {
            valid = false;
        }
        LockableViewPager lockableViewPager = this.pager;
        if (valid) {
            z = false;
        } else {
            z = true;
        }
        lockableViewPager.setLocked(z);
        this.next.setEnabled(valid);
        Button button = this.back;
        if (position == 0) {
            z2 = false;
        }
        button.setEnabled(z2);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedulerulewizard_wizard, container, false);
        this.pager = (LockableViewPager) view.findViewById(R.id.schedulerule_wizard_pager);
        this.back = (Button) view.findViewById(R.id.schedulerulewizard_back);
        this.next = (Button) view.findViewById(R.id.schedulerulewizard_next);
        this.progressContainer = (ViewGroup) view.findViewById(R.id.progress_container);
        this.pager.addOnPageChangeListener(new OnPageChangeListener() {
            public final void onPageSelected(int position) {
                if (WizardFragment.this.fragments[WizardFragment.this.lastPos].isAdded()) {
                    Log.d(WizardFragment.TAG, "commiting state for " + WizardFragment.this.lastPos);
                    WizardFragment.this.fragments[WizardFragment.this.lastPos].commitState(WizardFragment.this.state);
                }
                WizardFragment.this.lastPos = position;
                if (WizardFragment.this.fragments[position].isAdded()) {
                    WizardFragment.this.fragments[position].updateState(WizardFragment.this.state);
                }
                for (int i = 0; i < WizardFragment.this.circles.length; i++) {
                    boolean z;
                    Indicator indicator = WizardFragment.this.circles[i];
                    if (i < position) {
                        z = true;
                    } else {
                        z = false;
                    }
                    indicator.setChecked(z);
                    indicator = WizardFragment.this.circles[i];
                    if (i == position) {
                        z = true;
                    } else {
                        z = false;
                    }
                    indicator.setSelected(z);
                }
                if (position == WizardFragment.this.fragments.length - 1) {
                    ((ScheduleRuleWizardActivity) ((BaseScheduleRuleActivity) WizardFragment.this.getActivity())).getPreview();
                    WizardFragment.this.next.setText(R.string.create);
                } else {
                    WizardFragment.this.next.setText(R.string.next);
                }
                WizardFragment.this.updateButtons();
            }

            public final void onPageScrollStateChanged(int state) {
            }

            public final void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
        });
        this.back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int pagerPos = WizardFragment.this.pager.getCurrentItem();
                if (pagerPos == 0) {
                    WizardFragment.this.getActivity().onBackPressed();
                } else {
                    WizardFragment.this.pager.setCurrentItem(pagerPos - 1, true);
                }
            }
        });
        this.next.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int pagerPos = WizardFragment.this.pager.getCurrentItem();
                if (pagerPos + 1 == WizardFragment.this.pager.getAdapter().getCount()) {
                    WizardFragment.this.next.setEnabled(false);
                    WizardFragment.this.fragments[pagerPos].commitState(WizardFragment.this.state);
                    WizardFragment.this.moveToNextStage();
                    return;
                }
                WizardFragment.this.pager.setCurrentItem(pagerPos + 1, true);
            }
        });
        return view;
    }

    private void updateAllFragments(ScheduleRule state) {
        for (BaseScheduleRuleFragment fragment : this.fragments) {
            if (fragment.isAdded()) {
                fragment.updateState(state);
            }
        }
        updateButtons();
    }

    public void onResume() {
        super.onResume();
        if (this.state != null) {
            updateAllFragments(this.state);
        }
    }

    private void setUpWizardFragments$3c7ec8c3() {
        int c;
        for (int i = 0; i < this.fragments.length; i++) {
            this.progressContainer.addView(new Indicator(getContext()));
            if (i + 1 != this.fragments.length) {
                LayoutInflater.from(getContext()).inflate(R.layout.merge_indicatorsep, this.progressContainer);
            }
        }
        this.circles = new Indicator[(((this.progressContainer.getChildCount() - 1) / 2) + 1)];
        for (c = 0; c < this.progressContainer.getChildCount(); c += 2) {
            this.circles[c / 2] = (Indicator) this.progressContainer.getChildAt(c);
        }
        this.circles[0].setSelected(true);
        for (c = 0; c < this.circles.length; c++) {
            final int stage = c;
            this.circles[c].setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (WizardFragment.this.fragments[WizardFragment.this.lastPos].validate()) {
                        WizardFragment.this.pager.setCurrentItem(stage, true);
                    }
                }
            });
        }
        for (BaseScheduleRuleFragment fragment : this.fragments) {
            fragment.setListener(this);
        }
        this.pager.setOffscreenPageLimit(this.fragments.length);
        this.pager.setAdapter(this.adapter);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.fragments != null) {
            setUpWizardFragments$3c7ec8c3();
        }
    }

    public final void updateState(ScheduleRule state) {
        super.updateState(state);
        this.state = state;
        if (this.fragments == null) {
            ArrayList<BaseScheduleRuleFragment> tempFragments = new ArrayList();
            tempFragments.add(new WizardZoneSelectFragment());
            if (!state.isFlex()) {
                switch (state.getFrequencyOperator()) {
                    case INTERVAL:
                        tempFragments.add(new WizardIntervalSelectFragment());
                        break;
                    case WEEKDAY:
                        tempFragments.add(new WizardWeekdaysSelectFragment());
                        break;
                    default:
                        break;
                }
            }
            tempFragments.add(new HowOftenToWaterFragment());
            tempFragments.add(new TimeToWaterFragment());
            tempFragments.add(new WhenToStartFragment());
            tempFragments.add(new AutomationFragment());
            tempFragments.add(new DurationFragment());
            this.fragments = (BaseScheduleRuleFragment[]) tempFragments.toArray(new BaseScheduleRuleFragment[tempFragments.size()]);
            this.adapter = new FragmentPagerAdapter(getChildFragmentManager()) {
                public final Fragment getItem(int position) {
                    return WizardFragment.this.fragments[position];
                }

                public final int getCount() {
                    return WizardFragment.this.fragments.length;
                }
            };
            if (getView() != null) {
                setUpWizardFragments$3c7ec8c3();
            }
        }
        updateAllFragments(state);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.snippet_cancel, menu);
    }

    public final void onGoing$5cc6ebe() {
        Log.d(TAG, "fragment going");
    }

    public final void onComing(BaseScheduleRuleFragment fragment) {
        Log.d(TAG, "fragment coming");
        if (this.state != null) {
            fragment.updateState(this.state);
        }
    }

    public final void onStateChanged$5cc6ebe() {
        updateButtons();
    }
}
