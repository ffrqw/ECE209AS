package com.crashlytics.android.core;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.common.ResponseParser;
import io.fabric.sdk.android.services.network.HttpMethod;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import java.io.File;
import java.util.Map.Entry;

final class DefaultCreateReportSpiCall extends AbstractSpiCall implements CreateReportSpiCall {
    public DefaultCreateReportSpiCall(Kit kit, String protocolAndHostOverride, String url, HttpRequestFactory requestFactory) {
        super(kit, protocolAndHostOverride, url, requestFactory, HttpMethod.POST$6bc89afe);
    }

    public final boolean invoke(CreateReportRequest requestData) {
        HttpRequest httpRequest = getHttpRequest().header("X-CRASHLYTICS-API-KEY", requestData.apiKey).header("X-CRASHLYTICS-API-CLIENT-TYPE", "android").header("X-CRASHLYTICS-API-CLIENT-VERSION", this.kit.getVersion());
        for (Entry entry : requestData.report.getCustomHeaders().entrySet()) {
            httpRequest = httpRequest.header((String) entry.getKey(), (String) entry.getValue());
        }
        Report report = requestData.report;
        httpRequest.part("report[identifier]", null, report.getIdentifier());
        if (report.getFiles().length == 1) {
            Fabric.getLogger().d("CrashlyticsCore", "Adding single file " + report.getFileName() + " to report " + report.getIdentifier());
            httpRequest = httpRequest.part("report[file]", report.getFileName(), "application/octet-stream", report.getFile());
        } else {
            int i = 0;
            for (File file : report.getFiles()) {
                Fabric.getLogger().d("CrashlyticsCore", "Adding file " + file.getName() + " to report " + report.getIdentifier());
                httpRequest.part("report[file" + i + "]", file.getName(), "application/octet-stream", file);
                i++;
            }
        }
        Fabric.getLogger().d("CrashlyticsCore", "Sending report to: " + getUrl());
        int statusCode = httpRequest.code();
        Fabric.getLogger().d("CrashlyticsCore", "Create report request ID: " + httpRequest.header("X-REQUEST-ID"));
        Fabric.getLogger().d("CrashlyticsCore", "Result was: " + statusCode);
        return ResponseParser.parse(statusCode) == 0;
    }
}
