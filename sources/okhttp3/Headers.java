package okhttp3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import okhttp3.internal.Util;

public final class Headers {
    private final String[] namesAndValues;

    public static final class Builder {
        final List<String> namesAndValues = new ArrayList(20);

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

        final Builder addLenient(String name, String value) {
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
                throw new NullPointerException("name == null");
            } else if (name.isEmpty()) {
                throw new IllegalArgumentException("name is empty");
            } else {
                int i;
                char c;
                int length = name.length();
                for (i = 0; i < length; i++) {
                    c = name.charAt(i);
                    if (c <= ' ' || c >= '') {
                        throw new IllegalArgumentException(Util.format("Unexpected char %#04x at %d in header name: %s", Integer.valueOf(c), Integer.valueOf(i), name));
                    }
                }
                if (value == null) {
                    throw new NullPointerException("value == null");
                }
                i = 0;
                length = value.length();
                while (i < length) {
                    c = value.charAt(i);
                    if ((c > '\u001f' || c == '\t') && c < '') {
                        i++;
                    } else {
                        throw new IllegalArgumentException(Util.format("Unexpected char %#04x at %d in %s value: %s", Integer.valueOf(c), Integer.valueOf(i), name, value));
                    }
                }
            }
        }

        public final String get(String name) {
            for (int i = this.namesAndValues.size() - 2; i >= 0; i -= 2) {
                if (name.equalsIgnoreCase((String) this.namesAndValues.get(i))) {
                    return (String) this.namesAndValues.get(i + 1);
                }
            }
            return null;
        }

        public final Headers build() {
            return new Headers(this);
        }
    }

    Headers(Builder builder) {
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

    public final int size() {
        return this.namesAndValues.length / 2;
    }

    public final String name(int index) {
        return this.namesAndValues[index << 1];
    }

    public final String value(int index) {
        return this.namesAndValues[(index << 1) + 1];
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

    public final boolean equals(Object other) {
        return (other instanceof Headers) && Arrays.equals(((Headers) other).namesAndValues, this.namesAndValues);
    }

    public final int hashCode() {
        return Arrays.hashCode(this.namesAndValues);
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
