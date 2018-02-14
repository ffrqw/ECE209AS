package org.springframework.web.util;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents.UriTemplateVariables;

final class HierarchicalUriComponents extends UriComponents {
    static final PathComponent NULL_PATH_COMPONENT = new PathComponent() {
        public final String getPath() {
            return null;
        }

        public final PathComponent encode(String encoding) throws UnsupportedEncodingException {
            return this;
        }

        public final void verify() {
        }

        public final PathComponent expand(UriTemplateVariables uriVariables) {
            return this;
        }

        public final boolean equals(Object obj) {
            return this == obj;
        }

        public final int hashCode() {
            return 42;
        }
    };
    private final boolean encoded;
    private final String host;
    private final PathComponent path;
    private final String port;
    private final MultiValueMap<String, String> queryParams;
    private final String userInfo;

    interface PathComponent extends Serializable {
        PathComponent encode(String str) throws UnsupportedEncodingException;

        PathComponent expand(UriTemplateVariables uriTemplateVariables);

        String getPath();

        void verify();
    }

    static final class FullPathComponent implements PathComponent {
        private final String path;

        public FullPathComponent(String path) {
            this.path = path;
        }

        public final String getPath() {
            return this.path;
        }

        public final void verify() {
            HierarchicalUriComponents.verifyUriComponent(this.path, Type.PATH);
        }

        public final boolean equals(Object obj) {
            return this == obj || ((obj instanceof FullPathComponent) && this.path.equals(((FullPathComponent) obj).path));
        }

        public final PathComponent encode(String encoding) throws UnsupportedEncodingException {
            return new FullPathComponent(HierarchicalUriComponents.encodeUriComponent(this.path, encoding, Type.PATH));
        }

        public final PathComponent expand(UriTemplateVariables uriVariables) {
            return new FullPathComponent(UriComponents.expandUriComponent(this.path, uriVariables));
        }

        public final int hashCode() {
            return this.path.hashCode();
        }
    }

    static final class PathComponentComposite implements PathComponent {
        private final List<PathComponent> pathComponents;

        public PathComponentComposite(List<PathComponent> pathComponents) {
            this.pathComponents = pathComponents;
        }

        public final String getPath() {
            StringBuilder pathBuilder = new StringBuilder();
            for (PathComponent pathComponent : this.pathComponents) {
                pathBuilder.append(pathComponent.getPath());
            }
            return pathBuilder.toString();
        }

        public final PathComponent encode(String encoding) throws UnsupportedEncodingException {
            List<PathComponent> encodedComponents = new ArrayList(this.pathComponents.size());
            for (PathComponent pathComponent : this.pathComponents) {
                encodedComponents.add(pathComponent.encode(encoding));
            }
            return new PathComponentComposite(encodedComponents);
        }

        public final void verify() {
            for (PathComponent pathComponent : this.pathComponents) {
                pathComponent.verify();
            }
        }

        public final PathComponent expand(UriTemplateVariables uriVariables) {
            List<PathComponent> expandedComponents = new ArrayList(this.pathComponents.size());
            for (PathComponent pathComponent : this.pathComponents) {
                expandedComponents.add(pathComponent.expand(uriVariables));
            }
            return new PathComponentComposite(expandedComponents);
        }
    }

    static final class PathSegmentComponent implements PathComponent {
        private final List<String> pathSegments;

        public PathSegmentComponent(List<String> pathSegments) {
            this.pathSegments = Collections.unmodifiableList(new ArrayList(pathSegments));
        }

        public final String getPath() {
            StringBuilder pathBuilder = new StringBuilder();
            pathBuilder.append('/');
            Iterator<String> iterator = this.pathSegments.iterator();
            while (iterator.hasNext()) {
                pathBuilder.append((String) iterator.next());
                if (iterator.hasNext()) {
                    pathBuilder.append('/');
                }
            }
            return pathBuilder.toString();
        }

        public final boolean equals(Object obj) {
            return this == obj || ((obj instanceof PathSegmentComponent) && this.pathSegments.equals(((PathSegmentComponent) obj).pathSegments));
        }

        public final PathComponent encode(String encoding) throws UnsupportedEncodingException {
            List<String> pathSegments = this.pathSegments;
            List<String> encodedPathSegments = new ArrayList(pathSegments.size());
            for (String pathSegment : pathSegments) {
                encodedPathSegments.add(HierarchicalUriComponents.encodeUriComponent(pathSegment, encoding, Type.PATH_SEGMENT));
            }
            return new PathSegmentComponent(encodedPathSegments);
        }

