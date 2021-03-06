package com.j256.ormlite.android;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.misc.IOUtils;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.misc.VersionUtils;
import com.j256.ormlite.stmt.GenericRowMapper;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.GeneratedKeyHolder;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Savepoint;

public class AndroidDatabaseConnection implements DatabaseConnection {
    private static final String ANDROID_VERSION = "VERSION__5.0__";
    private static final String[] NO_STRING_ARGS = new String[0];
    private static Logger logger = LoggerFactory.getLogger(AndroidDatabaseConnection.class);
    private final boolean cancelQueriesEnabled;
    private final SQLiteDatabase db;
    private final boolean readWrite;

    private static class OurSavePoint implements Savepoint {
        private String name;

        public OurSavePoint(String name) {
            this.name = name;
        }

        public int getSavepointId() {
            return 0;
        }

        public String getSavepointName() {
            return this.name;
        }
    }

    static {
        VersionUtils.checkCoreVersusAndroidVersions(ANDROID_VERSION);
    }

    public AndroidDatabaseConnection(SQLiteDatabase db, boolean readWrite) {
        this(db, readWrite, false);
    }

    public AndroidDatabaseConnection(SQLiteDatabase db, boolean readWrite, boolean cancelQueriesEnabled) {
        this.db = db;
        this.readWrite = readWrite;
        this.cancelQueriesEnabled = cancelQueriesEnabled;
        logger.trace("{}: db {} opened, read-write = {}", (Object) this, (Object) db, Boolean.valueOf(readWrite));
    }

    public boolean isAutoCommitSupported() {
        return true;
    }

    public boolean isAutoCommit() throws SQLException {
        try {
            boolean inTransaction = this.db.inTransaction();
            logger.trace("{}: in transaction is {}", (Object) this, Boolean.valueOf(inTransaction));
            return !inTransaction;
        } catch (android.database.SQLException e) {
            throw SqlExceptionUtil.create("problems getting auto-commit from database", e);
        }
    }

    public void setAutoCommit(boolean autoCommit) {
        if (autoCommit) {
            if (this.db.inTransaction()) {
                this.db.setTransactionSuccessful();
                this.db.endTransaction();
            }
        } else if (!this.db.inTransaction()) {
            this.db.beginTransaction();
        }
    }

    public Savepoint setSavePoint(String name) throws SQLException {
        try {
            this.db.beginTransaction();
            logger.trace("{}: save-point set with name {}", (Object) this, (Object) name);
            return new OurSavePoint(name);
        } catch (android.database.SQLException e) {
            throw SqlExceptionUtil.create("problems beginning transaction " + name, e);
        }
    }

    public boolean isReadWrite() {
        return this.readWrite;
    }

    public void commit(Savepoint savepoint) throws SQLException {
        try {
            this.db.setTransactionSuccessful();
            this.db.endTransaction();
            if (savepoint == null) {
                logger.trace("{}: transaction is successfuly ended", (Object) this);
            } else {
                logger.trace("{}: transaction {} is successfuly ended", (Object) this, savepoint.getSavepointName());
            }
        } catch (android.database.SQLException e) {
            if (savepoint == null) {
                throw SqlExceptionUtil.create("problems commiting transaction", e);
            }
            throw SqlExceptionUtil.create("problems commiting transaction " + savepoint.getSavepointName(), e);
        }
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        try {
            this.db.endTransaction();
            if (savepoint == null) {
                logger.trace("{}: transaction is ended, unsuccessfuly", (Object) this);
            } else {
                logger.trace("{}: transaction {} is ended, unsuccessfuly", (Object) this, savepoint.getSavepointName());
            }
        } catch (android.database.SQLException e) {
            if (savepoint == null) {
                throw SqlExceptionUtil.create("problems rolling back transaction", e);
            }
            throw SqlExceptionUtil.create("problems rolling back transaction " + savepoint.getSavepointName(), e);
        }
    }

    public int executeStatement(String statementStr, int resultFlags) throws SQLException {
        return AndroidCompiledStatement.execSql(this.db, statementStr, statementStr, NO_STRING_ARGS);
    }

