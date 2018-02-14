package com.instabug.library;

import com.instabug.library.model.IssueType;

public interface OnSdkDismissedCallback {

    public enum IssueState {
        SUBMITTED,
        CANCELLED,
        IN_PROGRESS
    }

    void onSdkDismissed(IssueState issueState, IssueType issueType);
}
