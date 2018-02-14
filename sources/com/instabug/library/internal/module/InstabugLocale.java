package com.instabug.library.internal.module;

public enum InstabugLocale {
    ENGLISH("en"),
    ARABIC("ar"),
    GERMAN("de"),
    SPANISH("es"),
    FRENCH("fr"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KOREAN("ko"),
    POLISH("pl"),
    PORTUGUESE_BRAZIL("pt", "BR"),
    PORTUGUESE_PORTUGAL("pt", "PT"),
    RUSSIAN("ru"),
    SWEDISH("sv"),
    TURKISH("tr"),
    SIMPLIFIED_CHINESE("zh", "CN"),
    TRADITIONAL_CHINESE("zh", "TW"),
    CZECH("cs");
    
    private final String code;
    private final String country;

    private InstabugLocale(String str) {
        this.code = str;
        this.country = "";
    }

    private InstabugLocale(String str, String str2) {
        this.code = str;
        this.country = str2;
    }

    public final String getCode() {
        return this.code;
    }

    public final String getCountry() {
        return this.country;
    }
}
