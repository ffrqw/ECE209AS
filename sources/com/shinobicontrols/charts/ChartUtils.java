package com.shinobicontrols.charts;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import com.shinobicontrols.charts.Axis.Position;

public final class ChartUtils {
    private final by aD = new by();
    private final PointF ft = new PointF();
    private final Rect fu = new Rect();

    ChartUtils() {
    }

    public static void drawText(Canvas canvas, String labelText, int x, int y, Paint labelPaint) {
        int descent = (int) ((labelPaint.descent() + labelPaint.ascent()) / 2.0f);
        if (labelText != null) {
            String[] split = labelText.split("\n");
            if (split.length > 1) {
                y = (int) (((float) y) - ((labelPaint.getFontSpacing() / 2.0f) * ((float) (split.length - 1))));
            }
            for (String drawText : split) {
                canvas.drawText(drawText, (float) x, (float) (y - descent), labelPaint);
                y = (int) (((float) y) + labelPaint.getFontSpacing());
            }
        }
    }

    public static void drawTextBackground(Canvas canvas, Rect backgroundLabelRect, Paint backgroundLabelPaint) {
        canvas.drawRect(backgroundLabelRect, backgroundLabelPaint);
    }

    final Rect a(int i, int i2, String str, float f, Typeface typeface, af afVar) {
        if (str == null) {
            this.fu.set(0, 0, 0, 0);
            return this.fu;
        }
        this.aD.a(this.ft, str, f, typeface, afVar);
        this.fu.set(0, 0, (int) (this.ft.x + 5.0f), (int) this.ft.y);
        this.fu.offset(i - (this.fu.width() / 2), i2 - (this.fu.height() / 2));
        String[] split = str.split("\n");
        if (split.length > 1) {
            int height = this.fu.height() / 2;
            for (int i3 = 1; i3 < split.length; i3++) {
                Rect rect = this.fu;
                rect.top -= height;
                rect = this.fu;
                rect.bottom += height;
            }
        }
        return this.fu;
    }

    public static void drawTickMarkLine(Canvas canvas, TickMark tickMark) {
        canvas.drawRect(tickMark.sb, tickMark.getLinePaint());
    }

    public static void updateTooltipContent(Tooltip tooltip, DataPoint<?, ?> dataPoint) {
        if (tooltip == null) {
            throw new IllegalArgumentException("Cannot update null tooltip.");
        } else if (dataPoint == null) {
            throw new IllegalArgumentException(tooltip.getContext().getString(R.string.TooltipNullView));
        } else {
            tooltip.sJ.a(tooltip, (DataPoint) dataPoint);
        }
    }

    public static void drawCrosshair(ShinobiChart chart, Canvas canvas, Rect drawingBoundary, float pixelXValue, float pixelYValue, float targetCircleRadius, Paint paint) {
        a(canvas, pixelXValue, pixelYValue, targetCircleRadius, paint);
        if (chart.getCrosshair().bN()) {
            a(chart, canvas, drawingBoundary, pixelXValue, pixelYValue, targetCircleRadius, paint);
            b(chart, canvas, drawingBoundary, pixelXValue, pixelYValue, targetCircleRadius, paint);
        }
    }

    private static void a(Canvas canvas, float f, float f2, float f3, Paint paint) {
        canvas.drawCircle(f, f2, f3, paint);
    }

    private static void a(ShinobiChart shinobiChart, Canvas canvas, Rect rect, float f, float f2, float f3, Paint paint) {
        Crosshair crosshair = shinobiChart.getCrosshair();
        Object obj = (crosshair.fM != null ? crosshair.fM.getYAxis() : shinobiChart.getYAxis()).Q == Position.NORMAL ? 1 : null;
        canvas.drawLine(f + ((obj != null ? -1.0f : 1.0f) * f3), f2, obj != null ? (float) rect.left : (float) rect.right, f2, paint);
    }

    private static void b(ShinobiChart shinobiChart, Canvas canvas, Rect rect, float f, float f2, float f3, Paint paint) {
        Crosshair crosshair = shinobiChart.getCrosshair();
        Object obj = (crosshair.fM != null ? crosshair.fM.getXAxis() : shinobiChart.getXAxis()).Q == Position.NORMAL ? 1 : null;
        canvas.drawLine(f, f2 + ((obj != null ? 1.0f : -1.0f) * f3), f, obj != null ? (float) rect.bottom : (float) rect.top, paint);
    }
}
