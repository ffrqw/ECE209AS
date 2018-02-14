package com.instabug.library;

import android.graphics.BitmapFactory;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.instabug.library.Feature.State;
import com.instabug.library.IBGCustomTextPlaceHolder.Key;
import com.instabug.library.internal.d.a.f;
import com.instabug.library.util.InstabugSDKLogger;
import com.rachio.iro.R;

public final class l extends h implements OnClickListener, a {
    private ImageView a;

    public interface a {
        void b();

        void c();

        void d();
    }

    public final void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        view.findViewById(R.id.instabug_option_report_bug).setOnClickListener(this);
        view.findViewById(R.id.instabug_option_send_feedback).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.instabug_invocation_screen_title)).setText(com.instabug.library.util.l.a(Key.INVOCATION_HEADER, getString(R.string.instabug_str_invocation_header)));
        ((TextView) view.findViewById(R.id.instabug_option_report_bug_text)).setText(com.instabug.library.util.l.a(Key.REPORT_BUG, getString(R.string.instabug_str_bug_header)));
        ((TextView) view.findViewById(R.id.instabug_option_send_feedback_text)).setText(com.instabug.library.util.l.a(Key.REPORT_FEEDBACK, getString(R.string.instabug_str_feedback_header)));
        this.a = (ImageView) view.findViewById(R.id.instabug_btn_conversations);
        this.a.setOnClickListener(this);
        ImageView imageView = (ImageView) view.findViewById(R.id.instabug_pbi_image);
        byte[] decode = Base64.decode("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".getBytes(), 0);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.length));
        if (Instabug.getColorTheme() == IBGColorTheme.IBGColorThemeLight) {
            imageView.setColorFilter(new PorterDuffColorFilter(-9211021, Mode.SRC_IN));
        }
        TextView textView = (TextView) view.findViewById(R.id.instabug_pbi_text);
        CharSequence charSequence = textView.getText().toString();
        f();
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.WHITE_LABELING) == State.ENABLED) {
            view.findViewById(R.id.divider).setVisibility(8);
            view.findViewById(R.id.instabug_pbi_container).setVisibility(8);
        }
        if (!charSequence.contains("Instabug.com")) {
            charSequence = "Powered by Instabug.com";
        } else if (charSequence.contains("&") || charSequence.contains(",") || charSequence.contains("  ") || charSequence.contains("_") || charSequence.contains("-")) {
            charSequence = "Powered by Instabug.com";
        }
        textView.setText(charSequence);
    }

    protected final void a(Bundle bundle) {
    }

    protected final void b(Bundle bundle) {
    }

    protected final int b() {
        return R.layout.instabug_lyt_invocation;
    }

    protected final String c() {
        return getString(R.string.instabug_str_empty);
    }

    protected final void a() {
    }

    public final void onClick(View view) {
        int id = view.getId();
        if (id == R.id.instabug_option_report_bug) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            ((a) getActivity()).b();
        } else if (id == R.id.instabug_option_send_feedback) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            ((a) getActivity()).c();
        } else if (id == R.id.instabug_btn_conversations) {
            ((a) getActivity()).d();
        }
    }

    public final void a(boolean z) {
        InstabugSDKLogger.d(this, "Is visible " + z);
        if (z) {
            f();
        }
    }

    private void f() {
        Instabug.getSettingsBundle();
        if (s.f() == 0 || f.a().b().size() == 0 || InstabugFeaturesManager.getInstance().getFeatureState(Feature.IN_APP_MESSAGING) == State.DISABLED) {
            this.a.setVisibility(8);
            return;
        }
        int f = f.f();
        InstabugSDKLogger.v(this, "Unread count is " + f);
        if (f != 0) {
            this.a.setImageResource(com.instabug.library.util.a.a(getActivity(), R.attr.instabug_new_messages_icon));
        } else {
            this.a.setImageResource(com.instabug.library.util.a.a(getActivity(), R.attr.instabug_messages_icon));
        }
    }
}
