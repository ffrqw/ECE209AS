package com.shinobicontrols.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;

public class Legend extends LinearLayout {
    public static final int ALL = -1;
    public static final int VARIABLE = -2;
    private final float density = getResources().getDisplayMetrics().density;
    private int jA;
    private cb jB;
    private int jC = -2;
    cn jt = cn.a(this);
    private final List<cc> ju = new ArrayList();
    private final ce jv;
    private Placement jw = Placement.OUTSIDE_PLOT_AREA;
    private Position jx = Position.TOP_RIGHT;
    private LegendStyle jy;
    private final Title jz;

    public enum Placement {
        INSIDE_PLOT_AREA,
        ON_PLOT_AREA_BORDER,
        OUTSIDE_PLOT_AREA
    }

    public enum Position {
        BOTTOM_CENTER(81),
        BOTTOM_LEFT(83),
        BOTTOM_RIGHT(85),
        MIDDLE_LEFT(19),
        MIDDLE_RIGHT(21),
        TOP_CENTER(49),
        TOP_LEFT(51),
        TOP_RIGHT(53);
        
        private final int gravity;

        private Position(int gravity) {
            this.gravity = gravity;
        }

        final int getGravity() {
            return this.gravity;
        }
    }

    public enum SymbolAlignment {
        LEFT,
        RIGHT
    }

    Legend(Context context) {
        super(context);
        setOrientation(1);
        this.jz = e(context);
        this.jv = f(context);
        addView(this.jz);
        addView(this.jv);
    }

    private Title e(Context context) {
        Title title = new Title(context);
        title.setVisibility(8);
        LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 1;
        title.setLayoutParams(layoutParams);
        return title;
    }

    private ce f(Context context) {
        ce ceVar = new ce(context);
        ceVar.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        return ceVar;
    }

    public int getMaxSeriesPerRow() {
        return this.jC;
    }

    public Placement getPlacement() {
        return this.jw;
    }

    public Position getPosition() {
        return this.jx;
    }

    public LegendStyle getStyle() {
        return this.jy;
    }

    public String getTitle() {
        return this.jz.getText().toString();
    }

    void reload() {
        this.ju.clear();
        this.ju.addAll(this.jB.a(this.jy));
        this.jv.a(this.ju, g(this.jC, this.ju.size()));
        super.setVisibility(da());
        e();
        invalidate();
        requestLayout();
    }

    private int g(int i, int i2) {
        if (i >= 0) {
            return i;
        }
        if (i == -1 || this.jx == Position.TOP_CENTER || this.jx == Position.BOTTOM_CENTER) {
            return i2;
        }
        return 1;
    }

    private void e() {
        cZ();
        this.jz.a(this.jy.dd());
        this.jv.d(this.jy);
        int c = ca.c(this.density, this.jy.getPadding());
        setPadding(c, c, c, c);
        i(this.jy.getRowVerticalMargin());
    }

    @SuppressLint({"NewApi"})
    private void cZ() {
        Drawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(this.jy.getBackgroundColor());
        gradientDrawable.setStroke(ca.c(this.density, this.jy.getBorderWidth()), this.jy.getBorderColor());
        gradientDrawable.setCornerRadius(this.jy.getCornerRadius());
        if (VERSION.SDK_INT >= 16) {
            setBackground(gradientDrawable);
        } else {
            setBackgroundDrawable(gradientDrawable);
        }
    }

    private void i(float f) {
        int c = ca.c(this.density, f / 2.0f);
        if (this.jv.getChildCount() > 0 && this.jz != null && this.jz.getVisibility() != 8) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.jz.getLayoutParams();
            layoutParams.bottomMargin += c;
            ((LinearLayout.LayoutParams) this.jv.getLayoutParams()).topMargin = c;
        }
    }

    private int da() {
        return isEmpty() ? 8 : this.jA;
    }

    private boolean isEmpty() {
        return this.ju.isEmpty() && this.jz.getVisibility() == 8;
    }

    public void setMaxSeriesPerRow(int maxSeriesPerRow) {
        this.jC = maxSeriesPerRow;
        reload();
    }

    public void setPlacement(Placement placement) {
        this.jw = placement;
        this.jt = cn.a(this);
        reload();
    }

    public void setPosition(Position position) {
        this.jx = position;
        this.jt = cn.a(this);
        reload();
    }

    public void setStyle(LegendStyle style) {
        this.jy = style;
    }

    public void setTitle(String title) {
        this.jz.setText(title);
        if (Axis.a(title)) {
            this.jz.setVisibility(8);
        } else {
            this.jz.setVisibility(0);
        }
        reload();
    }

    public void setVisibility(int visibility) {
        this.jA = visibility;
        super.setVisibility(da());
    }

    void a(cb cbVar) {
        this.jB = cbVar;
    }
}
