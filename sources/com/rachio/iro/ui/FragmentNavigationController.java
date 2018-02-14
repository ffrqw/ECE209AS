package com.rachio.iro.ui;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import com.rachio.iro.R;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.fragment.BaseFragment;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FragmentNavigationController<F extends BaseFragment> {
    private static final String TAG = FragmentNavigationController.class.getName();
    private ActionBar actionBar;
    private int defaultTitle;
    private FragmentBackStackDemystifier fragmentBackStackDemystifier;
    private FragmentManager fragmentManager;
    private BaseFragment lastFragment;
    private Listener listener;
    private LinkedList<StackUnit> pathStack = new LinkedList();
    private final Path root;

    private static class FragmentBackStackDemystifier implements OnBackStackChangedListener {
        private final FragmentManager fragmentManager;
        private int lastCount = 0;
        private final Listener listener;

        interface Listener {
            void onFragmentBackStackPopped();

            void onFragmentBackStackPushed();
        }

        public FragmentBackStackDemystifier(FragmentManager fragmentManager, Listener listener) {
            this.fragmentManager = fragmentManager;
            this.listener = listener;
        }

        public final void onBackStackChanged() {
            int nowCount = this.fragmentManager.getBackStackEntryCount();
            if (nowCount > this.lastCount) {
                this.listener.onFragmentBackStackPushed();
            } else if (nowCount < this.lastCount) {
                this.listener.onFragmentBackStackPopped();
            }
            this.lastCount = nowCount;
        }

        public final void onSaveInstanceState(Bundle state) {
            if (state != null) {
                state.putInt("lastcount", this.lastCount);
            }
        }

        public final void onRestoreInstanceState(Bundle state) {
            if (state != null) {
                this.lastCount = state.getInt("lastcount", 0);
            }
        }
    }

    public interface Listener {
        void onAllPathsComplete();

        void onFragmentComing$3993877b(BaseFragment baseFragment);

        void onFragmentGoing(BaseFragment baseFragment);
    }

    public static class Path implements Serializable {
        public boolean hasOwnBackControl;
        private final LinkedList<Screen> screens;
        private final String tag;

        public Path(String tag) {
            this.screens = new LinkedList();
            this.hasOwnBackControl = false;
            this.tag = tag;
        }

        public Path(Screen screen) {
            this(screen.tag);
            registerScreen(screen);
        }

        public final void registerScreen(Screen screen) {
            this.screens.push(screen);
        }

        public String toString() {
            return this.tag;
        }

        public final Screen getFirst() {
            return (Screen) this.screens.peekLast();
        }

        public final Screen getNext(Screen screen) {
            int current = -1;
            for (int i = 0; i < this.screens.size(); i++) {
                Screen thisScreen = (Screen) this.screens.get(i);
                Log.d(FragmentNavigationController.TAG, thisScreen.toString());
                if (thisScreen == screen) {
                    current = i;
                }
            }
            if (current != -1) {
                return (Screen) this.screens.get(current - 1);
            }
            return null;
        }

        public final boolean isLast(Screen screen) {
            return this.screens.peek() == screen;
        }
    }

    public static class Screen implements Serializable {
        Map<Integer, Path> branches;
        final Class fragmentClass;
        final String tag;
        final int title;

        public Screen(String tag, Class fragmentClass) {
            this(tag, fragmentClass, -1);
        }

        public Screen(String tag, Class fragmentClass, int title) {
            this.branches = new TreeMap();
            this.tag = tag;
            this.fragmentClass = fragmentClass;
            this.title = title;
        }

        public final void registerBranch(int action, Path branchPath) {
            this.branches.put(Integer.valueOf(action), branchPath);
        }

        public final void registerBranches(int[] action, Path[] branchPaths) {
            for (int i = 0; i < 10; i++) {
                registerBranch(action[i], branchPaths[i]);
            }
        }

        public String toString() {
            return this.tag;
        }
    }

    private static class StackUnit implements Serializable {
        final Path path;
        private LinkedList<Screen> screenStack = new LinkedList();

        public StackUnit(Path path) {
            this.path = path;
        }
    }

    public FragmentNavigationController(Path rootPath) {
        this.root = rootPath;
    }

    public static Path createSingleScreenPath(String tag, Class fragmentClass, int title) {
        return new Path(new Screen(tag, fragmentClass, title));
    }

    private static BaseFragment createFragment(Class fragmentClass) {
        try {
            return (BaseFragment) fragmentClass.newInstance();
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InstantiationException ex2) {
            throw new RuntimeException(ex2);
        }
    }

    private void pushScreenOnPath(Screen screen, BaseFragment fragment, boolean addToBackStack, boolean animate, String tag) {
        ((StackUnit) this.pathStack.peek()).screenStack.push(screen);
        FragmentTransaction ft = this.fragmentManager.beginTransaction();
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        if (animate) {
            ft.setTransition(4097);
        }
        ft.replace(R.id.fragmentContainer, fragment);
        ft.commit();
        this.fragmentManager.executePendingTransactions();
        this.lastFragment = fragment;
        if (this.listener != null) {
            this.listener.onFragmentComing$3993877b(fragment);
        }
        updateActionBarTitle();
        updateBackButton();
        printState();
    }

    private void pushScreen(Screen screen, Path newPath) {
        if (this.listener != null) {
            this.listener.onFragmentGoing(getCurrentFragment());
        }
        String tag = null;
        boolean animate = false;
        if (newPath != null) {
            tag = newPath.toString();
            Log.d(TAG, "tagging transaction with " + tag);
            animate = true;
        }
        pushScreenOnPath(screen, createFragment(screen.fragmentClass), true, animate, tag);
    }

    public final void updateActionBarTitle() {
        int title = this.defaultTitle;
        int screenTitle = ((Screen) ((StackUnit) this.pathStack.peek()).screenStack.peek()).title;
        if (screenTitle != -1) {
            title = screenTitle;
        }
        this.actionBar.setTitle(title);
    }

    private void updateBackButton() {
        boolean hasPathesStacked;
        boolean z = false;
        if (this.pathStack.size() > 1) {
            hasPathesStacked = true;
        } else {
            hasPathesStacked = false;
        }
        boolean rootPathIsASingleScreen;
        if (this.root.screens.size() == 1) {
            rootPathIsASingleScreen = true;
        } else {
            rootPathIsASingleScreen = false;
        }
        ActionBar actionBar = this.actionBar;
        if (hasPathesStacked || (rootPathIsASingleScreen && !this.root.hasOwnBackControl)) {
            z = true;
        }
        actionBar.setDisplayHomeAsUpEnabled(z);
    }

    public final void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        try {
            this.defaultTitle = activity.getPackageManager().getActivityInfo(activity.getComponentName(), 0).labelRes;
            this.actionBar = activity.getSupportActionBar();
            this.fragmentManager = activity.getSupportFragmentManager();
            this.fragmentBackStackDemystifier = new FragmentBackStackDemystifier(this.fragmentManager, new Listener() {
                public final void onFragmentBackStackPopped() {
                    Log.d(FragmentNavigationController.TAG, "back stack popped");
                    if (FragmentNavigationController.this.listener != null) {
                        FragmentNavigationController.this.listener.onFragmentGoing(FragmentNavigationController.this.lastFragment);
                    }
                    FragmentNavigationController.this.lastFragment = FragmentNavigationController.this.getCurrentFragment();
                    StackUnit stackUnit = (StackUnit) FragmentNavigationController.this.pathStack.peek();
                    stackUnit.screenStack.pop();
                    if (stackUnit.screenStack.size() == 0) {
                        Log.d(FragmentNavigationController.TAG, "path has ended");
                        FragmentNavigationController.this.pathStack.pop();
                    }
                    if (FragmentNavigationController.this.pathStack.size() > 0 && ((StackUnit) FragmentNavigationController.this.pathStack.peek()).screenStack.size() > 0) {
                        FragmentNavigationController.this.updateActionBarTitle();
                        if (FragmentNavigationController.this.listener != null) {
                            Listener access$300 = FragmentNavigationController.this.listener;
                            stackUnit.screenStack.peek();
                            access$300.onFragmentComing$3993877b(FragmentNavigationController.this.getCurrentFragment());
                        }
                        FragmentNavigationController.this.updateBackButton();
                    }
                }

                public final void onFragmentBackStackPushed() {
                    Log.d(FragmentNavigationController.TAG, "back stack pushed");
                }
            });
            this.fragmentManager.addOnBackStackChangedListener(this.fragmentBackStackDemystifier);
            if (savedInstanceState == null) {
                startBranch(this.root);
            }
        } catch (NameNotFoundException nnfe) {
            throw new RuntimeException(nnfe);
        }
    }

    private void startBranch(Path path) {
        this.pathStack.push(new StackUnit(path));
        Screen screen = path.getFirst();
        if (path == this.root) {
            pushScreenOnPath(screen, createFragment(screen.fragmentClass), false, false, null);
        } else {
            pushScreen(screen, path);
        }
        printState();
    }

    public final void onAction(int action) {
        StackUnit stackUnit = (StackUnit) this.pathStack.peek();
        if (action == 0) {
            Path path = stackUnit.path;
            Screen screen = (Screen) ((StackUnit) this.pathStack.peek()).screenStack.peek();
            if (!path.isLast(screen)) {
                pushScreen(path.getNext(screen), null);
                return;
            } else if (this.pathStack.size() != 1) {
                Log.d(TAG, "ending path " + path.toString());
                if (this.listener != null) {
                    this.listener.onFragmentGoing(getCurrentFragment());
                }
                this.fragmentManager.popBackStack(path.toString(), 1);
                this.fragmentManager.executePendingTransactions();
                return;
            } else if (this.listener != null) {
                this.listener.onAllPathsComplete();
                return;
            } else {
                return;
            }
        }
        Screen currentScreen = (Screen) stackUnit.screenStack.peek();
        Path newPath = (Path) currentScreen.branches.get(Integer.valueOf(action));
        if (newPath != null) {
            startBranch(newPath);
        } else {
            printState();
            throw new RuntimeException("screen " + currentScreen.toString() + " doesn't support action " + action);
        }
    }

    public final F getCurrentFragment() {
        return (BaseFragment) this.fragmentManager.findFragmentById(R.id.fragmentContainer);
    }

    public final void setListener(Listener listener) {
        this.listener = listener;
    }

    private void printState() {
        Log.d(TAG, "-- state --");
        Iterator it = this.pathStack.iterator();
        while (it.hasNext()) {
            StackUnit p = (StackUnit) it.next();
            Log.d(TAG, "path " + p.path.tag + " stacked screens " + p.screenStack.size());
            Iterator it2 = p.screenStack.iterator();
            while (it2.hasNext()) {
                Log.d(TAG, "screen " + ((Screen) it2.next()).tag);
            }
        }
    }

    public final void onSaveInstanceState(Bundle outState) {
        printState();
        outState.putSerializable("managerstate", this.pathStack);
        this.fragmentBackStackDemystifier.onSaveInstanceState(outState);
    }

    public final void onRestoreInstanceState(Bundle inState) {
        ArrayList<StackUnit> stackUnits = new ArrayList((List) inState.getSerializable("managerstate"));
        this.pathStack.clear();
        this.pathStack.addAll(stackUnits);
        printState();
        this.fragmentBackStackDemystifier.onRestoreInstanceState(inState);
    }

    public final void onResume() {
        this.lastFragment = getCurrentFragment();
    }

    public static Path createSingleScreenPathWithABunchOfSingleScreen(String tag, Class fragmentClass, int title, int[] actions, String[] tags, Class[] fragmentsClasses, int[] titles) {
        Screen screen = new Screen(tag, fragmentClass, title);
        if (actions.length == tags.length && actions.length == fragmentsClasses.length) {
            int length = actions.length;
            int i = 0;
            while (i < length) {
                screen.registerBranch(actions[i], createSingleScreenPath(tags[i], fragmentsClasses[i], titles != null ? titles[i] : -1));
                i++;
            }
            Path path = new Path(tag);
            path.registerScreen(screen);
            return path;
        }
        throw new IllegalArgumentException();
    }
}
