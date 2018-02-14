package org.springframework.web.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public final class UriComponentsBuilder {
    private static final Pattern HTTP_URL_PATTERN = Pattern.compile("^(?i)(http|https):(//(([^@\\[/?#]*)@)?(\\[[\\p{XDigit}\\:\\.]*[%\\p{Alnum}]*\\]|[^\\[/?#:]*)(:(\\d*(?:\\{[^/]+?\\})?))?)?([^?#]*)(\\?(.*))?");
    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");
    private static final Pattern URI_PATTERN = Pattern.compile("^(([^:/?#]+):)?(//(([^@\\[/?#]*)@)?(\\[[\\p{XDigit}\\:\\.]*[%\\p{Alnum}]*\\]|[^\\[/?#:]*)(:(\\d*(?:\\{[^/]+?\\})?))?)?([^?#]*)(\\?([^#]*))?(#(.*))?");
    private String fragment;
    private String host;
    private CompositePathComponentBuilder pathBuilder = new CompositePathComponentBuilder();
    private String port;
    private final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap();
    private String scheme;
    private String ssp;
    private String userInfo;

    private interface PathComponentBuilder {
        PathComponent build();
    }

    private static class CompositePathComponentBuilder implements PathComponentBuilder {
        private final LinkedList<PathComponentBuilder> componentBuilders = new LinkedList();

        public final void addPath(String path) {
            if (StringUtils.hasText(path)) {
                FullPathComponentBuilder fpBuilder = (FullPathComponentBuilder) getLastBuilder(FullPathComponentBuilder.class);
                if (!(((PathSegmentComponentBuilder) getLastBuilder(PathSegmentComponentBuilder.class)) == null || path.startsWith("/"))) {
                    path = "/" + path;
                }
                if (fpBuilder == null) {
                    fpBuilder = new FullPathComponentBuilder();
                    this.componentBuilders.add(fpBuilder);
                }
                fpBuilder.append(path);
            }
        }

        private <T> T getLastBuilder(Class<T> builderClass) {
            if (!this.componentBuilders.isEmpty()) {
                PathComponentBuilder last = (PathComponentBuilder) this.componentBuilders.getLast();
                if (builderClass.isInstance(last)) {
                    return last;
                }
            }
            return null;
        }

        public final PathComponent build() {
            List<PathComponent> components = new ArrayList(this.componentBuilders.size());
            Iterator i$ = this.componentBuilders.iterator();
            while (i$.hasNext()) {
                PathComponent pathComponent = ((PathComponentBuilder) i$.next()).build();
                if (pathComponent != null) {
                    components.add(pathComponent);
                }
            }
            if (components.isEmpty()) {
                return HierarchicalUriComponents.NULL_PATH_COMPONENT;
            }
            if (components.size() == 1) {
                return (PathComponent) components.get(0);
            }
            return new PathComponentComposite(components);
        }
    }

    private static class FullPathComponentBuilder implements PathComponentBuilder {
        private final StringBuilder path;

        private FullPathComponentBuilder() {
            this.path = new StringBuilder();
        }

        public final void append(String path) {
            this.path.append(path);
        }

        public final PathComponent build() {
            if (this.path.length() == 0) {
                return null;
            }
            String path = this.path.toString();
            while (true) {
                int index = path.indexOf("//");
                if (index == -1) {
                    return new FullPathComponent(path);
                }
                path = path.substring(0, index) + path.substring(index + 1);
            }
        }
    }

    private static class PathSegmentComponentBuilder implements PathComponentBuilder {
        private final List<String> pathSegments = new LinkedList();

        private PathSegmentComponentBuilder() {
        }

        public final PathComponent build() {
            return this.pathSegments.isEmpty() ? null : new PathSegmentComponent(this.pathSegments);
        }
    }

    protected UriComponentsBuilder() {
    }

    public static UriComponentsBuilder fromUriString(String uri) {
        Assert.hasLength(uri, "'uri' must not be empty");
        Matcher matcher = URI_PATTERN.matcher(uri);
        if (matcher.matches()) {
            UriComponentsBuilder builder = new UriComponentsBuilder();
            String scheme = matcher.group(2);
            String userInfo = matcher.group(5);
            String host = matcher.group(6);
            String port = matcher.group(8);
            String path = matcher.group(9);
            String query = matcher.group(11);
            String fragment = matcher.group(13);
            boolean opaque = false;
            if (StringUtils.hasLength(scheme)) {
                if (!uri.substring(scheme.length()).startsWith(":/")) {
                    opaque = true;
                }
            }
            builder.scheme = scheme;
            if (opaque) {
                String ssp = uri.substring(scheme.length()).substring(1);
                if (StringUtils.hasLength(fragment)) {
                    ssp = ssp.substring(0, ssp.length() - (fragment.length() + 1));
                }
                builder.ssp = ssp;
                builder.userInfo = null;
                builder.host = null;
                builder.port = null;
                builder.pathBuilder = new CompositePathComponentBuilder();
                builder.queryParams.clear();
            } else {
                builder.userInfo = userInfo;
                builder.ssp = null;
                builder.host = host;
                builder.ssp = null;
                if (StringUtils.hasLength(port)) {
                    builder.port = port;
                    builder.ssp = null;
                }
                builder.pathBuilder.addPath(path);
                builder.ssp = null;
                if (query != null) {
                    Matcher matcher2 = QUERY_PARAM_PATTERN.matcher(query);
                    while (matcher2.find()) {
                        Object obj;
                        String group = matcher2.group(1);
                        CharSequence group2 = matcher2.group(2);
                        String group3 = matcher2.group(3);
                        String[] strArr = new Object[1];
                        if (group3 == null) {
                            group3 = StringUtils.hasLength(group2) ? "" : null;
                        }
                        strArr[0] = group3;
                        Assert.notNull(group, "'name' must not be null");
                        if (strArr == null || strArr.length == 0) {
                            obj = 1;
                        } else {
                            obj = null;
                        }
                        if (obj == null) {
                            for (int i = 0; i <= 0; i++) {
                                obj = strArr[0];
                                builder.queryParams.add(group, obj != null ? obj.toString() : null);
                            }
                        } else {
                            builder.queryParams.add(group, null);
                        }
                        builder.ssp = null;
                    }
                } else {
                    builder.queryParams.clear();
                }
                builder.ssp = null;
            }
            if (StringUtils.hasText(fragment)) {
                if (fragment != null) {
                    Assert.hasLength(fragment, "'fragment' must not be empty");
                    builder.fragment = fragment;
                } else {
                    builder.fragment = null;
                }
            }
            return builder;
        }
        throw new IllegalArgumentException("[" + uri + "] is not a valid URI");
    }

    public final UriComponents build() {
        if (this.ssp != null) {
            return new OpaqueUriComponents(this.scheme, this.ssp, this.fragment);
        }
        return new HierarchicalUriComponents(this.scheme, this.userInfo, this.host, this.port, this.pathBuilder.build(), this.queryParams, this.fragment, false, true);
    }
}
