package com.instabug.library;

import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.instabug.library.IBGCustomTextPlaceHolder.Key;
import com.instabug.library.internal.d.a;
import com.instabug.library.internal.d.a.h;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.k;
import com.instabug.library.util.l;
import com.rachio.iro.R;
import java.io.File;

public final class o extends Fragment implements OnClickListener {
    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.instabug_lyt_new_attachment, viewGroup, false);
        ((TextView) inflate.findViewById(R.id.instabug_btn_add_image_text)).setText(l.a(Key.ADD_IMAGE_FROM_GALLERY, getString(R.string.instabug_str_add_photo)));
        ((TextView) inflate.findViewById(R.id.instabug_btn_add_screenshot_text)).setText(l.a(Key.ADD_EXTRA_SCREENSHOT, getString(R.string.instabug_str_take_screenshot)));
        ((TextView) inflate.findViewById(R.id.instabug_btn_add_audio_text)).setText(l.a(Key.ADD_VOICE_MESSAGE, getString(R.string.instabug_str_record_audio)));
        inflate.findViewById(R.id.instabug_btn_add_image).setOnClickListener(this);
        inflate.findViewById(R.id.instabug_btn_take_screenshot).setOnClickListener(this);
        inflate.findViewById(R.id.instabug_btn_add_audio).setOnClickListener(this);
        if (VERSION.SDK_INT < 23 && !k.a(getActivity(), "android.permission.RECORD_AUDIO")) {
            Instabug.setShouldAudioRecordingOptionAppear(false);
        }
        if (!Instabug.shouldAudioRecordingOptionAppear()) {
            inflate.findViewById(R.id.instabug_btn_add_audio).setVisibility(8);
        }
        if (getArguments() != null && getArguments().getBoolean("hide_audio", true)) {
            inflate.findViewById(R.id.instabug_btn_add_audio).setVisibility(8);
        }
        return inflate;
    }

    public final void onClick(View view) {
        int id = view.getId();
        if (id == R.id.instabug_btn_add_image) {
            v.a().a(true);
            k.a((Fragment) this, "android.permission.WRITE_EXTERNAL_STORAGE", 1, new Runnable(this) {
                final /* synthetic */ o a;

                {
                    this.a = r1;
                }

                public final void run() {
                    o.a(this.a);
                }
            }, new Runnable(this) {
                final /* synthetic */ o a;

                {
                    this.a = r1;
                }

                public final void run() {
                    InstabugSDKLogger.i(this.a, "Permission granted");
                    o.a(this.a);
                }
            });
        } else if (id == R.id.instabug_btn_take_screenshot) {
            v.a().a(true);
            h.a(v.a().b());
            Instabug.setSDKState(b.TAKING_SCREENSHOT);
            getActivity().finish();
        } else if (id == R.id.instabug_btn_add_audio) {
            getFragmentManager().popBackStack();
            File file = new File(a.a(getActivity()), "audioMessage_" + String.valueOf(System.currentTimeMillis()) + ".mp4");
            FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
            getActivity().findViewById(R.id.instabug_fragment_blackout).setVisibility(0);
            beginTransaction.setCustomAnimations(R.anim.instabug_anim_options_sheet_enter, R.anim.instabug_anim_options_sheet_exit).add(R.id.instabug_bottomsheet_container, f.a(file.getAbsolutePath()), "record_audio").addToBackStack("Record Audio").commit();
        }
    }

    public final void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        switch (i) {
            case 161:
                if (i2 == -1 && intent != null) {
                    v.a().a(getActivity(), Uri.fromFile(new File(a.a(getActivity(), intent.getData()))));
                    getActivity().onBackPressed();
                } else if (i2 == 0) {
                    getActivity().onBackPressed();
                }
                v.a().a(false);
                return;
            default:
                return;
        }
    }

    static /* synthetic */ void a(o oVar) {
        Intent intent = new Intent("android.intent.action.PICK");
        intent.putExtra("android.intent.extra.LOCAL_ONLY", true);
        intent.setType("image/*");
        oVar.startActivityForResult(intent, 161);
    }
}
