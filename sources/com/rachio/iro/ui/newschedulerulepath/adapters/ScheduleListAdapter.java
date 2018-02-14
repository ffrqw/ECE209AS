package com.rachio.iro.ui.newschedulerulepath.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import com.rachio.iro.async.command.FetchCalendarCommand.ScheduleCalendarMeta;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.schedule.ScheduleItem;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ZoneInfo;
import com.rachio.iro.ui.view.ScheduleRowItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ScheduleListAdapter extends ArrayAdapter<ScheduleRule> {
    private final ScheduleCalendarMeta calendar;
    private boolean filtered;
    private Date filteredDate;
    private final List<ScheduleRule> filteredRules;
    private final List<ScheduleRule> rules;
    private final Map<String, Zone> zonesMap;

    private final class RuleFilter extends Filter {
        private RuleFilter() {
        }

        protected final FilterResults performFiltering(CharSequence constraint) {
            List<ScheduleRule> values = new ArrayList();
            FilterResults filterResults = new FilterResults();
            filterResults.count = 0;
            filterResults.values = values;
            if (constraint != null) {
                ScheduleListAdapter.this.filtered = true;
                Date when = new Date();
                when.setTime(Long.parseLong(constraint.toString()));
                ScheduleListAdapter.this.filteredDate = when;
                for (ScheduleRule r : ScheduleListAdapter.this.rules) {
                    if (ScheduleListAdapter.this.calendar.calendar.willRuleRunOnDate(r.id, when)) {
                        filterResults.count++;
                        values.add(r);
                    }
                }
            } else {
                ScheduleListAdapter.this.filtered = false;
                ScheduleListAdapter.this.filteredDate = null;
                values.addAll(ScheduleListAdapter.this.rules);
                filterResults.count = ScheduleListAdapter.this.rules.size();
            }
            return filterResults;
        }

        protected final void publishResults(CharSequence constraint, FilterResults results) {
            ScheduleListAdapter.this.filteredRules.clear();
            ScheduleListAdapter.this.filteredRules.addAll((List) results.values);
            ScheduleListAdapter.this.notifyDataSetChanged();
        }
    }

    public ScheduleListAdapter(Context context, List<ScheduleRule> rules, ScheduleCalendarMeta calendar) {
        super(context, -1, rules);
        this.rules = new ArrayList();
        this.filtered = false;
        this.calendar = calendar;
        this.filteredRules = rules;
        this.rules.addAll(this.filteredRules);
        this.zonesMap = calendar.zonesMap;
    }

    public ScheduleListAdapter(Context context, List<ScheduleRule> rules, Map<String, Zone> zonesMap) {
        super(context, -1, rules);
        this.rules = new ArrayList();
        this.filtered = false;
        this.calendar = null;
        this.filteredRules = rules;
        this.rules.addAll(this.filteredRules);
        this.zonesMap = zonesMap;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ScheduleRowItem view;
        if (convertView == null) {
            view = new ScheduleRowItem(getContext());
        } else {
            view = (ScheduleRowItem) convertView;
        }
        ScheduleRule rule = (ScheduleRule) getItem(position);
        int cycles = rule.cycleSoak ? rule.cycles : 1;
        int totalDuration = rule.totalDuration;
        int totalDurationNoCycle = rule.totalDurationNoCycle;
        List<ZoneInfo> zones = rule.zones;
        if (this.filtered) {
            ScheduleItem si = this.calendar.calendar.getScheduleItemForDate(this.filteredDate, rule.id);
            if (si != null) {
                cycles = si.cycleSoak ? si.totalCycleCount : 1;
                totalDuration = si.totalDuration;
                totalDurationNoCycle = si.getTotalDurationNoCycle();
                zones = si.zones;
            }
            if (zones != null) {
                Collections.sort(zones);
            }
        }
        view.setScheduleRule(rule, this.filtered, cycles, (long) totalDuration, (long) totalDurationNoCycle, this.zonesMap, zones);
        return view;
    }

    public Filter getFilter() {
        return new RuleFilter();
    }
}
