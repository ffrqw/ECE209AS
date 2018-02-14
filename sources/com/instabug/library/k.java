package com.instabug.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.instabug.library.Feature.State;
import com.instabug.library.IBGCustomTextPlaceHolder.Key;
import com.instabug.library.OnSdkDismissedCallback.IssueState;
import com.instabug.library.internal.b.a;
import com.instabug.library.internal.d.a.h;
import com.instabug.library.model.IssueType;
import com.instabug.library.model.d;
import com.instabug.library.model.e;
import com.instabug.library.model.f;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.b;
import com.instabug.library.util.c;
import com.instabug.library.util.l;
import com.rachio.iro.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public final class k extends h implements OnCompletionListener, TextWatcher, OnClickListener, OnFocusChangeListener {
    private EditText a;
    private EditText b;
    private LinearLayout c;
    private OnGlobalLayoutListener d;
    private a e;
    private ColorFilter f;
    private ImageView g;
    private int h;
    private PorterDuffColorFilter i;
    private String j;
    private String k;
    private IssueType l;
    private BroadcastReceiver m = new BroadcastReceiver(this) {
        final /* synthetic */ k a;

        {
            this.a = r1;
        }

        public final void onReceive(Context context, Intent intent) {
            InstabugSDKLogger.i(this, "Refreshing Attachments");
            this.a.i();
        }
    };

    public static Fragment a(IssueType issueType, String str, String str2) {
        Fragment kVar = new k();
        Bundle bundle = new Bundle();
        bundle.putSerializable("issue.type", issueType);
        bundle.putString("issue.message", str);
        bundle.putString("issue.message.hint", str2);
        kVar.setArguments(bundle);
        return kVar;
    }

    protected final void a() {
        this.l = (IssueType) getArguments().getSerializable("issue.type");
        this.j = getArguments().getString("issue.message");
        this.k = getArguments().getString("issue.message.hint");
    }

    public final void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ImageView imageView = (ImageButton) view.findViewById(R.id.instabug_btn_done);
        imageView.setOnClickListener(this);
        Instabug.getSettingsBundle();
        this.h = s.a().j();
        this.f = new PorterDuffColorFilter(this.h, Mode.SRC_IN);
        this.i = new PorterDuffColorFilter(-858993460, Mode.SRC_IN);
        c.a(imageView);
        this.c = (LinearLayout) view.findViewById(R.id.instabug_lyt_attachments_container);
        this.a = (EditText) view.findViewById(R.id.instabug_edtxt_email);
        this.a.setOnFocusChangeListener(this);
        this.a.addTextChangedListener(this);
        this.b = (EditText) view.findViewById(R.id.instabug_edtxt_message);
        this.b.setOnFocusChangeListener(this);
        this.b.addTextChangedListener(this);
        if (VERSION.SDK_INT < 11) {
            this.b.setBackgroundResource(R.drawable.instabug_edit_text_background);
            this.a.setBackgroundResource(R.drawable.instabug_edit_text_background);
        }
        Instabug.getSettingsBundle();
        if (!s.r()) {
            this.a.setVisibility(8);
            this.b.setGravity(16);
        }
        if (this.k != null) {
            this.b.setHint(this.k);
        }
        if (this.j != null) {
            this.b.setText(this.j);
        }
        this.a.setHint(l.a(Key.EMAIL_FIELD_HINT, getString(R.string.instabug_str_email_hint)));
        Instabug.getSettingsBundle();
        if (s.c() != null) {
            Instabug.getSettingsBundle();
            if (!s.c().equals("")) {
                EditText editText = this.a;
                Instabug.getSettingsBundle();
                editText.setText(s.c());
            }
        }
        this.d = new OnGlobalLayoutListener(this) {
            final /* synthetic */ k a;

            {
                this.a = r1;
            }

            public final void onGlobalLayout() {
                this.a.i();
                this.a.c.getViewTreeObserver().removeGlobalOnLayoutListener(this.a.d);
            }
        };
        this.c.getViewTreeObserver().addOnGlobalLayoutListener(this.d);
    }

    protected final void a(Bundle bundle) {
    }

    protected final void b(Bundle bundle) {
    }

    protected final String c() {
        if (this.l == IssueType.BUG) {
            return l.a(Key.BUG_REPORT_HEADER, getString(R.string.instabug_str_bug_header));
        }
        return l.a(Key.FEEDBACK_REPORT_HEADER, getString(R.string.instabug_str_feedback_header));
    }

    protected final int b() {
        return R.layout.instabug_lyt_feedback;
    }

    public final void onClick(View view) {
        int id = view.getId();
        e eVar;
        if (id == R.id.instabug_btn_done) {
            if (Instabug.isCommentFieldRequired() && (v.a().b().g() == null || v.a().b().g().trim().length() == 0)) {
                Toast.makeText(getActivity(), l.a(Key.INVALID_COMMENT_MESSAGE, getString(R.string.instabug_err_invalid_comment)), 0).show();
                return;
            }
            Instabug.getSettingsBundle();
            if (!s.t() || (v.a().b().f() != null && Patterns.EMAIL_ADDRESS.matcher(v.a().b().f()).matches())) {
                if (!(q.a().h() == null || q.a().i() == null)) {
                    for (e eVar2 : v.a().b().b()) {
                        if (eVar2.d().equalsIgnoreCase(q.a().i())) {
                            id = 1;
                            break;
                        }
                    }
                    boolean z = false;
                    if (id == 0) {
                        v.a().a(getActivity(), q.a().h(), e.a.ATTACHMENT_FILE, q.a().i());
                        InstabugSDKLogger.d(this, "External Attachment added");
                    }
                    q.a().a(null);
                    q.a().a(null);
                }
                com.instabug.library.internal.module.a aVar = new com.instabug.library.internal.module.a();
                Instabug.getSettingsBundle();
                s.b(v.a().b().f());
                f a = f.a(Instabug.getSettingsBundle(), Instabug.iG().b());
                Instabug.getSettingsBundle();
                if (q.a().b() != null) {
                    try {
                        Instabug.getSettingsBundle();
                        q.a().b().run();
                    } catch (Throwable e) {
                        InstabugSDKLogger.e(this, "Pre sending runnable failed to run.", e);
                    }
                }
                d b = v.a().b();
                Instabug.getSettingsBundle();
                q.a();
                a.a(b, m.a(), com.instabug.library.internal.module.a.a(getActivity()));
                h.b(v.a().b());
                getActivity().startService(new Intent(getActivity(), InstabugIssueUploaderService.class));
                v.a().a(IssueState.SUBMITTED);
                a(false, R.id.instabug_fragment_container);
                getFragmentManager().popBackStack(null, 1);
                getFragmentManager().beginTransaction().replace(R.id.instabug_fragment_container, new p()).commit();
                return;
            }
            Toast.makeText(getActivity(), l.a(Key.INVALID_EMAIL_MESSAGE, getString(R.string.instabug_err_invalid_email)), 0).show();
        } else if (id == R.id.instabug_btn_add_attachment) {
            FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
            getActivity().findViewById(R.id.instabug_fragment_blackout).setVisibility(0);
            a(false, R.id.instabug_fragment_container);
            beginTransaction = beginTransaction.setCustomAnimations(R.anim.instabug_anim_options_sheet_enter, R.anim.instabug_anim_options_sheet_exit);
            boolean k = k();
            r3 = new o();
            r4 = new Bundle();
            r4.putBoolean("hide_audio", k);
            r3.setArguments(r4);
            beginTransaction.add(R.id.instabug_bottomsheet_container, r3, "sheet").addToBackStack("Add attachment").commit();
        } else if (id == R.id.instabug_img_attachment) {
            eVar2 = (e) view.getTag();
            String c = c();
            a(false, R.id.instabug_fragment_container);
            FragmentTransaction beginTransaction2 = getFragmentManager().beginTransaction();
            Parcelable fromFile = Uri.fromFile(new File(eVar2.e()));
            r3 = new e();
            r4 = new Bundle();
            r4.putParcelable("image", fromFile);
            r4.putString("title", c);
            r3.setArguments(r4);
            beginTransaction2.add(R.id.instabug_fragment_container, r3, "annotation").addToBackStack("Draw Your Bug").commit();
        } else if (id == R.id.instabug_img_audio_attachment) {
            if (this.e != null) {
                j();
                return;
            }
            eVar2 = (e) view.getTag();
            this.g.setImageResource(R.drawable.instabug_ic_stop);
            this.e = new a(eVar2.e());
            this.e.c();
            this.e.a(this);
        } else if (id == R.id.instabug_btn_remove_attachment) {
            eVar2 = (e) view.getTag();
            if (this.e != null) {
                j();
            }
            v.a().b().b().remove(eVar2);
            new File(eVar2.e()).delete();
            i();
        }
    }

    private void j() {
        this.e.d();
        if (this.g != null) {
            this.g.setImageResource(R.drawable.instabug_ic_play);
        }
        this.e = null;
    }

    public final void onPause() {
        if (this.e != null) {
            this.e.d();
        }
        super.onPause();
    }

    public final void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public final void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public final void afterTextChanged(Editable editable) {
        String obj = this.a.getText().toString();
        if (com.instabug.library.util.e.a(obj)) {
            Instabug.getSettingsBundle();
            s.b(obj);
        }
        if (getActivity() != null) {
            v.a().b().b(obj);
            v.a().b().c(this.b.getText().toString());
        }
    }

    public final void onCompletion(MediaPlayer mediaPlayer) {
        j();
    }

    public final void onFocusChange(View view, boolean z) {
        if (z) {
            view.getBackground().setColorFilter(this.f);
        } else {
            view.getBackground().setColorFilter(this.i);
        }
    }

    final void i() {
        List b = v.a().b().b();
        this.g = null;
        LayoutParams layoutParams = (LayoutParams) this.c.getLayoutParams();
        layoutParams.height = (this.c.getMeasuredWidth() * getResources().getDisplayMetrics().heightPixels) / (getResources().getDisplayMetrics().widthPixels << 2);
        this.c.setLayoutParams(layoutParams);
        this.c.removeAllViews();
        LayoutInflater from = LayoutInflater.from(getActivity());
        ViewGroup.LayoutParams layoutParams2 = new LayoutParams(0, -1, 1.0f);
        int ceil = (int) Math.ceil((double) (2.0f * getResources().getDisplayMetrics().density));
        layoutParams2.setMargins(ceil, ceil, ceil, ceil);
        for (int i = 0; i < 4; i++) {
            if (i < b.size()) {
                e eVar = (e) b.get(i);
                try {
                    if (!e.a.ATTACHMENT_FILE.equals(eVar.b())) {
                        View inflate;
                        ImageView imageView;
                        if (e.a.AUDIO.equals(eVar.b())) {
                            inflate = from.inflate(R.layout.instabug_lyt_attachment_audio, this.c, false);
                            this.c.addView(inflate, layoutParams2);
                            imageView = (ImageView) inflate.findViewById(R.id.instabug_img_audio_attachment);
                            imageView.setTag(eVar);
                            imageView.setOnClickListener(this);
                            inflate.findViewById(R.id.instabug_btn_remove_attachment).setTag(eVar);
                            inflate.findViewById(R.id.instabug_btn_remove_attachment).setOnClickListener(this);
                            this.g = (ImageView) inflate.findViewById(R.id.instabug_btn_play_attachment);
                            this.g.setColorFilter(this.f);
                            this.g.getBackground().setColorFilter(this.f);
                            a aVar = new a(eVar.e());
                            int b2 = aVar.b();
                            aVar.d();
                            TextView textView = (TextView) inflate.findViewById(R.id.instabug_txt_attachment_length);
                            textView.setTextColor(this.h);
                            InstabugSDKLogger.d(this, "Audio length is " + b2 + " rounding would be " + Math.round(((float) b2) / 1000.0f));
                            textView.setText(String.format("00:%02d", new Object[]{Integer.valueOf(Math.round(((float) b2) / 1000.0f))}));
                        } else {
                            eVar = (e) b.get(i);
                            inflate = from.inflate(R.layout.instabug_lyt_attachment_image, this.c, false);
                            this.c.addView(inflate, layoutParams2);
                            imageView = (ImageView) inflate.findViewById(R.id.instabug_img_attachment);
                            imageView.setImageBitmap(b.a(getActivity().getContentResolver(), Uri.fromFile(new File(eVar.e()))));
                            imageView.setTag(eVar);
                            imageView.setOnClickListener(this);
                            inflate.findViewById(R.id.instabug_btn_remove_attachment).setTag(eVar);
                            inflate.findViewById(R.id.instabug_btn_remove_attachment).setOnClickListener(this);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.MULTIPLE_ATTACHMENTS) == State.ENABLED) {
                View inflate2 = from.inflate(R.layout.instabug_lyt_attachment_add, this.c, false);
                this.c.addView(inflate2, layoutParams2);
                inflate2.setOnClickListener(this);
                return;
            }
        }
    }

    private static boolean k() {
        for (e b : v.a().b().b()) {
            if (b.b().equals(e.a.AUDIO)) {
                return true;
            }
        }
        return false;
    }

    public final void onResume() {
        super.onResume();
        i();
    }

    public final void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.m, new IntentFilter("refresh.attachments"));
    }

    public final void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this.m);
    }

    private void a(boolean z, int i) {
        if (getFragmentManager().findFragmentById(i) instanceof a) {
            ((a) getFragmentManager().findFragmentById(i)).a(false);
        }
    }
}
