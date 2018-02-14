package com.crashlytics.android.core;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import io.fabric.sdk.android.services.settings.PromptSettingsData;
import java.util.concurrent.CountDownLatch;

final class CrashPromptDialog {
    private final Builder dialog;
    private final OptInLatch latch;

    interface AlwaysSendCallback {
        void sendUserReportsWithoutPrompting(boolean z);
    }

    private static class OptInLatch {
        private final CountDownLatch latch;
        private boolean send;

        private OptInLatch() {
            this.send = false;
            this.latch = new CountDownLatch(1);
        }

        final void setOptIn(boolean optIn) {
            this.send = optIn;
            this.latch.countDown();
        }

        final boolean getOptIn() {
            return this.send;
        }

        final void await() {
            try {
                this.latch.await();
            } catch (InterruptedException e) {
            }
        }
    }

    public static CrashPromptDialog create(Activity activity, PromptSettingsData promptData, final AlwaysSendCallback alwaysSendCallback) {
        final OptInLatch latch = new OptInLatch();
        DialogStringResolver stringResolver = new DialogStringResolver(activity, promptData);
        Builder builder = new Builder(activity);
        CharSequence message = stringResolver.getMessage();
        float f = activity.getResources().getDisplayMetrics().density;
        int i = (int) (((float) 5) * f);
        View textView = new TextView(activity);
        textView.setAutoLinkMask(15);
        textView.setText(message);
        textView.setTextAppearance(activity, 16973892);
        textView.setPadding(i, i, i, i);
        textView.setFocusable(false);
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setPadding((int) (((float) 14) * f), (int) (((float) 2) * f), (int) (((float) 10) * f), (int) (f * ((float) 12)));
        scrollView.addView(textView);
        builder.setView(scrollView).setTitle(stringResolver.getTitle()).setCancelable(false).setNeutralButton(stringResolver.getSendButtonTitle(), new OnClickListener() {
            public final void onClick(DialogInterface dialog, int which) {
                latch.setOptIn(true);
                dialog.dismiss();
            }
        });
        if (promptData.showCancelButton) {
            builder.setNegativeButton(stringResolver.getCancelButtonTitle(), new OnClickListener() {
                public final void onClick(DialogInterface dialog, int id) {
                    latch.setOptIn(false);
                    dialog.dismiss();
                }
            });
        }
        if (promptData.showAlwaysSendButton) {
            builder.setPositiveButton(stringResolver.getAlwaysSendButtonTitle(), new OnClickListener() {
                public final void onClick(DialogInterface dialog, int id) {
                    alwaysSendCallback.sendUserReportsWithoutPrompting(true);
                    latch.setOptIn(true);
                    dialog.dismiss();
                }
            });
        }
        return new CrashPromptDialog(builder, latch);
    }

    private CrashPromptDialog(Builder dialog, OptInLatch latch) {
        this.latch = latch;
        this.dialog = dialog;
    }

    public final void show() {
        this.dialog.show();
    }

    public final void await() {
        this.latch.await();
    }

    public final boolean getOptIn() {
        return this.latch.getOptIn();
    }
}
