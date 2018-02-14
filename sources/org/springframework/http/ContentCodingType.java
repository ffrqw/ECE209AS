package org.springframework.http;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;

public final class ContentCodingType implements Comparable<ContentCodingType> {
    public static final ContentCodingType ALL = valueOf("*");
    public static final ContentCodingType GZIP = valueOf("gzip");
    public static final ContentCodingType IDENTITY = valueOf("identity");
    public static final Comparator<ContentCodingType> QUALITY_VALUE_COMPARATOR = new Comparator<ContentCodingType>() {
        public final /* bridge */ /* synthetic */ int compare(Object x0, Object x1) {
            ContentCodingType contentCodingType = (ContentCodingType) x0;
            ContentCodingType contentCodingType2 = (ContentCodingType) x1;
            int compare = Double.compare(contentCodingType2.getQualityValue(), contentCodingType.getQualityValue());
            if (compare != 0) {
                return compare;
            }
            if (contentCodingType.isWildcardType() && !contentCodingType2.isWildcardType()) {
                return 1;
            }
            if (contentCodingType2.isWildcardType() && !contentCodingType.isWildcardType()) {
                return -1;
            }
            contentCodingType.getType().equals(contentCodingType2.getType());
            return 0;
        }
    };
    private static final BitSet TOKEN;
    private final Map<String, String> parameters;
    private final String type;

