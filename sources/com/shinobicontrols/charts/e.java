package com.shinobicontrols.charts;

import android.graphics.Typeface;
import android.widget.TextView;

abstract class e {
    static e G = new e() {
        final void b(Annotation annotation) {
            AnnotationStyle style = annotation.getStyle();
            if (style != null) {
                TextView textView = (TextView) annotation.getView();
                textView.setBackgroundColor(((Integer) style.F.sU).intValue());
                textView.setTextColor(((Integer) style.C.sU).intValue());
                textView.setTextSize(((Float) style.D.sU).floatValue());
                textView.setTypeface((Typeface) style.E.sU);
            }
        }
    };
    static e H = new e() {
        final void b(Annotation annotation) {
            AnnotationStyle style = annotation.getStyle();
            if (style != null) {
                annotation.getView().setBackgroundColor(((Integer) style.F.sU).intValue());
            }
        }
    };
    static e I = new e() {
        final void b(Annotation annotation) {
        }
    };

    abstract void b(Annotation annotation);

    e() {
    }
}
