package com.shinobicontrols.charts;

import android.graphics.Rect;
import android.view.ViewGroup.MarginLayoutParams;
import com.shinobicontrols.charts.Axis.Position;

abstract class cn {
    private static cn ka = new cn() {
        final void a(Rect rect, MarginLayoutParams marginLayoutParams, int i, int i2, float f) {
            rect.offset(0, (int) (((float) (marginLayoutParams.bottomMargin + (i2 / 2))) + (f / 2.0f)));
        }

        final void b(Rect rect, int i, int i2) {
            rect.bottom -= i2;
        }

        final int a(Position position, int i) {
            return position == Position.NORMAL ? i : 0;
        }

        final int a(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return (int) Math.max(0.0f, Math.max(0.0f, (((float) (i / 2)) - (f / 2.0f)) - ((float) a(position, i2))) - ((float) marginLayoutParams.bottomMargin));
        }

        final int b(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return 0;
        }

        final void a(Rect rect, int i, int i2, int i3, int i4) {
            rect.left += i;
            rect.top += i2;
            rect.right -= i3;
            rect.bottom -= i4;
        }
    };
    private static cn kb = new cn() {
        final void b(Rect rect, int i, int i2) {
            rect.bottom -= i2;
        }

        final int a(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return i;
        }

        final int b(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return 0;
        }

        final void a(Rect rect, int i, int i2, int i3, int i4) {
            rect.left += i;
            rect.right -= i3;
        }
    };
    private static cn kc = new cn() {
        final void b(Rect rect, int i, int i2) {
        }

        final int a(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return 0;
        }

        final int b(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return 0;
        }

        final void a(Rect rect, int i, int i2, int i3, int i4) {
            rect.left += i;
            rect.top += i2;
            rect.right -= i3;
            rect.bottom -= i4;
        }
    };
    private static cn kd = new cn() {
        final void a(Rect rect, MarginLayoutParams marginLayoutParams, int i, int i2, float f) {
            rect.offset(-((int) (((float) (marginLayoutParams.leftMargin + (i / 2))) + (f / 2.0f))), 0);
        }

        final void b(Rect rect, int i, int i2) {
            rect.left += i;
        }

        final int a(Position position, int i) {
            return position == Position.NORMAL ? i : 0;
        }

        final int a(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return 0;
        }

        final int b(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return (int) Math.max(0.0f, Math.max(0.0f, (((float) (i / 2)) - (f / 2.0f)) - ((float) a(position, i2))) - ((float) marginLayoutParams.leftMargin));
        }

        final void a(Rect rect, int i, int i2, int i3, int i4) {
            rect.left += i;
            rect.top += i2;
            rect.right -= i3;
            rect.bottom -= i4;
        }
    };
    private static cn ke = new cn() {
        final void b(Rect rect, int i, int i2) {
            rect.left += i;
        }

        final int a(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return 0;
        }

        final int b(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return i;
        }

        final void a(Rect rect, int i, int i2, int i3, int i4) {
            rect.top += i2;
            rect.bottom -= i4;
        }
    };
    private static cn kf = new cn() {
        final void b(Rect rect, int i, int i2) {
            rect.setEmpty();
        }

        final int a(Position position, int i) {
            return 0;
        }

        final int a(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return 0;
        }

        final int b(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return 0;
        }

        final void a(Rect rect, int i, int i2, int i3, int i4) {
        }
    };
    private static cn kg = new cn() {
        final void a(Rect rect, MarginLayoutParams marginLayoutParams, int i, int i2, float f) {
            rect.offset((int) (((float) (marginLayoutParams.rightMargin + (i / 2))) + (f / 2.0f)), 0);
        }

        final void b(Rect rect, int i, int i2) {
            rect.right -= i;
        }

        final int a(Position position, int i) {
            return position == Position.REVERSE ? i : 0;
        }

        final int a(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return 0;
        }

        final int b(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return (int) Math.max(0.0f, Math.max(0.0f, (((float) (i / 2)) - (f / 2.0f)) - ((float) a(position, i2))) - ((float) marginLayoutParams.rightMargin));
        }

        final void a(Rect rect, int i, int i2, int i3, int i4) {
            rect.left += i;
            rect.top += i2;
            rect.right -= i3;
            rect.bottom -= i4;
        }
    };
    private static cn kh = new cn() {
        final void b(Rect rect, int i, int i2) {
            rect.right -= i;
        }

        final int a(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return 0;
        }

        final int b(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return i;
        }

        final void a(Rect rect, int i, int i2, int i3, int i4) {
            rect.top += i2;
            rect.bottom -= i4;
        }
    };
    private static cn ki = new cn() {
        final void a(Rect rect, MarginLayoutParams marginLayoutParams, int i, int i2, float f) {
            rect.offset(0, -((int) (((float) (marginLayoutParams.topMargin + (i2 / 2))) + (f / 2.0f))));
        }

        final void b(Rect rect, int i, int i2) {
            rect.top += i2;
        }

        final int a(Position position, int i) {
            return position == Position.REVERSE ? i : 0;
        }

        final int a(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return (int) Math.max(0.0f, Math.max(0.0f, (((float) (i / 2)) - (f / 2.0f)) - ((float) a(position, i2))) - ((float) marginLayoutParams.topMargin));
        }

        final int b(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return 0;
        }

        final void a(Rect rect, int i, int i2, int i3, int i4) {
            rect.left += i;
            rect.top += i2;
            rect.right -= i3;
            rect.bottom -= i4;
        }
    };
    private static cn kj = new cn() {
        final void b(Rect rect, int i, int i2) {
            rect.top += i2;
        }

        final int a(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return i;
        }

        final int b(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams) {
            return 0;
        }

        final void a(Rect rect, int i, int i2, int i3, int i4) {
            rect.left += i;
            rect.right -= i3;
        }
    };

