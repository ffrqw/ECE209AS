package com.squareup.okhttp.internal.tls;

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

public final class OkHostnameVerifier implements HostnameVerifier {
    public static final OkHostnameVerifier INSTANCE = new OkHostnameVerifier();
    private static final Pattern VERIFY_AS_IP_ADDRESS = Pattern.compile("([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)");

    private OkHostnameVerifier() {
    }

    public final boolean verify(String host, SSLSession session) {
        try {
            boolean z;
            X509Certificate x509Certificate = (X509Certificate) session.getPeerCertificates()[0];
            if (VERIFY_AS_IP_ADDRESS.matcher(host).matches()) {
                List subjectAltNames = getSubjectAltNames(x509Certificate, 7);
                int size = subjectAltNames.size();
                int i = 0;
                while (i < size) {
                    if (host.equalsIgnoreCase((String) subjectAltNames.get(i))) {
                        z = true;
                    } else {
                        i++;
                    }
                }
                return false;
            }
            String toLowerCase = host.toLowerCase(Locale.US);
            List subjectAltNames2 = getSubjectAltNames(x509Certificate, 2);
            int size2 = subjectAltNames2.size();
            int i2 = 0;
            boolean z2 = false;
            while (i2 < size2) {
                if (verifyHostName(toLowerCase, (String) subjectAltNames2.get(i2))) {
                    return true;
                }
                i2++;
                z2 = true;
            }
            if (!z2) {
                String findMostSpecific = new DistinguishedNameParser(x509Certificate.getSubjectX500Principal()).findMostSpecific("cn");
                if (findMostSpecific != null) {
                    return verifyHostName(toLowerCase, findMostSpecific);
                }
            }
            z = false;
            return z;
        } catch (SSLException e) {
            return false;
        }
    }

    public static List<String> allSubjectAltNames(X509Certificate certificate) {
        List<String> altIpaNames = getSubjectAltNames(certificate, 7);
        List<String> altDnsNames = getSubjectAltNames(certificate, 2);
        List<String> result = new ArrayList(altIpaNames.size() + altDnsNames.size());
        result.addAll(altIpaNames);
        result.addAll(altDnsNames);
        return result;
    }

    private static List<String> getSubjectAltNames(X509Certificate certificate, int type) {
        List<String> result = new ArrayList();
        try {
            Collection<?> subjectAltNames = certificate.getSubjectAlternativeNames();
            if (subjectAltNames == null) {
                return Collections.emptyList();
            }
            Iterator it = subjectAltNames.iterator();
            while (it.hasNext()) {
                List<?> entry = (List) it.next();
                if (entry != null && entry.size() >= 2) {
                    Integer altNameType = (Integer) entry.get(0);
                    if (altNameType != null && altNameType.intValue() == type) {
                        String altName = (String) entry.get(1);
                        if (altName != null) {
                            result.add(altName);
                        }
                    }
                }
            }
            return result;
        } catch (CertificateParsingException e) {
            return Collections.emptyList();
        }
    }

    private static boolean verifyHostName(String hostName, String pattern) {
        if (hostName == null || hostName.length() == 0 || hostName.startsWith(".") || hostName.endsWith("..") || pattern == null || pattern.length() == 0 || pattern.startsWith(".") || pattern.endsWith("..")) {
            return false;
        }
        if (!hostName.endsWith(".")) {
            hostName = hostName + '.';
        }
        if (!pattern.endsWith(".")) {
            pattern = pattern + '.';
        }
        pattern = pattern.toLowerCase(Locale.US);
        if (!pattern.contains("*")) {
            return hostName.equals(pattern);
        }
        if (!pattern.startsWith("*.") || pattern.indexOf(42, 1) != -1 || hostName.length() < pattern.length() || "*.".equals(pattern)) {
            return false;
        }
        String suffix = pattern.substring(1);
        if (!hostName.endsWith(suffix)) {
            return false;
        }
        int suffixStartIndexInHostName = hostName.length() - suffix.length();
        if (suffixStartIndexInHostName <= 0 || hostName.lastIndexOf(46, suffixStartIndexInHostName - 1) == -1) {
            return true;
        }
        return false;
    }
}
