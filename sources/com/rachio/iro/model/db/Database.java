package com.rachio.iro.model.db;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.annotation.DatabaseOptions;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Database {
    public static final boolean DEBUG = false;
    public static final String EXTRA_ENTITYID = "entityid";
    private static final String TAG = Database.class.getCanonicalName();
    private static final boolean VERBOSEDEBUG = false;
    private Lock barrier = new ReentrantLock();
    private final LocalBroadcastManager broadcastManager;
    private final DatabaseHelper databaseHelper;

    public static class DatabasePreSaveCheckException extends Exception {
        public DatabasePreSaveCheckException() {
            this(null);
        }

        public DatabasePreSaveCheckException(String msg) {
            super(msg);
        }
    }

    public Database(Context context) {
        this.databaseHelper = (DatabaseHelper) OpenHelperManager.getHelper(context, DatabaseHelper.class);
        this.broadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public void close() {
        this.databaseHelper.close();
    }

    private <T, K> T findById(Dao<T, K> dao, K id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        T found;
        synchronized (dao) {
            try {
                found = dao.queryForId(id);
                if (found != null) {
                    dao.refresh(found);
                } else {
                    Log.d(TAG, "didn't find " + id);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return found;
    }

    public <T> T find(Class<T> type, String id) {
        return findById(this.databaseHelper.getDao((Class) type, (Object) id), id);
    }

    private static String getParentCol(Class<?> type) {
        DatabaseOptions options = (DatabaseOptions) type.getAnnotation(DatabaseOptions.class);
        if (options != null) {
            String col = options.parentcol();
            if (col.length() > 0) {
                return col;
            }
            return null;
        }
        throw new RuntimeException("class " + type + " doesn't have DatabaseOptions annotation");
    }

    private static String getAltParentCol(Class<?> type) {
        DatabaseOptions options = (DatabaseOptions) type.getAnnotation(DatabaseOptions.class);
        if (options != null) {
            return options.altparentcol();
        }
        throw new RuntimeException("class " + type + " doesn't have DatabaseOptions annotation");
    }

    public <T> List<T> findForParent(Class<T> type, String parentId) {
        try {
            return this.databaseHelper.getDao((Class) type, (Object) parentId).queryForEq(getParentCol(type), parentId);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Set<String> findIdsForCol(Class type, String col, String parentId) {
        try {
            Set<String> result = new TreeSet();
            for (String[] cols : this.databaseHelper.getDao(type, (Object) parentId).queryBuilder().selectColumns("id").where().eq(col, parentId).queryRaw()) {
                result.add(cols[0]);
            }
            return result;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public Set<String> findIdsForParent(Class type, String parentId) {
        return findIdsForCol(type, getParentCol(type), parentId);
    }

    public Set<String> findIdsForAltParent(Class type, String parentId) {
        return findIdsForCol(type, getAltParentCol(type), parentId);
    }

    public <T> List<T> findForAltParent(Class<T> type, String parentId) {
        try {
            return this.databaseHelper.getDao((Class) type, (Object) parentId).queryForEq(getAltParentCol(type), parentId);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public <T extends DatabaseObject, K> void deleteById(Class<T> type, K id) {
        try {
            DatabaseOptions options = getDatabaseOptions((Class) type);
            Dao<T, K> dao = this.databaseHelper.getDao((Class) type, (Object) id);
            Class<? extends DatabaseObject>[] descendants = getDescendants(type);
            if (descendants != null) {
                Log.d(TAG, "cleaning up descendants for " + type + " " + id);
                for (Class<? extends DatabaseObject> d : descendants) {
                    deleteForParent(d, id);
                }
            }
            if (getParentCol(type) != null) {
                DatabaseOptions parentOptions = getDatabaseOptions(options.parent());
                String[] firstResult = (String[]) dao.queryBuilder().selectRaw(parentCol).where().idEq(id).queryRaw().getFirstResult();
                if (firstResult != null) {
                    sendBroadcast(parentOptions.broadcast(), firstResult[0]);
                }
            }
            dao.deleteById(id);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private <T extends DatabaseObject, K> void deleteForIdCol(Class<T> type, String idCol, K id) {
        try {
            Dao<T, ?> dao = this.databaseHelper.getDao((Class) type, (Object) id);
            Class<? extends DatabaseObject>[] descendants = getDescendants(type);
            if (descendants != null) {
                for (T child : dao.queryBuilder().where().eq(idCol, id).query()) {
                    Log.d(TAG, "cleaning up descendants for child " + type.getName() + " " + child.id + " of  " + id);
                    for (Class<? extends DatabaseObject> d : descendants) {
                        deleteForParent(d, child.id);
                    }
                }
            }
            DeleteBuilder<T, ?> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq(idCol, id);
            deleteBuilder.delete();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public <T extends DatabaseObject> void deleteForAltParent(Class<T> type, String parentId) {
        deleteForIdCol(type, getAltParentCol(type), parentId);
    }

    public <T extends DatabaseObject, K> void deleteForParent(Class<T> type, K parentId) {
        deleteForIdCol(type, getParentCol(type), parentId);
    }

    public <T> long countForParent(Class<T> type, String parentId) {
        try {
            return this.databaseHelper.getDao((Class) type, (Object) parentId).queryBuilder().where().eq(getParentCol(type), parentId).countOf();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private <T, K> void doDelete(Dao<T, K> dao, Collection<T> items) {
        synchronized (dao) {
            try {
                dao.delete((Collection) items);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private <T, K> void doDelete(Dao<T, K> dao, T item) {
        synchronized (dao) {
            try {
                dao.delete((Object) item);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private Class<? extends DatabaseObject>[] getDescendants(Class<?> type) {
        DatabaseOptions options = getDatabaseOptions((Class) type);
        if (options.descendants().length > 0) {
            return options.descendants();
        }
        return null;
    }

    public <T extends DatabaseObject> void delete(T object) {
        lock();
        Class<? extends DatabaseObject>[] descendants = getDescendants(object.getClass());
        if (descendants != null) {
            for (Class<? extends DatabaseObject> c : descendants) {
                deleteForParent(c, object.id);
            }
        }
        doDelete(this.databaseHelper.getDao((Object) object, object.id), (Object) object);
        unlock();
    }

    public <T> void deleteAll(Class<T> type) {
        try {
            Log.d(TAG, "deleted " + this.databaseHelper.findDao(type).deleteBuilder().delete() + " of " + type.getName());
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public <T extends ModelObject> void delete(Collection<T> collection) {
    }

    private long getColumnLongValueForId(Dao<?, ?> dao, String id, String column) throws SQLException {
        String[] queryResult = dao.queryBuilder().selectColumns(column).where().eq("id", id).queryRawFirst();
        if (queryResult != null) {
            try {
                return Long.valueOf(queryResult[0]).longValue();
            } catch (NumberFormatException e) {
            }
        }
        return 0;
    }

    private void sendBroadcast(String broadcast, String id) {
        if (broadcast != null) {
            Intent i = new Intent().setAction(broadcast);
            i.putExtra(EXTRA_ENTITYID, id);
            this.broadcastManager.sendBroadcast(i);
        }
    }

    private <T extends DatabaseObject, K> CreateOrUpdateStatus doCreateOrUpdate(Dao<T, K> dao, T item, String broadcast, boolean ignoreLastUpdate) {
        try {
            CreateOrUpdateStatus createOrUpdateStatus;
            item.doPreSaveSanityCheck();
            synchronized (dao) {
                if (!ignoreLastUpdate) {
                    try {
                        Date existing = new Date(getColumnLongValueForId(dao, item.id, "lastUpdateDate"));
                        if (item.lastUpdateDate != null && existing.compareTo(item.lastUpdateDate) == 0) {
                            createOrUpdateStatus = null;
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                createOrUpdateStatus = dao.createOrUpdate(item);
                dao.refresh(item);
                sendBroadcast(broadcast, item.id);
            }
            return createOrUpdateStatus;
        } catch (DatabasePreSaveCheckException e) {
            throw new RuntimeException("Failed pre-save sanity check", e);
        }
    }

    private <T, K> T doRefresh(Dao<T, K> dao, T item) {
        synchronized (dao) {
            try {
                dao.refresh(item);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return item;
    }

    public <T extends ModelObject> T refresh(T object) {
        return (ModelObject) doRefresh(getDatabaseHelper().getDao((Object) object, new String()), object);
    }

    public <T extends DatabaseObject> void save(T object) {
        save(object, false);
    }

    public <T extends DatabaseObject> void save(T object, boolean broadcast) {
        save(object, broadcast, false, false);
    }

    public static DatabaseOptions getDatabaseOptions(DatabaseObject object) {
        return getDatabaseOptions(object.getClass());
    }

    public static DatabaseOptions getDatabaseOptions(Class clazz) {
        DatabaseOptions options = (DatabaseOptions) clazz.getAnnotation(DatabaseOptions.class);
        if (options != null) {
            return options;
        }
        throw new RuntimeException();
    }

    public <T extends DatabaseObject> void save(T object, boolean broadcast, boolean deep, boolean ignoreLastUpdate) {
        object.preSave();
        String b = null;
        DatabaseOptions options = getDatabaseOptions((DatabaseObject) object);
        if (broadcast) {
            b = options.broadcast();
            if (TextUtils.isEmpty(b)) {
                Log.d(TAG, "broadcast action is null, ignoring broadcast flag");
            }
        }
        if (!(ignoreLastUpdate || options.descendants().length == 0)) {
            ignoreLastUpdate = true;
        }
        CreateOrUpdateStatus status = doCreateOrUpdate(this.databaseHelper.getDao((Object) object, object.id), object, b, ignoreLastUpdate);
        if (deep) {
            if (status != null) {
                try {
                    if (status.isUpdated()) {
                        object.pruneCollections(this);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            object.saveTransients(this);
        }
    }

    public void lock() {
        int lockFailures = 0;
        while (lockFailures < 256) {
            try {
                if (!this.barrier.tryLock(1000, TimeUnit.MILLISECONDS)) {
                    Log.d(TAG, "waiting for lock");
                    lockFailures++;
                } else {
                    return;
                }
            } catch (InterruptedException e) {
            }
        }
        throw new RuntimeException("lock timeout");
    }

    public void unlock() {
        this.barrier.unlock();
    }

    public DatabaseHelper getDatabaseHelper() {
        return this.databaseHelper;
    }
}
