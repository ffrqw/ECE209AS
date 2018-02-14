package org.springframework.web.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class UriComponents implements Serializable {
    private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");
    private final String fragment;
    private final String scheme;

    public interface UriTemplateVariables {
        public static final Object SKIP_VALUE = UriTemplateVariables.class;

        Object getValue(String str);
    }

    private static class VarArgsTemplateVariables implements UriTemplateVariables {
        private final Iterator<Object> valueIterator;

        public VarArgsTemplateVariables(Object... uriVariableValues) {
            this.valueIterator = Arrays.asList(uriVariableValues).iterator();
        }

        public final Object getValue(String name) {
            if (this.valueIterator.hasNext()) {
                return this.valueIterator.next();
            }
            throw new IllegalArgumentException("Not enough variable values available to expand '" + name + "'");
        }
    }

    public abstract UriComponents encode(String str) throws UnsupportedEncodingException;

    abstract UriComponents expandInternal(UriTemplateVariables uriTemplateVariables);

    public abstract URI toUri();

    public abstract String toUriString();

    protected UriComponents(String scheme, String fragment) {
        this.scheme = scheme;
        this.fragment = fragment;
    }

    public final String getScheme() {
        return this.scheme;
    }

    public final String getFragment() {
        return this.fragment;
    }

    public final UriComponents encode() {
        try {
            return encode("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public final String toString() {
        return toUriString();
    }

    static String expandUriComponent(String source, UriTemplateVariables uriVariables) {
        if (source == null) {
            return null;
        }
        if (source.indexOf(123) == -1) {
            return source;
        }
        Matcher matcher = NAMES_PATTERN.matcher(source);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String variableName;
            String match = matcher.group(1);
            int indexOf = match.indexOf(58);
            if (indexOf != -1) {
                variableName = match.substring(0, indexOf);
            } else {
                variableName = match;
            }
            Object variableValue = uriVariables.getValue(variableName);
            if (!UriTemplateVariables.SKIP_VALUE.equals(variableValue)) {
                String variableValueString;
                if (variableValue != null) {
                    variableValueString = variableValue.toString();
                } else {
                    variableValueString = "";
                }
                matcher.appendReplacement(sb, Matcher.quoteReplacement(variableValueString));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
