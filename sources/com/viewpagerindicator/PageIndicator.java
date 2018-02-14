package com.viewpagerindicator;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public interface PageIndicator extends OnPageChangeListener {
    void setOnPageChangeListener(OnPageChangeListener onPageChangeListener);

    void setViewPager(ViewPager viewPager);
}
