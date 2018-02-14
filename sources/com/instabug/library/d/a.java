package com.instabug.library.d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.instabug.library.internal.d.a.b;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.view.ScaleImageView;
import com.rachio.iro.R;
import java.io.FileInputStream;

public final class a extends Fragment {
    private String a;
    private ProgressBar b;
    private ScaleImageView c;
    private float d;
    private float e;

    public static a a(String str) {
        a aVar = new a();
        Bundle bundle = new Bundle();
        bundle.putString("img_url", str);
        aVar.setArguments(bundle);
        return aVar;
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.a = getArguments().getString("img_url");
        } else if (bundle != null) {
            this.a = bundle.getString("img_url");
        }
    }

    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("img_url", this.a);
    }

    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.instabug_lyt_attachment, viewGroup, false);
        this.b = (ProgressBar) inflate.findViewById(R.id.instabug_attachment_progress_bar);
        this.c = (ScaleImageView) inflate.findViewById(R.id.instabug_img_attachment);
        return inflate;
    }

    public final void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.d = (float) (displayMetrics.widthPixels - ((int) a(24.0f, getActivity())));
        this.e = (float) (displayMetrics.heightPixels - ((int) a(24.0f, getActivity())));
        b.a(getActivity(), b.a$5bd48f4a(getActivity(), this.a, com.instabug.library.model.a.a.a$6ed2d6d3), new b.b(this) {
            final /* synthetic */ a a;

            {
                this.a = r1;
            }

            public final void a(com.instabug.library.model.a aVar) {
                InstabugSDKLogger.d(this, "Asset Entity downloaded: " + aVar.c().getPath());
                try {
                    this.a.c.setImageBitmap(a.a(BitmapFactory.decodeStream(new FileInputStream(aVar.c())), this.a.d, this.a.e));
                    if (this.a.b.getVisibility() == 0) {
                        this.a.b.setVisibility(8);
                    }
                } catch (Throwable e) {
                    InstabugSDKLogger.e(this, "Asset Entity downloading got FileNotFoundException error", e);
                }
            }

            public final void a(Throwable th) {
                InstabugSDKLogger.e(this, "Asset Entity downloading got error", th);
            }
        });
    }

    public static Bitmap a(Bitmap bitmap, float f, float f2) {
        Bitmap createBitmap = Bitmap.createBitmap((int) f, (int) f2, Config.ARGB_8888);
        if (bitmap.getWidth() < bitmap.getHeight() && f > f2) {
            return bitmap;
        }
        if (bitmap.getWidth() > bitmap.getHeight() && f < f2) {
            return bitmap;
        }
        Canvas canvas = new Canvas(createBitmap);
        Matrix matrix = new Matrix();
        if (bitmap.getWidth() < bitmap.getHeight()) {
            matrix.setScale(f / ((float) bitmap.getWidth()), f2 / ((float) bitmap.getHeight()));
        } else {
            matrix.setScale(f2 / ((float) bitmap.getHeight()), f / ((float) bitmap.getWidth()));
        }
        canvas.drawBitmap(bitmap, matrix, new Paint());
        return createBitmap;
    }

    private static float a(float f, Context context) {
        return (((float) context.getResources().getDisplayMetrics().densityDpi) / 160.0f) * 24.0f;
    }
}
