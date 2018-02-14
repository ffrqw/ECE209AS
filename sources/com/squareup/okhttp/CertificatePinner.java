package com.squareup.okhttp;

import com.squareup.okhttp.internal.Util;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLPeerUnverifiedException;
import okio.ByteString;

public final class CertificatePinner {
    public static final CertificatePinner DEFAULT = new CertificatePinner(new Builder());
    private final Map<String, Set<ByteString>> hostnameToPins;

    public static final class Builder {
        private final Map<String, Set<ByteString>> hostnameToPins = new LinkedHashMap();
    }

    private CertificatePinner(Builder builder) {
        this.hostnameToPins = Util.immutableMap(builder.hostnameToPins);
    }

    public final void check(String hostname, List<Certificate> peerCertificates) throws SSLPeerUnverifiedException {
        Collection collection;
        Set<ByteString> pins = null;
        Set<ByteString> set = (Set) this.hostnameToPins.get(hostname);
        int indexOf = hostname.indexOf(46);
        if (indexOf != hostname.lastIndexOf(46)) {
            collection = (Set) this.hostnameToPins.get("*." + hostname.substring(indexOf + 1));
        } else {
            collection = null;
        }
        if (!(set == null && collection == null)) {
            if (set != null && collection != null) {
                pins = new LinkedHashSet();
                pins.addAll(set);
                pins.addAll(collection);
            } else if (set != null) {
                pins = set;
            } else {
                Collection pins2 = collection;
            }
        }
        if (pins != null) {
            int i = 0;
            int size = peerCertificates.size();
            while (i < size) {
                if (!pins.contains(sha1((X509Certificate) peerCertificates.get(i)))) {
                    i++;
                } else {
                    return;
                }
            }
            StringBuilder message = new StringBuilder("Certificate pinning failure!").append("\n  Peer certificate chain:");
            size = peerCertificates.size();
            for (i = 0; i < size; i++) {
                X509Certificate x509Certificate = (X509Certificate) peerCertificates.get(i);
                message.append("\n    ").append(pin(x509Certificate)).append(": ").append(x509Certificate.getSubjectDN().getName());
            }
            message.append("\n  Pinned certificates for ").append(hostname).append(":");
            for (ByteString pin : pins) {
                message.append("\n    sha1/").append(pin.base64());
            }
            throw new SSLPeerUnverifiedException(message.toString());
        }
    }

    public static String pin(Certificate certificate) {
        if (certificate instanceof X509Certificate) {
            return "sha1/" + sha1((X509Certificate) certificate).base64();
        }
        throw new IllegalArgumentException("Certificate pinning requires X509 certificates");
    }

    private static ByteString sha1(X509Certificate x509Certificate) {
        return Util.sha1(ByteString.of(x509Certificate.getPublicKey().getEncoded()));
    }
}