        public final void verify() {
            for (String pathSegment : this.pathSegments) {
                HierarchicalUriComponents.verifyUriComponent(pathSegment, Type.PATH_SEGMENT);
            }
        }

        public final PathComponent expand(UriTemplateVariables uriVariables) {
            List<String> pathSegments = this.pathSegments;
            List<String> expandedPathSegments = new ArrayList(pathSegments.size());
            for (String pathSegment : pathSegments) {
                expandedPathSegments.add(UriComponents.expandUriComponent(pathSegment, uriVariables));
            }
            return new PathSegmentComponent(expandedPathSegments);
        }

        public final int hashCode() {
            return this.pathSegments.hashCode();
        }
    }

    enum Type {
        SCHEME {
            public final boolean isAllowed(int c) {
                return Type.isAlpha(c) || Type.isDigit(c) || 43 == c || 45 == c || 46 == c;
            }
        },
        AUTHORITY {
            public final boolean isAllowed(int c) {
                return isUnreserved(c) || Type.isSubDelimiter(c) || 58 == c || 64 == c;
            }
        },
        USER_INFO {
            public final boolean isAllowed(int c) {
                return isUnreserved(c) || Type.isSubDelimiter(c) || 58 == c;
            }
        },
        HOST_IPV4 {
            public final boolean isAllowed(int c) {
                return isUnreserved(c) || Type.isSubDelimiter(c);
            }
        },
        HOST_IPV6 {
            public final boolean isAllowed(int c) {
                return isUnreserved(c) || Type.isSubDelimiter(c) || 91 == c || 93 == c || 58 == c;
            }
        },
        PORT {
            public final boolean isAllowed(int c) {
                return Type.isDigit(c);
            }
        },
        PATH {
            public final boolean isAllowed(int c) {
                return isPchar(c) || 47 == c;
            }
        },
        PATH_SEGMENT {
            public final boolean isAllowed(int c) {
                return isPchar(c);
            }
        },
        QUERY {
            public final boolean isAllowed(int c) {
                return isPchar(c) || 47 == c || 63 == c;
            }
        },
        QUERY_PARAM {
            public final boolean isAllowed(int c) {
                if (61 == c || 43 == c || 38 == c) {
                    return false;
                }
                if (isPchar(c) || 47 == c || 63 == c) {
                    return true;
                }
                return false;
            }
        },
        FRAGMENT {
            public final boolean isAllowed(int c) {
                return isPchar(c) || 47 == c || 63 == c;
            }
        };

        public abstract boolean isAllowed(int i);

        protected static boolean isAlpha(int c) {
            return (c >= 97 && c <= 122) || (c >= 65 && c <= 90);
        }

        protected static boolean isDigit(int c) {
            return c >= 48 && c <= 57;
        }

        protected static boolean isSubDelimiter(int c) {
            return 33 == c || 36 == c || 38 == c || 39 == c || 40 == c || 41 == c || 42 == c || 43 == c || 44 == c || 59 == c || 61 == c;
        }

        protected final boolean isUnreserved(int c) {
            return isAlpha(c) || isDigit(c) || 45 == c || 46 == c || 95 == c || 126 == c;
        }

        protected final boolean isPchar(int c) {
            return isUnreserved(c) || isSubDelimiter(c) || 58 == c || 64 == c;
        }
    }

    protected final /* bridge */ /* synthetic */ UriComponents expandInternal(UriTemplateVariables x0) {
        Assert.state(!this.encoded, "Cannot expand an already encoded UriComponents object");
        String expandUriComponent = UriComponents.expandUriComponent(getScheme(), x0);
        String expandUriComponent2 = UriComponents.expandUriComponent(this.userInfo, x0);
        String expandUriComponent3 = UriComponents.expandUriComponent(this.host, x0);
        String expandUriComponent4 = UriComponents.expandUriComponent(this.port, x0);
        PathComponent expand = this.path.expand(x0);
        MultiValueMap linkedMultiValueMap = new LinkedMultiValueMap(this.queryParams.size());
        for (Entry entry : this.queryParams.entrySet()) {
            String expandUriComponent5 = UriComponents.expandUriComponent((String) entry.getKey(), x0);
            List arrayList = new ArrayList(((List) entry.getValue()).size());
            for (String expandUriComponent6 : (List) entry.getValue()) {
                arrayList.add(UriComponents.expandUriComponent(expandUriComponent6, x0));
            }
            linkedMultiValueMap.put(expandUriComponent5, arrayList);
        }
        return new HierarchicalUriComponents(expandUriComponent, expandUriComponent2, expandUriComponent3, expandUriComponent4, expand, linkedMultiValueMap, UriComponents.expandUriComponent(getFragment(), x0), false, false);
    }