    public CompiledStatement compileStatement(String statement, StatementType type, FieldType[] argFieldTypes, int resultFlags, boolean cacheStore) {
        Object stmt = new AndroidCompiledStatement(statement, this.db, type, this.cancelQueriesEnabled, cacheStore);
        logger.trace("{}: compiled statement got {}: {}", (Object) this, stmt, (Object) statement);
        return stmt;
    }

    public int insert(String statement, Object[] args, FieldType[] argFieldTypes, GeneratedKeyHolder keyHolder) throws SQLException {
        try {
            SQLiteStatement stmt = this.db.compileStatement(statement);
            bindArgs(stmt, args, argFieldTypes);
            long rowId = stmt.executeInsert();
            if (keyHolder != null) {
                keyHolder.addKey(Long.valueOf(rowId));
            }
            logger.trace("{}: insert statement is compiled and executed, changed {}: {}", (Object) this, Integer.valueOf(1), (Object) statement);
            closeQuietly(stmt);
            return 1;
        } catch (android.database.SQLException e) {
            throw SqlExceptionUtil.create("inserting to database failed: " + statement, e);
        } catch (Throwable th) {
            closeQuietly(null);
        }
    }

    public int update(String statement, Object[] args, FieldType[] argFieldTypes) throws SQLException {
        return update(statement, args, argFieldTypes, "updated");
    }

    public int delete(String statement, Object[] args, FieldType[] argFieldTypes) throws SQLException {
        return update(statement, args, argFieldTypes, "deleted");
    }

