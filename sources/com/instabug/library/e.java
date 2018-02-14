package com.instabug.library;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.instabug.library.IBGCustomTextPlaceHolder.Key;
import com.instabug.library.util.b;
import com.instabug.library.util.c;
import com.instabug.library.util.l;
import com.instabug.library.view.AnnotationView;
import com.rachio.iro.R;
import java.io.FileNotFoundException;

public final class e extends h implements OnClickListener {
    FrameLayout a;
    AnnotationView b;
    Uri c;
    String d;

    public interface a {
        void a(Uri uri, Bitmap bitmap);
    }

    public static e a(Uri uri) {
        e eVar = new e();
        Bundle bundle = new Bundle();
        bundle.putParcelable("image", uri);
        eVar.setArguments(bundle);
        return eVar;
    }

    protected final void a() {
        this.c = (Uri) getArguments().getParcelable("image");
        this.d = getArguments().getString("title");
    }

    protected final int b() {
        return R.layout.instabug_lyt_annotation;
    }

    protected final String c() {
        return l.a(Key.BUG_REPORT_HEADER, getString(R.string.instabug_str_bug_header));
    }

    public final void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (this.d != null) {
            a(this.d);
        }
        this.a = (FrameLayout) view.findViewById(R.id.instabug_image_container);
        this.b = (AnnotationView) view.findViewById(R.id.instabug_image);
        view.findViewById(R.id.instabug_btn_clear_annotation).setOnClickListener(this);
        view.findViewById(R.id.instabug_btn_pick_color_blue).setOnClickListener(this);
        view.findViewById(R.id.instabug_btn_pick_color_red).setOnClickListener(this);
        view.findViewById(R.id.instabug_btn_pick_color_yellow).setOnClickListener(this);
        view.findViewById(R.id.instabug_btn_pick_color_gray).setOnClickListener(this);
        view.findViewById(R.id.instabug_btn_pick_color_green).setOnClickListener(this);
        ImageView imageView = (ImageButton) view.findViewById(R.id.instabug_btn_done);
        imageView.setImageResource(R.drawable.instabug_ic_next);
        imageView.setOnClickListener(this);
        c.a(imageView);
        if (!d()) {
            try {
                this.b.setImageBitmap(b.a(getActivity().getContentResolver(), (Uri) getArguments().getParcelable("image")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    protected final void a(Bundle bundle) {
        this.b.buildDrawingCache();
        bundle.putParcelable("image", this.b.getDrawingCache());
    }

    protected final void b(Bundle bundle) {
        this.b.setImageBitmap((Bitmap) bundle.getParcelable("image"));
    }

    public final void onClick(View view) {
        int id = view.getId();
        if (id == R.id.instabug_btn_clear_annotation) {
            this.b.a();
        } else if (id == R.id.instabug_btn_done) {
            Uri uri = (Uri) getArguments().getParcelable("image");
            this.b.buildDrawingCache();
            ((a) getActivity()).a(uri, this.b.getDrawingCache());
        } else {
            ((FrameLayout) getView().findViewById(R.id.instabug_btn_pick_color_red_frame)).setForeground(null);
            ((FrameLayout) getView().findViewById(R.id.instabug_btn_pick_color_blue_frame)).setForeground(null);
            ((FrameLayout) getView().findViewById(R.id.instabug_btn_pick_color_yellow_frame)).setForeground(null);
            ((FrameLayout) getView().findViewById(R.id.instabug_btn_pick_color_gray_frame)).setForeground(null);
            ((FrameLayout) getView().findViewById(R.id.instabug_btn_pick_color_green_frame)).setForeground(null);
            if (id == R.id.instabug_btn_pick_color_blue) {
                ((FrameLayout) getView().findViewById(R.id.instabug_btn_pick_color_blue_frame)).setForeground(getResources().getDrawable(R.drawable.instabug_ic_check));
                this.b.a(R.color.instabug_annotation_color_blue);
            } else if (id == R.id.instabug_btn_pick_color_red) {
                ((FrameLayout) getView().findViewById(R.id.instabug_btn_pick_color_red_frame)).setForeground(getResources().getDrawable(R.drawable.instabug_ic_check));
                this.b.a(R.color.instabug_annotation_color_red);
            } else if (id == R.id.instabug_btn_pick_color_gray) {
                ((FrameLayout) getView().findViewById(R.id.instabug_btn_pick_color_gray_frame)).setForeground(getResources().getDrawable(R.drawable.instabug_ic_check));
                this.b.a(R.color.instabug_annotation_color_gray);
            } else if (id == R.id.instabug_btn_pick_color_yellow) {
                ((FrameLayout) getView().findViewById(R.id.instabug_btn_pick_color_yellow_frame)).setForeground(getResources().getDrawable(R.drawable.instabug_ic_check));
                this.b.a(R.color.instabug_annotation_color_yellow);
            } else if (id == R.id.instabug_btn_pick_color_green) {
                ((FrameLayout) getView().findViewById(R.id.instabug_btn_pick_color_green_frame)).setForeground(getResources().getDrawable(R.drawable.instabug_ic_check));
                this.b.a(R.color.instabug_annotation_color_green);
            }
        }
    }
}
