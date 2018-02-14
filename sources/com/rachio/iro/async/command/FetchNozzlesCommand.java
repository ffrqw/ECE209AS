package com.rachio.iro.async.command;

import com.rachio.iro.model.user.User;
import com.rachio.iro.model.zoneproperties.Nozzle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FetchNozzlesCommand extends BaseCommand<NozzleDataHolder> {
    private final Listener listener;
    private final String userId;

    public interface Listener {
        void onNozzlesLoaded(NozzleDataHolder nozzleDataHolder);
    }

    public static class NozzleDataHolder {
        public final List<Nozzle> allNozzles;
        public final List<Nozzle> editableNozzles;
        public final User user;
        public final Map<String, List<String>> zonesAssociatedToNozzles;

        public NozzleDataHolder(User user, List<Nozzle> allNozzles, List<Nozzle> editableNozzles, Map<String, List<String>> zonesAssociatedToNozzles) {
            this.user = user;
            this.allNozzles = allNozzles;
            this.editableNozzles = editableNozzles;
            this.zonesAssociatedToNozzles = zonesAssociatedToNozzles;
        }
    }

    protected final /* bridge */ /* synthetic */ void handleResult(Object obj) {
        this.listener.onNozzlesLoaded((NozzleDataHolder) obj);
    }

    public FetchNozzlesCommand(Listener listener, String userId) {
        if (listener == null || userId == null) {
            throw new IllegalArgumentException();
        }
        this.listener = listener;
        this.userId = userId;
        BaseCommand.component(listener).inject(this);
    }

    protected final /* bridge */ /* synthetic */ Object loadResult() {
        User user = (User) this.database.find(User.class, this.userId);
        if (user == null || user.nozzles == null) {
            return null;
        }
        Nozzle[] nozzleArr = user.nozzles;
        Arrays.sort(nozzleArr);
        List arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        for (Nozzle nozzle : nozzleArr) {
            if (nozzle.editable) {
                arrayList.add(nozzle);
            }
            arrayList2.add(nozzle);
        }
        return new NozzleDataHolder(user, arrayList2, arrayList, new TreeMap());
    }
}