    public <T> Object queryForOne(String statement, Object[] args, FieldType[] argFieldTypes, GenericRowMapper<T> rowMapper, ObjectCache objectCache) throws SQLException {
        android.database.SQLException e;
        Throwable th;
        Cursor cursor = null;
        AndroidDatabaseResults results = null;
        try {
            cursor = this.db.rawQuery(statement, toStrings(args));
            AndroidDatabaseResults results2 = new AndroidDatabaseResults(cursor, objectCache, true);
            try {
                logger.trace("{}: queried for one result: {}", (Object) this, (Object) statement);
                if (results2.first()) {
                    Object first = rowMapper.mapRow(results2);
                    if (results2.next()) {
                        first = MORE_THAN_ONE;
                        IOUtils.closeQuietly(results2);
                        closeQuietly(cursor);
                        return first;
                    }
                    IOUtils.closeQuietly(results2);
                    closeQuietly(cursor);
                    return first;
                }
                IOUtils.closeQuietly(results2);
                closeQuietly(cursor);
                return null;
            } catch (android.database.SQLException e2) {
                e = e2;
                results = results2;
                try {
                    throw SqlExceptionUtil.create("queryForOne from database failed: " + statement, e);
                } catch (Throwable th2) {
                    th = th2;
                    IOUtils.closeQuietly(results);
                    closeQuietly(cursor);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                results = results2;
                IOUtils.closeQuietly(results);
                closeQuietly(cursor);
                throw th;
            }
        } catch (android.database.SQLException e3) {
            e = e3;
            throw SqlExceptionUtil.create("queryForOne from database failed: " + statement, e);
        }
    }

    public long queryForLong(String statement) throws SQLException {
        SQLiteStatement stmt = null;
        try {
            stmt = this.db.compileStatement(statement);
            long result = stmt.simpleQueryForLong();
            logger.trace("{}: query for long simple query returned {}: {}", (Object) this, Long.valueOf(result), (Object) statement);
            closeQuietly(stmt);
            return result;
        } catch (android.database.SQLException e) {
            throw SqlExceptionUtil.create("queryForLong from database failed: " + statement, e);
        } catch (Throwable th) {
            closeQuietly(stmt);
        }
    }

    public long queryForLong(String statement, Object[] args, FieldType[] argFieldTypes) throws SQLException {
        android.database.SQLException e;
        Throwable th;
        Cursor cursor = null;
        AndroidDatabaseResults results = null;
        try {
            cursor = this.db.rawQuery(statement, toStrings(args));
            AndroidDatabaseResults results2 = new AndroidDatabaseResults(cursor, null, false);
            try {
                long result;
                if (results2.first()) {
                    result = results2.getLong(0);
                } else {
                    result = 0;
                }
                logger.trace("{}: query for long raw query returned {}: {}", (Object) this, Long.valueOf(result), (Object) statement);
                closeQuietly(cursor);
                IOUtils.closeQuietly(results2);
                return result;
            } catch (android.database.SQLException e2) {
                e = e2;
                results = results2;
                try {
                    throw SqlExceptionUtil.create("queryForLong from database failed: " + statement, e);
                } catch (Throwable th2) {
                    th = th2;
                    closeQuietly(cursor);
                    IOUtils.closeQuietly(results);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                results = results2;
                closeQuietly(cursor);
                IOUtils.closeQuietly(results);
                throw th;
            }
        } catch (android.database.SQLException e3) {
            e = e3;
            throw SqlExceptionUtil.create("queryForLong from database failed: " + statement, e);
        }
    }

    public void close() throws IOException {
        try {
            this.db.close();
            logger.trace("{}: db {} closed", (Object) this, this.db);
        } catch (android.database.SQLException e) {
            throw new IOException("problems closing the database connection", e);
        }
    }

    public void closeQuietly() {
        IOUtils.closeQuietly(this);
    }

    public boolean isClosed() throws SQLException {
        try {
            boolean isOpen = this.db.isOpen();
            logger.trace("{}: db {} isOpen returned {}", (Object) this, this.db, Boolean.valueOf(isOpen));
            return !isOpen;
        } catch (android.database.SQLException e) {
            throw SqlExceptionUtil.create("problems detecting if the database is closed", e);
        }
    }

    public boolean isTableExists(String tableName) {
        Cursor cursor = this.db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + tableName + "'", null);
        try {
            boolean result;
            if (cursor.getCount() > 0) {
                result = true;
            } else {
                result = false;
            }
            logger.trace("{}: isTableExists '{}' returned {}", (Object) this, (Object) tableName, Boolean.valueOf(result));
            return result;
        } finally {
            cursor.close();
        }
    }

    private int update(String statement, Object[] args, FieldType[] argFieldTypes, String label) throws SQLException {
        SQLiteStatement stmt = null;
        try {
            int result;
            stmt = this.db.compileStatement(statement);
            bindArgs(stmt, args, argFieldTypes);
            stmt.execute();
            closeQuietly(stmt);
            stmt = null;
            try {
                stmt = this.db.compileStatement("SELECT CHANGES()");
                result = (int) stmt.simpleQueryForLong();
            } catch (android.database.SQLException e) {
                result = 1;
            } finally {
                closeQuietly(stmt);
            }
            logger.trace("{} statement is compiled and executed, changed {}: {}", (Object) label, Integer.valueOf(result), (Object) statement);
            return result;
        } catch (android.database.SQLException e2) {
            throw SqlExceptionUtil.create("updating database failed: " + statement, e2);
        } catch (Throwable th) {
            closeQuietly(stmt);
        }
    }

    private void bindArgs(SQLiteStatement stmt, Object[] args, FieldType[] argFieldTypes) throws SQLException {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null) {
                    stmt.bindNull(i + 1);
                } else {
                    SqlType sqlType = argFieldTypes[i].getSqlType();
                    switch (sqlType) {
                        case STRING:
                        case LONG_STRING:
                        case CHAR:
                            stmt.bindString(i + 1, arg.toString());
                            break;
                        case BOOLEAN:
                        case BYTE:
                        case SHORT:
                        case INTEGER:
                        case LONG:
                            stmt.bindLong(i + 1, ((Number) arg).longValue());
                            break;
                        case FLOAT:
                        case DOUBLE:
                            stmt.bindDouble(i + 1, ((Number) arg).doubleValue());
                            break;
                        case BYTE_ARRAY:
                        case SERIALIZABLE:
                            stmt.bindBlob(i + 1, (byte[]) arg);
                            break;
                        case DATE:
                        case BLOB:
                        case BIG_DECIMAL:
                            throw new SQLException("Invalid Android type: " + sqlType);
                        default:
                            throw new SQLException("Unknown sql argument type: " + sqlType);
                    }
                }
            }
        }
    }

    private String[] toStrings(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        String[] strings = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                strings[i] = null;
            } else {
                strings[i] = arg.toString();
            }
        }
        return strings;
    }

    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
    }

    private void closeQuietly(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    private void closeQuietly(SQLiteStatement statement) {
        if (statement != null) {
            statement.close();
        }
    }
}