    HierarchicalUriComponents(String scheme, String userInfo, String host, String port, PathComponent path, MultiValueMap<String, String> queryParams, String fragment, boolean encoded, boolean verify) {
        super(scheme, fragment);
        this.userInfo = userInfo;
        this.host = host;
        this.port = port;
        if (path == null) {
            path = NULL_PATH_COMPONENT;
        }
        this.path = path;
        if (queryParams == null) {
            queryParams = new LinkedMultiValueMap(0);
        }
        this.queryParams = CollectionUtils.unmodifiableMultiValueMap(queryParams);
        this.encoded = encoded;
        if (verify && this.encoded) {
            verifyUriComponent(getScheme(), Type.SCHEME);
            verifyUriComponent(this.userInfo, Type.USER_INFO);
            verifyUriComponent(this.host, getHostType());
            this.path.verify();
            for (Entry entry : this.queryParams.entrySet()) {
                verifyUriComponent((String) entry.getKey(), Type.QUERY_PARAM);
                for (String verifyUriComponent : (List) entry.getValue()) {
                    verifyUriComponent(verifyUriComponent, Type.QUERY_PARAM);
                }
            }
            verifyUriComponent(getFragment(), Type.FRAGMENT);
        }
    }

    private int getPort() {
        if (this.port == null) {
            return -1;
        }
        if (!this.port.contains("{")) {
            return Integer.parseInt(this.port);
        }
        throw new IllegalStateException("The port contains a URI variable but has not been expanded yet: " + this.port);
    }

    private String getPath() {
        return this.path.getPath();
    }

    private String getQuery() {
        if (this.queryParams.isEmpty()) {
            return null;
        }
        StringBuilder queryBuilder = new StringBuilder();
        for (Entry<String, List<String>> entry : this.queryParams.entrySet()) {
            String name = (String) entry.getKey();
            Collection values = (List) entry.getValue();
            if (CollectionUtils.isEmpty(values)) {
                if (queryBuilder.length() != 0) {
                    queryBuilder.append('&');
                }
                queryBuilder.append(name);
            } else {
                for (Object value : values) {
                    if (queryBuilder.length() != 0) {
                        queryBuilder.append('&');
                    }
                    queryBuilder.append(name);
                    if (value != null) {
                        queryBuilder.append('=');
                        queryBuilder.append(value.toString());
                    }
                }
            }
        }
        return queryBuilder.toString();
    }

