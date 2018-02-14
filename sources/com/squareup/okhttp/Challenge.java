package com.squareup.okhttp;

import com.squareup.okhttp.internal.Util;

public final class Challenge {
    private final String realm;
    private final String scheme;

    public Challenge(String scheme, String realm) {
        this.scheme = scheme;
        this.realm = realm;
    }

    public final String getScheme() {
        return this.scheme;
    }

    public final String getRealm() {
        return this.realm;
    }

    public final boolean equals(Object o) {
        return (o instanceof Challenge) && Util.equal(this.scheme, ((Challenge) o).scheme) && Util.equal(this.realm, ((Challenge) o).realm);
    }

    public final int hashCode() {
        int hashCode;
        int i = 0;
        if (this.realm != null) {
            hashCode = this.realm.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (hashCode + 899) * 31;
        if (this.scheme != null) {
            i = this.scheme.hashCode();
        }
        return hashCode + i;
    }

    public final String toString() {
        return this.scheme + " realm=\"" + this.realm + "\"";
    }
}
