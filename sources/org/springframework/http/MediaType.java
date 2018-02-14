package org.springframework.http;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
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

public final class MediaType implements Comparable<MediaType> {
    public static final MediaType ALL = parseMediaType("*/*");
    public static final MediaType APPLICATION_ATOM_XML = parseMediaType("application/atom+xml");
    public static final MediaType APPLICATION_FORM_URLENCODED = parseMediaType("application/x-www-form-urlencoded");
    public static final MediaType APPLICATION_JSON = parseMediaType("application/json");
    public static final MediaType APPLICATION_OCTET_STREAM = parseMediaType("application/octet-stream");
    public static final MediaType APPLICATION_RSS_XML = parseMediaType("application/rss+xml");
    public static final MediaType APPLICATION_WILDCARD_XML = parseMediaType("application/*+xml");
    public static final MediaType APPLICATION_XHTML_XML = parseMediaType("application/xhtml+xml");
    public static final MediaType APPLICATION_XML = parseMediaType("application/xml");
    public static final MediaType IMAGE_GIF = parseMediaType("image/gif");
    public static final MediaType IMAGE_JPEG = parseMediaType("image/jpeg");
    public static final MediaType IMAGE_PNG = parseMediaType("image/png");
    public static final MediaType MULTIPART_FORM_DATA = parseMediaType("multipart/form-data");
    public static final Comparator<MediaType> QUALITY_VALUE_COMPARATOR = new Comparator<MediaType>() {
        public final /* bridge */ /* synthetic */ int compare(Object x0, Object x1) {
            MediaType mediaType = (MediaType) x0;
            MediaType mediaType2 = (MediaType) x1;
            int compare = Double.compare(mediaType2.getQualityValue(), mediaType.getQualityValue());
            if (compare != 0) {
                return compare;
            }
            if (!mediaType.isWildcardType() || mediaType2.isWildcardType()) {
                if (mediaType2.isWildcardType() && !mediaType.isWildcardType()) {
                    return -1;
                }
                if (!mediaType.getType().equals(mediaType2.getType())) {
                    return 0;
                }
                if (!mediaType.isWildcardSubtype() || mediaType2.isWildcardSubtype()) {
                    if (mediaType2.isWildcardSubtype() && !mediaType.isWildcardSubtype()) {
                        return -1;
                    }
                    if (!mediaType.getSubtype().equals(mediaType2.getSubtype())) {
                        return 0;
                    }
                    compare = mediaType.parameters.size();
                    int size = mediaType2.parameters.size();
                    if (size < compare) {
                        return -1;
                    }
                    if (size == compare) {
                        return 0;
                    }
                }
            }
            return 1;
        }
    };
    public static final Comparator<MediaType> SPECIFICITY_COMPARATOR = new Comparator<MediaType>() {
        public final /* bridge */ /* synthetic */ int compare(Object x0, Object x1) {
            MediaType mediaType = (MediaType) x0;
            MediaType mediaType2 = (MediaType) x1;
            if (!mediaType.isWildcardType() || mediaType2.isWildcardType()) {
                if (mediaType2.isWildcardType() && !mediaType.isWildcardType()) {
                    return -1;
                }
                if (!mediaType.getType().equals(mediaType2.getType())) {
                    return 0;
                }
                if (!mediaType.isWildcardSubtype() || mediaType2.isWildcardSubtype()) {
                    if (mediaType2.isWildcardSubtype() && !mediaType.isWildcardSubtype()) {
                        return -1;
                    }
                    if (!mediaType.getSubtype().equals(mediaType2.getSubtype())) {
                        return 0;
                    }
                    int compare = Double.compare(mediaType2.getQualityValue(), mediaType.getQualityValue());
                    if (compare != 0) {
                        return compare;
                    }
                    compare = mediaType.parameters.size();
                    int size = mediaType2.parameters.size();
                    if (size < compare) {
                        return -1;
                    }
                    if (size == compare) {
                        return 0;
                    }
                }
            }
            return 1;
        }
    };
    public static final MediaType TEXT_HTML = parseMediaType("text/html");
    public static final MediaType TEXT_PLAIN = parseMediaType("text/plain");
    public static final MediaType TEXT_XML = parseMediaType("text/xml");
    private static final BitSet TOKEN;
    private final Map<String, String> parameters;
    private final String subtype;
    private final String type;

