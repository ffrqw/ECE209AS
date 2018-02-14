package android.support.v4.app;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public abstract class FragmentPagerAdapter extends PagerAdapter {
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;
    private final FragmentManager mFragmentManager;

    public abstract Fragment getItem(int i);

    public FragmentPagerAdapter(FragmentManager fm) {
        this.mFragmentManager = fm;
    }

    public final void startUpdate(ViewGroup container) {
        if (container.getId() == -1) {
            throw new IllegalStateException("ViewPager with adapter " + this + " requires a view id");
        }
    }

    public final Object instantiateItem(ViewGroup container, int position) {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        long itemId = (long) position;
        Fragment fragment = this.mFragmentManager.findFragmentByTag(makeFragmentName(container.getId(), itemId));
        if (fragment != null) {
            this.mCurTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            this.mCurTransaction.add(container.getId(), fragment, makeFragmentName(container.getId(), itemId));
        }
        if (fragment != this.mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }
        return fragment;
    }

    public final void destroyItem(ViewGroup container, int position, Object object) {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        this.mCurTransaction.detach((Fragment) object);
    }

    public final void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != this.mCurrentPrimaryItem) {
            if (this.mCurrentPrimaryItem != null) {
                this.mCurrentPrimaryItem.setMenuVisibility(false);
                this.mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            this.mCurrentPrimaryItem = fragment;
        }
    }

    public final void finishUpdate(ViewGroup container) {
        if (this.mCurTransaction != null) {
            this.mCurTransaction.commitNowAllowingStateLoss();
            this.mCurTransaction = null;
        }
    }

    public final boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
