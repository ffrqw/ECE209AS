package com.squareup.picasso;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;

final class Dispatcher {
    boolean airplaneMode;
    final List<BitmapHunter> batch;
    final Cache cache;
    final Context context;
    final DispatcherThread dispatcherThread = new DispatcherThread();
    final Downloader downloader;
    final Map<Object, Action> failedActions;
    final Handler handler;
    final Map<String, BitmapHunter> hunterMap;
    final Handler mainThreadHandler;
    final Map<Object, Action> pausedActions;
    final Set<Object> pausedTags;
    final NetworkBroadcastReceiver receiver;
    final boolean scansNetworkChanges;
    final ExecutorService service;
    final Stats stats;

    private static class DispatcherHandler extends Handler {
        private final Dispatcher dispatcher;

        public DispatcherHandler(Looper looper, Dispatcher dispatcher) {
            super(looper);
            this.dispatcher = dispatcher;
        }

        public final void handleMessage(Message msg) {
            Dispatcher dispatcher;
            BitmapHunter bitmapHunter;
            switch (msg.what) {
                case 1:
                    this.dispatcher.performSubmit(msg.obj, true);
                    return;
                case 2:
                    Action action = (Action) msg.obj;
                    dispatcher = this.dispatcher;
                    String str = action.key;
                    bitmapHunter = (BitmapHunter) dispatcher.hunterMap.get(str);
                    if (bitmapHunter != null) {
                        bitmapHunter.detach(action);
                        if (bitmapHunter.cancel()) {
                            dispatcher.hunterMap.remove(str);
                            if (action.picasso.loggingEnabled) {
                                Utils.log("Dispatcher", "canceled", action.request.logId());
                            }
                        }
                    }
                    if (dispatcher.pausedTags.contains(action.tag)) {
                        dispatcher.pausedActions.remove(action.getTarget());
                        if (action.picasso.loggingEnabled) {
                            Utils.log("Dispatcher", "canceled", action.request.logId(), "because paused request got canceled");
                        }
                    }
                    Action action2 = (Action) dispatcher.failedActions.remove(action.getTarget());
                    if (action2 != null && action2.picasso.loggingEnabled) {
                        Utils.log("Dispatcher", "canceled", action2.request.logId(), "from replaying");
                        return;
                    }
                    return;
                case 4:
                    this.dispatcher.performComplete(msg.obj);
                    return;
                case 5:
                    this.dispatcher.performRetry((BitmapHunter) msg.obj);
                    return;
                case 6:
                    this.dispatcher.performError((BitmapHunter) msg.obj, false);
                    return;
                case 7:
                    this.dispatcher.performBatchComplete();
                    return;
                case 9:
                    this.dispatcher.performNetworkStateChange(msg.obj);
                    return;
                case 10:
                    boolean z;
                    dispatcher = this.dispatcher;
                    if (msg.arg1 == 1) {
                        z = true;
                    } else {
                        z = false;
                    }
                    dispatcher.airplaneMode = z;
                    return;
                case 11:
                    Object tag = msg.obj;
                    Dispatcher dispatcher2 = this.dispatcher;
                    if (dispatcher2.pausedTags.add(tag)) {
                        Iterator it = dispatcher2.hunterMap.values().iterator();
                        while (it.hasNext()) {
                            bitmapHunter = (BitmapHunter) it.next();
                            boolean z2 = bitmapHunter.picasso.loggingEnabled;
                            Action action3 = bitmapHunter.action;
                            List list = bitmapHunter.actions;
                            Object obj = (list == null || list.isEmpty()) ? null : 1;
                            if (action3 != null || obj != null) {
                                if (action3 != null && action3.tag.equals(tag)) {
                                    bitmapHunter.detach(action3);
                                    dispatcher2.pausedActions.put(action3.getTarget(), action3);
                                    if (z2) {
                                        Utils.log("Dispatcher", "paused", action3.request.logId(), "because tag '" + tag + "' was paused");
                                    }
                                }
                                if (obj != null) {
                                    for (int size = list.size() - 1; size >= 0; size--) {
                                        Action action4 = (Action) list.get(size);
                                        if (action4.tag.equals(tag)) {
                                            bitmapHunter.detach(action4);
                                            dispatcher2.pausedActions.put(action4.getTarget(), action4);
                                            if (z2) {
                                                Utils.log("Dispatcher", "paused", action4.request.logId(), "because tag '" + tag + "' was paused");
                                            }
                                        }
                                    }
                                }
                                if (bitmapHunter.cancel()) {
                                    it.remove();
                                    if (z2) {
                                        Utils.log("Dispatcher", "canceled", Utils.getLogIdsForHunter(bitmapHunter), "all actions paused");
                                    }
                                }
                            }
                        }
                        return;
                    }
                    return;
                case 12:
                    this.dispatcher.performResumeTag(msg.obj);
                    return;
                default:
                    final Message message = msg;
                    Picasso.HANDLER.post(new Runnable() {
                        public final void run() {
                            throw new AssertionError("Unknown handler message received: " + message.what);
                        }
                    });
                    return;
            }
        }
    }

