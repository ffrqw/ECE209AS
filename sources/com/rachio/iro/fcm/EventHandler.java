package com.rachio.iro.fcm;

import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.Event;
import com.rachio.iro.model.Event.Category;
import com.rachio.iro.model.Event.SubType;
import com.rachio.iro.model.EventData.DeltaContainer.Delta;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.ResponseCacheItem;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.db.DatabaseObject;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Device.AttachedSensor;
import com.rachio.iro.model.device.ShallowDevice.Model;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.device.Zone.PropertyIdHolder;
import com.rachio.iro.model.mapping.JsonMapper;
import com.rachio.iro.model.reporting.WaterUseResponse;
import com.rachio.iro.model.reporting.WeatherIntelligence;
import com.rachio.iro.model.schedule.ScheduleCalendar;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule.CycleSoakStatus;
import com.rachio.iro.model.schedule.ScheduleRule.FlexScheduleRule;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.user.User.DisplayUnit;
import com.rachio.iro.utils.CrashReporterUtils;
import com.rachio.iro.utils.DateUtils;
import com.rachio.iro.utils.StringUtils;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EventHandler {
    private static final String TAG = EventHandler.class.getName();
    private static Map<Class, ApplyHandler> applyHandlers;
    private static Map<Category, Map<SubType, DeltaApplyMethodHolder>> applyMethods = new TreeMap();
    private static Map<SubType, DeltaApplyMethodHolder> deviceApplyMethods = new TreeMap();
    private static InsertCallback deviceInsertCallback = new InsertCallback() {
        public final boolean beforeInsert(User loggedInUser, Database database, Event event, Object item) throws InsertCallbackException {
            if (loggedInUser == null) {
                return false;
            }
            Device d = (Device) item;
            if (d.owner != null) {
                d.managerUser = loggedInUser;
            } else {
                d.user = loggedInUser;
            }
            return true;
        }
    };
    private static ChangeCallback invalidateCalendarCallback = new ChangeCallback() {
        public final void onChangeHappened$2668ceab(Database database, Event event) {
            if (event.deviceId != null) {
                ResponseCacheItem.invalidate(database, event.deviceId, ScheduleCalendar.class);
            }
        }
    };
    private static Map<SubType, DeltaApplyMethodHolder> personApplyMethods = new TreeMap();
    private static Map<SubType, DeltaApplyMethodHolder> scheduleApplyMethods = new TreeMap();
    private static InsertCallback scheduleRuleInsertCallback = new InsertCallback() {
        public final boolean beforeInsert(User loggedInUser, Database database, Event event, Object item) throws InsertCallbackException {
            if (event.deviceId == null) {
                return false;
            }
            Device d = (Device) database.find(Device.class, event.deviceId);
            if (d != null) {
                ((ScheduleRule) item).device = d;
                return true;
            }
            throw new InsertCallbackException("couldn't find device to attach schedule rule to, id was " + event.deviceId);
        }
    };
    private static InsertCallback zoneInsertCallback = new InsertCallback() {
        public final boolean beforeInsert(User loggedInUser, Database database, Event event, Object item) throws InsertCallbackException {
            if (event.deviceId != null) {
                Device d = (Device) database.find(Device.class, event.deviceId);
                if (d != null) {
                    ((Zone) item).device = d;
                    return true;
                }
                throw new InsertCallbackException("couldn't find device to attach zone to, id was " + event.deviceId);
            }
            throw new InsertCallbackException("deviceid was null");
        }
    };
    Database database;
    private long lastEvent = 0;
    private final ObjectMapper objectMapper = JsonMapper.createMapperForPubNub();
    PrefsWrapper prefsWrapper;
    RestClient restClient;

    private interface ApplyHandler {
        boolean apply(Object obj, Field field, Method method, Method method2, Delta delta) throws IllegalAccessException, InvocationTargetException;
    }

    private static class DeltaApplyMethodHolder {
        final ChangeCallback changeCallback;
        final Constructor<?> emptyConstructor;
        final InsertCallback insertCallback;
        final Class<? extends DatabaseObject> type;

        public interface InsertCallback {
            boolean beforeInsert(User user, Database database, Event event, Object obj) throws InsertCallbackException;
        }

        public interface ChangeCallback {
            void onChangeHappened$2668ceab(Database database, Event event);
        }

        public static class InsertCallbackException extends Exception {
            public InsertCallbackException(String msg) {
                super(msg);
            }
        }

        public DeltaApplyMethodHolder(Class<? extends DatabaseObject> type, Constructor<?> emptyConstructor, InsertCallback insertCallback, ChangeCallback changeCallback) {
            this.type = type;
            this.emptyConstructor = emptyConstructor;
            this.insertCallback = insertCallback;
            this.changeCallback = changeCallback;
        }
    }

    @Inherited
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DeltaApplyOptions {
        boolean deltasAreIncomplete() default false;

        String[] ignoredProperties() default {};

        String[] silentProperties() default {};
    }

    private static class EnumApplyHandler<T extends Enum> implements ApplyHandler {
        private final Class<T> c;

        public EnumApplyHandler(Class<T> c) {
            this.c = c;
        }

        public final boolean apply(Object item, Field field, Method getter, Method setter, Delta delta) throws IllegalAccessException, InvocationTargetException {
            T oldValue;
            T newValue;
            if (delta.oldValue != null) {
                oldValue = Enum.valueOf(this.c, (String) delta.oldValue);
            } else {
                oldValue = null;
            }
            if (delta.newValue != null) {
                newValue = Enum.valueOf(this.c, (String) delta.newValue);
            } else {
                newValue = null;
            }
            Enum ourValue = (Enum) EventHandler.getOldValueFromFieldOrGetter(field, getter, item);
            if (newValue.equals(ourValue)) {
                return false;
            }
            EventHandler.access$100(oldValue, newValue, ourValue);
            EventHandler.access$200(field, setter, item, newValue);
            return true;
        }
    }

    static /* synthetic */ void access$100(Object x0, Object x1, Object x2) {
    }

    static {
        Map hashMap = new HashMap();
        applyHandlers = hashMap;
        hashMap.put(Integer.TYPE, new ApplyHandler() {
            public final boolean apply(Object item, Field field, Method getter, Method setter, Delta delta) throws IllegalAccessException, InvocationTargetException {
                int oldValue;
                int newValue;
                if (delta.oldValue != null) {
                    oldValue = ((Integer) delta.oldValue).intValue();
                } else {
                    oldValue = 0;
                }
                if (delta.newValue != null) {
                    newValue = ((Integer) delta.newValue).intValue();
                } else {
                    newValue = 0;
                }
                int ourValue = field.getInt(item);
                if (newValue == ourValue) {
                    return false;
                }
                Log.d(EventHandler.TAG, "old value;" + oldValue + " new value;" + newValue + " our value;" + ourValue);
                EventHandler.access$200(field, setter, item, Integer.valueOf(newValue));
                return true;
            }
        });
        applyHandlers.put(Long.TYPE, new ApplyHandler() {
            public final boolean apply(Object item, Field field, Method getter, Method setter, Delta delta) throws IllegalAccessException {
                long oldValue;
                long newValue;
                if (delta.oldValue != null) {
                    oldValue = ((Long) delta.oldValue).longValue();
                } else {
                    oldValue = 0;
                }
                if (delta.newValue != null) {
                    newValue = ((Long) delta.newValue).longValue();
                } else {
                    newValue = 0;
                }
                long ourValue = field.getLong(item);
                if (newValue == ourValue) {
                    return false;
                }
                Log.d(EventHandler.TAG, "old value;" + oldValue + " new value;" + newValue + " our value;" + ourValue);
                field.setLong(item, newValue);
                return true;
            }
        });
        applyHandlers.put(Boolean.TYPE, new ApplyHandler() {
            public final boolean apply(Object item, Field field, Method getter, Method setter, Delta delta) throws IllegalAccessException {
                boolean oldValue;
                if (delta.oldValue != null) {
                    oldValue = ((Boolean) delta.oldValue).booleanValue();
                } else {
                    oldValue = false;
                }
                boolean newValue = ((Boolean) delta.newValue).booleanValue();
                boolean ourValue = field.getBoolean(item);
                if (newValue == ourValue) {
                    return false;
                }
                Log.d(EventHandler.TAG, "old value;" + oldValue + " new value;" + newValue + " our value;" + ourValue);
                field.setBoolean(item, newValue);
                return true;
            }
        });
        applyHandlers.put(Float.TYPE, new ApplyHandler() {
            public final boolean apply(Object item, Field field, Method getter, Method setter, Delta delta) throws IllegalAccessException {
                float oldValue;
                float newValue;
                if (delta.oldValue != null) {
                    oldValue = EventHandler.access$700(delta.oldValue);
                } else {
                    oldValue = 0.0f;
                }
                if (delta.newValue != null) {
                    newValue = EventHandler.access$700(delta.newValue);
                } else {
                    newValue = 0.0f;
                }
                float ourValue = field.getFloat(item);
                if (newValue == ourValue) {
                    return false;
                }
                Log.d(EventHandler.TAG, "old value;" + oldValue + " new value;" + newValue + " our value;" + ourValue);
                field.setFloat(item, newValue);
                return true;
            }
        });
        applyHandlers.put(Double.TYPE, new ApplyHandler() {
            public final boolean apply(Object item, Field field, Method getter, Method setter, Delta delta) throws IllegalAccessException {
                double oldValue;
                double newValue;
                if (delta.oldValue != null) {
                    oldValue = EventHandler.access$800(delta.oldValue);
                } else {
                    oldValue = 0.0d;
                }
                if (delta.newValue != null) {
                    newValue = EventHandler.access$800(delta.newValue);
                } else {
                    newValue = 0.0d;
                }
                double ourValue = field.getDouble(item);
                if (newValue == ourValue) {
                    return false;
                }
                Log.d(EventHandler.TAG, "old value;" + oldValue + " new value;" + newValue + " our value;" + ourValue);
                field.setDouble(item, newValue);
                return true;
            }
        });
        applyHandlers.put(String.class, new ApplyHandler() {
            public final boolean apply(Object item, Field field, Method getter, Method setter, Delta delta) throws IllegalAccessException, InvocationTargetException {
                String oldValue = delta.oldValue;
                String newValue = delta.newValue;
                String ourValue = (String) EventHandler.getOldValueFromFieldOrGetter(field, getter, item);
                if (StringUtils.equals(newValue, ourValue)) {
                    return false;
                }
                EventHandler.access$100(oldValue, newValue, ourValue);
                EventHandler.access$200(field, setter, item, newValue);
                return true;
            }
        });
        applyHandlers.put(Date.class, new ApplyHandler() {
            public final boolean apply(Object item, Field field, Method getter, Method setter, Delta delta) throws IllegalAccessException, InvocationTargetException {
                Date oldValue;
                Date newValue;
                if (delta.oldValue != null) {
                    oldValue = new Date(((Long) delta.oldValue).longValue());
                } else {
                    oldValue = null;
                }
                if (delta.newValue != null) {
                    newValue = new Date(((Long) delta.newValue).longValue());
                } else {
                    newValue = null;
                }
                Date ourValue = (Date) EventHandler.getOldValueFromFieldOrGetter(field, getter, item);
                if (DateUtils.equals(ourValue, newValue)) {
                    return false;
                }
                EventHandler.access$100(oldValue, newValue, ourValue);
                EventHandler.access$200(field, setter, item, newValue);
                return true;
            }
        });
        applyHandlers.put(DisplayUnit.class, new EnumApplyHandler(DisplayUnit.class));
        applyHandlers.put(CycleSoakStatus.class, new EnumApplyHandler(CycleSoakStatus.class));
        applyHandlers.put(PropertyIdHolder.class, new ApplyHandler() {
            public final boolean apply(Object item, Field field, Method getter, Method setter, Delta delta) throws IllegalAccessException, InvocationTargetException {
                String newValue = delta.newValue;
                String oldValue = delta.oldValue;
                String ourValue = ((PropertyIdHolder) EventHandler.getOldValueFromFieldOrGetter(field, getter, item)).id;
                EventHandler.access$100(oldValue, newValue, ourValue);
                if (StringUtils.equals(newValue, ourValue)) {
                    return false;
                }
                PropertyIdHolder newId = new PropertyIdHolder();
                newId.id = newValue;
                EventHandler.access$200(field, setter, item, newId);
                return true;
            }
        });
        applyHandlers.put(LinkedHashMap.class, new ApplyHandler() {
            public final boolean apply(Object item, Field field, Method getter, Method setter, Delta delta) throws IllegalAccessException {
                System.out.println("xxxxxx " + delta.newValue.getClass().getCanonicalName());
                return false;
            }
        });
        applyHandlers.put(Model.class, new ApplyHandler() {
            public final boolean apply(Object item, Field field, Method getter, Method setter, Delta delta) throws IllegalAccessException, InvocationTargetException {
                Model newModel = null;
                for (Model m : Model.values()) {
                    if (StringUtils.equals(m.toString(), (String) delta.newValue)) {
                        newModel = m;
                        break;
                    }
                }
                EventHandler.access$200(field, setter, item, newModel);
                return true;
            }
        });
        applyHandlers.put(AttachedSensor[].class, new ApplyHandler() {
            public final boolean apply(Object item, Field field, Method getter, Method setter, Delta delta) throws IllegalAccessException, InvocationTargetException {
                Log.d(EventHandler.TAG, "new value " + delta.newValue.getClass() + " " + delta.newValue);
                List<LinkedHashMap<String, Object>> sensorValueMaps = delta.newValue;
                AttachedSensor[] newSensors = new AttachedSensor[sensorValueMaps.size()];
                for (int i = 0; i < newSensors.length; i++) {
                    newSensors[i] = (AttachedSensor) EventHandler.createInstanceFromHashMap(AttachedSensor.class, (LinkedHashMap) sensorValueMaps.get(i));
                }
                return false;
            }
        });
        try {
            deviceApplyMethods.put(SubType.ZONE_DELTA, new DeltaApplyMethodHolder(Zone.class, Zone.class.getConstructor(new Class[0]), zoneInsertCallback, invalidateCalendarCallback));
            deviceApplyMethods.put(SubType.DEVICE_DELTA, new DeltaApplyMethodHolder(Device.class, Device.class.getConstructor(new Class[0]), deviceInsertCallback, null));
            scheduleApplyMethods.put(SubType.SCHEDULE_RULE_DELTA, new DeltaApplyMethodHolder(ScheduleRule.class, ScheduleRule.class.getConstructor(new Class[0]), scheduleRuleInsertCallback, invalidateCalendarCallback));
            scheduleApplyMethods.put(SubType.FLEX_SCHEDULE_RULE_DELTA, new DeltaApplyMethodHolder(FlexScheduleRule.class, ScheduleRule.class.getConstructor(new Class[0]), scheduleRuleInsertCallback, invalidateCalendarCallback));
            personApplyMethods.put(SubType.PERSON_DELTA, new DeltaApplyMethodHolder(User.class, null, null, null));
            applyMethods.put(Category.DEVICE, deviceApplyMethods);
            applyMethods.put(Category.SCHEDULE, scheduleApplyMethods);
            applyMethods.put(Category.PERSON, personApplyMethods);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T getOldValueFromFieldOrGetter(Field field, Method getter, Object item) {
        if (field != null) {
            try {
                return field.get(item);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (InvocationTargetException ex2) {
                throw new RuntimeException(ex2);
            }
        } else if (getter != null) {
            return getter.invoke(item, new Object[0]);
        } else {
            throw new IllegalStateException();
        }
    }

    private static <T> T createInstanceFromHashMap(Class<T> type, LinkedHashMap<String, Object> hashMap) {
        try {
            T instance = type.newInstance();
            for (String k : hashMap.keySet()) {
                Log.d(TAG, k + " " + hashMap.get(k));
            }
            return instance;
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex2) {
            throw new RuntimeException(ex2);
        }
    }

    private boolean applyValue(Object item, Field field, Method getter, Method setter, Delta delta) {
        Class fieldType;
        Object obj = 1;
        if (field != null) {
            try {
                fieldType = field.getType();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e2) {
                throw new RuntimeException(e2);
            }
        } else if (getter == null || setter == null) {
            throw new IllegalStateException();
        } else {
            fieldType = setter.getParameterTypes()[0];
        }
        if (delta.newValue != null) {
            Class deltaFieldType = delta.newValue.getClass();
            if (!deltaFieldType.equals(fieldType)) {
                if (!(deltaFieldType == Integer.class && (fieldType == Integer.TYPE || fieldType == Long.TYPE || fieldType == Float.TYPE || fieldType == Double.TYPE))) {
                    if (!((deltaFieldType == Long.class && fieldType == Long.TYPE) || ((deltaFieldType == Float.class && fieldType == Float.TYPE) || ((deltaFieldType == Double.class && fieldType == Double.TYPE) || ((deltaFieldType == Boolean.class && fieldType == Boolean.TYPE) || ((deltaFieldType == Double.class && fieldType == Float.TYPE) || ((deltaFieldType == Long.class && fieldType == Date.class) || ((deltaFieldType == String.class && fieldType == PropertyIdHolder.class) || ((deltaFieldType == String.class && Enum.class.isAssignableFrom(fieldType)) || (fieldType.isArray() && List.class.isAssignableFrom(deltaFieldType))))))))))) {
                        obj = null;
                    }
                }
                if (obj == null) {
                    CrashReporterUtils.silentExceptionThatCrashesDebugBuilds(new Exception("delta has class " + deltaFieldType.getCanonicalName() + " but we have " + fieldType.getCanonicalName() + " in " + item.getClass().getCanonicalName() + " for " + delta.propertyName));
                }
            }
        }
        ApplyHandler handler = (ApplyHandler) applyHandlers.get(fieldType);
        if (handler != null) {
            return handler.apply(item, field, getter, setter, delta);
        }
        throw new RuntimeException("unhandled field type " + field.getType());
    }

    private DatabaseObject invokeFindMethod(Class<? extends DatabaseObject> type, String id) {
        DatabaseObject item = (DatabaseObject) this.database.find(type, id);
        if (item == null || (item instanceof ModelObject)) {
            return item;
        }
        throw new RuntimeException(item.getClass() + " is not a sub class of " + ModelObject.class.getName());
    }

    private DatabaseObject fetchEntity(String entityId, Event event, Class<? extends DatabaseObject> type, InsertCallback insertCallback) {
        HttpResponseErrorHandler errorHandler = new HttpResponseErrorHandler();
        DatabaseObject entity = (DatabaseObject) this.restClient.getObjectById(null, entityId, type, errorHandler);
        if (entity == null || errorHandler.hasError) {
            return null;
        }
        if (insertCallback != null) {
            try {
                if (!insertCallback.beforeInsert(null, this.database, event, entity)) {
                    return entity;
                }
            } catch (InsertCallbackException ice) {
                CrashReporterUtils.silentExceptionThatCrashesDebugBuilds(ice);
                return entity;
            }
        }
        this.database.save(entity, true);
        return entity;
    }

    private void handleDeltaEvent(Event event) {
        if (applyMethods.containsKey(event.category)) {
            Map<SubType, DeltaApplyMethodHolder> categoryMethods = (Map) applyMethods.get(event.category);
            if (categoryMethods.containsKey(event.subType)) {
                DeltaApplyMethodHolder deltaApplyMethodHolder = (DeltaApplyMethodHolder) categoryMethods.get(event.subType);
                this.database.lock();
                String stringValue = event.getStringValue("id");
                String stringValue2 = event.getStringValue("type");
                if (stringValue == null) {
                    throw new IllegalStateException("id is null");
                } else if (stringValue2 == null) {
                    throw new IllegalStateException("type is null");
                } else {
                    if (stringValue2.equals("INSERT")) {
                        InsertCallback insertCallback = deltaApplyMethodHolder.insertCallback;
                        Class cls = deltaApplyMethodHolder.type;
                        if (invokeFindMethod(cls, stringValue) == null) {
                            fetchEntity(stringValue, event, cls, insertCallback);
                        }
                    } else if (stringValue2.equals("DELETE")) {
                        this.database.deleteById(deltaApplyMethodHolder.type, stringValue);
                    } else if (stringValue2.equals("UPDATE")) {
                        boolean deltasAreIncomplete;
                        List list;
                        List list2;
                        Class cls2 = deltaApplyMethodHolder.type;
                        DeltaApplyOptions deltaApplyOptions = (DeltaApplyOptions) cls2.getAnnotation(DeltaApplyOptions.class);
                        if (deltaApplyOptions != null) {
                            List asList = Arrays.asList(deltaApplyOptions.ignoredProperties());
                            List asList2 = Arrays.asList(deltaApplyOptions.silentProperties());
                            deltasAreIncomplete = deltaApplyOptions.deltasAreIncomplete();
                            list = asList2;
                            list2 = asList;
                        } else {
                            deltasAreIncomplete = false;
                            list = null;
                            list2 = null;
                        }
                        if (deltasAreIncomplete) {
                            Log.d(TAG, "deltas are incomplete for this class, fetching the whole thing");
                            fetchEntity(stringValue, event, deltaApplyMethodHolder.type, deltaApplyMethodHolder.insertCallback);
                        } else {
                            DatabaseObject invokeFindMethod = invokeFindMethod(deltaApplyMethodHolder.type, stringValue);
                            if (invokeFindMethod == null) {
                                CrashReporterUtils.silentExceptionThatCrashesDebugBuilds(new Exception("couldn't find item " + stringValue));
                            } else {
                                int i;
                                String replaceFirst;
                                int i2 = 0;
                                boolean z = false;
                                Field[] fields = cls2.getFields();
                                Map treeMap = new TreeMap();
                                for (Field field : fields) {
                                    treeMap.put(field.getName(), field);
                                }
                                Method[] methods = cls2.getMethods();
                                TreeMap treeMap2 = new TreeMap();
                                for (Method method : methods) {
                                    replaceFirst = method.getName().replaceFirst("set", "");
                                    replaceFirst = replaceFirst.substring(0, 1).toLowerCase() + replaceFirst.substring(1);
                                    if (method.getName().startsWith("set")) {
                                        treeMap2.put(replaceFirst, method);
                                    }
                                }
                                TreeMap treeMap3 = new TreeMap();
                                for (Method method2 : methods) {
                                    replaceFirst = method2.getName().replaceFirst("get", "");
                                    replaceFirst = replaceFirst.substring(0, 1).toLowerCase() + replaceFirst.substring(1);
                                    if (method2.getName().startsWith("get")) {
                                        treeMap3.put(replaceFirst, method2);
                                    }
                                }
                                Delta[] deltaArr = event.getDeltaContainer("updatedProperties").deltas;
                                String[] strArr = new String[deltaArr.length];
                                for (i = 0; i < strArr.length; i++) {
                                    strArr[i] = deltaArr[i].propertyName;
                                }
                                int length = deltaArr.length;
                                int i3 = 0;
                                while (i3 < length) {
                                    int i4;
                                    Delta delta = deltaArr[i3];
                                    if (list2 == null || !list2.contains(delta.propertyName)) {
                                        if (treeMap.containsKey(delta.propertyName)) {
                                            i2 |= applyValue(invokeFindMethod, (Field) treeMap.get(delta.propertyName), null, null, delta);
                                            if (StringUtils.equals(delta.propertyName, "lastUpdateDate") && i2 == 0) {
                                                break;
                                            }
                                            i4 = i2;
                                        } else {
                                            if (treeMap3.containsKey(delta.propertyName)) {
                                                if (treeMap2.containsKey(delta.propertyName)) {
                                                    i2 |= applyValue(invokeFindMethod, null, (Method) treeMap3.get(delta.propertyName), (Method) treeMap2.get(delta.propertyName), delta);
                                                    if (StringUtils.equals(delta.propertyName, "lastUpdateDate") && i2 == 0) {
                                                        break;
                                                    }
                                                    i4 = i2;
                                                }
                                            }
                                            CrashReporterUtils.silentExceptionThatCrashesDebugBuilds(new Exception("unhandled property " + delta.propertyName + " for class " + invokeFindMethod.getClass().getCanonicalName()));
                                            i4 = i2;
                                        }
                                        deltasAreIncomplete = (z || i4 == 0 || list == null || list.contains(delta.propertyName)) ? z : true;
                                    } else {
                                        deltasAreIncomplete = z;
                                        i4 = i2;
                                    }
                                    i3++;
                                    z = deltasAreIncomplete;
                                    i2 = i4;
                                }
                                if (i2 != 0) {
                                    if (!z) {
                                        Log.d(TAG, "deltas made no changes worth shouting about");
                                    }
                                    this.database.save(invokeFindMethod, z);
                                    if (deltaApplyMethodHolder.changeCallback != null) {
                                        deltaApplyMethodHolder.changeCallback.onChangeHappened$2668ceab(this.database, event);
                                    }
                                }
                            }
                        }
                    } else {
                        CrashReporterUtils.silentExceptionThatCrashesDebugBuilds(new Exception("unhandled type " + stringValue2));
                    }
                    this.database.unlock();
                    return;
                }
            }
            throw new RuntimeException("unhandled delta sub type " + event.subType);
        }
        throw new RuntimeException("unhandled delta category " + event.category);
    }

    public final void handleEvent(String eventString) {
        try {
            Event event = (Event) this.objectMapper.readValue(eventString, Event.class);
            if (event == null) {
                CrashReporterUtils.silentExceptionThatCrashesDebugBuilds(new Exception("event type is null, probably a missing type!"));
            } else if (event.type != null) {
                switch (event.type) {
                    case SCHEDULE_STATUS:
                        this.database.lock();
                        Device device = (Device) this.database.find(Device.class, event.deviceId);
                        if (device != null) {
                            Device device2 = (Device) this.restClient.getObjectById(this.database, event.deviceId, Device.class, new HttpResponseErrorHandler());
                            if (device2 != null) {
                                ResponseCacheItem.invalidate(this.database, device2.id, WaterUseResponse.class);
                                ResponseCacheItem.invalidate(this.database, device2.id, WeatherIntelligence.class);
                                device.scheduleExecution = device2.scheduleExecution;
                                this.database.save(device, true, false, true);
                            }
                        }
                        this.database.unlock();
                        return;
                    case ZONE_STATUS:
                        this.database.lock();
                        User.getLoggedInUser(this.database, this.prefsWrapper);
                        this.database.unlock();
                        return;
                    case DEVICE_STATUS:
                    case RAIN_DELAY:
                    case WEATHER_INTELLIGENCE:
                    case RAIN_SENSOR_DETECTION:
                    case WATER_BUDGET:
                    case USER_ACTION:
                    case BROWNOUT:
                        return;
                    case DELTA:
                        handleDeltaEvent(event);
                        return;
                    default:
                        CrashReporterUtils.silentExceptionThatCrashesDebugBuilds(new RuntimeException("unhandled event type " + event.type));
                        return;
                }
                throw new RuntimeException(e);
            } else {
                CrashReporterUtils.silentExceptionThatCrashesDebugBuilds(new IllegalStateException("unhandled or unknown event type"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static /* synthetic */ void access$200(Field x0, Method x1, Object x2, Object x3) throws IllegalAccessException, InvocationTargetException {
        if (x0 != null) {
            x0.set(x2, x3);
        } else if (x1 != null) {
            x1.invoke(x2, new Object[]{x3});
        } else {
            throw new IllegalStateException();
        }
    }

    static /* synthetic */ float access$700(Object x0) {
        if (x0 instanceof Integer) {
            return (float) ((Integer) x0).intValue();
        }
        if (x0 instanceof Double) {
            return ((Double) x0).floatValue();
        }
        return ((Float) x0).floatValue();
    }

    static /* synthetic */ double access$800(Object x0) {
        if (x0 instanceof Integer) {
            return (double) ((Integer) x0).intValue();
        }
        return ((Double) x0).doubleValue();
    }
}
