package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import java.util.regex.Pattern;

public class VersionUtil {
    private static final Pattern V_SEP = Pattern.compile("[-_./;:]");

    public static Version versionFor(Class<?> cls) {
        Version version = packageVersionFor(cls);
        return version == null ? Version.unknownVersion() : version;
    }

    public static Version packageVersionFor(Class<?> cls) {
        Version v = null;
        Class<?> vClass;
        try {
            vClass = Class.forName(cls.getPackage().getName() + ".PackageVersion", true, cls.getClassLoader());
            v = ((Versioned) vClass.newInstance()).version();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get Versioned out of " + vClass);
        } catch (Exception e2) {
        }
        return v == null ? Version.unknownVersion() : v;
    }

    public static Version parseVersion(String s, String groupId, String artifactId) {
        int i = 0;
        if (s != null) {
            s = s.trim();
            if (s.length() > 0) {
                int parseVersionPart;
                String[] parts = V_SEP.split(s);
                int parseVersionPart2 = parseVersionPart(parts[0]);
                if (parts.length > 1) {
                    parseVersionPart = parseVersionPart(parts[1]);
                } else {
                    parseVersionPart = 0;
                }
                if (parts.length > 2) {
                    i = parseVersionPart(parts[2]);
                }
                return new Version(parseVersionPart2, parseVersionPart, i, parts.length > 3 ? parts[3] : null, groupId, artifactId);
            }
        }
        return Version.unknownVersion();
    }

    protected static int parseVersionPart(String s) {
        int number = 0;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c > '9' || c < '0') {
                break;
            }
            number = (number * 10) + (c - 48);
        }
        return number;
    }

    public static final void throwInternal() {
        throw new RuntimeException("Internal error: this code path should never get executed");
    }
}
