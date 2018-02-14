package com.rachio.iro.utils;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
    private ArrayList<View> views = new ArrayList();

    public final int getItemPosition(Object object) {
        int index = this.views.indexOf(object);
        if (index == -1) {
            return -2;
        }
        return index;
    }

    public final Object instantiateItem(ViewGroup container, int position) {
        View v = (View) this.views.get(position);
        container.addView(v);
        return v;
    }

    public final void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) this.views.get(position));
    }

    public final int getCount() {
        return this.views.size();
    }

    public final boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
