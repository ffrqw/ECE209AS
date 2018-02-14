package com.squareup.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.ImageView;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;

public class Picasso {
    static final Handler HANDLER = new Handler(Looper.getMainLooper()) {
        public final void handleMessage(Message msg) {
            Action action;
            int n;
            int i;
            switch (msg.what) {
                case 3:
                    action = msg.obj;
                    if (action.picasso.loggingEnabled) {
                        Utils.log("Main", "canceled", action.request.logId(), "target got garbage collected");
                    }
                    action.picasso.cancelExistingRequest(action.getTarget());
                    return;
                case 8:
                    List<BitmapHunter> batch = msg.obj;
                    n = batch.size();
                    for (i = 0; i < n; i++) {
                        BitmapHunter hunter = (BitmapHunter) batch.get(i);
                        hunter.picasso.complete(hunter);
                    }
                    return;
                case 13:
                    List<Action> batch2 = msg.obj;
                    n = batch2.size();
                    for (i = 0; i < n; i++) {
                        action = (Action) batch2.get(i);
                        action.picasso.resumeAction(action);
                    }
                    return;
                default:
                    throw new AssertionError("Unknown handler message received: " + msg.what);
            }
        }
    };
    static volatile Picasso singleton = null;
    final Cache cache;
    private final CleanupThread cleanupThread;
    final Context context;
    final Config defaultBitmapConfig;
    final Dispatcher dispatcher;
    boolean indicatorsEnabled;
    private final Listener listener;
    volatile boolean loggingEnabled;
    final ReferenceQueue<Object> referenceQueue;
    private final List<RequestHandler> requestHandlers;
    private final RequestTransformer requestTransformer;
    boolean shutdown;
    final Stats stats;
    final Map<Object, Action> targetToAction;
    final Map<ImageView, DeferredRequestCreator> targetToDeferredRequestCreator;

    public static class Builder {
        private Cache cache;
        private final Context context;
        private Downloader downloader;
        private ExecutorService service;
        private RequestTransformer transformer;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context.getApplicationContext();
        }