    abstract int a(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams);

    abstract void a(Rect rect, int i, int i2, int i3, int i4);

    abstract int b(int i, float f, Position position, int i2, MarginLayoutParams marginLayoutParams);

    abstract void b(Rect rect, int i, int i2);

    cn() {
    }

    static cn a(Legend legend) {
        if (legend == null || legend.getPosition() == null || legend.getPlacement() == null) {
            return kf;
        }
        switch (legend.getPosition()) {
            case TOP_LEFT:
            case MIDDLE_LEFT:
            case BOTTOM_LEFT:
                switch (legend.getPlacement()) {
                    case OUTSIDE_PLOT_AREA:
                        return ke;
                    case ON_PLOT_AREA_BORDER:
                        return kd;
                    case INSIDE_PLOT_AREA:
                        return kc;
                    default:
                        throw new AssertionError("Legend Placement invalid:" + legend.getPlacement());
                }
            case TOP_RIGHT:
            case MIDDLE_RIGHT:
            case BOTTOM_RIGHT:
                switch (legend.getPlacement()) {
                    case OUTSIDE_PLOT_AREA:
                        return kh;
                    case ON_PLOT_AREA_BORDER:
                        return kg;
                    case INSIDE_PLOT_AREA:
                        return kc;
                    default:
                        throw new AssertionError("Legend Placement invalid:" + legend.getPlacement());
                }
            case TOP_CENTER:
                switch (legend.getPlacement()) {
                    case OUTSIDE_PLOT_AREA:
                        return kj;
                    case ON_PLOT_AREA_BORDER:
                        return ki;
                    case INSIDE_PLOT_AREA:
                        return kc;
                    default:
                        throw new AssertionError("Legend Placement invalid:" + legend.getPlacement());
                }
            case BOTTOM_CENTER:
                switch (legend.getPlacement()) {
                    case OUTSIDE_PLOT_AREA:
                        return kb;
                    case ON_PLOT_AREA_BORDER:
                        return ka;
                    case INSIDE_PLOT_AREA:
                        return kc;
                    default:
                        throw new AssertionError("Legend Placement invalid:" + legend.getPlacement());
                }
            default:
                throw new AssertionError("Legend Position invalid:" + legend.getPosition());
        }
    }

    void a(Rect rect, MarginLayoutParams marginLayoutParams, int i, int i2, float f) {
    }

    int a(Position position, int i) {
        return 0;
    }
}