    public final /* bridge */ /* synthetic */ int compareTo(Object x0) {
        ContentCodingType contentCodingType = (ContentCodingType) x0;
        int compareToIgnoreCase = this.type.compareToIgnoreCase(contentCodingType.type);
        if (compareToIgnoreCase != 0) {
            return compareToIgnoreCase;
        }
        compareToIgnoreCase = this.parameters.size() - contentCodingType.parameters.size();
        if (compareToIgnoreCase != 0) {
            return compareToIgnoreCase;
        }
        TreeSet treeSet = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        treeSet.addAll(this.parameters.keySet());
        TreeSet treeSet2 = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        treeSet2.addAll(contentCodingType.parameters.keySet());
        Iterator it = treeSet.iterator();
        Iterator it2 = treeSet2.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            String str2 = (String) it2.next();
            int compareToIgnoreCase2 = str.compareToIgnoreCase(str2);
            if (compareToIgnoreCase2 != 0) {
                return compareToIgnoreCase2;
            }
            str = (String) this.parameters.get(str);
            str2 = (String) contentCodingType.parameters.get(str2);
            if (str2 == null) {
                str2 = "";
            }
            compareToIgnoreCase = str.compareTo(str2);
            if (compareToIgnoreCase != 0) {
                return compareToIgnoreCase;
            }
        }
        return 0;
    }

    static {
        BitSet ctl = new BitSet(128);
        for (int i = 0; i <= 31; i++) {
            ctl.set(i);
        }
        ctl.set(127);
        BitSet separators = new BitSet(128);
        separators.set(40);
        separators.set(41);
        separators.set(60);
        separators.set(62);
        separators.set(64);
        separators.set(44);
        separators.set(59);
        separators.set(58);
        separators.set(92);
        separators.set(34);
        separators.set(47);
        separators.set(91);
        separators.set(93);
        separators.set(63);
        separators.set(61);
        separators.set(123);
        separators.set(125);
        separators.set(32);
        separators.set(9);
        BitSet bitSet = new BitSet(128);
        TOKEN = bitSet;
        bitSet.set(0, 128);
        TOKEN.andNot(ctl);
        TOKEN.andNot(separators);
    }

    private ContentCodingType(String type, Map<String, String> parameters) {
        Assert.hasLength(type, "'type' must not be empty");
        checkToken(type);
        this.type = type.toLowerCase(Locale.ENGLISH);
        if (CollectionUtils.isEmpty((Map) parameters)) {
            this.parameters = Collections.emptyMap();
            return;
        }
        Map<String, String> m = new LinkedCaseInsensitiveMap(parameters.size(), Locale.ENGLISH);
        for (Entry<String, String> entry : parameters.entrySet()) {
            String attribute = (String) entry.getKey();
            String value = (String) entry.getValue();
            Assert.hasLength(attribute, "parameter attribute must not be empty");
            Assert.hasLength(value, "parameter value must not be empty");
            checkToken(attribute);
            if ("q".equals(attribute)) {
                boolean z;
                String unquote = unquote(value);
                double parseDouble = Double.parseDouble(unquote);
                if (parseDouble < 0.0d || parseDouble > 1.0d) {
                    z = false;
                } else {
                    z = true;
                }
                Assert.isTrue(z, "Invalid quality value \"" + unquote + "\": should be between 0.0 and 1.0");
            } else if (!isQuotedString(value)) {
                checkToken(value);
            }
            m.put(attribute, unquote(value));
        }
        this.parameters = Collections.unmodifiableMap(m);
    }

    private static void checkToken(String s) {
        int i = 0;
        while (i < s.length()) {
            char ch = s.charAt(i);
            if (TOKEN.get(ch)) {
                i++;
            } else {
                throw new IllegalArgumentException("Invalid token character '" + ch + "' in token \"" + s + "\"");
            }
        }
    }

    private static boolean isQuotedString(String s) {
        return s.length() > 1 && s.startsWith("\"") && s.endsWith("\"");
    }

    private String unquote(String s) {
        if (s == null) {
            return null;
        }
        return isQuotedString(s) ? s.substring(1, s.length() - 1) : s;
    }

    public final String getType() {
        return this.type;
    }

    public final boolean isWildcardType() {
        return "*".equals(this.type);
    }

    public final double getQualityValue() {
        String qualityFactory = (String) this.parameters.get("q");
        return qualityFactory != null ? Double.parseDouble(qualityFactory) : 1.0d;
    }

    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ContentCodingType)) {
            return false;
        }
        ContentCodingType otherType = (ContentCodingType) other;
        if (this.type.equalsIgnoreCase(otherType.type) && this.parameters.equals(otherType.parameters)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return (this.type.hashCode() * 31) + this.parameters.hashCode();
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        appendTo(builder);
        return builder.toString();
    }

    private void appendTo(StringBuilder builder) {
        builder.append(this.type);
        for (Entry entry : this.parameters.entrySet()) {
            builder.append(';');
            builder.append((String) entry.getKey());
            builder.append('=');
            builder.append((String) entry.getValue());
        }
    }

    private static ContentCodingType valueOf(String value) {
        return parseCodingType(value);
    }

    private static ContentCodingType parseCodingType(String codingType) {
        Assert.hasLength(codingType, "'codingType' must not be empty");
        String[] parts = StringUtils.tokenizeToStringArray(codingType, ";");
        String type = parts[0].trim();
        Map<String, String> parameters = null;
        if (parts.length > 1) {
            parameters = new LinkedHashMap(parts.length - 1);
            for (int i = 1; i < parts.length; i++) {
                String parameter = parts[i];
                int eqIndex = parameter.indexOf(61);
                if (eqIndex != -1) {
                    parameters.put(parameter.substring(0, eqIndex), parameter.substring(eqIndex + 1, parameter.length()));
                }
            }
        }
        return new ContentCodingType(type, parameters);
    }

    public static String toString(Collection<ContentCodingType> codingTypes) {
        StringBuilder builder = new StringBuilder();
        Iterator<ContentCodingType> iterator = codingTypes.iterator();
        while (iterator.hasNext()) {
            ((ContentCodingType) iterator.next()).appendTo(builder);
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public static List<ContentCodingType> parseCodingTypes(String codingTypes) {
        if (!StringUtils.hasLength(codingTypes)) {
            return Collections.emptyList();
        }
        String[] tokens = codingTypes.split(",");
        List<ContentCodingType> result = new ArrayList(tokens.length);
        String[] arr$ = tokens;
        int len$ = tokens.length;
        for (int i$ = 0; i$ < len$; i$++) {
            result.add(parseCodingType(arr$[i$]));
        }
        return result;
    }
}
