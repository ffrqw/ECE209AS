package com.j256.ormlite.db;

import com.j256.ormlite.android.DatabaseTableConfigUtil;
import com.j256.ormlite.field.DataPersister;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.types.DateStringType;
import com.j256.ormlite.field.types.SqlDateStringType;
import com.j256.ormlite.field.types.SqlDateType;
import com.j256.ormlite.field.types.TimeStampStringType;
import com.j256.ormlite.field.types.TimeStampType;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import java.sql.SQLException;

public class SqliteAndroidDatabaseType extends BaseSqliteDatabaseType {
    public void loadDriver() {
    }

    public boolean isDatabaseUrlThisType(String url, String dbTypePart) {
        return true;
    }

    protected String getDriverClassName() {
        return null;
    }

    public String getDatabaseName() {
        return "Android SQLite";
    }

    protected void appendDateType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        appendStringType(sb, fieldType, fieldWidth);
    }

    public void appendEscapedEntityName(StringBuilder sb, String name) {
        sb.append('`').append(name).append('`');
    }

    protected void appendBooleanType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        appendShortType(sb, fieldType, fieldWidth);
    }

    public DataPersister getDataPersister(DataPersister defaultPersister, FieldType fieldType) {
        if (defaultPersister == null) {
            return super.getDataPersister(defaultPersister, fieldType);
        }
        switch (defaultPersister.getSqlType()) {
            case DATE:
                if (defaultPersister instanceof TimeStampType) {
                    return TimeStampStringType.getSingleton();
                }
                if (defaultPersister instanceof SqlDateType) {
                    return SqlDateStringType.getSingleton();
                }
                return DateStringType.getSingleton();
            default:
                return super.getDataPersister(defaultPersister, fieldType);
        }
    }

    public boolean isNestedSavePointsSupported() {
        return false;
    }

    public boolean isBatchUseTransaction() {
        return true;
    }

    public <T> DatabaseTableConfig<T> extractDatabaseTableConfig(ConnectionSource connectionSource, Class<T> clazz) throws SQLException {
        return DatabaseTableConfigUtil.fromClass(connectionSource, clazz);
    }
}
