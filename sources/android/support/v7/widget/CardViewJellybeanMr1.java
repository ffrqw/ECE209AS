package android.support.v7.widget;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

@TargetApi(17)
final class CardViewJellybeanMr1 extends CardViewGingerbread {
    CardViewJellybeanMr1() {
    }

    public final void initStatic() {
        RoundRectDrawableWithShadow.sRoundRectHelper = new RoundRectHelper() {
            public final void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius, Paint paint) {
                canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint);
            }
        };
    }
}
