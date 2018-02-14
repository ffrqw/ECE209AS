package com.squareup.picasso;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;

final class ContactsPhotoRequestHandler extends RequestHandler {
    private static final UriMatcher matcher;
    private final Context context;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        matcher = uriMatcher;
        uriMatcher.addURI("com.android.contacts", "contacts/lookup/*/#", 1);
        matcher.addURI("com.android.contacts", "contacts/lookup/*", 1);
        matcher.addURI("com.android.contacts", "contacts/#/photo", 2);
        matcher.addURI("com.android.contacts", "contacts/#", 3);
        matcher.addURI("com.android.contacts", "display_photo/#", 4);
    }

    ContactsPhotoRequestHandler(Context context) {
        this.context = context;
    }

    public final boolean canHandleRequest(Request data) {
        Uri uri = data.uri;
        return "content".equals(uri.getScheme()) && Contacts.CONTENT_URI.getHost().equals(uri.getHost()) && matcher.match(data.uri) != -1;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final com.squareup.picasso.RequestHandler.Result load(com.squareup.picasso.Request r6, int r7) throws java.io.IOException {
        /*
        r5 = this;
        r2 = 0;
        r1 = r5.context;
        r3 = r1.getContentResolver();
        r1 = r6.uri;
        r4 = matcher;
        r4 = r4.match(r1);
        switch(r4) {
            case 1: goto L_0x0027;
            case 2: goto L_0x0038;
            case 3: goto L_0x0032;
            case 4: goto L_0x0038;
            default: goto L_0x0012;
        };
    L_0x0012:
        r2 = new java.lang.IllegalStateException;
        r3 = new java.lang.StringBuilder;
        r4 = "Invalid uri: ";
        r3.<init>(r4);
        r1 = r3.append(r1);
        r1 = r1.toString();
        r2.<init>(r1);
        throw r2;
    L_0x0027:
        r1 = android.provider.ContactsContract.Contacts.lookupContact(r3, r1);
        if (r1 != 0) goto L_0x0032;
    L_0x002d:
        r0 = r2;
    L_0x002e:
        if (r0 != 0) goto L_0x003d;
    L_0x0030:
        r1 = r2;
    L_0x0031:
        return r1;
    L_0x0032:
        r4 = 1;
        r0 = android.provider.ContactsContract.Contacts.openContactPhotoInputStream(r3, r1, r4);
        goto L_0x002e;
    L_0x0038:
        r0 = r3.openInputStream(r1);
        goto L_0x002e;
    L_0x003d:
        r1 = new com.squareup.picasso.RequestHandler$Result;
        r2 = okio.Okio.source(r0);
        r3 = com.squareup.picasso.Picasso.LoadedFrom.DISK;
        r1.<init>(r2, r3);
        goto L_0x0031;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.picasso.ContactsPhotoRequestHandler.load(com.squareup.picasso.Request, int):com.squareup.picasso.RequestHandler$Result");
    }
}
