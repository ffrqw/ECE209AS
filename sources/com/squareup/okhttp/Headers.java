package com.squareup.okhttp;

import com.squareup.okhttp.internal.http.HttpDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class Headers {
    private final String[] namesAndValues;

    public static final class Builder {
        private final List<String> namesAndValues = new ArrayList(20);

        final Builder addLenient(String line) {
            int index = line.indexOf(":", 1);
            if (index != -1) {
                return addLenient(line.substring(0, index), line.substring(index + 1));
            }
            if (line.startsWith(":")) {
                return addLenient("", line.substring(1));
            }
            return addLenient("", line);
        }

        public final Builder add(String name, String value) {
            checkNameAndValue(name, value);
            return addLenient(name, value);
        }

        private Builder addLenient(String name, String value) {
            this.namesAndValues.add(name);
            this.namesAndValues.add(value.trim());
            return this;
        }

        public final Builder removeAll(String name) {
            int i = 0;
            while (i < this.namesAndValues.size()) {
                if (name.equalsIgnoreCase((String) this.namesAndValues.get(i))) {
                    this.namesAndValues.remove(i);
                    this.namesAndValues.remove(i);
                    i -= 2;
                }
                i += 2;
            }
            return this;
        }

        public final Builder set(String name, String value) {
            checkNameAndValue(name, value);
            removeAll(name);
            addLenient(name, value);
            return this;
        }

        private static void checkNameAndValue(String name, String value) {
            if (name == null) {
                throw new IllegalArgumentException("name == null");
            } else if (name.isEmpty()) {
                throw new IllegalArgumentException("name is empty");
            } else {
                int i;
                char c;
                int length = name.length();
                for (i = 0; i < length; i++) {
                    c = name.charAt(i);
                    if (c <= '\u001f' || c >= '') {
                        throw new IllegalArgumentException(String.format("Unexpected char %#04x at %d in header name: %s", new Object[]{Integer.valueOf(c), Integer.valueOf(i), name}));
                    }
                }
                if (value == null) {
                    throw new IllegalArgumentException("value == null");
                }
                length = value.length();
                for (i = 0; i < length; i++) {
                    c = value.charAt(i);
                    if (c <= '\u001f' || c >= '') {
                        throw new IllegalArgumentException(String.format("Unexpected char %#04x at %d in header value: %s", new Object[]{Integer.valueOf(c), Integer.valueOf(i), value}));
                    }
                }
            }
        }

        public final Headers build() {
            return new Headers();
        }
    }

    private Headers(Builder builder) {
        this.namesAndValues = (String[]) builder.namesAndValues.toArray(new String[builder.namesAndValues.size()]);
    }

    public final String get(String name) {
        String[] strArr = this.namesAndValues;
        for (int length = strArr.length - 2; length >= 0; length -= 2) {
            if (name.equalsIgnoreCase(strArr[length])) {
                return strArr[length + 1];
            }
        }
        return null;
    }

    public final Date getDate(String name) {
        String value = get(name);
        return value != null ? HttpDate.parse(value) : null;
    }

    public final int size() {
        return this.namesAndValues.length / 2;
    }

    public final String name(int index) {
        int nameIndex = index << 1;
        if (nameIndex < 0 || nameIndex >= this.namesAndValues.length) {
            return null;
        }
        return this.namesAndValues[nameIndex];
    }

    public final String value(int index) {
        int valueIndex = (index << 1) + 1;
        if (valueIndex < 0 || valueIndex >= this.namesAndValues.length) {
            return null;
        }
        return this.namesAndValues[valueIndex];
    }

    public final Set<String> names() {
        TreeSet<String> result = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        int size = this.namesAndValues.length / 2;
        for (int i = 0; i < size; i++) {
            result.add(name(i));
        }
        return Collections.unmodifiableSet(result);
    }

    public final List<String> values(String name) {
        List<String> result = null;
        int size = this.namesAndValues.length / 2;
        for (int i = 0; i < size; i++) {
            if (name.equalsIgnoreCase(name(i))) {
                if (result == null) {
                    result = new ArrayList(2);
                }
                result.add(value(i));
            }
        }
        if (result != null) {
            return Collections.unmodifiableList(result);
        }
        return Collections.emptyList();
    }

    public final Builder newBuilder() {
        Builder result = new Builder();
        Collections.addAll(result.namesAndValues, this.namesAndValues);
        return result;
    }

    public final String toString() {
        StringBuilder result = new StringBuilder();
        int size = this.namesAndValues.length / 2;
        for (int i = 0; i < size; i++) {
            result.append(name(i)).append(": ").append(value(i)).append("\n");
        }
        return result.toString();
    }
}
