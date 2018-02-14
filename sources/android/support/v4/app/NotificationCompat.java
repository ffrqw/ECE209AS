package android.support.v4.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.NotificationCompatBase.Action.Factory;
import android.support.v4.app.RemoteInputCompatBase.RemoteInput;
import android.support.v4.os.BuildCompat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class NotificationCompat {
    static final NotificationCompatImpl IMPL;

    public static class Action extends android.support.v4.app.NotificationCompatBase.Action {
        public static final Factory FACTORY = new Factory() {
        };
        final Bundle mExtras;
        private final RemoteInput[] mRemoteInputs;

        public final int getIcon() {
            return 0;
        }

        public final CharSequence getTitle() {
            return null;
        }

        public final PendingIntent getActionIntent() {
            return null;
        }

        public final Bundle getExtras() {
            return this.mExtras;
        }

        public final boolean getAllowGeneratedReplies() {
            return false;
        }

        public final /* bridge */ /* synthetic */ RemoteInput[] getRemoteInputs() {
            return this.mRemoteInputs;
        }
    }

    public static abstract class Style {
        Builder mBuilder;
        boolean mSummaryTextSet = false;

        public void addCompatExtras(Bundle extras) {
        }
    }

    public static class BigPictureStyle extends Style {
    }

    public static class BigTextStyle extends Style {
        CharSequence mBigText;

        public final BigTextStyle bigText(CharSequence cs) {
            this.mBigText = Builder.limitCharSequenceLength(cs);
            return this;
        }
    }

    public static class Builder {
        public ArrayList<Action> mActions = new ArrayList();
        int mColor = 0;
        PendingIntent mContentIntent;
        public CharSequence mContentText;
        public CharSequence mContentTitle;
        public Context mContext;
        boolean mLocalOnly = false;
        public Notification mNotification = new Notification();
        public ArrayList<String> mPeople;
        int mPriority;
        boolean mShowWhen = true;
        public Style mStyle;
        int mVisibility = 0;

        public Builder(Context context) {
            this.mContext = context;
            this.mNotification.when = System.currentTimeMillis();
            this.mNotification.audioStreamType = -1;
            this.mPriority = 0;
            this.mPeople = new ArrayList();
        }

        public final Builder setSmallIcon(int icon) {
            this.mNotification.icon = icon;
            return this;
        }

        public final Builder setContentTitle(CharSequence title) {
            this.mContentTitle = limitCharSequenceLength(title);
            return this;
        }

        public final Builder setContentText(CharSequence text) {
            this.mContentText = limitCharSequenceLength(text);
            return this;
        }

        public final Builder setContentIntent(PendingIntent intent) {
            this.mContentIntent = intent;
            return this;
        }

        public final Builder setTicker(CharSequence tickerText) {
            this.mNotification.tickerText = limitCharSequenceLength(tickerText);
            return this;
        }

        public final Builder setSound(Uri sound) {
            this.mNotification.sound = sound;
            this.mNotification.audioStreamType = -1;
            return this;
        }

        public final Builder setLocalOnly(boolean b) {
            this.mLocalOnly = true;
            return this;
        }

        public final Builder setPriority(int pri) {
            this.mPriority = 1;
            return this;
        }

        public final Builder setStyle(Style style) {
            if (this.mStyle != style) {
                this.mStyle = style;
                if (this.mStyle != null) {
                    Style style2 = this.mStyle;
                    if (style2.mBuilder != this) {
                        style2.mBuilder = this;
                        if (style2.mBuilder != null) {
                            style2.mBuilder.setStyle(style2);
                        }
                    }
                }
            }
            return this;
        }

        public final Builder setColor(int argb) {
            this.mColor = argb;
            return this;
        }

        public final Notification build() {
            return NotificationCompat.IMPL.build(this, new BuilderExtender());
        }

        protected static CharSequence limitCharSequenceLength(CharSequence cs) {
            if (cs != null && cs.length() > 5120) {
                return cs.subSequence(0, 5120);
            }
            return cs;
        }

        public final Builder setAutoCancel(boolean autoCancel) {
            Notification notification = this.mNotification;
            notification.flags |= 16;
            return this;
        }
    }

    protected static class BuilderExtender {
        protected BuilderExtender() {
        }

        public static Notification build(Builder b, NotificationBuilderWithBuilderAccessor builder) {
            return builder.build();
        }
    }

    public static class InboxStyle extends Style {
        ArrayList<CharSequence> mTexts = new ArrayList();
    }

    public static class MessagingStyle extends Style {
        List<Message> mMessages = new ArrayList();

        public static final class Message {
            private final CharSequence mSender;
            private final CharSequence mText;
            private final long mTimestamp;

            public final CharSequence getText() {
                return this.mText;
            }

            public final long getTimestamp() {
                return this.mTimestamp;
            }

            public final CharSequence getSender() {
                return this.mSender;
            }

            static Bundle[] getBundleArrayForMessages(List<Message> messages) {
                Bundle[] bundles = new Bundle[messages.size()];
                int N = messages.size();
                for (int i = 0; i < N; i++) {
                    Message message = (Message) messages.get(i);
                    Bundle bundle = new Bundle();
                    if (message.mText != null) {
                        bundle.putCharSequence("text", message.mText);
                    }
                    bundle.putLong("time", message.mTimestamp);
                    if (message.mSender != null) {
                        bundle.putCharSequence("sender", message.mSender);
                    }
                    bundles[i] = bundle;
                }
                return bundles;
            }
        }

        MessagingStyle() {
        }

        public final void addCompatExtras(Bundle extras) {
            super.addCompatExtras(extras);
            if (!this.mMessages.isEmpty()) {
                extras.putParcelableArray("android.messages", Message.getBundleArrayForMessages(this.mMessages));
            }
        }
    }

    interface NotificationCompatImpl {
        Notification build(Builder builder, BuilderExtender builderExtender);
    }

    static class NotificationCompatImplBase implements NotificationCompatImpl {
        NotificationCompatImplBase() {
        }

        public Notification build(Builder b, BuilderExtender extender) {
            Notification result = NotificationCompatBase.add(b.mNotification, b.mContext, b.mContentTitle, b.mContentText, b.mContentIntent, null);
            if (b.mPriority > 0) {
                result.flags |= 128;
            }
            return result;
        }

        public Bundle getExtras(Notification n) {
            return null;
        }
    }

    static class NotificationCompatImplJellybean extends NotificationCompatImplBase {
        NotificationCompatImplJellybean() {
        }

        public Notification build(Builder b, BuilderExtender extender) {
            android.support.v4.app.NotificationCompatJellybean.Builder builder = new android.support.v4.app.NotificationCompatJellybean.Builder(b.mContext, b.mNotification, b.mContentTitle, b.mContentText, null, null, 0, b.mContentIntent, null, null, 0, 0, false, false, b.mPriority, null, b.mLocalOnly, null, null, false, null, null, null);
            NotificationCompat.addActionsToBuilder(builder, b.mActions);
            NotificationCompat.addStyleToBuilderJellybean(builder, b.mStyle);
            Notification notification = BuilderExtender.build(b, builder);
            if (b.mStyle != null) {
                Bundle extras = getExtras(notification);
                if (extras != null) {
                    b.mStyle.addCompatExtras(extras);
                }
            }
            return notification;
        }

        public Bundle getExtras(Notification n) {
            return NotificationCompatJellybean.getExtras(n);
        }
    }

    static class NotificationCompatImplKitKat extends NotificationCompatImplJellybean {
        NotificationCompatImplKitKat() {
        }

        public Notification build(Builder b, BuilderExtender extender) {
            android.support.v4.app.NotificationCompatKitKat.Builder builder = new android.support.v4.app.NotificationCompatKitKat.Builder(b.mContext, b.mNotification, b.mContentTitle, b.mContentText, null, null, 0, b.mContentIntent, null, null, 0, 0, false, b.mShowWhen, false, b.mPriority, null, b.mLocalOnly, b.mPeople, null, null, false, null, null, null);
            NotificationCompat.addActionsToBuilder(builder, b.mActions);
            NotificationCompat.addStyleToBuilderJellybean(builder, b.mStyle);
            return BuilderExtender.build(b, builder);
        }

        public final Bundle getExtras(Notification n) {
            return n.extras;
        }
    }

    static class NotificationCompatImplApi20 extends NotificationCompatImplKitKat {
        NotificationCompatImplApi20() {
        }

        public Notification build(Builder b, BuilderExtender extender) {
            android.support.v4.app.NotificationCompatApi20.Builder builder = new android.support.v4.app.NotificationCompatApi20.Builder(b.mContext, b.mNotification, b.mContentTitle, b.mContentText, null, null, 0, b.mContentIntent, null, null, 0, 0, false, b.mShowWhen, false, b.mPriority, null, b.mLocalOnly, b.mPeople, null, null, false, null, null, null);
            NotificationCompat.addActionsToBuilder(builder, b.mActions);
            NotificationCompat.addStyleToBuilderJellybean(builder, b.mStyle);
            Notification notification = BuilderExtender.build(b, builder);
            if (b.mStyle != null) {
                b.mStyle.addCompatExtras(notification.extras);
            }
            return notification;
        }
    }

    static class NotificationCompatImplApi21 extends NotificationCompatImplApi20 {
        NotificationCompatImplApi21() {
        }

        public Notification build(Builder b, BuilderExtender extender) {
            android.support.v4.app.NotificationCompatApi21.Builder builder = new android.support.v4.app.NotificationCompatApi21.Builder(b.mContext, b.mNotification, b.mContentTitle, b.mContentText, null, null, 0, b.mContentIntent, null, null, 0, 0, false, b.mShowWhen, false, b.mPriority, null, b.mLocalOnly, null, b.mPeople, null, b.mColor, 0, null, null, false, null, null, null, null);
            NotificationCompat.addActionsToBuilder(builder, b.mActions);
            NotificationCompat.addStyleToBuilderJellybean(builder, b.mStyle);
            Notification notification = BuilderExtender.build(b, builder);
            if (b.mStyle != null) {
                b.mStyle.addCompatExtras(notification.extras);
            }
            return notification;
        }
    }

    static class NotificationCompatImplApi24 extends NotificationCompatImplApi21 {
        NotificationCompatImplApi24() {
        }

        public final Notification build(Builder b, BuilderExtender extender) {
            android.support.v4.app.NotificationCompatApi24.Builder builder = new android.support.v4.app.NotificationCompatApi24.Builder(b.mContext, b.mNotification, b.mContentTitle, b.mContentText, null, null, 0, b.mContentIntent, null, null, 0, 0, false, b.mShowWhen, false, b.mPriority, null, b.mLocalOnly, null, b.mPeople, null, b.mColor, 0, null, null, false, null, null, null, null, null);
            NotificationCompat.addActionsToBuilder(builder, b.mActions);
            NotificationCompat.addStyleToBuilderApi24(builder, b.mStyle);
            Notification notification = BuilderExtender.build(b, builder);
            if (b.mStyle != null) {
                b.mStyle.addCompatExtras(notification.extras);
            }
            return notification;
        }
    }

    static class NotificationCompatImplHoneycomb extends NotificationCompatImplBase {
        NotificationCompatImplHoneycomb() {
        }

        public final Notification build(Builder b, BuilderExtender extender) {
            boolean z;
            boolean z2 = true;
            Context context = b.mContext;
            Notification notification = b.mNotification;
            CharSequence charSequence = b.mContentTitle;
            CharSequence charSequence2 = b.mContentText;
            PendingIntent pendingIntent = b.mContentIntent;
            android.app.Notification.Builder lights = new android.app.Notification.Builder(context).setWhen(notification.when).setSmallIcon(notification.icon, notification.iconLevel).setContent(notification.contentView).setTicker(notification.tickerText, null).setSound(notification.sound, notification.audioStreamType).setVibrate(notification.vibrate).setLights(notification.ledARGB, notification.ledOnMS, notification.ledOffMS);
            if ((notification.flags & 2) != 0) {
                z = true;
            } else {
                z = false;
            }
            lights = lights.setOngoing(z);
            if ((notification.flags & 8) != 0) {
                z = true;
            } else {
                z = false;
            }
            lights = lights.setOnlyAlertOnce(z);
            if ((notification.flags & 16) != 0) {
                z = true;
            } else {
                z = false;
            }
            android.app.Notification.Builder deleteIntent = lights.setAutoCancel(z).setDefaults(notification.defaults).setContentTitle(charSequence).setContentText(charSequence2).setContentInfo(null).setContentIntent(pendingIntent).setDeleteIntent(notification.deleteIntent);
            if ((notification.flags & 128) == 0) {
                z2 = false;
            }
            return deleteIntent.setFullScreenIntent(null, z2).setLargeIcon(null).setNumber(0).getNotification();
        }
    }

    static class NotificationCompatImplIceCreamSandwich extends NotificationCompatImplBase {
        NotificationCompatImplIceCreamSandwich() {
        }

        public final Notification build(Builder b, BuilderExtender extender) {
            return BuilderExtender.build(b, new android.support.v4.app.NotificationCompatIceCreamSandwich.Builder(b.mContext, b.mNotification, b.mContentTitle, b.mContentText, null, null, 0, b.mContentIntent, null, null, 0, 0, false));
        }
    }

    static void addActionsToBuilder(NotificationBuilderWithActions builder, ArrayList<Action> actions) {
        Iterator it = actions.iterator();
        while (it.hasNext()) {
            builder.addAction((Action) it.next());
        }
    }

    static void addStyleToBuilderJellybean(NotificationBuilderWithBuilderAccessor builder, Style style) {
        if (style == null) {
            return;
        }
        if (style instanceof BigTextStyle) {
            NotificationCompatJellybean.addBigTextStyle(builder, null, false, null, ((BigTextStyle) style).mBigText);
        } else if (style instanceof InboxStyle) {
            NotificationCompatJellybean.addInboxStyle(builder, null, false, null, ((InboxStyle) style).mTexts);
        } else if (style instanceof BigPictureStyle) {
            NotificationCompatJellybean.addBigPictureStyle(builder, null, false, null, null, null, false);
        }
    }

    static void addStyleToBuilderApi24(NotificationBuilderWithBuilderAccessor builder, Style style) {
        if (style == null) {
            return;
        }
        if (style instanceof MessagingStyle) {
            MessagingStyle messagingStyle = (MessagingStyle) style;
            List<CharSequence> texts = new ArrayList();
            List<Long> timestamps = new ArrayList();
            List<CharSequence> senders = new ArrayList();
            List<String> dataMimeTypes = new ArrayList();
            List<Uri> dataUris = new ArrayList();
            for (Message message : messagingStyle.mMessages) {
                texts.add(message.getText());
                timestamps.add(Long.valueOf(message.getTimestamp()));
                senders.add(message.getSender());
                dataMimeTypes.add(null);
                dataUris.add(null);
            }
            NotificationCompatApi24.addMessagingStyle(builder, null, null, texts, timestamps, senders, dataMimeTypes, dataUris);
            return;
        }
        addStyleToBuilderJellybean(builder, style);
    }

    static {
        if (BuildCompat.isAtLeastN()) {
            IMPL = new NotificationCompatImplApi24();
        } else if (VERSION.SDK_INT >= 21) {
            IMPL = new NotificationCompatImplApi21();
        } else if (VERSION.SDK_INT >= 20) {
            IMPL = new NotificationCompatImplApi20();
        } else if (VERSION.SDK_INT >= 19) {
            IMPL = new NotificationCompatImplKitKat();
        } else if (VERSION.SDK_INT >= 16) {
            IMPL = new NotificationCompatImplJellybean();
        } else if (VERSION.SDK_INT >= 14) {
            IMPL = new NotificationCompatImplIceCreamSandwich();
        } else if (VERSION.SDK_INT >= 11) {
            IMPL = new NotificationCompatImplHoneycomb();
        } else {
            IMPL = new NotificationCompatImplBase();
        }
    }
}
