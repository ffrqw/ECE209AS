package com.instabug.library;

import android.app.Activity;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.instabug.library.IBGCustomTextPlaceHolder.Key;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.k;
import com.instabug.library.util.l;
import com.rachio.iro.R;
import java.util.Timer;
import java.util.TimerTask;

public final class f extends Fragment implements OnTouchListener {
    private ImageView a;
    private boolean b = false;
    private ImageView c;
    private PorterDuffColorFilter d;
    private Timer e;
    private com.instabug.library.internal.b.b f;
    private int g;
    private TimerTask h = new a(this);
    private TextView i;
    private TextView j;
    private boolean k;
    private boolean l = false;
    private b m;

    public interface b {
        void a(String str);
    }

    class a extends TimerTask {
        final /* synthetic */ f a;

        a(f fVar) {
            this.a = fVar;
        }

        public final void run() {
            this.a.getActivity().runOnUiThread(new Runnable(this) {
                final /* synthetic */ a a;

                {
                    this.a = r1;
                }

                public final void run() {
                    if (this.a.a.g >= 50) {
                        this.a.a.i.setTextColor(-65536);
                    } else {
                        TextView d = this.a.a.i;
                        Instabug.getSettingsBundle();
                        d.setTextColor(s.a().j());
                    }
                    if (this.a.a.g == 60) {
                        this.a.a.b();
                    } else {
                        this.a.a.i.setText(String.format("00:%02d", new Object[]{Integer.valueOf(this.a.a.g)}));
                    }
                    this.a.a.g = this.a.a.g + 1;
                }
            });
        }
    }

    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.k = false;
        this.e = new Timer();
        this.f = new com.instabug.library.internal.b.b(getArguments().getString("com.instabug.library.audio_attachment_path"));
        View inflate = layoutInflater.inflate(R.layout.instabug_lyt_record_audio, viewGroup, false);
        inflate.findViewById(R.id.instabug_btn_record_audio).setOnTouchListener(this);
        this.a = (ImageView) inflate.findViewById(R.id.instabug_bk_record_audio);
        this.c = (ImageView) inflate.findViewById(R.id.instabug_img_record_audio);
        Instabug.getSettingsBundle();
        this.d = new PorterDuffColorFilter(s.a().j(), Mode.SRC_IN);
        this.a.setColorFilter(this.d);
        this.c.setColorFilter(this.d);
        this.i = (TextView) inflate.findViewById(R.id.instabug_txt_timer);
        this.i.setText(String.format("00:%02d", new Object[]{Integer.valueOf(0)}));
        this.j = (TextView) inflate.findViewById(R.id.instabug_txt_recording_title);
        this.j.setText(l.a(Key.VOICE_MESSAGE_PRESS_AND_HOLD_TO_RECORD, getString(R.string.instabug_str_hold_to_record)));
        return inflate;
    }

    public final void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        a();
    }

    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.m = (b) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement InstabugAudioRecordingFragment.Callbacks");
        }
    }

    public final void onDetach() {
        super.onDetach();
        this.m = null;
    }

    private void a() {
        k.a((Fragment) this, "android.permission.RECORD_AUDIO", 1, new Runnable(this) {
            final /* synthetic */ f a;

            {
                this.a = r1;
            }

            public final void run() {
                if (this.a.l) {
                    InstabugSDKLogger.d(this.a, "Shouldn't try to explain why get this permission, either first time or never again selected OR permission not in manifest");
                    Toast.makeText(this.a.getContext(), this.a.f(), 0).show();
                    Instabug.setShouldAudioRecordingOptionAppear(false);
                    return;
                }
                this.a.l = true;
            }
        }, new Runnable(this) {
            final /* synthetic */ f a;

            {
                this.a = r1;
            }

            public final void run() {
                InstabugSDKLogger.d(this.a, "Audio recording permission already granted before");
                this.a.k = true;
            }
        });
    }

    public final void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        switch (i) {
            case 1:
                if (iArr[0] == 0) {
                    InstabugSDKLogger.d(this, "Audio recording permission granted by user");
                    this.k = true;
                    return;
                }
                InstabugSDKLogger.d(this, "Audio recording permission denied by user");
                this.k = false;
                Toast.makeText(getContext(), f(), 0).show();
                return;
            default:
                super.onRequestPermissionsResult(i, strArr, iArr);
                return;
        }
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                if (!this.b && this.k) {
                    this.g = 0;
                    this.e.scheduleAtFixedRate(this.h, 0, 1000);
                    this.f.a();
                    this.b = true;
                    this.a.setImageResource(R.drawable.instabug_record_default_active);
                    this.c.setColorFilter(null);
                    this.j.setText(l.a(Key.VOICE_MESSAGE_RELEASE_TO_ATTACH, getString(R.string.instabug_str_release_stop_record)));
                }
                if (!this.k) {
                    a();
                    break;
                }
                break;
            case 1:
            case 3:
                if (this.b && this.k && c() && motionEvent.getAction() == 1) {
                    b();
                    break;
                }
        }
        return true;
    }

    private void b() {
        if (this.m != null) {
            this.m.a(getArguments().getString("com.instabug.library.audio_attachment_path"));
        }
    }

    private boolean c() {
        try {
            this.h.cancel();
            this.e.cancel();
            this.h = new a(this);
            this.e = new Timer();
            this.b = false;
            this.a.setImageResource(R.drawable.instabug_record_default_bk);
            this.c.setColorFilter(this.d);
            this.i.setText(String.format("00:%02d", new Object[]{Integer.valueOf(0)}));
            this.j.setText(l.a(Key.VOICE_MESSAGE_PRESS_AND_HOLD_TO_RECORD, getString(R.string.instabug_str_hold_to_record)));
            this.f.b();
            if (this.g > 3) {
                return true;
            }
            return false;
        } catch (Throwable e) {
            if (this.g > 1) {
                Toast.makeText(getActivity(), "Unknown error occurred", 0).show();
            }
            InstabugSDKLogger.e(this, "Error capturing audio stream", e);
            return false;
        }
    }

    public static Fragment a(String str) {
        Fragment fVar = new f();
        Bundle bundle = new Bundle();
        bundle.putString("com.instabug.library.audio_attachment_path", str);
        fVar.setArguments(bundle);
        return fVar;
    }

    private String f() {
        return l.a(Key.AUDIO_RECORDING_PERMISSION_DENIED, getString(R.string.instabug_audio_recorder_permission_denied));
    }
}
