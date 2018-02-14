package com.google.gson;

import com.google.gson.internal.Excluder;
import com.google.gson.internal.bind.TypeAdapters;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GsonBuilder {
    private boolean complexMapKeySerialization = false;
    private int dateStyle = 2;
    private boolean escapeHtmlChars = true;
    private Excluder excluder = Excluder.DEFAULT;
    private final List<TypeAdapterFactory> factories = new ArrayList();
    private FieldNamingStrategy fieldNamingPolicy = FieldNamingPolicy.IDENTITY;
    private boolean generateNonExecutableJson = false;
    private final List<TypeAdapterFactory> hierarchyFactories = new ArrayList();
    private final Map<Type, InstanceCreator<?>> instanceCreators = new HashMap();
    private boolean lenient = false;
    private LongSerializationPolicy longSerializationPolicy = LongSerializationPolicy.DEFAULT;
    private boolean prettyPrinting = false;
    private boolean serializeNulls = false;
    private boolean serializeSpecialFloatingPointValues = false;
    private int timeStyle = 2;

    public final GsonBuilder disableHtmlEscaping() {
        this.escapeHtmlChars = false;
        return this;
    }

    public final Gson create() {
        TypeAdapter defaultDateTypeAdapter;
        TypeAdapter defaultDateTypeAdapter2;
        TypeAdapter defaultDateTypeAdapter3;
        List<TypeAdapterFactory> factories = new ArrayList((this.factories.size() + this.hierarchyFactories.size()) + 3);
        factories.addAll(this.factories);
        Collections.reverse(factories);
        Collections.reverse(this.hierarchyFactories);
        factories.addAll(this.hierarchyFactories);
        String str = null;
        int i = this.dateStyle;
        int i2 = this.timeStyle;
        if (str == null || "".equals(str.trim())) {
            if (!(i == 2 || i2 == 2)) {
                defaultDateTypeAdapter = new DefaultDateTypeAdapter(Date.class, i, i2);
                defaultDateTypeAdapter2 = new DefaultDateTypeAdapter(Timestamp.class, i, i2);
                defaultDateTypeAdapter3 = new DefaultDateTypeAdapter(java.sql.Date.class, i, i2);
            }
            return new Gson(this.excluder, this.fieldNamingPolicy, this.instanceCreators, false, false, false, this.escapeHtmlChars, false, false, false, this.longSerializationPolicy, factories);
        }
        defaultDateTypeAdapter = new DefaultDateTypeAdapter(Date.class, str);
        defaultDateTypeAdapter2 = new DefaultDateTypeAdapter(Timestamp.class, str);
        defaultDateTypeAdapter3 = new DefaultDateTypeAdapter(java.sql.Date.class, str);
        factories.add(TypeAdapters.newFactory(Date.class, defaultDateTypeAdapter));
        factories.add(TypeAdapters.newFactory(Timestamp.class, defaultDateTypeAdapter2));
        factories.add(TypeAdapters.newFactory(java.sql.Date.class, defaultDateTypeAdapter3));
        return new Gson(this.excluder, this.fieldNamingPolicy, this.instanceCreators, false, false, false, this.escapeHtmlChars, false, false, false, this.longSerializationPolicy, factories);
    }
}
