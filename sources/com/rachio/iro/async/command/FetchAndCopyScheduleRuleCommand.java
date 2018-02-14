package com.rachio.iro.async.command;

import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.user.User.DisplayUnit;

public class FetchAndCopyScheduleRuleCommand extends BaseCommand<ScheduleRuleAndCopy> {
    private final Listener listener;
    private final String ruleId;

    public interface Listener {
        void onScheduleRuleAndCopyLoaded(ScheduleRuleAndCopy scheduleRuleAndCopy);
    }

    public static class ScheduleRuleAndCopy {
        public final ScheduleRule copy;
        public final ScheduleRule original;
        public final DisplayUnit units;

        public ScheduleRuleAndCopy(ScheduleRule original, ScheduleRule copy, DisplayUnit units) {
            this.original = original;
            this.copy = copy;
            this.units = units;
        }
    }

    protected final /* bridge */ /* synthetic */ void handleResult(Object obj) {
        this.listener.onScheduleRuleAndCopyLoaded((ScheduleRuleAndCopy) obj);
    }

    public FetchAndCopyScheduleRuleCommand(String ruleId, Listener listener) {
        this.listener = listener;
        this.ruleId = ruleId;
        BaseCommand.component(listener).inject(this);
    }

    protected final /* bridge */ /* synthetic */ Object loadResult() {
        ScheduleRule scheduleRule = (ScheduleRule) this.database.find(ScheduleRule.class, this.ruleId);
        if (scheduleRule == null) {
            return null;
        }
        ScheduleRule scheduleRule2 = (ScheduleRule) ModelObject.deepClone(ScheduleRule.class, scheduleRule);
        scheduleRule2.device = scheduleRule.device;
        return new ScheduleRuleAndCopy(scheduleRule, scheduleRule2, ((User) this.database.find(User.class, this.prefsWrapper.getLoggedInUserId())).displayUnit);
    }
}