    public final /* bridge */ /* synthetic */ int compareTo(Object x0) {
        MediaType mediaType = (MediaType) x0;
        int compareToIgnoreCase = this.type.compareToIgnoreCase(mediaType.type);
        if (compareToIgnoreCase != 0) {
            return compareToIgnoreCase;
        }
        compareToIgnoreCase = this.subtype.compareToIgnoreCase(mediaType.subtype);
        if (compareToIgnoreCase != 0) {
            return compareToIgnoreCase;
        }
        compareToIgnoreCase = this.parameters.size() - mediaType.parameters.size();
        if (compareToIgnoreCase != 0) {
            return compareToIgnoreCase;
        }
        TreeSet treeSet = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        treeSet.addAll(this.parameters.keySet());
        TreeSet treeSet2 = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        treeSet2.addAll(mediaType.parameters.keySet());
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
            str2 = (String) mediaType.parameters.get(str2);
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

    public MediaType(String type, String subtype) {
        this(type, subtype, Collections.emptyMap());
    }

    public MediaType(String type, String subtype, Charset charset) {
        this(type, subtype, Collections.singletonMap("charset", charset.name()));
    }

    private MediaType(String type, String subtype, Map<String, String> parameters) {
        Assert.hasLength(type, "type must not be empty");
        Assert.hasLength(subtype, "subtype must not be empty");
        checkToken(type);
        checkToken(subtype);
        this.type = type.toLowerCase(Locale.ENGLISH);
        this.subtype = subtype.toLowerCase(Locale.ENGLISH);
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
            } else if ("charset".equals(attribute)) {
                Charset.forName(unquote(value));
            } else if (!isQuotedString(value)) {
                checkToken(value);
            }
            m.put(attribute, value);
        }
        this.parameters = Collections.unmodifiableMap(m);
    }

    private static void checkToken(String token) {
        int i = 0;
        while (i < token.length()) {
            char ch = token.charAt(i);
            if (TOKEN.get(ch)) {
                i++;
            } else {
                throw new IllegalArgumentException("Invalid token character '" + ch + "' in token \"" + token + "\"");
            }
        }
    }

    private static boolean isQuotedString(String s) {
        if (s.length() < 2) {
            return false;
        }
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            return true;
        }
        return false;
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

    public final String getSubtype() {
        return this.subtype;
    }

    public final boolean isWildcardSubtype() {
        return "*".equals(this.subtype) || this.subtype.startsWith("*+");
    }

    public final Charset getCharSet() {
        String charSet = getParameter("charset");
        return charSet != null ? Charset.forName(unquote(charSet)) : null;
    }

    public final double getQualityValue() {
        String qualityFactory = getParameter("q");
        return qualityFactory != null ? Double.parseDouble(unquote(qualityFactory)) : 1.0d;
    }

    private String getParameter(String name) {
        return (String) this.parameters.get(name);
    }

    public final boolean includes(MediaType other) {
        if (other == null) {
            return false;
        }
        if (isWildcardType()) {
            return true;
        }
        if (!this.type.equals(other.type)) {
            return false;
        }
        if (this.subtype.equals(other.subtype)) {
            return true;
        }
        if (!isWildcardSubtype()) {
            return false;
        }
        int thisPlusIdx = this.subtype.indexOf(43);
        if (thisPlusIdx == -1) {
            return true;
        }
        int otherPlusIdx = other.subtype.indexOf(43);
        if (otherPlusIdx == -1) {
            return false;
        }
        String thisSubtypeNoSuffix = this.subtype.substring(0, thisPlusIdx);
        if (this.subtype.substring(thisPlusIdx + 1).equals(other.subtype.substring(otherPlusIdx + 1)) && "*".equals(thisSubtypeNoSuffix)) {
            return true;
        }
        return false;
    }

    public final boolean isCompatibleWith(MediaType other) {
        if (other == null) {
            return false;
        }
        if (isWildcardType() || other.isWildcardType()) {
            return true;
        }
        if (!this.type.equals(other.type)) {
            return false;
        }
        if (this.subtype.equals(other.subtype)) {
            return true;
        }
        if (!isWildcardSubtype() && !other.isWildcardSubtype()) {
            return false;
        }
        int thisPlusIdx = this.subtype.indexOf(43);
        int otherPlusIdx = other.subtype.indexOf(43);
        if (thisPlusIdx == -1 && otherPlusIdx == -1) {
            return true;
        }
        if (thisPlusIdx == -1 || otherPlusIdx == -1) {
            return false;
        }
        String thisSubtypeNoSuffix = this.subtype.substring(0, thisPlusIdx);
        String otherSubtypeNoSuffix = other.subtype.substring(0, otherPlusIdx);
        if (!this.subtype.substring(thisPlusIdx + 1).equals(other.subtype.substring(otherPlusIdx + 1))) {
            return false;
        }
        if ("*".equals(thisSubtypeNoSuffix) || "*".equals(otherSubtypeNoSuffix)) {
            return true;
        }
        return false;
    }

    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MediaType)) {
            return false;
        }
        MediaType otherType = (MediaType) other;
        if (this.type.equalsIgnoreCase(otherType.type) && this.subtype.equalsIgnoreCase(otherType.subtype) && this.parameters.equals(otherType.parameters)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return (((this.type.hashCode() * 31) + this.subtype.hashCode()) * 31) + this.parameters.hashCode();
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        appendTo(builder);
        return builder.toString();
    }

    private void appendTo(StringBuilder builder) {
        builder.append(this.type);
        builder.append('/');
        builder.append(this.subtype);
        for (Entry entry : this.parameters.entrySet()) {
            builder.append(';');
            builder.append((String) entry.getKey());
            builder.append('=');
            builder.append((String) entry.getValue());
        }
    }

    public static MediaType parseMediaType(String mediaType) {
        Assert.hasLength(mediaType, "'mediaType' must not be empty");
        String[] parts = StringUtils.tokenizeToStringArray(mediaType, ";");
        String fullType = parts[0].trim();
        if ("*".equals(fullType)) {
            fullType = "*/*";
        }
        int subIndex = fullType.indexOf(47);
        if (subIndex == -1) {
            throw new InvalidMediaTypeException(mediaType, "does not contain '/'");
        } else if (subIndex == fullType.length() - 1) {
            throw new InvalidMediaTypeException(mediaType, "does not contain subtype after '/'");
        } else {
            String type = fullType.substring(0, subIndex);
            String subtype = fullType.substring(subIndex + 1, fullType.length());
            if (!"*".equals(type) || "*".equals(subtype)) {
                Map parameters = null;
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
                try {
                    return new MediaType(type, subtype, parameters);
                } catch (UnsupportedCharsetException ex) {
                    throw new InvalidMediaTypeException(mediaType, "unsupported charset '" + ex.getCharsetName() + "'");
                } catch (IllegalArgumentException ex2) {
                    throw new InvalidMediaTypeException(mediaType, ex2.getMessage());
                }
            }
            throw new InvalidMediaTypeException(mediaType, "wildcard type is legal only in '*/*' (all media types)");
        }
    }

    public static String toString(Collection<MediaType> mediaTypes) {
        StringBuilder builder = new StringBuilder();
        Iterator<MediaType> iterator = mediaTypes.iterator();
        while (iterator.hasNext()) {
            ((MediaType) iterator.next()).appendTo(builder);
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public static void sortBySpecificity(List<MediaType> mediaTypes) {
        Assert.notNull(mediaTypes, "'mediaTypes' must not be null");
        if (mediaTypes.size() > 1) {
            Collections.sort(mediaTypes, SPECIFICITY_COMPARATOR);
        }
    }

    public MediaType(MediaType other, Map<String, String> parameters) {
        this(other.type, other.subtype, (Map) parameters);
    }
}
