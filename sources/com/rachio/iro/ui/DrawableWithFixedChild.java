package com.rachio.iro.ui;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class DrawableWithFixedChild extends Drawable {
    private final Drawable child;

    public DrawableWithFixedChild(Drawable child) {
        this.child = child;
    }

    public void draw(Canvas canvas) {
        this.child.draw(canvas);
    }

    public void setAlpha(int alpha) {
        this.child.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.child.setColorFilter(colorFilter);
    }

    public int getOpacity() {
        return this.child.getOpacity();
    }

    private void updateChildBounds() {
        Rect bounds = getBounds();
        Rect childBounds = copyBounds();
        childBounds.left = childBounds.right - this.child.getIntrinsicWidth();
        childBounds.top = bounds.top + ((childBounds.height() - this.child.getIntrinsicHeight()) / 2);
        childBounds.bottom = (bounds.top + childBounds.top) + this.child.getIntrinsicHeight();
        this.child.setBounds(childBounds);
    }

    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
        updateChildBounds();
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        updateChildBounds();
    }
}
