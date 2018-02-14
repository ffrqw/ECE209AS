package com.rachio.iro.ui.newschedulerulepath.views;

public interface Expandable {

    public interface Listener {
        void onExpanded(Expandable expandable);
    }

    void collapse();

    void setExpandListener(Listener listener);
}
