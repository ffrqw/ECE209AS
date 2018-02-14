package android.support.v4.view;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

public abstract class PagerAdapter {
    private final DataSetObservable mObservable = new DataSetObservable();
    private DataSetObserver mViewPagerObserver;

    public abstract int getCount();

    public abstract boolean isViewFromObject(View view, Object obj);

    public void startUpdate(ViewGroup container) {
    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
    }

    public void finishUpdate(ViewGroup container) {
    }

    public int getItemPosition(Object object) {
        return -1;
    }

    public final void registerDataSetObserver(DataSetObserver observer) {
        this.mObservable.registerObserver(observer);
    }

    public final void unregisterDataSetObserver(DataSetObserver observer) {
        this.mObservable.unregisterObserver(observer);
    }

    final void setViewPagerObserver(DataSetObserver observer) {
        synchronized (this) {
            this.mViewPagerObserver = observer;
        }
    }

    public CharSequence getPageTitle(int position) {
        return null;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        throw new UnsupportedOperationException("Required method instantiateItem was not overridden");
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        throw new UnsupportedOperationException("Required method destroyItem was not overridden");
    }
}
