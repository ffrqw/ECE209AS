package com.rachio.iro.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.utils.CrashReporterUtils;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

@DatabaseTable
public class ResponseCacheItem {
    private static final boolean DEBUG = false;
    private static final String TAG = ResponseCacheItem.class.getName();
    @DatabaseField
    public String anchor;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public Serializable data;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public Class dataClass;
    public boolean hasExpired = false;
    @DatabaseField(generatedId = true)
    public long id;
    @DatabaseField
    public long timestamp;
    @DatabaseField(unique = true)
    public String url;

    public static ResponseCacheItem get(Database database, String url, long expiry, long eviction) {
        if (eviction != -1 && expiry > eviction) {
            throw new IllegalArgumentException("eviction time must be bigger than expiry");
        } else if (url == null) {
            throw new IllegalArgumentException("url cannot be null");
        } else {
            Dao<ResponseCacheItem, Long> responseCacheItemDao = database.getDatabaseHelper().getDao(ResponseCacheItem.class, new Long(0));
            try {
                ResponseCacheItem item = (ResponseCacheItem) responseCacheItemDao.queryBuilder().where().eq("url", url).queryForFirst();
                if (item == null || expiry <= 0) {
                    return item;
                }
                long now = System.currentTimeMillis();
                long evictionAt = item.timestamp + eviction;
                if (item.timestamp + expiry >= now) {
                    return item;
                }
                item.hasExpired = true;
                if (eviction != -1 && evictionAt >= now) {
                    return item;
                }
                responseCacheItemDao.deleteById(Long.valueOf(item.id));
                return null;
            } catch (SQLException ex) {
                CrashReporterUtils.silentExceptionThatCrashesDebugBuilds(ex);
                try {
                    DeleteBuilder db = responseCacheItemDao.deleteBuilder();
                    db.where().eq("url", url);
                    db.delete();
                    return null;
                } catch (SQLException dex) {
                    throw new RuntimeException(dex);
                }
            }
        }
    }

    public static void put(Database database, String anchor, String url, Class dataClass, Serializable data) {
        database.lock();
        ResponseCacheItem item = new ResponseCacheItem();
        item.timestamp = System.currentTimeMillis();
        item.anchor = anchor;
        item.url = url;
        item.dataClass = dataClass;
        item.data = data;
        ResponseCacheItem existing = get(database, url, -1, -1);
        if (existing != null) {
            item.id = existing.id;
        }
        try {
            database.getDatabaseHelper().getDao(ResponseCacheItem.class, new Long(0)).createOrUpdate(item);
            database.unlock();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void clean(Database database) {
    }

    public static void invalidate(Database database, String anchor, Class<?> type) {
        try {
            Dao<ResponseCacheItem, Long> responseCacheItemDao = database.getDatabaseHelper().getDao(ResponseCacheItem.class, new Long(0));
            List<ResponseCacheItem> items = responseCacheItemDao.queryBuilder().where().eq("anchor", anchor).query();
            if (items != null) {
                for (Object i : items) {
                    if (i.dataClass.equals(type)) {
                        responseCacheItemDao.delete(i);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
