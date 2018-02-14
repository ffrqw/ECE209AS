package android.support.v4.app;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.v4.util.SimpleArrayMap;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public final class FragmentController {
    private final FragmentHostCallback<?> mHost;

    public static final FragmentController createController(FragmentHostCallback<?> callbacks) {
        return new FragmentController(callbacks);
    }

    private FragmentController(FragmentHostCallback<?> callbacks) {
        this.mHost = callbacks;
    }

    public final FragmentManager getSupportFragmentManager() {
        return this.mHost.mFragmentManager;
    }

    public final Fragment findFragmentByWho(String who) {
        return this.mHost.mFragmentManager.findFragmentByWho(who);
    }

    public final void attachHost(Fragment parent) {
        this.mHost.mFragmentManager.attachController(this.mHost, this.mHost, null);
    }

    public final View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return this.mHost.mFragmentManager.onCreateView(parent, name, context, attrs);
    }

    public final void noteStateNotSaved() {
        this.mHost.mFragmentManager.mStateSaved = false;
    }

    public final Parcelable saveAllState() {
        return this.mHost.mFragmentManager.saveAllState();
    }

    public final void restoreAllState(Parcelable state, FragmentManagerNonConfig nonConfig) {
        this.mHost.mFragmentManager.restoreAllState(state, nonConfig);
    }

    public final FragmentManagerNonConfig retainNestedNonConfig() {
        return this.mHost.mFragmentManager.retainNonConfig();
    }

    public final void dispatchCreate() {
        this.mHost.mFragmentManager.dispatchCreate();
    }

    public final void dispatchActivityCreated() {
        this.mHost.mFragmentManager.dispatchActivityCreated();
    }

    public final void dispatchStart() {
        this.mHost.mFragmentManager.dispatchStart();
    }

    public final void dispatchResume() {
        this.mHost.mFragmentManager.dispatchResume();
    }

    public final void dispatchPause() {
        this.mHost.mFragmentManager.dispatchPause();
    }

    public final void dispatchStop() {
        this.mHost.mFragmentManager.dispatchStop();
    }

    public final void dispatchReallyStop() {
        this.mHost.mFragmentManager.dispatchReallyStop();
    }

    public final void dispatchDestroy() {
        this.mHost.mFragmentManager.dispatchDestroy();
    }

    public final void dispatchMultiWindowModeChanged(boolean isInMultiWindowMode) {
        this.mHost.mFragmentManager.dispatchMultiWindowModeChanged(isInMultiWindowMode);
    }

    public final void dispatchPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        this.mHost.mFragmentManager.dispatchPictureInPictureModeChanged(isInPictureInPictureMode);
    }

    public final void dispatchConfigurationChanged(Configuration newConfig) {
        this.mHost.mFragmentManager.dispatchConfigurationChanged(newConfig);
    }

    public final void dispatchLowMemory() {
        this.mHost.mFragmentManager.dispatchLowMemory();
    }

    public final boolean dispatchCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        return this.mHost.mFragmentManager.dispatchCreateOptionsMenu(menu, inflater);
    }

    public final boolean dispatchPrepareOptionsMenu(Menu menu) {
        return this.mHost.mFragmentManager.dispatchPrepareOptionsMenu(menu);
    }

    public final boolean dispatchOptionsItemSelected(MenuItem item) {
        return this.mHost.mFragmentManager.dispatchOptionsItemSelected(item);
    }

    public final boolean dispatchContextItemSelected(MenuItem item) {
        return this.mHost.mFragmentManager.dispatchContextItemSelected(item);
    }

    public final void dispatchOptionsMenuClosed(Menu menu) {
        this.mHost.mFragmentManager.dispatchOptionsMenuClosed(menu);
    }

    public final boolean execPendingActions() {
        return this.mHost.mFragmentManager.execPendingActions();
    }

    public final void doLoaderStart() {
        this.mHost.doLoaderStart();
    }

    public final void doLoaderStop(boolean retain) {
        this.mHost.doLoaderStop(retain);
    }

    public final void doLoaderDestroy() {
        this.mHost.doLoaderDestroy();
    }

    public final void reportLoaderStart() {
        this.mHost.reportLoaderStart();
    }

    public final SimpleArrayMap<String, LoaderManager> retainLoaderNonConfig() {
        return this.mHost.retainLoaderNonConfig();
    }

    public final void restoreLoaderNonConfig(SimpleArrayMap<String, LoaderManager> loaderManagers) {
        this.mHost.restoreLoaderNonConfig(loaderManagers);
    }

    public final void dumpLoaders(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        this.mHost.dumpLoaders(prefix, fd, writer, args);
    }
}
