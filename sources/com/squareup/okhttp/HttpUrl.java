package com.squareup.okhttp;

import com.shinobicontrols.charts.R;
import java.net.IDN;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import okio.Buffer;

public final class HttpUrl {
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private final String fragment;
    private final String host;
    private final String password;
    private final List<String> pathSegments;
    private final int port;
    private final List<String> queryNamesAndValues;
    private final String scheme;
    private final String url;
    private final String username;

    public static final class Builder {
        String encodedFragment;
        String encodedPassword = "";
        final List<String> encodedPathSegments = new ArrayList();
        List<String> encodedQueryNamesAndValues;
        String encodedUsername = "";
        String host;
        int port = -1;
        String scheme;

        enum ParseResult {
            ;

            static {
                SUCCESS$4627f6df = 1;
                MISSING_SCHEME$4627f6df = 2;
                UNSUPPORTED_SCHEME$4627f6df = 3;
                INVALID_PORT$4627f6df = 4;
                INVALID_HOST$4627f6df = 5;
                $VALUES$2d7603e4 = new int[]{1, 2, 3, 4, 5};
            }
        }

        public Builder() {
            this.encodedPathSegments.add("");
        }

        public final Builder host(String host) {
            if (host == null) {
                throw new IllegalArgumentException("host == null");
            }
            String encoded = canonicalizeHost(host, 0, host.length());
            if (encoded == null) {
                throw new IllegalArgumentException("unexpected host: " + host);
            }
            this.host = encoded;
            return this;
        }

        final int effectivePort() {
            return this.port != -1 ? this.port : HttpUrl.defaultPort(this.scheme);
        }

        public final Builder encodedQuery(String encodedQuery) {
            this.encodedQueryNamesAndValues = encodedQuery != null ? HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(encodedQuery, " \"'<>#", true, true, true)) : null;
            return this;
        }

        public final HttpUrl build() {
            if (this.scheme == null) {
                throw new IllegalStateException("scheme == null");
            } else if (this.host != null) {
                return new HttpUrl();
            } else {
                throw new IllegalStateException("host == null");
            }
        }

        public final String toString() {
            StringBuilder result = new StringBuilder();
            result.append(this.scheme);
            result.append("://");
            if (!(this.encodedUsername.isEmpty() && this.encodedPassword.isEmpty())) {
                result.append(this.encodedUsername);
                if (!this.encodedPassword.isEmpty()) {
                    result.append(':');
                    result.append(this.encodedPassword);
                }
                result.append('@');
            }
            if (this.host.indexOf(58) != -1) {
                result.append('[');
                result.append(this.host);
                result.append(']');
            } else {
                result.append(this.host);
            }
            int effectivePort = effectivePort();
            if (effectivePort != HttpUrl.defaultPort(this.scheme)) {
                result.append(':');
                result.append(effectivePort);
            }
            HttpUrl.pathSegmentsToString(result, this.encodedPathSegments);
            if (this.encodedQueryNamesAndValues != null) {
                result.append('?');
                HttpUrl.namesAndValuesToQueryString(result, this.encodedQueryNamesAndValues);
            }
            if (this.encodedFragment != null) {
                result.append('#');
                result.append(this.encodedFragment);
            }
            return result.toString();
        }

