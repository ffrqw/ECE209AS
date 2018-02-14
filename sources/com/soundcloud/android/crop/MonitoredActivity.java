package com.soundcloud.android.crop;

import android.app.Activity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Iterator;

abstract class MonitoredActivity extends Activity {
    private final ArrayList<LifeCycleListener> listeners = new ArrayList();

    public interface LifeCycleListener {
        void onActivityDestroyed$18452068();

        void onActivityStarted$18452068();

        void onActivityStopped$18452068();
    }

    public static class LifeCycleAdapter implements LifeCycleListener {
        public void onActivityDestroyed$18452068() {
        }

        public void onActivityStarted$18452068() {
        }

        public void onActivityStopped$18452068() {
        }
    }

    MonitoredActivity() {
    }

    public void addLifeCycleListener(LifeCycleListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public void removeLifeCycleListener(LifeCycleListener listener) {
        this.listeners.remove(listener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            it.next();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((LifeCycleListener) it.next()).onActivityDestroyed$18452068();
        }
    }

    protected void onStart() {
        super.onStart();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((LifeCycleListener) it.next()).onActivityStarted$18452068();
        }
    }

    protected void onStop() {
        super.onStop();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((LifeCycleListener) it.next()).onActivityStopped$18452068();
        }
    }
}
