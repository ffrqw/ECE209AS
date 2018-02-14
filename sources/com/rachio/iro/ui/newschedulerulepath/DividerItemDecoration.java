package com.rachio.iro.ui.newschedulerulepath;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.view.View;

public class DividerItemDecoration extends ItemDecoration {
    private static final int[] ATTRS = new int[]{16843284};
    private Drawable mDivider;
    private int mOrientation = 1;

    public DividerItemDecoration(Context context, int orientation) {
        TypedArray a = context.obtainStyledAttributes(ATTRS);
        this.mDivider = a.getDrawable(0);
        a.recycle();
    }

    public void onDraw(Canvas c, RecyclerView parent) {
        int paddingLeft;
        int width;
        int childCount;
        int i;
        if (this.mOrientation == 1) {
            paddingLeft = parent.getPaddingLeft();
            width = parent.getWidth() - parent.getPaddingRight();
            childCount = parent.getChildCount();
            for (i = 0; i < childCount; i++) {
                View childAt = parent.getChildAt(i);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                int bottom = (layoutParams.bottomMargin + childAt.getBottom()) + Math.round(ViewCompat.getTranslationY(childAt));
                this.mDivider.setBounds(paddingLeft, bottom, width, this.mDivider.getIntrinsicHeight() + bottom);
                this.mDivider.draw(c);
            }
            return;
        }
        paddingLeft = parent.getPaddingTop();
        width = parent.getHeight() - parent.getPaddingBottom();
        childCount = parent.getChildCount();
        for (i = 0; i < childCount; i++) {
            childAt = parent.getChildAt(i);
            layoutParams = (LayoutParams) childAt.getLayoutParams();
            bottom = (layoutParams.rightMargin + childAt.getRight()) + Math.round(ViewCompat.getTranslationX(childAt));
            this.mDivider.setBounds(bottom, paddingLeft, this.mDivider.getIntrinsicHeight() + bottom, width);
            this.mDivider.draw(c);
        }
    }

    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if (this.mOrientation == 1) {
            outRect.set(0, 0, 0, this.mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, this.mDivider.getIntrinsicWidth(), 0);
        }
    }
}
