package io.fabric.sdk.android;

public final class KitInfo {
    private final String buildType;
    private final String identifier;
    private final String version;

    public KitInfo(String identifier, String version, String buildType) {
        this.identifier = identifier;
        this.version = version;
        this.buildType = buildType;
    }

    public final String getIdentifier() {
        return this.identifier;
    }

    public final String getVersion() {
        return this.version;
    }

    public final String getBuildType() {
        return this.buildType;
    }
}