        final int parse$6547b47a(HttpUrl base, String input) {
            int limit;
            int schemeDelimiterOffset;
            boolean hasUsername;
            boolean hasPassword;
            int slashCount;
            int passwordColonOffset;
            String canonicalUsername;
            int length = input.length();
            int pos = 0;
            while (pos < length) {
                char charAt;
                switch (input.charAt(pos)) {
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                    case ' ':
                        pos++;
                    default:
                        break;
                }
                length = input.length() - 1;
                while (length >= pos) {
                    char charAt2;
                    int componentDelimiterOffset;
                    int portColonOffset;
                    int pathDelimiterOffset;
                    int queryDelimiterOffset;
                    switch (input.charAt(length)) {
                        case '\t':
                        case '\n':
                        case '\f':
                        case '\r':
                        case ' ':
                            length--;
                        default:
                            limit = length + 1;
                            break;
                    }
                    if (limit - pos >= 2) {
                        charAt2 = input.charAt(pos);
                        if ((charAt2 >= 'a' || charAt2 > 'z') && (charAt2 < 'A' || charAt2 > 'Z')) {
                            schemeDelimiterOffset = -1;
                            if (schemeDelimiterOffset != -1) {
                                if (input.regionMatches(true, pos, "https:", 0, 6)) {
                                    this.scheme = "https";
                                    pos += 6;
                                } else {
                                    if (!input.regionMatches(true, pos, "http:", 0, 5)) {
                                        return ParseResult.UNSUPPORTED_SCHEME$4627f6df;
                                    }
                                    this.scheme = "http";
                                    pos += 5;
                                }
                            } else if (base == null) {
                                return ParseResult.MISSING_SCHEME$4627f6df;
                            } else {
                                this.scheme = base.scheme;
                            }
                            hasUsername = false;
                            hasPassword = false;
                            slashCount = 0;
                            while (length < limit) {
                                charAt = input.charAt(length);
                                if (charAt == '\\' || charAt == '/') {
                                    slashCount++;
                                } else {
                                    if (slashCount < 2 || base == null || !base.scheme.equals(this.scheme)) {
                                        pos += slashCount;
                                        while (true) {
                                            componentDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "@/\\?#");
                                            switch (componentDelimiterOffset == limit ? input.charAt(componentDelimiterOffset) : -1) {
                                                case -1:
                                                case R.styleable.ChartTheme_sc_seriesAreaGradientColor1 /*35*/:
                                                case R.styleable.ChartTheme_sc_seriesAreaGradientColor4 /*47*/:
                                                case R.styleable.ChartTheme_sc_defaultCrustColor /*63*/:
                                                case 92:
                                                    portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
                                                    if (portColonOffset + 1 >= componentDelimiterOffset) {
                                                        this.host = canonicalizeHost(input, pos, portColonOffset);
                                                        this.port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                                                        if (this.port == -1) {
                                                            return ParseResult.INVALID_PORT$4627f6df;
                                                        }
                                                    }
                                                    this.host = canonicalizeHost(input, pos, portColonOffset);
                                                    this.port = HttpUrl.defaultPort(this.scheme);
                                                    if (this.host == null) {
                                                        pos = componentDelimiterOffset;
                                                        break;
                                                    }
                                                    return ParseResult.INVALID_HOST$4627f6df;
                                                case 64:
                                                    if (hasPassword) {
                                                        passwordColonOffset = HttpUrl.delimiterOffset(input, pos, componentDelimiterOffset, ":");
                                                        canonicalUsername = HttpUrl.canonicalize(input, pos, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                                        if (hasUsername) {
                                                            canonicalUsername = this.encodedUsername + "%40" + canonicalUsername;
                                                        }
                                                        this.encodedUsername = canonicalUsername;
                                                        if (passwordColonOffset != componentDelimiterOffset) {
                                                            hasPassword = true;
                                                            this.encodedPassword = HttpUrl.canonicalize(input, passwordColonOffset + 1, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                                        }
                                                        hasUsername = true;
                                                    } else {
                                                        this.encodedPassword += "%40" + HttpUrl.canonicalize(input, pos, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                                    }
                                                    pos = componentDelimiterOffset + 1;
                                                    continue;
                                                    continue;
                                                    continue;
                                                    continue;
                                                    continue;
                                                    continue;
                                                    continue;
                                                    continue;
                                                    continue;
                                                    continue;
                                                default:
                                                    continue;
                                                    continue;
                                                    continue;
                                                    continue;
                                                    continue;
                                                    continue;
                                                    continue;
                                                    continue;
                                                    continue;
                                                    continue;
                                            }
                                        }
                                    } else {
                                        this.encodedUsername = base.encodedUsername();
                                        this.encodedPassword = base.encodedPassword();
                                        this.host = base.host;
                                        this.port = base.port;
                                        this.encodedPathSegments.clear();
                                        this.encodedPathSegments.addAll(base.encodedPathSegments());
                                        if (pos == limit || input.charAt(pos) == '#') {
                                            encodedQuery(base.encodedQuery());
                                        }
                                    }
                                    pathDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "?#");
                                    resolvePath(input, pos, pathDelimiterOffset);
                                    pos = pathDelimiterOffset;
                                    if (pathDelimiterOffset < limit && input.charAt(pos) == '?') {
                                        queryDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "#");
                                        this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(input, pos + 1, queryDelimiterOffset, " \"'<>#", true, true, true));
                                        pos = queryDelimiterOffset;
                                    }
                                    if (pos < limit && input.charAt(pos) == '#') {
                                        this.encodedFragment = HttpUrl.canonicalize(input, pos + 1, limit, "", true, false, false);
                                    }
                                    return ParseResult.SUCCESS$4627f6df;
                                }
                            }
                            if (slashCount < 2) {
                            }
                            pos += slashCount;
                            while (true) {
                                componentDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "@/\\?#");
                                if (componentDelimiterOffset == limit) {
                                }
                                switch (componentDelimiterOffset == limit ? input.charAt(componentDelimiterOffset) : -1) {
                                    case -1:
                                    case R.styleable.ChartTheme_sc_seriesAreaGradientColor1 /*35*/:
                                    case R.styleable.ChartTheme_sc_seriesAreaGradientColor4 /*47*/:
                                    case R.styleable.ChartTheme_sc_defaultCrustColor /*63*/:
                                    case 92:
                                        portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
                                        if (portColonOffset + 1 >= componentDelimiterOffset) {
                                            this.host = canonicalizeHost(input, pos, portColonOffset);
                                            this.port = HttpUrl.defaultPort(this.scheme);
                                        } else {
                                            this.host = canonicalizeHost(input, pos, portColonOffset);
                                            this.port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                                            if (this.port == -1) {
                                                return ParseResult.INVALID_PORT$4627f6df;
                                            }
                                        }
                                        if (this.host == null) {
                                            pos = componentDelimiterOffset;
                                            break;
                                        }
                                        return ParseResult.INVALID_HOST$4627f6df;
                                    case 64:
                                        if (hasPassword) {
                                            this.encodedPassword += "%40" + HttpUrl.canonicalize(input, pos, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                        } else {
                                            passwordColonOffset = HttpUrl.delimiterOffset(input, pos, componentDelimiterOffset, ":");
                                            canonicalUsername = HttpUrl.canonicalize(input, pos, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                            if (hasUsername) {
                                                canonicalUsername = this.encodedUsername + "%40" + canonicalUsername;
                                            }
                                            this.encodedUsername = canonicalUsername;
                                            if (passwordColonOffset != componentDelimiterOffset) {
                                                hasPassword = true;
                                                this.encodedPassword = HttpUrl.canonicalize(input, passwordColonOffset + 1, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                            }
                                            hasUsername = true;
                                        }
                                        pos = componentDelimiterOffset + 1;
                                        continue;
                                        continue;
                                        continue;
                                        continue;
                                        continue;
                                        continue;
                                        continue;
                                        continue;
                                        continue;
                                        continue;
                                    default:
                                        continue;
                                        continue;
                                        continue;
                                        continue;
                                        continue;
                                        continue;
                                        continue;
                                        continue;
                                        continue;
                                        continue;
                                }
                                pathDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "?#");
                                resolvePath(input, pos, pathDelimiterOffset);
                                pos = pathDelimiterOffset;
                                queryDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "#");
                                this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(input, pos + 1, queryDelimiterOffset, " \"'<>#", true, true, true));
                                pos = queryDelimiterOffset;
                                this.encodedFragment = HttpUrl.canonicalize(input, pos + 1, limit, "", true, false, false);
                                return ParseResult.SUCCESS$4627f6df;
                            }
                        }
                        schemeDelimiterOffset = pos + 1;
                        while (schemeDelimiterOffset < limit) {
                            charAt2 = input.charAt(schemeDelimiterOffset);
                            if ((charAt2 < 'a' || charAt2 > 'z') && ((charAt2 < 'A' || charAt2 > 'Z') && !((charAt2 >= '0' && charAt2 <= '9') || charAt2 == '+' || charAt2 == '-' || charAt2 == '.'))) {
                                if (charAt2 != ':') {
                                    schemeDelimiterOffset = -1;
                                }
                                if (schemeDelimiterOffset != -1) {
                                    if (input.regionMatches(true, pos, "https:", 0, 6)) {
                                        if (!input.regionMatches(true, pos, "http:", 0, 5)) {
                                            return ParseResult.UNSUPPORTED_SCHEME$4627f6df;
                                        }
                                        this.scheme = "http";
                                        pos += 5;
                                    } else {
                                        this.scheme = "https";
                                        pos += 6;
                                    }
                                } else if (base == null) {
                                    return ParseResult.MISSING_SCHEME$4627f6df;
                                } else {
                                    this.scheme = base.scheme;
                                }
                                hasUsername = false;
                                hasPassword = false;
                                slashCount = 0;
                                for (length = pos; length < limit; length++) {
                                    charAt = input.charAt(length);
                                    if (charAt == '\\') {
                                    }
                                    slashCount++;
                                }
                                if (slashCount < 2) {
                                }
                                pos += slashCount;
                                while (true) {
                                    componentDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "@/\\?#");
                                    if (componentDelimiterOffset == limit) {
                                    }
                                    switch (componentDelimiterOffset == limit ? input.charAt(componentDelimiterOffset) : -1) {
                                        case -1:
                                        case R.styleable.ChartTheme_sc_seriesAreaGradientColor1 /*35*/:
                                        case R.styleable.ChartTheme_sc_seriesAreaGradientColor4 /*47*/:
                                        case R.styleable.ChartTheme_sc_defaultCrustColor /*63*/:
                                        case 92:
                                            portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
                                            if (portColonOffset + 1 >= componentDelimiterOffset) {
                                                this.host = canonicalizeHost(input, pos, portColonOffset);
                                                this.port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                                                if (this.port == -1) {
                                                    return ParseResult.INVALID_PORT$4627f6df;
                                                }
                                            }
                                            this.host = canonicalizeHost(input, pos, portColonOffset);
                                            this.port = HttpUrl.defaultPort(this.scheme);
                                            if (this.host == null) {
                                                pos = componentDelimiterOffset;
                                                break;
                                            }
                                            return ParseResult.INVALID_HOST$4627f6df;
                                        case 64:
                                            if (hasPassword) {
                                                passwordColonOffset = HttpUrl.delimiterOffset(input, pos, componentDelimiterOffset, ":");
                                                canonicalUsername = HttpUrl.canonicalize(input, pos, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                                if (hasUsername) {
                                                    canonicalUsername = this.encodedUsername + "%40" + canonicalUsername;
                                                }
                                                this.encodedUsername = canonicalUsername;
                                                if (passwordColonOffset != componentDelimiterOffset) {
                                                    hasPassword = true;
                                                    this.encodedPassword = HttpUrl.canonicalize(input, passwordColonOffset + 1, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                                }
                                                hasUsername = true;
                                            } else {
                                                this.encodedPassword += "%40" + HttpUrl.canonicalize(input, pos, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                            }
                                            pos = componentDelimiterOffset + 1;
                                            continue;
                                            continue;
                                            continue;
                                            continue;
                                            continue;
                                            continue;
                                            continue;
                                            continue;
                                            continue;
                                            continue;
                                        default:
                                            continue;
                                            continue;
                                            continue;
                                            continue;
                                            continue;
                                            continue;
                                            continue;
                                            continue;
                                            continue;
                                            continue;
                                    }
                                    pathDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "?#");
                                    resolvePath(input, pos, pathDelimiterOffset);
                                    pos = pathDelimiterOffset;
                                    queryDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "#");
                                    this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(input, pos + 1, queryDelimiterOffset, " \"'<>#", true, true, true));
                                    pos = queryDelimiterOffset;
                                    this.encodedFragment = HttpUrl.canonicalize(input, pos + 1, limit, "", true, false, false);
                                    return ParseResult.SUCCESS$4627f6df;
                                }
                            }
                            schemeDelimiterOffset++;
                        }
                    }
                    schemeDelimiterOffset = -1;
                    if (schemeDelimiterOffset != -1) {
                        if (input.regionMatches(true, pos, "https:", 0, 6)) {
                            this.scheme = "https";
                            pos += 6;
                        } else {
                            if (!input.regionMatches(true, pos, "http:", 0, 5)) {
                                return ParseResult.UNSUPPORTED_SCHEME$4627f6df;
                            }
                            this.scheme = "http";
                            pos += 5;
                        }
                    } else if (base == null) {
                        return ParseResult.MISSING_SCHEME$4627f6df;
                    } else {
                        this.scheme = base.scheme;
                    }
                    hasUsername = false;
                    hasPassword = false;
                    slashCount = 0;
                    for (length = pos; length < limit; length++) {
                        charAt = input.charAt(length);
                        if (charAt == '\\') {
                        }
                        slashCount++;
                    }
                    if (slashCount < 2) {
                    }
                    pos += slashCount;
                    while (true) {
                        componentDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "@/\\?#");
                        if (componentDelimiterOffset == limit) {
                        }
                        switch (componentDelimiterOffset == limit ? input.charAt(componentDelimiterOffset) : -1) {
                            case -1:
                            case R.styleable.ChartTheme_sc_seriesAreaGradientColor1 /*35*/:
                            case R.styleable.ChartTheme_sc_seriesAreaGradientColor4 /*47*/:
                            case R.styleable.ChartTheme_sc_defaultCrustColor /*63*/:
                            case 92:
                                portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
                                if (portColonOffset + 1 >= componentDelimiterOffset) {
                                    this.host = canonicalizeHost(input, pos, portColonOffset);
                                    this.port = HttpUrl.defaultPort(this.scheme);
                                } else {
                                    this.host = canonicalizeHost(input, pos, portColonOffset);
                                    this.port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                                    if (this.port == -1) {
                                        return ParseResult.INVALID_PORT$4627f6df;
                                    }
                                }
                                if (this.host == null) {
                                    pos = componentDelimiterOffset;
                                    break;
                                }
                                return ParseResult.INVALID_HOST$4627f6df;
                            case 64:
                                if (hasPassword) {
                                    this.encodedPassword += "%40" + HttpUrl.canonicalize(input, pos, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                } else {
                                    passwordColonOffset = HttpUrl.delimiterOffset(input, pos, componentDelimiterOffset, ":");
                                    canonicalUsername = HttpUrl.canonicalize(input, pos, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                    if (hasUsername) {
                                        canonicalUsername = this.encodedUsername + "%40" + canonicalUsername;
                                    }
                                    this.encodedUsername = canonicalUsername;
                                    if (passwordColonOffset != componentDelimiterOffset) {
                                        hasPassword = true;
                                        this.encodedPassword = HttpUrl.canonicalize(input, passwordColonOffset + 1, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                    }
                                    hasUsername = true;
                                }
                                pos = componentDelimiterOffset + 1;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                            default:
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                        }
                        pathDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "?#");
                        resolvePath(input, pos, pathDelimiterOffset);
                        pos = pathDelimiterOffset;
                        queryDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "#");
                        this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(input, pos + 1, queryDelimiterOffset, " \"'<>#", true, true, true));
                        pos = queryDelimiterOffset;
                        this.encodedFragment = HttpUrl.canonicalize(input, pos + 1, limit, "", true, false, false);
                        return ParseResult.SUCCESS$4627f6df;
                    }
                }
                limit = pos;
                if (limit - pos >= 2) {
                    charAt2 = input.charAt(pos);
                    if (charAt2 >= 'a') {
                    }
                    schemeDelimiterOffset = -1;
                    if (schemeDelimiterOffset != -1) {
                        if (input.regionMatches(true, pos, "https:", 0, 6)) {
                            if (!input.regionMatches(true, pos, "http:", 0, 5)) {
                                return ParseResult.UNSUPPORTED_SCHEME$4627f6df;
                            }
                            this.scheme = "http";
                            pos += 5;
                        } else {
                            this.scheme = "https";
                            pos += 6;
                        }
                    } else if (base == null) {
                        return ParseResult.MISSING_SCHEME$4627f6df;
                    } else {
                        this.scheme = base.scheme;
                    }
                    hasUsername = false;
                    hasPassword = false;
                    slashCount = 0;
                    for (length = pos; length < limit; length++) {
                        charAt = input.charAt(length);
                        if (charAt == '\\') {
                        }
                        slashCount++;
                    }
                    if (slashCount < 2) {
                    }
                    pos += slashCount;
                    while (true) {
                        componentDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "@/\\?#");
                        if (componentDelimiterOffset == limit) {
                        }
                        switch (componentDelimiterOffset == limit ? input.charAt(componentDelimiterOffset) : -1) {
                            case -1:
                            case R.styleable.ChartTheme_sc_seriesAreaGradientColor1 /*35*/:
                            case R.styleable.ChartTheme_sc_seriesAreaGradientColor4 /*47*/:
                            case R.styleable.ChartTheme_sc_defaultCrustColor /*63*/:
                            case 92:
                                portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
                                if (portColonOffset + 1 >= componentDelimiterOffset) {
                                    this.host = canonicalizeHost(input, pos, portColonOffset);
                                    this.port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                                    if (this.port == -1) {
                                        return ParseResult.INVALID_PORT$4627f6df;
                                    }
                                }
                                this.host = canonicalizeHost(input, pos, portColonOffset);
                                this.port = HttpUrl.defaultPort(this.scheme);
                                if (this.host == null) {
                                    pos = componentDelimiterOffset;
                                    break;
                                }
                                return ParseResult.INVALID_HOST$4627f6df;
                            case 64:
                                if (hasPassword) {
                                    passwordColonOffset = HttpUrl.delimiterOffset(input, pos, componentDelimiterOffset, ":");
                                    canonicalUsername = HttpUrl.canonicalize(input, pos, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                    if (hasUsername) {
                                        canonicalUsername = this.encodedUsername + "%40" + canonicalUsername;
                                    }
                                    this.encodedUsername = canonicalUsername;
                                    if (passwordColonOffset != componentDelimiterOffset) {
                                        hasPassword = true;
                                        this.encodedPassword = HttpUrl.canonicalize(input, passwordColonOffset + 1, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                    }
                                    hasUsername = true;
                                } else {
                                    this.encodedPassword += "%40" + HttpUrl.canonicalize(input, pos, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                }
                                pos = componentDelimiterOffset + 1;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                            default:
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                        }
                        pathDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "?#");
                        resolvePath(input, pos, pathDelimiterOffset);
                        pos = pathDelimiterOffset;
                        queryDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "#");
                        this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(input, pos + 1, queryDelimiterOffset, " \"'<>#", true, true, true));
                        pos = queryDelimiterOffset;
                        this.encodedFragment = HttpUrl.canonicalize(input, pos + 1, limit, "", true, false, false);
                        return ParseResult.SUCCESS$4627f6df;
                    }
                }
                schemeDelimiterOffset = -1;
                if (schemeDelimiterOffset != -1) {
                    if (input.regionMatches(true, pos, "https:", 0, 6)) {
                        this.scheme = "https";
                        pos += 6;
                    } else {
                        if (!input.regionMatches(true, pos, "http:", 0, 5)) {
                            return ParseResult.UNSUPPORTED_SCHEME$4627f6df;
                        }
                        this.scheme = "http";
                        pos += 5;
                    }
                } else if (base == null) {
                    return ParseResult.MISSING_SCHEME$4627f6df;
                } else {
                    this.scheme = base.scheme;
                }
                hasUsername = false;
                hasPassword = false;
                slashCount = 0;
                for (length = pos; length < limit; length++) {
                    charAt = input.charAt(length);
                    if (charAt == '\\') {
                    }
                    slashCount++;
                }
                if (slashCount < 2) {
                }
                pos += slashCount;
                while (true) {
                    componentDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "@/\\?#");
                    if (componentDelimiterOffset == limit) {
                    }
                    switch (componentDelimiterOffset == limit ? input.charAt(componentDelimiterOffset) : -1) {
                        case -1:
                        case R.styleable.ChartTheme_sc_seriesAreaGradientColor1 /*35*/:
                        case R.styleable.ChartTheme_sc_seriesAreaGradientColor4 /*47*/:
                        case R.styleable.ChartTheme_sc_defaultCrustColor /*63*/:
                        case 92:
                            portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
                            if (portColonOffset + 1 >= componentDelimiterOffset) {
                                this.host = canonicalizeHost(input, pos, portColonOffset);
                                this.port = HttpUrl.defaultPort(this.scheme);
                            } else {
                                this.host = canonicalizeHost(input, pos, portColonOffset);
                                this.port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                                if (this.port == -1) {
                                    return ParseResult.INVALID_PORT$4627f6df;
                                }
                            }
                            if (this.host == null) {
                                pos = componentDelimiterOffset;
                                break;
                            }
                            return ParseResult.INVALID_HOST$4627f6df;
                        case 64:
                            if (hasPassword) {
                                this.encodedPassword += "%40" + HttpUrl.canonicalize(input, pos, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                            } else {
                                passwordColonOffset = HttpUrl.delimiterOffset(input, pos, componentDelimiterOffset, ":");
                                canonicalUsername = HttpUrl.canonicalize(input, pos, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                if (hasUsername) {
                                    canonicalUsername = this.encodedUsername + "%40" + canonicalUsername;
                                }
                                this.encodedUsername = canonicalUsername;
                                if (passwordColonOffset != componentDelimiterOffset) {
                                    hasPassword = true;
                                    this.encodedPassword = HttpUrl.canonicalize(input, passwordColonOffset + 1, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                }
                                hasUsername = true;
                            }
                            pos = componentDelimiterOffset + 1;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                        default:
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                    }
                    pathDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "?#");
                    resolvePath(input, pos, pathDelimiterOffset);
                    pos = pathDelimiterOffset;
                    queryDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "#");
                    this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(input, pos + 1, queryDelimiterOffset, " \"'<>#", true, true, true));
                    pos = queryDelimiterOffset;
                    this.encodedFragment = HttpUrl.canonicalize(input, pos + 1, limit, "", true, false, false);
                    return ParseResult.SUCCESS$4627f6df;
                }
            }
            pos = length;
            length = input.length() - 1;
            while (length >= pos) {
                switch (input.charAt(length)) {
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                    case ' ':
                        length--;
                    default:
                        limit = length + 1;
                        break;
                }
                if (limit - pos >= 2) {
                    charAt2 = input.charAt(pos);
                    if (charAt2 >= 'a') {
                    }
                    schemeDelimiterOffset = -1;
                    if (schemeDelimiterOffset != -1) {
                        if (input.regionMatches(true, pos, "https:", 0, 6)) {
                            if (!input.regionMatches(true, pos, "http:", 0, 5)) {
                                return ParseResult.UNSUPPORTED_SCHEME$4627f6df;
                            }
                            this.scheme = "http";
                            pos += 5;
                        } else {
                            this.scheme = "https";
                            pos += 6;
                        }
                    } else if (base == null) {
                        return ParseResult.MISSING_SCHEME$4627f6df;
                    } else {
                        this.scheme = base.scheme;
                    }
                    hasUsername = false;
                    hasPassword = false;
                    slashCount = 0;
                    for (length = pos; length < limit; length++) {
                        charAt = input.charAt(length);
                        if (charAt == '\\') {
                        }
                        slashCount++;
                    }
                    if (slashCount < 2) {
                    }
                    pos += slashCount;
                    while (true) {
                        componentDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "@/\\?#");
                        if (componentDelimiterOffset == limit) {
                        }
                        switch (componentDelimiterOffset == limit ? input.charAt(componentDelimiterOffset) : -1) {
                            case -1:
                            case R.styleable.ChartTheme_sc_seriesAreaGradientColor1 /*35*/:
                            case R.styleable.ChartTheme_sc_seriesAreaGradientColor4 /*47*/:
                            case R.styleable.ChartTheme_sc_defaultCrustColor /*63*/:
                            case 92:
                                portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
                                if (portColonOffset + 1 >= componentDelimiterOffset) {
                                    this.host = canonicalizeHost(input, pos, portColonOffset);
                                    this.port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                                    if (this.port == -1) {
                                        return ParseResult.INVALID_PORT$4627f6df;
                                    }
                                }
                                this.host = canonicalizeHost(input, pos, portColonOffset);
                                this.port = HttpUrl.defaultPort(this.scheme);
                                if (this.host == null) {
                                    pos = componentDelimiterOffset;
                                    break;
                                }
                                return ParseResult.INVALID_HOST$4627f6df;
                            case 64:
                                if (hasPassword) {
                                    passwordColonOffset = HttpUrl.delimiterOffset(input, pos, componentDelimiterOffset, ":");
                                    canonicalUsername = HttpUrl.canonicalize(input, pos, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                    if (hasUsername) {
                                        canonicalUsername = this.encodedUsername + "%40" + canonicalUsername;
                                    }
                                    this.encodedUsername = canonicalUsername;
                                    if (passwordColonOffset != componentDelimiterOffset) {
                                        hasPassword = true;
                                        this.encodedPassword = HttpUrl.canonicalize(input, passwordColonOffset + 1, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                    }
                                    hasUsername = true;
                                } else {
                                    this.encodedPassword += "%40" + HttpUrl.canonicalize(input, pos, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                }
                                pos = componentDelimiterOffset + 1;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                            default:
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                                continue;
                        }
                        pathDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "?#");
                        resolvePath(input, pos, pathDelimiterOffset);
                        pos = pathDelimiterOffset;
                        queryDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "#");
                        this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(input, pos + 1, queryDelimiterOffset, " \"'<>#", true, true, true));
                        pos = queryDelimiterOffset;
                        this.encodedFragment = HttpUrl.canonicalize(input, pos + 1, limit, "", true, false, false);
                        return ParseResult.SUCCESS$4627f6df;
                    }
                }
                schemeDelimiterOffset = -1;
                if (schemeDelimiterOffset != -1) {
                    if (input.regionMatches(true, pos, "https:", 0, 6)) {
                        this.scheme = "https";
                        pos += 6;
                    } else {
                        if (!input.regionMatches(true, pos, "http:", 0, 5)) {
                            return ParseResult.UNSUPPORTED_SCHEME$4627f6df;
                        }
                        this.scheme = "http";
                        pos += 5;
                    }
                } else if (base == null) {
                    return ParseResult.MISSING_SCHEME$4627f6df;
                } else {
                    this.scheme = base.scheme;
                }
                hasUsername = false;
                hasPassword = false;
                slashCount = 0;
                for (length = pos; length < limit; length++) {
                    charAt = input.charAt(length);
                    if (charAt == '\\') {
                    }
                    slashCount++;
                }
                if (slashCount < 2) {
                }
                pos += slashCount;
                while (true) {
                    componentDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "@/\\?#");
                    if (componentDelimiterOffset == limit) {
                    }
                    switch (componentDelimiterOffset == limit ? input.charAt(componentDelimiterOffset) : -1) {
                        case -1:
                        case R.styleable.ChartTheme_sc_seriesAreaGradientColor1 /*35*/:
                        case R.styleable.ChartTheme_sc_seriesAreaGradientColor4 /*47*/:
                        case R.styleable.ChartTheme_sc_defaultCrustColor /*63*/:
                        case 92:
                            portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
                            if (portColonOffset + 1 >= componentDelimiterOffset) {
                                this.host = canonicalizeHost(input, pos, portColonOffset);
                                this.port = HttpUrl.defaultPort(this.scheme);
                            } else {
                                this.host = canonicalizeHost(input, pos, portColonOffset);
                                this.port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                                if (this.port == -1) {
                                    return ParseResult.INVALID_PORT$4627f6df;
                                }
                            }
                            if (this.host == null) {
                                pos = componentDelimiterOffset;
                                break;
                            }
                            return ParseResult.INVALID_HOST$4627f6df;
                        case 64:
                            if (hasPassword) {
                                this.encodedPassword += "%40" + HttpUrl.canonicalize(input, pos, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                            } else {
                                passwordColonOffset = HttpUrl.delimiterOffset(input, pos, componentDelimiterOffset, ":");
                                canonicalUsername = HttpUrl.canonicalize(input, pos, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                if (hasUsername) {
                                    canonicalUsername = this.encodedUsername + "%40" + canonicalUsername;
                                }
                                this.encodedUsername = canonicalUsername;
                                if (passwordColonOffset != componentDelimiterOffset) {
                                    hasPassword = true;
                                    this.encodedPassword = HttpUrl.canonicalize(input, passwordColonOffset + 1, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                }
                                hasUsername = true;
                            }
                            pos = componentDelimiterOffset + 1;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                        default:
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                    }
                    pathDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "?#");
                    resolvePath(input, pos, pathDelimiterOffset);
                    pos = pathDelimiterOffset;
                    queryDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "#");
                    this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(input, pos + 1, queryDelimiterOffset, " \"'<>#", true, true, true));
                    pos = queryDelimiterOffset;
                    this.encodedFragment = HttpUrl.canonicalize(input, pos + 1, limit, "", true, false, false);
                    return ParseResult.SUCCESS$4627f6df;
                }
            }
            limit = pos;
            if (limit - pos >= 2) {
                charAt2 = input.charAt(pos);
                if (charAt2 >= 'a') {
                }
                schemeDelimiterOffset = -1;
                if (schemeDelimiterOffset != -1) {
                    if (input.regionMatches(true, pos, "https:", 0, 6)) {
                        if (!input.regionMatches(true, pos, "http:", 0, 5)) {
                            return ParseResult.UNSUPPORTED_SCHEME$4627f6df;
                        }
                        this.scheme = "http";
                        pos += 5;
                    } else {
                        this.scheme = "https";
                        pos += 6;
                    }
                } else if (base == null) {
                    return ParseResult.MISSING_SCHEME$4627f6df;
                } else {
                    this.scheme = base.scheme;
                }
                hasUsername = false;
                hasPassword = false;
                slashCount = 0;
                for (length = pos; length < limit; length++) {
                    charAt = input.charAt(length);
                    if (charAt == '\\') {
                    }
                    slashCount++;
                }
                if (slashCount < 2) {
                }
                pos += slashCount;
                while (true) {
                    componentDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "@/\\?#");
                    if (componentDelimiterOffset == limit) {
                    }
                    switch (componentDelimiterOffset == limit ? input.charAt(componentDelimiterOffset) : -1) {
                        case -1:
                        case R.styleable.ChartTheme_sc_seriesAreaGradientColor1 /*35*/:
                        case R.styleable.ChartTheme_sc_seriesAreaGradientColor4 /*47*/:
                        case R.styleable.ChartTheme_sc_defaultCrustColor /*63*/:
                        case 92:
                            portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
                            if (portColonOffset + 1 >= componentDelimiterOffset) {
                                this.host = canonicalizeHost(input, pos, portColonOffset);
                                this.port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                                if (this.port == -1) {
                                    return ParseResult.INVALID_PORT$4627f6df;
                                }
                            }
                            this.host = canonicalizeHost(input, pos, portColonOffset);
                            this.port = HttpUrl.defaultPort(this.scheme);
                            if (this.host == null) {
                                pos = componentDelimiterOffset;
                                break;
                            }
                            return ParseResult.INVALID_HOST$4627f6df;
                        case 64:
                            if (hasPassword) {
                                passwordColonOffset = HttpUrl.delimiterOffset(input, pos, componentDelimiterOffset, ":");
                                canonicalUsername = HttpUrl.canonicalize(input, pos, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                if (hasUsername) {
                                    canonicalUsername = this.encodedUsername + "%40" + canonicalUsername;
                                }
                                this.encodedUsername = canonicalUsername;
                                if (passwordColonOffset != componentDelimiterOffset) {
                                    hasPassword = true;
                                    this.encodedPassword = HttpUrl.canonicalize(input, passwordColonOffset + 1, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                                }
                                hasUsername = true;
                            } else {
                                this.encodedPassword += "%40" + HttpUrl.canonicalize(input, pos, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                            }
                            pos = componentDelimiterOffset + 1;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                        default:
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                            continue;
                    }
                    pathDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "?#");
                    resolvePath(input, pos, pathDelimiterOffset);
                    pos = pathDelimiterOffset;
                    queryDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "#");
                    this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(input, pos + 1, queryDelimiterOffset, " \"'<>#", true, true, true));
                    pos = queryDelimiterOffset;
                    this.encodedFragment = HttpUrl.canonicalize(input, pos + 1, limit, "", true, false, false);
                    return ParseResult.SUCCESS$4627f6df;
                }
            }
            schemeDelimiterOffset = -1;
            if (schemeDelimiterOffset != -1) {
                if (input.regionMatches(true, pos, "https:", 0, 6)) {
                    this.scheme = "https";
                    pos += 6;
                } else {
                    if (!input.regionMatches(true, pos, "http:", 0, 5)) {
                        return ParseResult.UNSUPPORTED_SCHEME$4627f6df;
                    }
                    this.scheme = "http";
                    pos += 5;
                }
            } else if (base == null) {
                return ParseResult.MISSING_SCHEME$4627f6df;
            } else {
                this.scheme = base.scheme;
            }
            hasUsername = false;
            hasPassword = false;
            slashCount = 0;
            for (length = pos; length < limit; length++) {
                charAt = input.charAt(length);
                if (charAt == '\\') {
                }
                slashCount++;
            }
            if (slashCount < 2) {
            }
            pos += slashCount;
            while (true) {
                componentDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "@/\\?#");
                if (componentDelimiterOffset == limit) {
                }
                switch (componentDelimiterOffset == limit ? input.charAt(componentDelimiterOffset) : -1) {
                    case -1:
                    case R.styleable.ChartTheme_sc_seriesAreaGradientColor1 /*35*/:
                    case R.styleable.ChartTheme_sc_seriesAreaGradientColor4 /*47*/:
                    case R.styleable.ChartTheme_sc_defaultCrustColor /*63*/:
                    case 92:
                        portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
                        if (portColonOffset + 1 >= componentDelimiterOffset) {
                            this.host = canonicalizeHost(input, pos, portColonOffset);
                            this.port = HttpUrl.defaultPort(this.scheme);
                        } else {
                            this.host = canonicalizeHost(input, pos, portColonOffset);
                            this.port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                            if (this.port == -1) {
                                return ParseResult.INVALID_PORT$4627f6df;
                            }
                        }
                        if (this.host == null) {
                            pos = componentDelimiterOffset;
                            break;
                        }
                        return ParseResult.INVALID_HOST$4627f6df;
                    case 64:
                        if (hasPassword) {
                            this.encodedPassword += "%40" + HttpUrl.canonicalize(input, pos, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                        } else {
                            passwordColonOffset = HttpUrl.delimiterOffset(input, pos, componentDelimiterOffset, ":");
                            canonicalUsername = HttpUrl.canonicalize(input, pos, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                            if (hasUsername) {
                                canonicalUsername = this.encodedUsername + "%40" + canonicalUsername;
                            }
                            this.encodedUsername = canonicalUsername;
                            if (passwordColonOffset != componentDelimiterOffset) {
                                hasPassword = true;
                                this.encodedPassword = HttpUrl.canonicalize(input, passwordColonOffset + 1, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, true);
                            }
                            hasUsername = true;
                        }
                        pos = componentDelimiterOffset + 1;
                        continue;
                        continue;
                        continue;
                        continue;
                        continue;
                        continue;
                        continue;
                        continue;
                        continue;
                        continue;
                    default:
                        continue;
                        continue;
                        continue;
                        continue;
                        continue;
                        continue;
                        continue;
                        continue;
                        continue;
                        continue;
                }
                pathDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "?#");
                resolvePath(input, pos, pathDelimiterOffset);
                pos = pathDelimiterOffset;
                queryDelimiterOffset = HttpUrl.delimiterOffset(input, pos, limit, "#");
                this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(input, pos + 1, queryDelimiterOffset, " \"'<>#", true, true, true));
                pos = queryDelimiterOffset;
                this.encodedFragment = HttpUrl.canonicalize(input, pos + 1, limit, "", true, false, false);
                return ParseResult.SUCCESS$4627f6df;
            }
        }

        private void resolvePath(String input, int pos, int limit) {
            if (pos != limit) {
                char c = input.charAt(pos);
                if (c == '/' || c == '\\') {
                    this.encodedPathSegments.clear();
                    this.encodedPathSegments.add("");
                    pos++;
                } else {
                    this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, "");
                }
                int i = pos;
                while (i < limit) {
                    boolean segmentHasTrailingSlash;
                    boolean z;
                    int pathSegmentDelimiterOffset = HttpUrl.delimiterOffset(input, i, limit, "/\\");
                    if (pathSegmentDelimiterOffset < limit) {
                        segmentHasTrailingSlash = true;
                    } else {
                        segmentHasTrailingSlash = false;
                    }
                    String canonicalize = HttpUrl.canonicalize(input, i, pathSegmentDelimiterOffset, " \"<>^`{}|/\\?#", true, false, true);
                    if (canonicalize.equals(".") || canonicalize.equalsIgnoreCase("%2e")) {
                        z = true;
                    } else {
                        z = false;
                    }
                    if (!z) {
                        if (canonicalize.equals("..") || canonicalize.equalsIgnoreCase("%2e.") || canonicalize.equalsIgnoreCase(".%2e") || canonicalize.equalsIgnoreCase("%2e%2e")) {
                            z = true;
                        } else {
                            z = false;
                        }
                        if (!z) {
                            if (((String) this.encodedPathSegments.get(this.encodedPathSegments.size() - 1)).isEmpty()) {
                                this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, canonicalize);
                            } else {
                                this.encodedPathSegments.add(canonicalize);
                            }
                            if (segmentHasTrailingSlash) {
                                this.encodedPathSegments.add("");
                            }
                        } else if (!((String) this.encodedPathSegments.remove(this.encodedPathSegments.size() - 1)).isEmpty() || this.encodedPathSegments.isEmpty()) {
                            this.encodedPathSegments.add("");
                        } else {
                            this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, "");
                        }
                    }
                    i = pathSegmentDelimiterOffset;
                    if (segmentHasTrailingSlash) {
                        i++;
                    }
                }
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static int portColonOffset(java.lang.String r3, int r4, int r5) {
            /*
            r0 = r4;
        L_0x0001:
            if (r0 >= r5) goto L_0x001a;
        L_0x0003:
            r1 = r3.charAt(r0);
            switch(r1) {
                case 58: goto L_0x001b;
                case 91: goto L_0x000d;
                default: goto L_0x000a;
            };
        L_0x000a:
            r0 = r0 + 1;
            goto L_0x0001;
        L_0x000d:
            r0 = r0 + 1;
            if (r0 >= r5) goto L_0x000a;
        L_0x0011:
            r1 = r3.charAt(r0);
            r2 = 93;
            if (r1 != r2) goto L_0x000d;
        L_0x0019:
            goto L_0x000a;
        L_0x001a:
            r0 = r5;
        L_0x001b:
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.squareup.okhttp.HttpUrl.Builder.portColonOffset(java.lang.String, int, int):int");
        }

        private static String canonicalizeHost(String input, int pos, int limit) {
            int i = 0;
            String percentDecoded = HttpUrl.percentDecode(input, pos, limit, false);
            if (!percentDecoded.startsWith("[") || !percentDecoded.endsWith("]")) {
                return domainToAscii(percentDecoded);
            }
            InetAddress inetAddress = decodeIpv6(percentDecoded, 1, percentDecoded.length() - 1);
            if (inetAddress == null) {
                return null;
            }
            byte[] address = inetAddress.getAddress();
            if (address.length == 16) {
                int i2 = 0;
                int i3 = -1;
                int i4 = 0;
                while (i4 < address.length) {
                    int i5 = i4;
                    while (i5 < 16 && address[i5] == (byte) 0 && address[i5 + 1] == (byte) 0) {
                        i5 += 2;
                    }
                    int i6 = i5 - i4;
                    if (i6 > i2) {
                        i2 = i6;
                        i3 = i4;
                    }
                    i4 = i5 + 2;
                }
                Buffer buffer = new Buffer();
                while (i < address.length) {
                    if (i == i3) {
                        buffer.writeByte(58);
                        i += i2;
                        if (i == 16) {
                            buffer.writeByte(58);
                        }
                    } else {
                        if (i > 0) {
                            buffer.writeByte(58);
                        }
                        buffer.writeHexadecimalUnsignedLong((long) (((address[i] & 255) << 8) | (address[i + 1] & 255)));
                        i += 2;
                    }
                }
                return buffer.readUtf8();
            }
            throw new AssertionError();
        }

        private static InetAddress decodeIpv6(String input, int pos, int limit) {
            byte[] address = new byte[16];
            int b = 0;
            int compress = -1;
            int groupOffset = -1;
            int i = 1;
            while (i < limit) {
                if (b == 16) {
                    return null;
                }
                int value;
                int hexDigit;
                int groupLength;
                if (i + 2 <= limit) {
                    if (input.regionMatches(i, "::", 0, 2)) {
                        if (compress != -1) {
                            return null;
                        }
                        i += 2;
                        b += 2;
                        compress = b;
                        if (i == limit) {
                            break;
                        }
                        value = 0;
                        groupOffset = i;
                        while (i < limit) {
                            hexDigit = HttpUrl.decodeHexDigit(input.charAt(i));
                            if (hexDigit != -1) {
                                break;
                            }
                            value = (value << 4) + hexDigit;
                            i++;
                        }
                        groupLength = i - groupOffset;
                        if (groupLength != 0 || groupLength > 4) {
                            return null;
                        }
                        int i2 = b + 1;
                        address[b] = (byte) (value >>> 8);
                        b = i2 + 1;
                        address[i2] = (byte) value;
                    }
                }
                if (b != 0) {
                    if (input.regionMatches(i, ":", 0, 1)) {
                        i++;
                    } else {
                        if (!input.regionMatches(i, ".", 0, 1)) {
                            return null;
                        }
                        Object obj;
                        int i3 = b - 2;
                        int i4 = groupOffset;
                        int i5 = i3;
                        loop2:
                        while (i4 < limit) {
                            if (i5 == 16) {
                                obj = null;
                                break;
                            }
                            if (i5 != i3) {
                                if (input.charAt(i4) != '.') {
                                    obj = null;
                                    break;
                                }
                                i4++;
                            }
                            int i6 = 0;
                            int i7 = i4;
                            while (i7 < limit) {
                                char charAt = input.charAt(i7);
                                if (charAt >= '0' && charAt <= '9') {
                                    if (i6 == 0 && i4 != i7) {
                                        obj = null;
                                        break loop2;
                                    }
                                    i6 = ((i6 * 10) + charAt) - 48;
                                    if (i6 > 255) {
                                        obj = null;
                                        break loop2;
                                    }
                                    i7++;
                                } else {
                                    break;
                                }
                            }
                            if (i7 - i4 == 0) {
                                obj = null;
                                break;
                            }
                            i4 = i5 + 1;
                            address[i5] = (byte) i6;
                            i5 = i4;
                            i4 = i7;
                        }
                        if (i5 != i3 + 4) {
                            obj = null;
                        } else {
                            obj = 1;
                        }
                        if (obj == null) {
                            return null;
                        }
                        b += 2;
                    }
                }
                value = 0;
                groupOffset = i;
                while (i < limit) {
                    hexDigit = HttpUrl.decodeHexDigit(input.charAt(i));
                    if (hexDigit != -1) {
                        break;
                    }
                    value = (value << 4) + hexDigit;
                    i++;
                }
                groupLength = i - groupOffset;
                if (groupLength != 0) {
                }
                return null;
            }
            if (b != 16) {
                if (compress == -1) {
                    return null;
                }
                System.arraycopy(address, compress, address, 16 - (b - compress), b - compress);
                Arrays.fill(address, compress, (16 - b) + compress, (byte) 0);
            }
            try {
                return InetAddress.getByAddress(address);
            } catch (UnknownHostException e) {
                throw new AssertionError();
            }
        }

        private static String domainToAscii(String input) {
            Object obj = 1;
            try {
                String result = IDN.toASCII(input).toLowerCase(Locale.US);
                if (result.isEmpty()) {
                    return null;
                }
                for (int i = 0; i < result.length(); i++) {
                    char charAt = result.charAt(i);
                    if (charAt <= '\u001f' || charAt >= '' || " #%/:?@[\\]".indexOf(charAt) != -1) {
                        break;
                    }
                }
                obj = null;
                if (obj != null) {
                    return null;
                }
                return result;
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        private static int parsePort(String input, int pos, int limit) {
            try {
                int i = Integer.parseInt(HttpUrl.canonicalize(input, pos, limit, "", false, false, true));
                return (i <= 0 || i > 65535) ? -1 : i;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
    }

    private HttpUrl(Builder builder) {
        String str = null;
        this.scheme = builder.scheme;
        this.username = percentDecode(builder.encodedUsername, false);
        this.password = percentDecode(builder.encodedPassword, false);
        this.host = builder.host;
        this.port = builder.effectivePort();
        this.pathSegments = percentDecode(builder.encodedPathSegments, false);
        this.queryNamesAndValues = builder.encodedQueryNamesAndValues != null ? percentDecode(builder.encodedQueryNamesAndValues, true) : null;
        if (builder.encodedFragment != null) {
            str = percentDecode(builder.encodedFragment, false);
        }
        this.fragment = str;
        this.url = builder.toString();
    }

    public final URL url() {
        try {
            return new URL(this.url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public final URI uri() {
        int i = 0;
        try {
            String str;
            int i2;
            Builder builder = new Builder();
            builder.scheme = this.scheme;
            builder.encodedUsername = encodedUsername();
            builder.encodedPassword = encodedPassword();
            builder.host = this.host;
            builder.port = this.port != defaultPort(this.scheme) ? this.port : -1;
            builder.encodedPathSegments.clear();
            builder.encodedPathSegments.addAll(encodedPathSegments());
            builder.encodedQuery(encodedQuery());
            if (this.fragment == null) {
                str = null;
            } else {
                str = this.url.substring(this.url.indexOf(35) + 1);
            }
            builder.encodedFragment = str;
            int size = builder.encodedPathSegments.size();
            for (i2 = 0; i2 < size; i2++) {
                builder.encodedPathSegments.set(i2, canonicalize((String) builder.encodedPathSegments.get(i2), "[]", true, false, true));
            }
            if (builder.encodedQueryNamesAndValues != null) {
                i2 = builder.encodedQueryNamesAndValues.size();
                while (i < i2) {
                    str = (String) builder.encodedQueryNamesAndValues.get(i);
                    if (str != null) {
                        builder.encodedQueryNamesAndValues.set(i, canonicalize(str, "\\^`{|}", true, true, true));
                    }
                    i++;
                }
            }
            if (builder.encodedFragment != null) {
                builder.encodedFragment = canonicalize(builder.encodedFragment, " \"#<>\\^`{|}", true, false, false);
            }
            return new URI(builder.toString());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("not valid as a java.net.URI: " + this.url);
        }
    }

    public final String scheme() {
        return this.scheme;
    }

    public final boolean isHttps() {
        return this.scheme.equals("https");
    }

    public final String encodedUsername() {
        if (this.username.isEmpty()) {
            return "";
        }
        int usernameStart = this.scheme.length() + 3;
        return this.url.substring(usernameStart, delimiterOffset(this.url, usernameStart, this.url.length(), ":@"));
    }

    public final String encodedPassword() {
        if (this.password.isEmpty()) {
            return "";
        }
        return this.url.substring(this.url.indexOf(58, this.scheme.length() + 3) + 1, this.url.indexOf(64));
    }

    public final String host() {
        return this.host;
    }

    public final int port() {
        return this.port;
    }

    public static int defaultPort(String scheme) {
        if (scheme.equals("http")) {
            return 80;
        }
        if (scheme.equals("https")) {
            return 443;
        }
        return -1;
    }

    public final String encodedPath() {
        int pathStart = this.url.indexOf(47, this.scheme.length() + 3);
        return this.url.substring(pathStart, delimiterOffset(this.url, pathStart, this.url.length(), "?#"));
    }

    static void pathSegmentsToString(StringBuilder out, List<String> pathSegments) {
        int size = pathSegments.size();
        for (int i = 0; i < size; i++) {
            out.append('/');
            out.append((String) pathSegments.get(i));
        }
    }

    public final List<String> encodedPathSegments() {
        int pathStart = this.url.indexOf(47, this.scheme.length() + 3);
        int pathEnd = delimiterOffset(this.url, pathStart, this.url.length(), "?#");
        List<String> result = new ArrayList();
        int i = pathStart;
        while (i < pathEnd) {
            i++;
            int segmentEnd = delimiterOffset(this.url, i, pathEnd, "/");
            result.add(this.url.substring(i, segmentEnd));
            i = segmentEnd;
        }
        return result;
    }

    public final String encodedQuery() {
        if (this.queryNamesAndValues == null) {
            return null;
        }
        int queryStart = this.url.indexOf(63) + 1;
        return this.url.substring(queryStart, delimiterOffset(this.url, queryStart + 1, this.url.length(), "#"));
    }

    static void namesAndValuesToQueryString(StringBuilder out, List<String> namesAndValues) {
        int size = namesAndValues.size();
        for (int i = 0; i < size; i += 2) {
            String name = (String) namesAndValues.get(i);
            String value = (String) namesAndValues.get(i + 1);
            if (i > 0) {
                out.append('&');
            }
            out.append(name);
            if (value != null) {
                out.append('=');
                out.append(value);
            }
        }
    }

    static List<String> queryStringToNamesAndValues(String encodedQuery) {
        List<String> result = new ArrayList();
        int pos = 0;
        while (pos <= encodedQuery.length()) {
            int ampersandOffset = encodedQuery.indexOf(38, pos);
            if (ampersandOffset == -1) {
                ampersandOffset = encodedQuery.length();
            }
            int equalsOffset = encodedQuery.indexOf(61, pos);
            if (equalsOffset == -1 || equalsOffset > ampersandOffset) {
                result.add(encodedQuery.substring(pos, ampersandOffset));
                result.add(null);
            } else {
                result.add(encodedQuery.substring(pos, equalsOffset));
                result.add(encodedQuery.substring(equalsOffset + 1, ampersandOffset));
            }
            pos = ampersandOffset + 1;
        }
        return result;
    }

    public final String query() {
        if (this.queryNamesAndValues == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        namesAndValuesToQueryString(result, this.queryNamesAndValues);
        return result.toString();
    }

    public final HttpUrl resolve(String link) {
        Builder builder = new Builder();
        return builder.parse$6547b47a(this, link) == ParseResult.SUCCESS$4627f6df ? builder.build() : null;
    }

    public static HttpUrl parse(String url) {
        Builder builder = new Builder();
        if (builder.parse$6547b47a(null, url) == ParseResult.SUCCESS$4627f6df) {
            return builder.build();
        }
        return null;
    }

    public static HttpUrl get(URL url) {
        return parse(url.toString());
    }

    public final boolean equals(Object o) {
        return (o instanceof HttpUrl) && ((HttpUrl) o).url.equals(this.url);
    }

    public final int hashCode() {
        return this.url.hashCode();
    }

    public final String toString() {
        return this.url;
    }

    private static int delimiterOffset(String input, int pos, int limit, String delimiters) {
        for (int i = pos; i < limit; i++) {
            if (delimiters.indexOf(input.charAt(i)) != -1) {
                return i;
            }
        }
        return limit;
    }

    private static String percentDecode(String encoded, boolean plusIsSpace) {
        return percentDecode(encoded, 0, encoded.length(), plusIsSpace);
    }

    private static List<String> percentDecode(List<String> list, boolean plusIsSpace) {
        List<String> result = new ArrayList(list.size());
        for (String s : list) {
            result.add(s != null ? percentDecode(s, plusIsSpace) : null);
        }
        return Collections.unmodifiableList(result);
    }

    static String percentDecode(String encoded, int pos, int limit, boolean plusIsSpace) {
        for (int i = pos; i < limit; i++) {
            char c = encoded.charAt(i);
            if (c == '%' || (c == '+' && plusIsSpace)) {
                Buffer out = new Buffer();
                out.writeUtf8(encoded, pos, i);
                int i2 = i;
                while (i2 < limit) {
                    int codePointAt = encoded.codePointAt(i2);
                    if (codePointAt != 37 || i2 + 2 >= limit) {
                        if (codePointAt == 43 && plusIsSpace) {
                            out.writeByte(32);
                        }
                        out.writeUtf8CodePoint(codePointAt);
                    } else {
                        int decodeHexDigit = decodeHexDigit(encoded.charAt(i2 + 1));
                        int decodeHexDigit2 = decodeHexDigit(encoded.charAt(i2 + 2));
                        if (!(decodeHexDigit == -1 || decodeHexDigit2 == -1)) {
                            out.writeByte((decodeHexDigit << 4) + decodeHexDigit2);
                            i2 += 2;
                        }
                        out.writeUtf8CodePoint(codePointAt);
                    }
                    i2 += Character.charCount(codePointAt);
                }
                return out.readUtf8();
            }
        }
        return encoded.substring(pos, limit);
    }

    static int decodeHexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        }
        if (c >= 'a' && c <= 'f') {
            return (c - 97) + 10;
        }
        if (c < 'A' || c > 'F') {
            return -1;
        }
        return (c - 65) + 10;
    }

    static String canonicalize(String input, int pos, int limit, String encodeSet, boolean alreadyEncoded, boolean plusIsSpace, boolean asciiOnly) {
        int i = pos;
        while (i < limit) {
            int codePoint = input.codePointAt(i);
            if (codePoint < 32 || codePoint == 127 || ((codePoint >= 128 && asciiOnly) || encodeSet.indexOf(codePoint) != -1 || ((codePoint == 37 && !alreadyEncoded) || (codePoint == 43 && plusIsSpace)))) {
                Buffer out = new Buffer();
                out.writeUtf8(input, pos, i);
                Buffer buffer = null;
                while (i < limit) {
                    int codePointAt = input.codePointAt(i);
                    if (!(alreadyEncoded && (codePointAt == 9 || codePointAt == 10 || codePointAt == 12 || codePointAt == 13))) {
                        if (codePointAt == 43 && plusIsSpace) {
                            String str;
                            if (alreadyEncoded) {
                                str = "+";
                            } else {
                                str = "%2B";
                            }
                            out.writeUtf8(str);
                        } else if (codePointAt < 32 || codePointAt == 127 || ((codePointAt >= 128 && asciiOnly) || encodeSet.indexOf(codePointAt) != -1 || (codePointAt == 37 && !alreadyEncoded))) {
                            if (buffer == null) {
                                buffer = new Buffer();
                            }
                            buffer.writeUtf8CodePoint(codePointAt);
                            while (!buffer.exhausted()) {
                                int readByte = buffer.readByte() & 255;
                                out.writeByte(37);
                                out.writeByte(HEX_DIGITS[(readByte >> 4) & 15]);
                                out.writeByte(HEX_DIGITS[readByte & 15]);
                            }
                        } else {
                            out.writeUtf8CodePoint(codePointAt);
                        }
                    }
                    i += Character.charCount(codePointAt);
                }
                return out.readUtf8();
            }
            i += Character.charCount(codePoint);
        }
        return input.substring(pos, limit);
    }

    static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean plusIsSpace, boolean asciiOnly) {
        return canonicalize(input, 0, input.length(), encodeSet, true, plusIsSpace, asciiOnly);
    }
}
