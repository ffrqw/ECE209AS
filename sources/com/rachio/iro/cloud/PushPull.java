package com.rachio.iro.cloud;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import com.rachio.iro.IroApplication;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.annotation.DatabaseOptions;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.db.DatabaseObject;
import com.rachio.iro.utils.ProgressDialogAsyncTask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PushPull extends IntentService {
    private static final String TAG = PushPull.class.getSimpleName();
    public Database database;
    public RestClient restClient;

    private static class PullJob<T extends DatabaseObject> implements Callable<T> {
        private final boolean altParent;
        private final Database database;
        private final String id;
        private final String parentId;
        private final RestClient restClient;
        private final Class<T> type;

        public PullJob(Database database, RestClient restClient, Class<T> type, String id, String parentId, boolean altParent) {
            this.database = database;
            this.restClient = restClient;
            this.type = type;
            this.id = id;
            this.parentId = parentId;
            this.altParent = altParent;
        }

        public /* bridge */ /* synthetic */ Object call() throws Exception {
            return PushPull.pullEntityAndSaveUnlocked(this.database, this.restClient, this.type, this.id, this.parentId, this.altParent);
        }
    }

    public static abstract class PushPullUpdateAsyncTask<Type extends DatabaseObject> extends ProgressDialogAsyncTask<Type, Void, Type> {
    }

    public PushPull() {
        super("pushpull");
    }

    public static <T extends DatabaseObject> T pullEntityAndSave(Database database, RestClient restClient, Class<T> type, String id) {
        return pullEntityAndSave(database, restClient, type, id, null);
    }

    public static <T extends DatabaseObject> T pullEntityAndSave(Database database, RestClient restClient, Class<T> type, String id, String parentId) {
        return pullEntityAndSave(database, restClient, type, id, parentId, false);
    }

    private static <T extends DatabaseObject> T pullEntityAndSaveUnlocked(Database database, RestClient restClient, Class<T> type, String id, String parentId, boolean altParent) {
        System.currentTimeMillis();
        HttpResponseErrorHandler errorHandler = new HttpResponseErrorHandler();
        T entity = (DatabaseObject) restClient.getObjectById(database, id, type, errorHandler);
        System.currentTimeMillis();
        if (errorHandler.hasError || entity == null) {
            entity = null;
        } else {
            DatabaseOptions databaseOptions = Database.getDatabaseOptions((Class) type);
            if (databaseOptions.parent() != DatabaseObject.class) {
                if (parentId == null) {
                    throw new RuntimeException(type + "'s parent is " + databaseOptions.parent() + " but the parent id was null");
                }
                try {
                    DatabaseObject parent = (DatabaseObject) databaseOptions.parent().newInstance();
                    parent.id = parentId;
                    if (altParent) {
                        entity.setAltParent(parent);
                    } else {
                        entity.setParent(parent);
                    }
                } catch (InstantiationException ex) {
                    throw new RuntimeException(ex);
                } catch (IllegalAccessException ex2) {
                    throw new RuntimeException(ex2);
                }
            }
            database.save(entity, true, true, false);
            System.currentTimeMillis();
        }
        System.currentTimeMillis();
        return entity;
    }

    public static <T extends DatabaseObject> T pullEntityAndSave(Database database, RestClient restClient, Class<T> type, String id, String parentId, boolean altParent) {
        database.lock();
        T entity = pullEntityAndSaveUnlocked(database, restClient, type, id, parentId, altParent);
        database.unlock();
        return entity;
    }

    private static <T extends DatabaseObject> Collection<T> pullAndSaveEntities(Database database, RestClient restClient, Class<T> type, Collection<String> ids, String parentId, boolean altParent) {
        List<T> result = new ArrayList();
        int threads = 1;
        if (VERSION.SDK_INT >= 21) {
            threads = 3;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        Iterator<String> iterator = ids.iterator();
        ArrayList<PullJob<T>> jobs = new ArrayList();
        while (iterator.hasNext()) {
            database.lock();
            jobs.clear();
            while (jobs.size() < 10 && iterator.hasNext()) {
                jobs.add(new PullJob(database, restClient, type, (String) iterator.next(), parentId, altParent));
            }
            try {
                for (Future<T> r : executorService.invokeAll(jobs)) {
                    result.add(r.get());
                }
                database.unlock();
                if (iterator.hasNext()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            } catch (InterruptedException ie) {
                throw new RuntimeException(ie);
            } catch (ExecutionException ee) {
                throw new RuntimeException(ee);
            }
        }
        executorService.shutdown();
        return result;
    }

    public static <T extends DatabaseObject> T updateEntityAndSave(Database database, RestClient restClient, Class<T> type, T entity) {
        database.lock();
        HttpResponseErrorHandler errorHandler = new HttpResponseErrorHandler();
        DatabaseObject result = (DatabaseObject) restClient.putObject(type, entity, errorHandler);
        if (!(errorHandler.hasError || entity == null)) {
            result.setParent(entity.getParent());
            database.save(entity);
        }
        database.unlock();
        return result;
    }

    private static Intent createBackgroundIntent(Context context, Class type, String parentId, boolean altParent) {
        Intent i = new Intent(context, PushPull.class);
        i.setAction("pull");
        i.putExtra("type", type);
        i.putExtra("parentid", parentId);
        i.putExtra("altparent", altParent);
        return i;
    }

    public static <T extends DatabaseObject> void backgroundPullEntityAndSave(Context context, Class<T> type, String id, String parentId, boolean altParent) {
        Intent i = createBackgroundIntent(context, type, parentId, altParent);
        i.putExtra("id", id);
        context.startService(i);
    }

    public static <T extends DatabaseObject> void backgroundPullAndSaveEntities(Context context, Class<T> type, ArrayList<String> ids, String parentId, boolean altParent) {
        Intent i = createBackgroundIntent(context, type, parentId, altParent);
        i.putStringArrayListExtra("ids", ids);
        context.startService(i);
    }

    public void onCreate() {
        super.onCreate();
        IroApplication.get(this).component().inject(this);
    }

    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals("pull")) {
            Bundle extras = intent.getExtras();
            Class<? extends DatabaseObject> type = (Class) extras.getSerializable("type");
            String id = extras.getString("id");
            ArrayList<String> ids = extras.getStringArrayList("ids");
            if (id == null && ids == null) {
                throw new IllegalStateException();
            } else if (id == null || ids == null) {
                String parentId = extras.getString("parentid");
                boolean altParent = extras.getBoolean("altparent");
                if (ids != null) {
                    pullAndSaveEntities(this.database, this.restClient, type, ids, parentId, altParent);
                } else {
                    pullEntityAndSave(this.database, this.restClient, type, id, parentId, altParent);
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