    static class DispatcherThread extends HandlerThread {
        DispatcherThread() {
            super("Picasso-Dispatcher", 10);
        }
    }

    static class NetworkBroadcastReceiver extends BroadcastReceiver {
        private final Dispatcher dispatcher;

        NetworkBroadcastReceiver(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        final void register() {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.AIRPLANE_MODE");
            if (this.dispatcher.scansNetworkChanges) {
                filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            }
            this.dispatcher.context.registerReceiver(this, filter);
        }

        public final void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                Dispatcher dispatcher;
                if ("android.intent.action.AIRPLANE_MODE".equals(action)) {
                    if (intent.hasExtra("state")) {
                        dispatcher = this.dispatcher;
                        dispatcher.handler.sendMessage(dispatcher.handler.obtainMessage(10, intent.getBooleanExtra("state", false) ? 1 : 0, 0));
                    }
                } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) Utils.getService(context, "connectivity");
                    dispatcher = this.dispatcher;
                    dispatcher.handler.sendMessage(dispatcher.handler.obtainMessage(9, connectivityManager.getActiveNetworkInfo()));
                }
            }
        }
    }

    Dispatcher(Context context, ExecutorService service, Handler mainThreadHandler, Downloader downloader, Cache cache, Stats stats) {
        this.dispatcherThread.start();
        Utils.flushStackLocalLeaks(this.dispatcherThread.getLooper());
        this.context = context;
        this.service = service;
        this.hunterMap = new LinkedHashMap();
        this.failedActions = new WeakHashMap();
        this.pausedActions = new WeakHashMap();
        this.pausedTags = new HashSet();
        this.handler = new DispatcherHandler(this.dispatcherThread.getLooper(), this);
        this.downloader = downloader;
        this.mainThreadHandler = mainThreadHandler;
        this.cache = cache;
        this.stats = stats;
        this.batch = new ArrayList(4);
        this.airplaneMode = Utils.isAirplaneModeOn(this.context);
        this.scansNetworkChanges = Utils.hasPermission(context, "android.permission.ACCESS_NETWORK_STATE");
        this.receiver = new NetworkBroadcastReceiver(this);
        this.receiver.register();
    }

    final void dispatchFailed(BitmapHunter hunter) {
        this.handler.sendMessage(this.handler.obtainMessage(6, hunter));
    }

    final void performSubmit(Action action, boolean dismissFailed) {
        if (this.pausedTags.contains(action.tag)) {
            this.pausedActions.put(action.getTarget(), action);
            if (action.picasso.loggingEnabled) {
                Utils.log("Dispatcher", "paused", action.request.logId(), "because tag '" + action.tag + "' is paused");
                return;
            }
            return;
        }
        BitmapHunter hunter = (BitmapHunter) this.hunterMap.get(action.key);
        if (hunter != null) {
            boolean z = hunter.picasso.loggingEnabled;
            Request request = action.request;
            if (hunter.action == null) {
                hunter.action = action;
                if (!z) {
                    return;
                }
                if (hunter.actions == null || hunter.actions.isEmpty()) {
                    Utils.log("Hunter", "joined", request.logId(), "to empty hunter");
                    return;
                } else {
                    Utils.log("Hunter", "joined", request.logId(), Utils.getLogIdsForHunter(hunter, "to "));
                    return;
                }
            }
            if (hunter.actions == null) {
                hunter.actions = new ArrayList(3);
            }
            hunter.actions.add(action);
            if (z) {
                Utils.log("Hunter", "joined", request.logId(), Utils.getLogIdsForHunter(hunter, "to "));
            }
            int i = action.request.priority$159b5429;
            if (i - 1 > hunter.priority$159b5429 - 1) {
                hunter.priority$159b5429 = i;
            }
        } else if (!this.service.isShutdown()) {
            hunter = BitmapHunter.forRequest(action.picasso, this, this.cache, this.stats, action);
            hunter.future = this.service.submit(hunter);
            this.hunterMap.put(action.key, hunter);
            if (dismissFailed) {
                this.failedActions.remove(action.getTarget());
            }
            if (action.picasso.loggingEnabled) {
                Utils.log("Dispatcher", "enqueued", action.request.logId());
            }
        } else if (action.picasso.loggingEnabled) {
            Utils.log("Dispatcher", "ignored", action.request.logId(), "because shut down");
        }
    }

    final void performResumeTag(Object tag) {
        if (this.pausedTags.remove(tag)) {
            List<Action> batch = null;
            Iterator<Action> i = this.pausedActions.values().iterator();
            while (i.hasNext()) {
                Action action = (Action) i.next();
                if (action.tag.equals(tag)) {
                    if (batch == null) {
                        batch = new ArrayList();
                    }
                    batch.add(action);
                    i.remove();
                }
            }
            if (batch != null) {
                this.mainThreadHandler.sendMessage(this.mainThreadHandler.obtainMessage(13, batch));
            }
        }
    }

    final void performRetry(BitmapHunter hunter) {
        boolean willReplay = true;
        if (!hunter.isCancelled()) {
            if (this.service.isShutdown()) {
                performError(hunter, false);
                return;
            }
            NetworkInfo networkInfo = null;
            if (this.scansNetworkChanges) {
                networkInfo = ((ConnectivityManager) Utils.getService(this.context, "connectivity")).getActiveNetworkInfo();
            }
            boolean z = this.airplaneMode;
            if (hunter.retryCount > 0) {
                hunter.retryCount--;
                z = hunter.requestHandler.shouldRetry$552f0f64(networkInfo);
            } else {
                z = false;
            }
            if (z) {
                if (hunter.picasso.loggingEnabled) {
                    Utils.log("Dispatcher", "retrying", Utils.getLogIdsForHunter(hunter));
                }
                if (hunter.exception instanceof ContentLengthException) {
                    hunter.networkPolicy |= NetworkPolicy.NO_CACHE.index;
                }
                hunter.future = this.service.submit(hunter);
                return;
            }
            if (!(this.scansNetworkChanges && hunter.requestHandler.supportsReplay())) {
                willReplay = false;
            }
            performError(hunter, willReplay);
            if (willReplay) {
                Action action = hunter.action;
                if (action != null) {
                    markForReplay(action);
                }
                List list = hunter.actions;
                if (list != null) {
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        markForReplay((Action) list.get(i));
                    }
                }
            }
        }
    }

    final void performBatchComplete() {
        List<BitmapHunter> copy = new ArrayList(this.batch);
        this.batch.clear();
        this.mainThreadHandler.sendMessage(this.mainThreadHandler.obtainMessage(8, copy));
        logBatch(copy);
    }

    final void performNetworkStateChange(NetworkInfo info) {
        if (this.service instanceof PicassoExecutorService) {
            ((PicassoExecutorService) this.service).adjustThreadCount(info);
        }
        if (info != null && info.isConnected() && !this.failedActions.isEmpty()) {
            Iterator it = this.failedActions.values().iterator();
            while (it.hasNext()) {
                Action action = (Action) it.next();
                it.remove();
                if (action.picasso.loggingEnabled) {
                    Utils.log("Dispatcher", "replaying", action.request.logId());
                }
                performSubmit(action, false);
            }
        }
    }

    private void markForReplay(Action action) {
        Object target = action.getTarget();
        if (target != null) {
            action.willReplay = true;
            this.failedActions.put(target, action);
        }
    }

    private void batch(BitmapHunter hunter) {
        if (!hunter.isCancelled()) {
            if (hunter.result != null) {
                hunter.result.prepareToDraw();
            }
            this.batch.add(hunter);
            if (!this.handler.hasMessages(7)) {
                this.handler.sendEmptyMessageDelayed(7, 200);
            }
        }
    }

    private static void logBatch(List<BitmapHunter> copy) {
        if (!copy.isEmpty() && ((BitmapHunter) copy.get(0)).picasso.loggingEnabled) {
            StringBuilder builder = new StringBuilder();
            for (BitmapHunter bitmapHunter : copy) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(Utils.getLogIdsForHunter(bitmapHunter));
            }
            Utils.log("Dispatcher", "delivered", builder.toString());
        }
    }

    final void performComplete(BitmapHunter hunter) {
        if (MemoryPolicy.shouldWriteToMemoryCache(hunter.memoryPolicy)) {
            this.cache.set(hunter.key, hunter.result);
        }
        this.hunterMap.remove(hunter.key);
        batch(hunter);
        if (hunter.picasso.loggingEnabled) {
            Utils.log("Dispatcher", "batched", Utils.getLogIdsForHunter(hunter), "for completion");
        }
    }

    final void performError(BitmapHunter hunter, boolean willReplay) {
        if (hunter.picasso.loggingEnabled) {
            Utils.log("Dispatcher", "batched", Utils.getLogIdsForHunter(hunter), "for error" + (willReplay ? " (will replay)" : ""));
        }
        this.hunterMap.remove(hunter.key);
        batch(hunter);
    }
}
