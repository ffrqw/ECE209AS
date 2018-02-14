package com.instabug.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class j {
    public static Intent a(Context context, String str, String str2) {
        b.a();
        b.a(context);
        Intent intent = new Intent(context, InstabugFeedbackActivity.class);
        intent.putExtra("com.instabug.library.process", 163);
        intent.putExtra("com.instabug.library.conversation.sender.name", str);
        intent.putExtra("com.instabug.library.conversation.issue.number", str2);
        intent.addFlags(65536);
        return intent;
    }

    public static Intent a(Context context, Uri uri, Uri uri2) {
        Intent intent = new Intent(context, InstabugFeedbackActivity.class);
        intent.putExtra("com.instabug.library.process", 162);
        intent.putExtra("com.instabug.library.image", uri);
        intent.putExtra("com.instabug.library.file", uri2);
        intent.addFlags(65536);
        return intent;
    }

    public static Intent a(Activity activity, Uri uri, Uri uri2) {
        Intent intent = new Intent(activity, InstabugFeedbackActivity.class);
        intent.putExtra("com.instabug.library.process", 160);
        intent.putExtra("com.instabug.library.image", uri);
        intent.putExtra("com.instabug.library.file", uri2);
        intent.addFlags(65536);
        return intent;
    }

    public static Intent a(Context context) {
        b.a();
        b.a(context);
        Intent intent = new Intent(context, InstabugFeedbackActivity.class);
        intent.putExtra("com.instabug.library.process", 164);
        intent.addFlags(65536);
        return intent;
    }
}
