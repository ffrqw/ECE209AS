package com.rachio.iro.model.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.model.Event;
import com.rachio.iro.model.ResponseCacheItem;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.user.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "Iro.db";
    private static final int DATABASE_VERSION = 24;
    private static final String TAG = DatabaseHelper.class.getCanonicalName();
    public static final long serialVersionUID = 1;
    private static final Class[] tableClasses = new Class[]{User.class, Device.class, Zone.class, ScheduleRule.class, Event.class, ResponseCacheItem.class};
    private HashMap<Class, Dao<?, ?>> daoLookUpCache = new HashMap();
    private final HashMap<Class<?>, Dao<?, ?>> daos = new HashMap();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, (IroApplication.VERSIONCODE << 8) | 24, (int) R.raw.ormlite_config);
        try {
            for (Class<?> c : tableClasses) {
                Dao<?, ?> dao = getDao(c);
                dao.setObjectCache(true);
                this.daos.put(c, dao);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
        try {
            for (Class c : tableClasses) {
                TableUtils.createTable(this.connectionSource, c);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        SQLiteDatabase sQLiteDatabase = db;
        Cursor tables = sQLiteDatabase.query("sqlite_master", new String[]{"tbl_name"}, "type='table'", null, null, null, null);
        if (tables.moveToFirst()) {
            while (!tables.isAfterLast()) {
                String tableName = tables.getString(0);
                if (!(tableName.startsWith("android_") || tableName.startsWith("sqlite_"))) {
                    Log.d(TAG, "Dropping table " + tableName);
                    db.execSQL("DROP TABLE " + tableName);
                }
                tables.moveToNext();
            }
        }
        tables.close();
        onCreate(db, connectionSource);
    }

    private Dao<?, ?> getDaoForType(Class<?> type) {
        Dao<?, ?> dao = (Dao) this.daoLookUpCache.get(type);
        if (dao != null) {
            return dao;
        }
        List<Class> candidates = new ArrayList();
        for (Class c : this.daos.keySet()) {
            if (c.isAssignableFrom(type)) {
                candidates.add(c);
            }
        }
        if (candidates.size() == 0) {
            throw new RuntimeException("didn't find dao for " + type.getName());
        }
        while (candidates.size() > 1) {
            Class c1 = (Class) candidates.get(0);
            Class c2 = (Class) candidates.get(1);
            if (c1.isAssignableFrom(c2)) {
                candidates.remove(0);
            } else if (c2.isAssignableFrom(c1)) {
                candidates.remove(1);
            }
        }
        dao = (Dao) this.daos.get(candidates.get(0));
        this.daoLookUpCache.put(type, dao);
        return dao;
    }

    public <T, K> Dao<T, K> getDao(T item, K k) {
        return getDaoForType(item.getClass());
    }

    public <T, K> Dao<T, K> getDao(Class<T> type, K k) {
        return getDaoForType(type);
    }

    public <T> Dao<T, ?> findDao(Class<T> type) {
        return getDaoForType(type);
    }
}
