package com.instabug.library.model;

public enum IssueType {
    BUG("bug"),
    FEEDBACK("feedback"),
    CRASH("crash");
    
    private final String name;

    private IssueType(String str) {
        this.name = str;
    }

    public final String toString() {
        return this.name;
    }
}
