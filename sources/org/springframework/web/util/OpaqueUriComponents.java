package org.springframework.web.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.UriComponents.UriTemplateVariables;

final class OpaqueUriComponents extends UriComponents {
    private static final MultiValueMap<String, String> QUERY_PARAMS_NONE = new LinkedMultiValueMap(0);
    private final String ssp;

    OpaqueUriComponents(String scheme, String schemeSpecificPart, String fragment) {
        super(scheme, fragment);
        this.ssp = schemeSpecificPart;
    }

    public final UriComponents encode(String encoding) throws UnsupportedEncodingException {
        return this;
    }

    protected final UriComponents expandInternal(UriTemplateVariables uriVariables) {
        return new OpaqueUriComponents(UriComponents.expandUriComponent(getScheme(), uriVariables), UriComponents.expandUriComponent(this.ssp, uriVariables), UriComponents.expandUriComponent(getFragment(), uriVariables));
    }

    public final String toUriString() {
        StringBuilder uriBuilder = new StringBuilder();
        if (getScheme() != null) {
            uriBuilder.append(getScheme());
            uriBuilder.append(':');
        }
        if (this.ssp != null) {
            uriBuilder.append(this.ssp);
        }
        if (getFragment() != null) {
            uriBuilder.append('#');
            uriBuilder.append(getFragment());
        }
        return uriBuilder.toString();
    }

    public final URI toUri() {
        try {
            return new URI(getScheme(), this.ssp, getFragment());
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not create URI object: " + ex.getMessage(), ex);
        }
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OpaqueUriComponents)) {
            return false;
        }
        OpaqueUriComponents other = (OpaqueUriComponents) obj;
        if (ObjectUtils.nullSafeEquals(getScheme(), other.getScheme()) && ObjectUtils.nullSafeEquals(this.ssp, other.ssp) && ObjectUtils.nullSafeEquals(getFragment(), other.getFragment())) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return (((ObjectUtils.nullSafeHashCode(getScheme()) * 31) + ObjectUtils.nullSafeHashCode(this.ssp)) * 31) + ObjectUtils.nullSafeHashCode(getFragment());
    }
}
