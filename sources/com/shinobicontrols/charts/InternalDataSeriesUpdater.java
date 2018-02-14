package com.shinobicontrols.charts;

interface InternalDataSeriesUpdater {

    public interface PostUpdateCallback {
        public static final PostUpdateCallback NO_ACTION = new PostUpdateCallback() {
            public final void postAction(Series<?> series) {
            }
        };

        void postAction(Series<?> series);
    }

    public interface PreUpdateCallback {
        public static final PreUpdateCallback NO_ACTION = new PreUpdateCallback() {
            public final void preAction(Series<?> series) {
            }
        };

        void preAction(Series<?> series);
    }

    void a(Series<?> series, PreUpdateCallback preUpdateCallback, PostUpdateCallback postUpdateCallback);
}
