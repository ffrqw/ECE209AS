package com.instabug.library.util;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.instabug.library.internal.d.a.b;
import com.instabug.library.model.h;
import com.instabug.library.view.CircularImageView;
import com.rachio.iro.R;
import java.io.FileInputStream;

public final class i {
    private static i e;
    private View a;
    private LinearLayout b;
    private float c;
    private boolean d = false;

    public interface a {
        void a(boolean z);

        void onClick();
    }

    public static i a() {
        if (e == null) {
            e = new i();
        }
        return e;
    }

    private i() {
    }

    public final void a(final Activity activity, h hVar, final a aVar) {
        Animation loadAnimation = AnimationUtils.loadAnimation(activity, R.anim.notification_open_anim);
        if (this.d) {
            a(null, aVar, true);
        }
        this.a = ((LayoutInflater) activity.getSystemService("layout_inflater")).inflate(R.layout.instabug_lyt_notification, null);
        this.b = (LinearLayout) this.a.findViewById(R.id.notificationLinearLayout);
        this.b.setOnClickListener(new OnClickListener(this) {
            final /* synthetic */ i c;

            public final void onClick(View view) {
                InstabugSDKLogger.d(this, " onClick");
                this.c.a(AnimationUtils.loadAnimation(activity, R.anim.notification_close_anim), aVar, true);
                aVar.onClick();
            }
        });
        this.b.setOnTouchListener(new OnTouchListener(this) {
            final /* synthetic */ i c;

            public final boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()) {
                    case 0:
                        this.c.c = motionEvent.getX();
                        InstabugSDKLogger.d(this.c, "Action was DOWN");
                        break;
                    case 1:
                        float x = motionEvent.getX();
                        InstabugSDKLogger.d(this.c, "Action was UP, started at " + this.c.c + " ended at " + x + " THRESHOLD is 150");
                        if (this.c.c < x && Math.abs(this.c.c - x) > 150.0f) {
                            InstabugSDKLogger.d(this.c, "Left to Right swipe performed");
                            this.c.a(AnimationUtils.loadAnimation(activity, R.anim.notification_swipe_right_anim), aVar, true);
                            return true;
                        } else if (this.c.c > x && Math.abs(this.c.c - x) > 150.0f) {
                            InstabugSDKLogger.d(this.c, "Right to Left swipe performed");
                            this.c.a(AnimationUtils.loadAnimation(activity, R.anim.notification_swipe_left_anim), aVar, true);
                            return true;
                        }
                        break;
                    case 2:
                        InstabugSDKLogger.d(this.c, "Action was MOVE");
                        break;
                }
                return false;
            }
        });
        final CircularImageView circularImageView = (CircularImageView) this.a.findViewById(R.id.senderAvatarImageView);
        circularImageView.setBackgroundResource(R.drawable.instabug_ic_avatar);
        b.a(activity, b.a$5bd48f4a(activity, hVar.c(), com.instabug.library.model.a.a.a$6ed2d6d3), new b.b(this) {
            final /* synthetic */ i b;

            public final void a(com.instabug.library.model.a aVar) {
                InstabugSDKLogger.d(this, "Asset Entity downloaded: " + aVar.c().getPath());
                try {
                    circularImageView.setBackgroundResource(0);
                    circularImageView.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(aVar.c())));
                } catch (Throwable e) {
                    InstabugSDKLogger.e(this, "Asset Entity downloading got FileNotFoundException error", e);
                }
            }

            public final void a(Throwable th) {
                InstabugSDKLogger.e(this, "Asset Entity downloading got error", th);
            }
        });
        ((TextView) this.a.findViewById(R.id.senderNameTextView)).setText(hVar.b());
        ((TextView) this.a.findViewById(R.id.messageTextView)).setText(hVar.a());
        activity.getWindow().addContentView(this.a, new LayoutParams(-1, -2));
        this.d = true;
        if (loadAnimation != null) {
            this.a.startAnimation(loadAnimation);
        }
        new Handler().postDelayed(new Runnable(this) {
            final /* synthetic */ i c;

            public final void run() {
                this.c.a(AnimationUtils.loadAnimation(activity, R.anim.notification_close_anim), aVar, false);
            }
        }, 5000);
    }

    private void a(Animation animation, a aVar, boolean z) {
        if (this.d && this.a != null && this.a.getParent() != null && (this.a.getParent() instanceof ViewGroup)) {
            this.b.setOnClickListener(null);
            this.b.setOnTouchListener(null);
            ViewGroup viewGroup = (ViewGroup) this.a.getParent();
            if (animation != null) {
                this.a.startAnimation(animation);
            }
            viewGroup.removeView(this.a);
            this.d = false;
            aVar.a(z);
        }
    }
}
