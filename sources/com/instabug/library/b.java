package com.instabug.library;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.os.Build.VERSION;
import android.support.v4.app.NotificationCompat.Builder;
import com.instabug.library.model.g;
import com.instabug.library.model.h;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.f;
import com.instabug.library.util.i;
import com.instabug.library.util.i.a;
import com.rachio.iro.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class b {
    private static b b;
    private f a;

    public static b a() {
        if (b == null) {
            b = new b();
        }
        return b;
    }

    private b() {
    }

    public final void a(final Activity activity, final List<g> list) {
        if (activity == null || list == null) {
            InstabugSDKLogger.wtf(this, "Sending system notification using activity while application isn't running");
            return;
        }
        this.a = new f(activity);
        if (s.g()) {
            h hVar;
            final int a = a((List) list);
            g gVar = (g) list.get(list.size() - 1);
            h hVar2;
            switch (a) {
                case 0:
                    hVar2 = new h();
                    hVar2.a(a((Context) activity, 0, (List) list));
                    hVar2.b(a(activity, 0, gVar.f()));
                    hVar2.c(gVar.h());
                    hVar = hVar2;
                    break;
                case 1:
                    hVar2 = new h();
                    hVar2.a(a((Context) activity, 1, (List) list));
                    hVar2.b(a(activity, 1, gVar.f()));
                    hVar2.c(gVar.h());
                    hVar = hVar2;
                    break;
                default:
                    hVar = null;
                    break;
            }
            i.a().a(activity, hVar, new a(this) {
                final /* synthetic */ b d;

                public final void onClick() {
                    b.a(this.d, activity, list, a);
                }

                public final void a(boolean z) {
                    if (!z) {
                        this.d.b(activity, list);
                    }
                }
            });
            return;
        }
        b(activity, list);
    }

    public final void a(Context context, List<g> list) {
        if (context == null) {
            throw new IllegalStateException("You forgot to set context for this notification");
        } else if (list == null) {
            throw new IllegalStateException("No data for this notification");
        } else {
            this.a = new f(context);
            b(context, list);
        }
    }

    private void b(Context context, List<g> list) {
        CharSequence charSequence = "";
        Intent intent = null;
        switch (a((List) list)) {
            case 0:
                g gVar = (g) list.get(list.size() - 1);
                charSequence = a(context, 0, (List) list);
                intent = j.a(context, gVar.f(), gVar.g());
                break;
            case 1:
                charSequence = a(context, 1, (List) list);
                intent = j.a(context);
                break;
        }
        int w = s.w();
        if (w == -1 || w == 0) {
            w = this.a.b();
        }
        Builder contentIntent = new Builder(context).setSmallIcon(w).setContentTitle(this.a.a()).setContentText(charSequence).setAutoCancel(true).setSound(RingtoneManager.getDefaultUri(2)).setContentIntent(PendingIntent.getActivity(context, 0, intent, 134217728));
        if (!(context instanceof Activity) && VERSION.SDK_INT >= 16) {
            contentIntent.setPriority(1);
        }
        ((NotificationManager) context.getSystemService("notification")).notify(0, contentIntent.build());
    }

    public final void a(Context context, String str) {
        Intent a = j.a(context);
        int w = s.w();
        if (w == -1 || w == 0) {
            w = this.a.b();
        }
        Builder contentIntent = new Builder(context).setSmallIcon(w).setContentTitle(this.a.a()).setContentText(str).setAutoCancel(true).setSound(RingtoneManager.getDefaultUri(2)).setContentIntent(PendingIntent.getActivity(context, 0, a, 134217728));
        if (!(context instanceof Activity) && VERSION.SDK_INT >= 16) {
            contentIntent.setPriority(1);
        }
        ((NotificationManager) context.getSystemService("notification")).notify(0, contentIntent.build());
    }

    public static void a(Context context) {
        ((NotificationManager) context.getSystemService("notification")).cancel(0);
    }

    private static int a(List<g> list) {
        List<g> arrayList = new ArrayList();
        arrayList.addAll(list);
        int parseInt = Integer.parseInt(((g) list.get(0)).g());
        Collections.sort(arrayList, new g.a(1));
        int i = parseInt;
        int i2 = 1;
        for (g g : arrayList) {
            parseInt = Integer.parseInt(g.g());
            if (parseInt != i) {
                i = i2 + 1;
            } else {
                parseInt = i;
                i = i2;
            }
            i2 = i;
            i = parseInt;
        }
        if (i2 == 1) {
            return 0;
        }
        return 1;
    }

    private String a(Activity activity, int i, String str) {
        switch (i) {
            case 0:
                return str.substring(0, str.indexOf(60)) + "(" + this.a.a() + activity.getString(R.string.instabug_str_notification_title) + ")";
            case 1:
                return this.a.a() + activity.getString(R.string.instabug_str_notification_title);
            default:
                return "";
        }
    }

    private static String a(Context context, int i, List<g> list) {
        switch (i) {
            case 0:
                return ((g) list.get(list.size() - 1)).c();
            case 1:
                Resources resources = context.getResources();
                String[] split = ((g) list.get(list.size() - 1)).f().split(" ");
                return String.format(resources.getString(R.string.instabug_str_notifications_body), new Object[]{Integer.valueOf(list.size()), split[0]});
            default:
                return "";
        }
    }

    public final void b(Context context) {
        if (s.o()) {
            final MediaPlayer create = MediaPlayer.create(context, R.raw.new_message);
            InstabugSDKLogger.v(this, "Created MediaPlayer to play notification sound");
            create.start();
            create.setOnCompletionListener(new OnCompletionListener(this) {
                final /* synthetic */ b b;

                public final void onCompletion(MediaPlayer mediaPlayer) {
                    create.release();
                }
            });
            return;
        }
        InstabugSDKLogger.v(this, "Notification sounds disabled, not playing sounds");
    }

    static /* synthetic */ void a(b bVar, Activity activity, List list, int i) {
        switch (i) {
            case 0:
                g gVar = (g) list.get(list.size() - 1);
                if (activity instanceof InstabugFeedbackActivity) {
                    ((InstabugFeedbackActivity) activity).b(gVar.g());
                    return;
                } else {
                    activity.startActivity(j.a((Context) activity, gVar.f(), gVar.g()));
                    return;
                }
            case 1:
                if (activity instanceof InstabugFeedbackActivity) {
                    ((InstabugFeedbackActivity) activity).a();
                    return;
                } else {
                    activity.startActivity(j.a(activity));
                    return;
                }
            default:
                return;
        }
    }
}