        public final Picasso build() {
            Context context = this.context;
            if (this.downloader == null) {
                this.downloader = new OkHttp3Downloader(context);
            }
            if (this.cache == null) {
                this.cache = new LruCache(context);
            }
            if (this.service == null) {
                this.service = new PicassoExecutorService();
            }
            if (this.transformer == null) {
                this.transformer = RequestTransformer.IDENTITY;
            }
            Stats stats = new Stats(this.cache);
            return new Picasso(context, new Dispatcher(context, this.service, Picasso.HANDLER, this.downloader, this.cache, stats), this.cache, null, this.transformer, null, stats, null, false, false);
        }
    }

    private static class CleanupThread extends Thread {
        private final Handler handler;
        private final ReferenceQueue<Object> referenceQueue;

        CleanupThread(ReferenceQueue<Object> referenceQueue, Handler handler) {
            this.referenceQueue = referenceQueue;
            this.handler = handler;
            setDaemon(true);
            setName("Picasso-refQueue");
        }

        public final void run() {
            Process.setThreadPriority(10);
            while (true) {
                try {
                    RequestWeakReference<?> remove = (RequestWeakReference) this.referenceQueue.remove(1000);
                    Message message = this.handler.obtainMessage();
                    if (remove != null) {
                        message.what = 3;
                        message.obj = remove.action;
                        this.handler.sendMessage(message);
                    } else {
                        message.recycle();
                    }
                } catch (InterruptedException e) {
                    return;
                } catch (final Exception e2) {
                    this.handler.post(new Runnable() {
                        public final void run() {
                            throw new RuntimeException(e2);
                        }
                    });
                    return;
                }
            }
        }
    }

    public interface Listener {
    }

    public enum LoadedFrom {
        MEMORY(-16711936),
        DISK(-16776961),
        NETWORK(-65536);
        
        final int debugColor;

        private LoadedFrom(int debugColor) {
            this.debugColor = debugColor;
        }
    }

    public enum Priority {
        ;

        static {
            LOW$159b5429 = 1;
            NORMAL$159b5429 = 2;
            HIGH$159b5429 = 3;
            $VALUES$1df6b4e4 = new int[]{1, 2, 3};
        }
    }

    public interface RequestTransformer {
        public static final RequestTransformer IDENTITY = new RequestTransformer() {
            public final Request transformRequest(Request request) {
                return request;
            }
        };

        Request transformRequest(Request request);
    }

    Picasso(Context context, Dispatcher dispatcher, Cache cache, Listener listener, RequestTransformer requestTransformer, List<RequestHandler> extraRequestHandlers, Stats stats, Config defaultBitmapConfig, boolean indicatorsEnabled, boolean loggingEnabled) {
        this.context = context;
        this.dispatcher = dispatcher;
        this.cache = cache;
        this.listener = listener;
        this.requestTransformer = requestTransformer;
        this.defaultBitmapConfig = defaultBitmapConfig;
        List<RequestHandler> allRequestHandlers = new ArrayList((extraRequestHandlers != null ? extraRequestHandlers.size() : 0) + 7);
        allRequestHandlers.add(new ResourceRequestHandler(context));
        if (extraRequestHandlers != null) {
            allRequestHandlers.addAll(extraRequestHandlers);
        }
        allRequestHandlers.add(new ContactsPhotoRequestHandler(context));
        allRequestHandlers.add(new MediaStoreRequestHandler(context));
        allRequestHandlers.add(new ContentStreamRequestHandler(context));
        allRequestHandlers.add(new AssetRequestHandler(context));
        allRequestHandlers.add(new FileRequestHandler(context));
        allRequestHandlers.add(new NetworkRequestHandler(dispatcher.downloader, stats));
        this.requestHandlers = Collections.unmodifiableList(allRequestHandlers);
        this.stats = stats;
        this.targetToAction = new WeakHashMap();
        this.targetToDeferredRequestCreator = new WeakHashMap();
        this.indicatorsEnabled = indicatorsEnabled;
        this.loggingEnabled = loggingEnabled;
        this.referenceQueue = new ReferenceQueue();
        this.cleanupThread = new CleanupThread(this.referenceQueue, HANDLER);
        this.cleanupThread.start();
    }

    public final void cancelRequest(ImageView view) {
        if (view == null) {
            throw new IllegalArgumentException("view cannot be null.");
        }
        cancelExistingRequest(view);
    }

    public final RequestCreator load(String path) {
        if (path == null) {
            return new RequestCreator(this, null, 0);
        }
        if (path.trim().length() != 0) {
            return new RequestCreator(this, Uri.parse(path), 0);
        }
        throw new IllegalArgumentException("Path must not be empty.");
    }

    final List<RequestHandler> getRequestHandlers() {
        return this.requestHandlers;
    }

    final Request transformRequest(Request request) {
        Request transformed = this.requestTransformer.transformRequest(request);
        if (transformed != null) {
            return transformed;
        }
        throw new IllegalStateException("Request transformer " + this.requestTransformer.getClass().getCanonicalName() + " returned null for " + request);
    }

    final void enqueueAndSubmit(Action action) {
        Object target = action.getTarget();
        if (!(target == null || this.targetToAction.get(target) == action)) {
            cancelExistingRequest(target);
            this.targetToAction.put(target, action);
        }
        submit(action);
    }

    final void submit(Action action) {
        Dispatcher dispatcher = this.dispatcher;
        dispatcher.handler.sendMessage(dispatcher.handler.obtainMessage(1, action));
    }

    final Bitmap quickMemoryCacheCheck(String key) {
        Bitmap cached = this.cache.get(key);
        if (cached != null) {
            this.stats.dispatchCacheHit();
        } else {
            this.stats.handler.sendEmptyMessage(1);
        }
        return cached;
    }

    final void complete(BitmapHunter hunter) {
        boolean hasMultiple;
        boolean shouldDeliver = false;
        Action single = hunter.action;
        List<Action> joined = hunter.actions;
        if (joined == null || joined.isEmpty()) {
            hasMultiple = false;
        } else {
            hasMultiple = true;
        }
        if (single != null || hasMultiple) {
            shouldDeliver = true;
        }
        if (shouldDeliver) {
            Uri uri = hunter.data.uri;
            Exception exception = hunter.exception;
            Bitmap result = hunter.result;
            LoadedFrom from = hunter.loadedFrom;
            if (single != null) {
                deliverAction(result, from, single, exception);
            }
            if (hasMultiple) {
                int n = joined.size();
                for (int i = 0; i < n; i++) {
                    deliverAction(result, from, (Action) joined.get(i), exception);
                }
            }
        }
    }

    final void resumeAction(Action action) {
        Bitmap bitmap = null;
        if (MemoryPolicy.shouldReadFromMemoryCache(action.memoryPolicy)) {
            bitmap = quickMemoryCacheCheck(action.key);
        }
        if (bitmap != null) {
            deliverAction(bitmap, LoadedFrom.MEMORY, action, null);
            if (this.loggingEnabled) {
                Utils.log("Main", "completed", action.request.logId(), "from " + LoadedFrom.MEMORY);
                return;
            }
            return;
        }
        enqueueAndSubmit(action);
        if (this.loggingEnabled) {
            Utils.log("Main", "resumed", action.request.logId());
        }
    }

    final void cancelExistingRequest(Object target) {
        Utils.checkMain();
        Action action = (Action) this.targetToAction.remove(target);
        if (action != null) {
            action.cancel();
            Dispatcher dispatcher = this.dispatcher;
            dispatcher.handler.sendMessage(dispatcher.handler.obtainMessage(2, action));
        }
        if (target instanceof ImageView) {
            DeferredRequestCreator deferredRequestCreator = (DeferredRequestCreator) this.targetToDeferredRequestCreator.remove((ImageView) target);
            if (deferredRequestCreator != null) {
                deferredRequestCreator.cancel();
            }
        }
    }

    public static Picasso with() {
        if (singleton == null) {
            synchronized (Picasso.class) {
                if (singleton == null) {
                    if (PicassoProvider.context == null) {
                        throw new IllegalStateException("context == null");
                    }
                    singleton = new Builder(PicassoProvider.context).build();
                }
            }
        }
        return singleton;
    }

    private void deliverAction(Bitmap result, LoadedFrom from, Action action, Exception e) {
        if (!action.cancelled) {
            if (!action.willReplay) {
                this.targetToAction.remove(action.getTarget());
            }
            if (result == null) {
                action.error(e);
                if (this.loggingEnabled) {
                    Utils.log("Main", "errored", action.request.logId(), e.getMessage());
                }
            } else if (from == null) {
                throw new AssertionError("LoadedFrom cannot be null.");
            } else {
                action.complete(result, from);
                if (this.loggingEnabled) {
                    Utils.log("Main", "completed", action.request.logId(), "from " + from);
                }
            }
        }
    }
}
