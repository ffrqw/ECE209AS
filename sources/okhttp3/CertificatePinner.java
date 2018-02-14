package okhttp3;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLPeerUnverifiedException;
import okhttp3.internal.Util;
import okhttp3.internal.tls.CertificateChainCleaner;
import okio.ByteString;

public final class CertificatePinner {
    public static final CertificatePinner DEFAULT = new Builder().build();
    private final CertificateChainCleaner certificateChainCleaner;
    private final Set<Pin> pins;

    public static final class Builder {
        private final List<Pin> pins = new ArrayList();

        public final CertificatePinner build() {
            return new CertificatePinner(new LinkedHashSet(this.pins), null);
        }
    }

    static final class Pin {
        final String canonicalHostname;
        final ByteString hash;
        final String hashAlgorithm;
        final String pattern;

        public final boolean equals(Object other) {
            return (other instanceof Pin) && this.pattern.equals(((Pin) other).pattern) && this.hashAlgorithm.equals(((Pin) other).hashAlgorithm) && this.hash.equals(((Pin) other).hash);
        }

        public final int hashCode() {
            return ((((this.pattern.hashCode() + 527) * 31) + this.hashAlgorithm.hashCode()) * 31) + this.hash.hashCode();
        }

        public final String toString() {
            return this.hashAlgorithm + this.hash.base64();
        }
    }

    CertificatePinner(Set<Pin> pins, CertificateChainCleaner certificateChainCleaner) {
        this.pins = pins;
        this.certificateChainCleaner = certificateChainCleaner;
    }

    public final boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return (other instanceof CertificatePinner) && Util.equal(this.certificateChainCleaner, ((CertificatePinner) other).certificateChainCleaner) && this.pins.equals(((CertificatePinner) other).pins);
    }

    public final int hashCode() {
        return ((this.certificateChainCleaner != null ? this.certificateChainCleaner.hashCode() : 0) * 31) + this.pins.hashCode();
    }

    final CertificatePinner withCertificateChainCleaner(CertificateChainCleaner certificateChainCleaner) {
        return Util.equal(this.certificateChainCleaner, certificateChainCleaner) ? this : new CertificatePinner(this.pins, certificateChainCleaner);
    }

    public static String pin(Certificate certificate) {
        if (certificate instanceof X509Certificate) {
            return "sha256/" + sha256((X509Certificate) certificate).base64();
        }
        throw new IllegalArgumentException("Certificate pinning requires X509 certificates");
    }

    private static ByteString sha256(X509Certificate x509Certificate) {
        return ByteString.of(x509Certificate.getPublicKey().getEncoded()).sha256();
    }

    public final void check(String hostname, List<Certificate> peerCertificates) throws SSLPeerUnverifiedException {
        List<Pin> pins = Collections.emptyList();
        for (Pin pin : this.pins) {
            boolean regionMatches;
            if (pin.pattern.startsWith("*.")) {
                regionMatches = hostname.regionMatches(false, hostname.indexOf(46) + 1, pin.canonicalHostname, 0, pin.canonicalHostname.length());
            } else {
                regionMatches = hostname.equals(pin.canonicalHostname);
            }
            if (regionMatches) {
                if (pins.isEmpty()) {
                    pins = new ArrayList();
                }
                pins.add(pin);
            }
        }
        if (!pins.isEmpty()) {
            int c;
            X509Certificate x509Certificate;
            int pinsSize;
            int p;
            if (this.certificateChainCleaner != null) {
                peerCertificates = this.certificateChainCleaner.clean(peerCertificates, hostname);
            }
            int certsSize = peerCertificates.size();
            for (c = 0; c < certsSize; c++) {
                x509Certificate = (X509Certificate) peerCertificates.get(c);
                ByteString sha1 = null;
                ByteString sha256 = null;
                pinsSize = pins.size();
                for (p = 0; p < pinsSize; p++) {
                    Pin pin2 = (Pin) pins.get(p);
                    if (pin2.hashAlgorithm.equals("sha256/")) {
                        if (sha256 == null) {
                            sha256 = sha256(x509Certificate);
                        }
                        if (pin2.hash.equals(sha256)) {
                            return;
                        }
                    } else if (pin2.hashAlgorithm.equals("sha1/")) {
                        if (sha1 == null) {
                            sha1 = ByteString.of(x509Certificate.getPublicKey().getEncoded()).sha1();
                        }
                        if (pin2.hash.equals(sha1)) {
                            return;
                        }
                    } else {
                        throw new AssertionError();
                    }
                }
            }
            StringBuilder message = new StringBuilder("Certificate pinning failure!").append("\n  Peer certificate chain:");
            certsSize = peerCertificates.size();
            for (c = 0; c < certsSize; c++) {
                x509Certificate = (X509Certificate) peerCertificates.get(c);
                message.append("\n    ").append(pin(x509Certificate)).append(": ").append(x509Certificate.getSubjectDN().getName());
            }
            message.append("\n  Pinned certificates for ").append(hostname).append(":");
            pinsSize = pins.size();
            for (p = 0; p < pinsSize; p++) {
                message.append("\n    ").append((Pin) pins.get(p));
            }
            throw new SSLPeerUnverifiedException(message.toString());
        }
    }
}
