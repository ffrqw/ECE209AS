package com.google.gson;

import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class DefaultDateTypeAdapter extends TypeAdapter<Date> {
    private final Class<? extends Date> dateType;
    private final DateFormat enUsFormat;
    private final DateFormat localFormat;

    public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
        Date date = (Date) obj;
        synchronized (this.localFormat) {
            jsonWriter.value(this.enUsFormat.format(date));
        }
    }

    DefaultDateTypeAdapter(Class<? extends Date> dateType, String datePattern) {
        this((Class) dateType, new SimpleDateFormat(datePattern, Locale.US), new SimpleDateFormat(datePattern));
    }

    public DefaultDateTypeAdapter(Class<? extends Date> dateType, int dateStyle, int timeStyle) {
        this((Class) dateType, DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US), DateFormat.getDateTimeInstance(dateStyle, timeStyle));
    }

    private DefaultDateTypeAdapter(Class<? extends Date> dateType, DateFormat enUsFormat, DateFormat localFormat) {
        if (dateType == Date.class || dateType == java.sql.Date.class || dateType == Timestamp.class) {
            this.dateType = dateType;
            this.enUsFormat = enUsFormat;
            this.localFormat = localFormat;
            return;
        }
        throw new IllegalArgumentException("Date type must be one of " + Date.class + ", " + Timestamp.class + ", or " + java.sql.Date.class + " but was " + dateType);
    }

    private Date deserializeToDate(String s) {
        Date parse;
        synchronized (this.localFormat) {
            try {
                parse = this.localFormat.parse(s);
            } catch (ParseException e) {
                try {
                    parse = this.enUsFormat.parse(s);
                } catch (ParseException e2) {
                    try {
                        parse = ISO8601Utils.parse(s, new ParsePosition(0));
                    } catch (ParseException e3) {
                        throw new JsonSyntaxException(s, e3);
                    }
                }
            }
        }
        return parse;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DefaultDateTypeAdapter");
        sb.append('(').append(this.localFormat.getClass().getSimpleName()).append(')');
        return sb.toString();
    }

    public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() != JsonToken.STRING) {
            throw new JsonParseException("The date should be a string value");
        }
        Date deserializeToDate = deserializeToDate(jsonReader.nextString());
        if (this.dateType == Date.class) {
            return deserializeToDate;
        }
        if (this.dateType == Timestamp.class) {
            return new Timestamp(deserializeToDate.getTime());
        }
        if (this.dateType == java.sql.Date.class) {
            return new java.sql.Date(deserializeToDate.getTime());
        }
        throw new AssertionError();
    }
}
