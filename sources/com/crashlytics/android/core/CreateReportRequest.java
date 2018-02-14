package com.crashlytics.android.core;

final class CreateReportRequest {
    public final String apiKey;
    public final Report report;

    public CreateReportRequest(String apiKey, Report report) {
        this.apiKey = apiKey;
        this.report = report;
    }
}