    static String encodeUriComponent(String source, String encoding, Type type) throws UnsupportedEncodingException {
        if (source == null) {
            return null;
        }
        Assert.hasLength(encoding, "Encoding must not be empty");
        Object bytes = source.getBytes(encoding);
        Assert.notNull(bytes, "Source must not be null");
        Assert.notNull(type, "Type must not be null");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bytes.length);
        for (int i : bytes) {
            int i2;
            if (i2 < 0) {
                i2 = (byte) (i2 + 256);
            }
            if (type.isAllowed(i2)) {
                byteArrayOutputStream.write(i2);
            } else {
                byteArrayOutputStream.write(37);
                char toUpperCase = Character.toUpperCase(Character.forDigit((i2 >> 4) & 15, 16));
                char toUpperCase2 = Character.toUpperCase(Character.forDigit(i2 & 15, 16));
                byteArrayOutputStream.write(toUpperCase);
                byteArrayOutputStream.write(toUpperCase2);
            }
        }
        return new String(byteArrayOutputStream.toByteArray(), "US-ASCII");
    }

    private Type getHostType() {
        return (this.host == null || !this.host.startsWith("[")) ? Type.HOST_IPV4 : Type.HOST_IPV6;
    }

    private static void verifyUriComponent(String source, Type type) {
        if (source != null) {
            int length = source.length();
            int i = 0;
            while (i < length) {
                char ch = source.charAt(i);
                if (ch == '%') {
                    if (i + 2 < length) {
                        char hex1 = source.charAt(i + 1);
                        char hex2 = source.charAt(i + 2);
                        int u = Character.digit(hex1, 16);
                        int l = Character.digit(hex2, 16);
                        if (u == -1 || l == -1) {
                            throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                        }
                        i += 2;
                    } else {
                        throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                    }
                } else if (!type.isAllowed(ch)) {
                    throw new IllegalArgumentException("Invalid character '" + ch + "' for " + type.name() + " in \"" + source + "\"");
                }
                i++;
            }
        }
    }

    public final String toUriString() {
        StringBuilder uriBuilder = new StringBuilder();
        if (getScheme() != null) {
            uriBuilder.append(getScheme());
            uriBuilder.append(':');
        }
        if (!(this.userInfo == null && this.host == null)) {
            uriBuilder.append("//");
            if (this.userInfo != null) {
                uriBuilder.append(this.userInfo);
                uriBuilder.append('@');
            }
            if (this.host != null) {
                uriBuilder.append(this.host);
            }
            if (getPort() != -1) {
                uriBuilder.append(':');
                uriBuilder.append(this.port);
            }
        }
        String path = getPath();
        if (StringUtils.hasLength(path)) {
            if (!(uriBuilder.length() == 0 || path.charAt(0) == '/')) {
                uriBuilder.append('/');
            }
            uriBuilder.append(path);
        }
        String query = getQuery();
        if (query != null) {
            uriBuilder.append('?');
            uriBuilder.append(query);
        }
        if (getFragment() != null) {
            uriBuilder.append('#');
            uriBuilder.append(getFragment());
        }
        return uriBuilder.toString();
    }

    public final URI toUri() {
        try {
            if (this.encoded) {
                return new URI(toString());
            }
            String path = getPath();
            if (!(!StringUtils.hasLength(path) || path.charAt(0) == '/' || (getScheme() == null && this.userInfo == null && this.host == null && getPort() == -1))) {
                path = "/" + path;
            }
            return new URI(getScheme(), this.userInfo, this.host, getPort(), path, getQuery(), getFragment());
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not create URI object: " + ex.getMessage(), ex);
        }
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HierarchicalUriComponents)) {
            return false;
        }
        HierarchicalUriComponents other = (HierarchicalUriComponents) obj;
        if (ObjectUtils.nullSafeEquals(getScheme(), other.getScheme()) && ObjectUtils.nullSafeEquals(this.userInfo, other.userInfo) && ObjectUtils.nullSafeEquals(this.host, other.host) && getPort() == other.getPort() && this.path.equals(other.path) && this.queryParams.equals(other.queryParams) && ObjectUtils.nullSafeEquals(getFragment(), other.getFragment())) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return (((((((((((ObjectUtils.nullSafeHashCode(getScheme()) * 31) + ObjectUtils.nullSafeHashCode(this.userInfo)) * 31) + ObjectUtils.nullSafeHashCode(this.host)) * 31) + ObjectUtils.nullSafeHashCode(this.port)) * 31) + this.path.hashCode()) * 31) + this.queryParams.hashCode()) * 31) + ObjectUtils.nullSafeHashCode(getFragment());
    }

    public final /* bridge */ /* synthetic */ UriComponents encode(String x0) throws UnsupportedEncodingException {
        Assert.hasLength(x0, "Encoding must not be empty");
        if (this.encoded) {
            return this;
        }
        String encodeUriComponent = encodeUriComponent(getScheme(), x0, Type.SCHEME);
        String encodeUriComponent2 = encodeUriComponent(this.userInfo, x0, Type.USER_INFO);
        String encodeUriComponent3 = encodeUriComponent(this.host, x0, getHostType());
        PathComponent encode = this.path.encode(x0);
        MultiValueMap linkedMultiValueMap = new LinkedMultiValueMap(this.queryParams.size());
        for (Entry entry : this.queryParams.entrySet()) {
            String encodeUriComponent4 = encodeUriComponent((String) entry.getKey(), x0, Type.QUERY_PARAM);
            List arrayList = new ArrayList(((List) entry.getValue()).size());
            for (String encodeUriComponent5 : (List) entry.getValue()) {
                arrayList.add(encodeUriComponent(encodeUriComponent5, x0, Type.QUERY_PARAM));
            }
            linkedMultiValueMap.put(encodeUriComponent4, arrayList);
        }
        return new HierarchicalUriComponents(encodeUriComponent, encodeUriComponent2, encodeUriComponent3, this.port, encode, linkedMultiValueMap, encodeUriComponent(getFragment(), x0, Type.FRAGMENT), true, false);
    }
}
