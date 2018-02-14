package com.instabug.library.c;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import com.instabug.library.IBGColorTheme;
import com.instabug.library.Instabug;
import com.instabug.library.model.d;
import com.rachio.iro.R;

public final class b {
    private static b e;
    private a a;
    private d b;
    private View c;
    private boolean d = false;

    public interface a {
        void a(d dVar);
    }

    public static b a(a aVar) {
        if (e == null) {
            e = new b(aVar);
        }
        return e;
    }

    private b(a aVar) {
        this.a = aVar;
    }

    public final void a() {
        if (this.d && this.c != null && this.c.getParent() != null && (this.c.getParent() instanceof ViewGroup)) {
            ((ViewGroup) this.c.getParent()).removeView(this.c);
            this.d = false;
        }
    }

    public final void a(Activity activity, d dVar) {
        this.b = dVar;
        if (this.d) {
            a();
        }
        this.c = ((LayoutInflater) activity.getSystemService("layout_inflater")).inflate(R.layout.instabug_floating_bar_take_screenshot, null);
        ImageButton imageButton = (ImageButton) this.c.findViewById(R.id.captureImageButton);
        imageButton.setOnClickListener(new OnClickListener(this) {
            final /* synthetic */ b a;

            {
                this.a = r1;
            }

            public final void onClick(View view) {
                this.a.a();
                this.a.a.a(this.a.b);
            }
        });
        if (Instabug.getColorTheme().equals(IBGColorTheme.IBGColorThemeDark)) {
            imageButton.setImageResource(R.drawable.instabug_ic_capture_screenshot_dark);
        } else {
            imageButton.setImageResource(R.drawable.instabug_ic_capture_screenshot_light);
        }
        activity.getWindow().addContentView(this.c, new LayoutParams(-1, -2, 80));
        this.d = true;
    }
}
