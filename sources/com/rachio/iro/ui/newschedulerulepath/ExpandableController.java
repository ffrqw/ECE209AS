package com.rachio.iro.ui.newschedulerulepath;

import com.rachio.iro.ui.newschedulerulepath.views.Expandable;
import com.rachio.iro.ui.newschedulerulepath.views.Expandable.Listener;
import java.util.ArrayList;
import java.util.Iterator;

public class ExpandableController implements Listener {
    private ArrayList<Expandable> expandables = new ArrayList();

    public final void add(Expandable expandable) {
        expandable.setExpandListener(this);
        this.expandables.add(expandable);
    }

    public final void onExpanded(Expandable expandable) {
        Iterator it = this.expandables.iterator();
        while (it.hasNext()) {
            Expandable e = (Expandable) it.next();
            if (e != expandable) {
                e.collapse();
            }
        }
    }
}
