package com.j256.ormlite.field.types;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.support.DatabaseResults;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class EnumIntegerType extends BaseEnumType {
    private static final EnumIntegerType singleTon = new EnumIntegerType();

    public static EnumIntegerType getSingleton() {
        return singleTon;
    }

    private EnumIntegerType() {
        super(SqlType.INTEGER);
    }

    protected EnumIntegerType(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    public Object parseDefaultString(FieldType fieldType, String defaultStr) {
        return Integer.valueOf(Integer.parseInt(defaultStr));
    }

    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return Integer.valueOf(results.getInt(columnPos));
    }

    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        if (fieldType == null) {
            return sqlArg;
        }
        Integer valInteger = (Integer) sqlArg;
        Map<Integer, Enum<?>> enumIntMap = (Map) fieldType.getDataTypeConfigObj();
        if (enumIntMap == null) {
            return BaseEnumType.enumVal(fieldType, valInteger, null, fieldType.getUnknownEnumVal());
        }
        return BaseEnumType.enumVal(fieldType, valInteger, (Enum) enumIntMap.get(valInteger), fieldType.getUnknownEnumVal());
    }

    public Object javaToSqlArg(FieldType fieldType, Object obj) {
        return Integer.valueOf(((Enum) obj).ordinal());
    }

    public boolean isEscapedValue() {
        return false;
    }

    public Object makeConfigObject(FieldType fieldType) throws SQLException {
        Map<Integer, Enum<?>> enumIntMap = new HashMap();
        Enum[] constants = (Enum[]) fieldType.getType().getEnumConstants();
        if (constants == null) {
            throw new SQLException("Field " + fieldType + " improperly configured as type " + this);
        }
        Enum[] arr$ = constants;
        int len$ = constants.length;
        for (int i$ = 0; i$ < len$; i$++) {
            Enum<?> enumVal = arr$[i$];
            enumIntMap.put(Integer.valueOf(enumVal.ordinal()), enumVal);
        }
        return enumIntMap;
    }

    public Class<?> getPrimaryClass() {
        return Integer.TYPE;
    }
}
