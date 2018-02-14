package com.j256.ormlite.misc;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionManager {
    private static final String SAVE_POINT_PREFIX = "ORMLITE";
    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);
    private static AtomicInteger savePointCounter = new AtomicInteger();
    private ConnectionSource connectionSource;

    public TransactionManager(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        initialize();
    }

    public void initialize() {
        if (this.connectionSource == null) {
            throw new IllegalStateException("dataSource was not set on " + getClass().getSimpleName());
        }
    }

    public <T> T callInTransaction(Callable<T> callable) throws SQLException {
        return callInTransaction(this.connectionSource, (Callable) callable);
    }

    public <T> T callInTransaction(String tableName, Callable<T> callable) throws SQLException {
        return callInTransaction(tableName, this.connectionSource, (Callable) callable);
    }

    public static <T> T callInTransaction(ConnectionSource connectionSource, Callable<T> callable) throws SQLException {
        return callInTransaction(null, connectionSource, (Callable) callable);
    }

    public static <T> T callInTransaction(String tableName, ConnectionSource connectionSource, Callable<T> callable) throws SQLException {
        DatabaseConnection connection = connectionSource.getReadWriteConnection(tableName);
        try {
            T callInTransaction = callInTransaction(connection, connectionSource.saveSpecialConnection(connection), connectionSource.getDatabaseType(), callable);
            return callInTransaction;
        } finally {
            connectionSource.clearSpecialConnection(connection);
            connectionSource.releaseConnection(connection);
        }
    }

    public static <T> T callInTransaction(DatabaseConnection connection, DatabaseType databaseType, Callable<T> callable) throws SQLException {
        return callInTransaction(connection, false, databaseType, callable);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <T> T callInTransaction(com.j256.ormlite.support.DatabaseConnection r9, boolean r10, com.j256.ormlite.db.DatabaseType r11, java.util.concurrent.Callable<T> r12) throws java.sql.SQLException {
        /*
        r8 = 1;
        r2 = 0;
        r1 = 0;
        r4 = 0;
        if (r10 != 0) goto L_0x000c;
    L_0x0006:
        r5 = r11.isNestedSavePointsSupported();	 Catch:{ all -> 0x0069 }
        if (r5 == 0) goto L_0x0047;
    L_0x000c:
        r5 = r9.isAutoCommitSupported();	 Catch:{ all -> 0x0069 }
        if (r5 == 0) goto L_0x0024;
    L_0x0012:
        r5 = r9.isAutoCommit();	 Catch:{ all -> 0x0069 }
        if (r5 == 0) goto L_0x0024;
    L_0x0018:
        r5 = 0;
        r9.setAutoCommit(r5);	 Catch:{ all -> 0x0069 }
        r2 = 1;
        r5 = logger;	 Catch:{ all -> 0x0069 }
        r6 = "had to set auto-commit to false";
        r5.debug(r6);	 Catch:{ all -> 0x0069 }
    L_0x0024:
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0069 }
        r6 = "ORMLITE";
        r5.<init>(r6);	 Catch:{ all -> 0x0069 }
        r6 = savePointCounter;	 Catch:{ all -> 0x0069 }
        r6 = r6.incrementAndGet();	 Catch:{ all -> 0x0069 }
        r5 = r5.append(r6);	 Catch:{ all -> 0x0069 }
        r5 = r5.toString();	 Catch:{ all -> 0x0069 }
        r4 = r9.setSavePoint(r5);	 Catch:{ all -> 0x0069 }
        if (r4 != 0) goto L_0x005d;
    L_0x003f:
        r5 = logger;	 Catch:{ all -> 0x0069 }
        r6 = "started savePoint transaction";
        r5.debug(r6);	 Catch:{ all -> 0x0069 }
    L_0x0046:
        r1 = 1;
    L_0x0047:
        r3 = r12.call();	 Catch:{ SQLException -> 0x0077, Exception -> 0x0087 }
        if (r1 == 0) goto L_0x0050;
    L_0x004d:
        commit(r9, r4);	 Catch:{ SQLException -> 0x0077, Exception -> 0x0087 }
    L_0x0050:
        if (r2 == 0) goto L_0x005c;
    L_0x0052:
        r9.setAutoCommit(r8);
        r5 = logger;
        r6 = "restored auto-commit to true";
        r5.debug(r6);
    L_0x005c:
        return r3;
    L_0x005d:
        r5 = logger;	 Catch:{ all -> 0x0069 }
        r6 = "started savePoint transaction {}";
        r7 = r4.getSavepointName();	 Catch:{ all -> 0x0069 }
        r5.debug(r6, r7);	 Catch:{ all -> 0x0069 }
        goto L_0x0046;
    L_0x0069:
        r5 = move-exception;
        if (r2 == 0) goto L_0x0076;
    L_0x006c:
        r9.setAutoCommit(r8);
        r6 = logger;
        r7 = "restored auto-commit to true";
        r6.debug(r7);
    L_0x0076:
        throw r5;
    L_0x0077:
        r0 = move-exception;
        if (r1 == 0) goto L_0x007d;
    L_0x007a:
        rollBack(r9, r4);	 Catch:{ SQLException -> 0x007e }
    L_0x007d:
        throw r0;	 Catch:{ all -> 0x0069 }
    L_0x007e:
        r5 = move-exception;
        r5 = logger;	 Catch:{ all -> 0x0069 }
        r6 = "after commit exception, rolling back to save-point also threw exception";
        r5.error(r0, r6);	 Catch:{ all -> 0x0069 }
        goto L_0x007d;
    L_0x0087:
        r0 = move-exception;
        if (r1 == 0) goto L_0x008d;
    L_0x008a:
        rollBack(r9, r4);	 Catch:{ SQLException -> 0x0094 }
    L_0x008d:
        r5 = "Transaction callable threw non-SQL exception";
        r5 = com.j256.ormlite.misc.SqlExceptionUtil.create(r5, r0);	 Catch:{ all -> 0x0069 }
        throw r5;	 Catch:{ all -> 0x0069 }
    L_0x0094:
        r5 = move-exception;
        r5 = logger;	 Catch:{ all -> 0x0069 }
        r6 = "after commit exception, rolling back to save-point also threw exception";
        r5.error(r0, r6);	 Catch:{ all -> 0x0069 }
        goto L_0x008d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.j256.ormlite.misc.TransactionManager.callInTransaction(com.j256.ormlite.support.DatabaseConnection, boolean, com.j256.ormlite.db.DatabaseType, java.util.concurrent.Callable):T");
    }

    public void setConnectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    private static void commit(DatabaseConnection connection, Savepoint savePoint) throws SQLException {
        Object name = savePoint == null ? null : savePoint.getSavepointName();
        connection.commit(savePoint);
        if (name == null) {
            logger.debug("committed savePoint transaction");
        } else {
            logger.debug("committed savePoint transaction {}", name);
        }
    }

    private static void rollBack(DatabaseConnection connection, Savepoint savePoint) throws SQLException {
        Object name = savePoint == null ? null : savePoint.getSavepointName();
        connection.rollback(savePoint);
        if (name == null) {
            logger.debug("rolled back savePoint transaction");
        } else {
            logger.debug("rolled back savePoint transaction {}", name);
        }
    }
}
