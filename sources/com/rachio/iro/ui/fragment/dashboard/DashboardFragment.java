package com.rachio.iro.ui.fragment.dashboard;

import android.os.Bundle;
import android.view.MenuItem;
import com.rachio.iro.ui.fragment.BaseFragment;

public abstract class DashboardFragment extends BaseFragment {
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            initState(getArguments());
        }
    }

    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            initState(savedInstanceState);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initState(Bundle bundle) {
    }
}
