package android.support.v4.app;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.os.BuildCompat;
import android.support.v4.util.ArraySet;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.LogWriter;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* compiled from: FragmentManager */
final class FragmentManagerImpl extends FragmentManager implements LayoutInflaterFactory {
    static final Interpolator ACCELERATE_CUBIC = new AccelerateInterpolator(1.5f);
    static final Interpolator ACCELERATE_QUINT = new AccelerateInterpolator(2.5f);
    static boolean DEBUG = false;
    static final Interpolator DECELERATE_CUBIC = new DecelerateInterpolator(1.5f);
    static final Interpolator DECELERATE_QUINT = new DecelerateInterpolator(2.5f);
    static final boolean HONEYCOMB;
    static Field sAnimationListenerField = null;
    ArrayList<Fragment> mActive;
    ArrayList<Fragment> mAdded;
    ArrayList<Integer> mAvailBackStackIndices;
    ArrayList<Integer> mAvailIndices;
    ArrayList<BackStackRecord> mBackStack;
    ArrayList<OnBackStackChangedListener> mBackStackChangeListeners;
    ArrayList<BackStackRecord> mBackStackIndices;
    FragmentContainer mContainer;
    ArrayList<Fragment> mCreatedMenus;
    int mCurState = 0;
    boolean mDestroyed;
    Runnable mExecCommit = new Runnable() {
        public final void run() {
            FragmentManagerImpl.this.execPendingActions();
        }
    };
    boolean mExecutingActions;
    boolean mHavePendingDeferredStart;
    FragmentHostCallback mHost;
    boolean mNeedMenuInvalidate;
    String mNoTransactionsBecause;
    Fragment mParent;
    ArrayList<OpGenerator> mPendingActions;
    ArrayList<StartEnterTransitionListener> mPostponedTransactions;
    SparseArray<Parcelable> mStateArray = null;
    Bundle mStateBundle = null;
    boolean mStateSaved;
    ArrayList<Fragment> mTmpAddedFragments;
    ArrayList<Boolean> mTmpIsPop;
    ArrayList<BackStackRecord> mTmpRecords;

    /* compiled from: FragmentManager */
    interface OpGenerator {
        boolean generateOps(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2);
    }

    /* compiled from: FragmentManager */
    static class AnimateOnHWLayerIfNeededListener implements AnimationListener {
        private AnimationListener mOriginalListener;
        private boolean mShouldRunOnHWLayer;
        View mView;

        public AnimateOnHWLayerIfNeededListener(View v, Animation anim) {
            if (v != null && anim != null) {
                this.mView = v;
            }
        }

        public AnimateOnHWLayerIfNeededListener(View v, Animation anim, AnimationListener listener) {
            if (v != null && anim != null) {
                this.mOriginalListener = listener;
                this.mView = v;
                this.mShouldRunOnHWLayer = true;
            }
        }

        public void onAnimationStart(Animation animation) {
            if (this.mOriginalListener != null) {
                this.mOriginalListener.onAnimationStart(animation);
            }
        }

        public void onAnimationEnd(Animation animation) {
            if (this.mView != null && this.mShouldRunOnHWLayer) {
                if (ViewCompat.isAttachedToWindow(this.mView) || BuildCompat.isAtLeastN()) {
                    this.mView.post(new Runnable() {
                        public final void run() {
                            ViewCompat.setLayerType(AnimateOnHWLayerIfNeededListener.this.mView, 0, null);
                        }
                    });
                } else {
                    ViewCompat.setLayerType(this.mView, 0, null);
                }
            }
            if (this.mOriginalListener != null) {
                this.mOriginalListener.onAnimationEnd(animation);
            }
        }

        public void onAnimationRepeat(Animation animation) {
            if (this.mOriginalListener != null) {
                this.mOriginalListener.onAnimationRepeat(animation);
            }
        }
    }

    /* compiled from: FragmentManager */
    static class FragmentTag {
        public static final int[] Fragment = new int[]{16842755, 16842960, 16842961};
    }

    /* compiled from: FragmentManager */
    private class PopBackStackState implements OpGenerator {
        final int mFlags;
        final int mId;
        final String mName;

        PopBackStackState(String name, int id, int flags) {
            this.mName = name;
            this.mId = id;
            this.mFlags = flags;
        }

        public final boolean generateOps(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop) {
            return FragmentManagerImpl.this.popBackStackState(records, isRecordPop, this.mName, this.mId, this.mFlags);
        }
    }

    /* compiled from: FragmentManager */
    static class StartEnterTransitionListener implements OnStartEnterTransitionListener {
        private final boolean mIsBack;
        private int mNumPostponed;
        private final BackStackRecord mRecord;

        StartEnterTransitionListener(BackStackRecord record, boolean isBack) {
            this.mIsBack = isBack;
            this.mRecord = record;
        }

        public final void onStartEnterTransition() {
            this.mNumPostponed--;
            if (this.mNumPostponed == 0) {
                this.mRecord.mManager.scheduleCommit();
            }
        }

        public final void startListening() {
            this.mNumPostponed++;
        }

        public final boolean isReady() {
            return this.mNumPostponed == 0;
        }

        public final void completeTransaction() {
            boolean canceled;
            boolean z = false;
            if (this.mNumPostponed > 0) {
                canceled = true;
            } else {
                canceled = false;
            }
            FragmentManagerImpl manager = this.mRecord.mManager;
            int numAdded = manager.mAdded.size();
            for (int i = 0; i < numAdded; i++) {
                Fragment fragment = (Fragment) manager.mAdded.get(i);
                fragment.setOnStartEnterTransitionListener(null);
                if (canceled && fragment.isPostponed()) {
                    fragment.startPostponedEnterTransition();
                }
            }
            FragmentManagerImpl fragmentManagerImpl = this.mRecord.mManager;
            BackStackRecord backStackRecord = this.mRecord;
            boolean z2 = this.mIsBack;
            if (!canceled) {
                z = true;
            }
            FragmentManagerImpl.access$300(fragmentManagerImpl, backStackRecord, z2, z, true);
        }

        public final void cancelTransaction() {
            FragmentManagerImpl.access$300(this.mRecord.mManager, this.mRecord, this.mIsBack, false, false);
        }
    }

    public final boolean execPendingActions() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.ssa.SSATransform.placePhi(SSATransform.java:82)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:50)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r4 = this;
        r3 = 1;
        r4.ensureExecReady(r3);
        r0 = 0;
    L_0x0005:
        r1 = r4.mTmpRecords;
        r2 = r4.mTmpIsPop;
        r1 = r4.generateOpsForPendingActions(r1, r2);
        if (r1 == 0) goto L_0x0022;
    L_0x000f:
        r4.mExecutingActions = r3;
        r1 = r4.mTmpRecords;	 Catch:{ all -> 0x001d }
        r2 = r4.mTmpIsPop;	 Catch:{ all -> 0x001d }
        r4.optimizeAndExecuteOps(r1, r2);	 Catch:{ all -> 0x001d }
        r4.cleanupExec();
        r0 = 1;
        goto L_0x0005;
    L_0x001d:
        r1 = move-exception;
        r4.cleanupExec();
        throw r1;
    L_0x0022:
        r4.doPendingDeferredStart();
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentManagerImpl.execPendingActions():boolean");
    }

    FragmentManagerImpl() {
    }

    static /* synthetic */ void access$300(FragmentManagerImpl x0, BackStackRecord x1, boolean x2, boolean x3, boolean x4) {
        ArrayList arrayList = new ArrayList(1);
        ArrayList arrayList2 = new ArrayList(1);
        arrayList.add(x1);
        arrayList2.add(Boolean.valueOf(x2));
        executeOps(arrayList, arrayList2, 0, 1);
        if (x3) {
            FragmentTransition.startTransitions(x0, arrayList, arrayList2, 0, 1, true);
        }
        if (x4) {
            x0.moveToState(x0.mCurState, true);
        }
        if (x0.mActive != null) {
            int size = x0.mActive.size();
            for (int i = 0; i < size; i++) {
                Fragment fragment = (Fragment) x0.mActive.get(i);
                if (fragment != null && fragment.mView != null && fragment.mIsNewlyAdded && x1.interactsWith(fragment.mContainerId)) {
                    if (VERSION.SDK_INT >= 11 && fragment.mPostponedAlpha > 0.0f) {
                        fragment.mView.setAlpha(fragment.mPostponedAlpha);
                    }
                    if (x4) {
                        fragment.mPostponedAlpha = 0.0f;
                    } else {
                        fragment.mPostponedAlpha = -1.0f;
                        fragment.mIsNewlyAdded = false;
                    }
                }
            }
        }
    }

    static {
        boolean z = false;
        if (VERSION.SDK_INT >= 11) {
            z = true;
        }
        HONEYCOMB = z;
    }

    private void throwException(RuntimeException ex) {
        Log.e("FragmentManager", ex.getMessage());
        Log.e("FragmentManager", "Activity state:");
        PrintWriter pw = new PrintWriter(new LogWriter("FragmentManager"));
        if (this.mHost != null) {
            try {
                this.mHost.onDump("  ", null, pw, new String[0]);
            } catch (Exception e) {
                Log.e("FragmentManager", "Failed dumping state", e);
            }
        } else {
            try {
                dump("  ", null, pw, new String[0]);
            } catch (Exception e2) {
                Log.e("FragmentManager", "Failed dumping state", e2);
            }
        }
        throw ex;
    }

    public final FragmentTransaction beginTransaction() {
        return new BackStackRecord(this);
    }

    public final boolean executePendingTransactions() {
        boolean updates = execPendingActions();
        forcePostponedTransactions();
        return updates;
    }

    public final void popBackStack() {
        enqueueAction(new PopBackStackState(null, -1, 0), false);
    }

    public final boolean popBackStackImmediate() {
        checkStateLoss();
        return popBackStackImmediate(null, -1, 0);
    }

    public final void popBackStack(String name, int flags) {
        enqueueAction(new PopBackStackState(name, -1, 1), false);
    }

    public final void popBackStack(int id, int flags) {
        if (id < 0) {
            throw new IllegalArgumentException("Bad id: " + id);
        }
        enqueueAction(new PopBackStackState(null, id, 1), false);
    }

    private boolean popBackStackImmediate(String name, int id, int flags) {
        execPendingActions();
        ensureExecReady(true);
        boolean executePop = popBackStackState(this.mTmpRecords, this.mTmpIsPop, null, -1, 0);
        if (executePop) {
            this.mExecutingActions = true;
            try {
                optimizeAndExecuteOps(this.mTmpRecords, this.mTmpIsPop);
            } finally {
                cleanupExec();
            }
        }
        doPendingDeferredStart();
        return executePop;
    }

    public final int getBackStackEntryCount() {
        return this.mBackStack != null ? this.mBackStack.size() : 0;
    }

    public final BackStackEntry getBackStackEntryAt(int index) {
        return (BackStackEntry) this.mBackStack.get(index);
    }

    public final void addOnBackStackChangedListener(OnBackStackChangedListener listener) {
        if (this.mBackStackChangeListeners == null) {
            this.mBackStackChangeListeners = new ArrayList();
        }
        this.mBackStackChangeListeners.add(listener);
    }

    private Fragment getFragment(Bundle bundle, String key) {
        int index = bundle.getInt(key, -1);
        if (index == -1) {
            return null;
        }
        if (index >= this.mActive.size()) {
            throwException(new IllegalStateException("Fragment no longer exists for key " + key + ": index " + index));
        }
        Fragment f = (Fragment) this.mActive.get(index);
        if (f != null) {
            return f;
        }
        throwException(new IllegalStateException("Fragment no longer exists for key " + key + ": index " + index));
        return f;
    }

    public final List<Fragment> getFragments() {
        return this.mActive;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("FragmentManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        if (this.mParent != null) {
            DebugUtils.buildShortClassTag(this.mParent, sb);
        } else {
            DebugUtils.buildShortClassTag(this.mHost, sb);
        }
        sb.append("}}");
        return sb.toString();
    }

    public final void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        int N;
        int i;
        Fragment f;
        String innerPrefix = prefix + "    ";
        if (this.mActive != null) {
            N = this.mActive.size();
            if (N > 0) {
                writer.print(prefix);
                writer.print("Active Fragments in ");
                writer.print(Integer.toHexString(System.identityHashCode(this)));
                writer.println(":");
                for (i = 0; i < N; i++) {
                    f = (Fragment) this.mActive.get(i);
                    writer.print(prefix);
                    writer.print("  #");
                    writer.print(i);
                    writer.print(": ");
                    writer.println(f);
                    if (f != null) {
                        f.dump(innerPrefix, fd, writer, args);
                    }
                }
            }
        }
        if (this.mAdded != null) {
            N = this.mAdded.size();
            if (N > 0) {
                writer.print(prefix);
                writer.println("Added Fragments:");
                for (i = 0; i < N; i++) {
                    f = (Fragment) this.mAdded.get(i);
                    writer.print(prefix);
                    writer.print("  #");
                    writer.print(i);
                    writer.print(": ");
                    writer.println(f.toString());
                }
            }
        }
        if (this.mCreatedMenus != null) {
            N = this.mCreatedMenus.size();
            if (N > 0) {
                writer.print(prefix);
                writer.println("Fragments Created Menus:");
                for (i = 0; i < N; i++) {
                    f = (Fragment) this.mCreatedMenus.get(i);
                    writer.print(prefix);
                    writer.print("  #");
                    writer.print(i);
                    writer.print(": ");
                    writer.println(f.toString());
                }
            }
        }
        if (this.mBackStack != null) {
            N = this.mBackStack.size();
            if (N > 0) {
                writer.print(prefix);
                writer.println("Back Stack:");
                for (i = 0; i < N; i++) {
                    BackStackRecord bs = (BackStackRecord) this.mBackStack.get(i);
                    writer.print(prefix);
                    writer.print("  #");
                    writer.print(i);
                    writer.print(": ");
                    writer.println(bs.toString());
                    bs.dump$ec96877(innerPrefix, writer);
                }
            }
        }
        synchronized (this) {
            if (this.mBackStackIndices != null) {
                N = this.mBackStackIndices.size();
                if (N > 0) {
                    writer.print(prefix);
                    writer.println("Back Stack Indices:");
                    for (i = 0; i < N; i++) {
                        bs = (BackStackRecord) this.mBackStackIndices.get(i);
                        writer.print(prefix);
                        writer.print("  #");
                        writer.print(i);
                        writer.print(": ");
                        writer.println(bs);
                    }
                }
            }
            if (this.mAvailBackStackIndices != null && this.mAvailBackStackIndices.size() > 0) {
                writer.print(prefix);
                writer.print("mAvailBackStackIndices: ");
                writer.println(Arrays.toString(this.mAvailBackStackIndices.toArray()));
            }
        }
        if (this.mPendingActions != null) {
            N = this.mPendingActions.size();
            if (N > 0) {
                writer.print(prefix);
                writer.println("Pending Actions:");
                for (i = 0; i < N; i++) {
                    OpGenerator r = (OpGenerator) this.mPendingActions.get(i);
                    writer.print(prefix);
                    writer.print("  #");
                    writer.print(i);
                    writer.print(": ");
                    writer.println(r);
                }
            }
        }
        writer.print(prefix);
        writer.println("FragmentManager misc state:");
        writer.print(prefix);
        writer.print("  mHost=");
        writer.println(this.mHost);
        writer.print(prefix);
        writer.print("  mContainer=");
        writer.println(this.mContainer);
        if (this.mParent != null) {
            writer.print(prefix);
            writer.print("  mParent=");
            writer.println(this.mParent);
        }
        writer.print(prefix);
        writer.print("  mCurState=");
        writer.print(this.mCurState);
        writer.print(" mStateSaved=");
        writer.print(this.mStateSaved);
        writer.print(" mDestroyed=");
        writer.println(this.mDestroyed);
        if (this.mNeedMenuInvalidate) {
            writer.print(prefix);
            writer.print("  mNeedMenuInvalidate=");
            writer.println(this.mNeedMenuInvalidate);
        }
        if (this.mNoTransactionsBecause != null) {
            writer.print(prefix);
            writer.print("  mNoTransactionsBecause=");
            writer.println(this.mNoTransactionsBecause);
        }
        if (this.mAvailIndices != null && this.mAvailIndices.size() > 0) {
            writer.print(prefix);
            writer.print("  mAvailIndices: ");
            writer.println(Arrays.toString(this.mAvailIndices.toArray()));
        }
    }

    private static Animation makeOpenCloseAnimation$376f30fd(float startScale, float endScale, float startAlpha, float endAlpha) {
        AnimationSet set = new AnimationSet(false);
        ScaleAnimation scale = new ScaleAnimation(startScale, endScale, startScale, endScale, 1, 0.5f, 1, 0.5f);
        scale.setInterpolator(DECELERATE_QUINT);
        scale.setDuration(220);
        set.addAnimation(scale);
        AlphaAnimation alpha = new AlphaAnimation(startAlpha, endAlpha);
        alpha.setInterpolator(DECELERATE_CUBIC);
        alpha.setDuration(220);
        set.addAnimation(alpha);
        return set;
    }

    private static Animation makeFadeAnimation$424ea1bd(float start, float end) {
        AlphaAnimation anim = new AlphaAnimation(start, end);
        anim.setInterpolator(DECELERATE_CUBIC);
        anim.setDuration(220);
        return anim;
    }

    private Animation loadAnimation(Fragment fragment, int transit, boolean enter, int transitionStyle) {
        Animation animObj = fragment.onCreateAnimation(transit, enter, fragment.getNextAnim());
        if (animObj != null) {
            return animObj;
        }
        if (fragment.getNextAnim() != 0) {
            Animation anim = AnimationUtils.loadAnimation(this.mHost.mContext, fragment.getNextAnim());
            if (anim != null) {
                return anim;
            }
        }
        if (transit == 0) {
            return null;
        }
        int styleIndex;
        switch (transit) {
            case 4097:
                styleIndex = enter ? 1 : 2;
                break;
            case 4099:
                styleIndex = enter ? 5 : 6;
                break;
            case 8194:
                styleIndex = enter ? 3 : 4;
                break;
            default:
                styleIndex = -1;
                break;
        }
        if (styleIndex < 0) {
            return null;
        }
        switch (styleIndex) {
            case 1:
                return makeOpenCloseAnimation$376f30fd(1.125f, 1.0f, 0.0f, 1.0f);
            case 2:
                return makeOpenCloseAnimation$376f30fd(1.0f, 0.975f, 1.0f, 0.0f);
            case 3:
                return makeOpenCloseAnimation$376f30fd(0.975f, 1.0f, 0.0f, 1.0f);
            case 4:
                return makeOpenCloseAnimation$376f30fd(1.0f, 1.075f, 1.0f, 0.0f);
            case 5:
                return makeFadeAnimation$424ea1bd(0.0f, 1.0f);
            case 6:
                return makeFadeAnimation$424ea1bd(1.0f, 0.0f);
            default:
                if (transitionStyle == 0 && this.mHost.onHasWindowAnimations()) {
                    transitionStyle = this.mHost.onGetWindowAnimations();
                }
                if (transitionStyle == 0) {
                    return null;
                }
                return null;
        }
    }

    public final void performPendingDeferredStart(Fragment f) {
        if (!f.mDeferStart) {
            return;
        }
        if (this.mExecutingActions) {
            this.mHavePendingDeferredStart = true;
            return;
        }
        f.mDeferStart = false;
        moveToState(f, this.mCurState, 0, 0, false);
    }

    private static void setHWLayerAnimListenerIfAlpha(View v, Animation anim) {
        Object obj = null;
        if (v != null && anim != null) {
            if (VERSION.SDK_INT >= 19 && ViewCompat.getLayerType(v) == 0 && ViewCompat.hasOverlappingRendering(v)) {
                Object obj2;
                if (anim instanceof AlphaAnimation) {
                    obj2 = 1;
                } else {
                    if (anim instanceof AnimationSet) {
                        List animations = ((AnimationSet) anim).getAnimations();
                        for (int i = 0; i < animations.size(); i++) {
                            if (animations.get(i) instanceof AlphaAnimation) {
                                i = 1;
                                break;
                            }
                        }
                    }
                    obj2 = null;
                }
                if (obj2 != null) {
                    obj = 1;
                }
            }
            if (obj != null) {
                AnimationListener originalListener = null;
                try {
                    if (sAnimationListenerField == null) {
                        Field declaredField = Animation.class.getDeclaredField("mListener");
                        sAnimationListenerField = declaredField;
                        declaredField.setAccessible(true);
                    }
                    originalListener = (AnimationListener) sAnimationListenerField.get(anim);
                } catch (NoSuchFieldException e) {
                    Log.e("FragmentManager", "No field with the name mListener is found in Animation class", e);
                } catch (IllegalAccessException e2) {
                    Log.e("FragmentManager", "Cannot access Animation's mListener field", e2);
                }
                ViewCompat.setLayerType(v, 2, null);
                anim.setAnimationListener(new AnimateOnHWLayerIfNeededListener(v, anim, originalListener));
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final void moveToState(android.support.v4.app.Fragment r15, int r16, int r17, int r18, boolean r19) {
        /*
        r14 = this;
        r2 = r15.mAdded;
        if (r2 == 0) goto L_0x0008;
    L_0x0004:
        r2 = r15.mDetached;
        if (r2 == 0) goto L_0x000f;
    L_0x0008:
        r2 = 1;
        r0 = r16;
        if (r0 <= r2) goto L_0x000f;
    L_0x000d:
        r16 = 1;
    L_0x000f:
        r2 = r15.mRemoving;
        if (r2 == 0) goto L_0x001d;
    L_0x0013:
        r2 = r15.mState;
        r0 = r16;
        if (r0 <= r2) goto L_0x001d;
    L_0x0019:
        r0 = r15.mState;
        r16 = r0;
    L_0x001d:
        r2 = r15.mDeferStart;
        if (r2 == 0) goto L_0x002d;
    L_0x0021:
        r2 = r15.mState;
        r3 = 4;
        if (r2 >= r3) goto L_0x002d;
    L_0x0026:
        r2 = 3;
        r0 = r16;
        if (r0 <= r2) goto L_0x002d;
    L_0x002b:
        r16 = 3;
    L_0x002d:
        r2 = r15.mState;
        r0 = r16;
        if (r2 >= r0) goto L_0x02ca;
    L_0x0033:
        r2 = r15.mFromLayout;
        if (r2 == 0) goto L_0x003c;
    L_0x0037:
        r2 = r15.mInLayout;
        if (r2 != 0) goto L_0x003c;
    L_0x003b:
        return;
    L_0x003c:
        r2 = r15.getAnimatingAway();
        if (r2 == 0) goto L_0x0052;
    L_0x0042:
        r2 = 0;
        r15.setAnimatingAway(r2);
        r4 = r15.getStateAfterAnimating();
        r5 = 0;
        r6 = 0;
        r7 = 1;
        r2 = r14;
        r3 = r15;
        r2.moveToState(r3, r4, r5, r6, r7);
    L_0x0052:
        r2 = r15.mState;
        switch(r2) {
            case 0: goto L_0x008e;
            case 1: goto L_0x0187;
            case 2: goto L_0x026d;
            case 3: goto L_0x0275;
            case 4: goto L_0x0281;
            default: goto L_0x0057;
        };
    L_0x0057:
        r2 = r15.mState;
        r0 = r16;
        if (r2 == r0) goto L_0x003b;
    L_0x005d:
        r2 = "FragmentManager";
        r3 = new java.lang.StringBuilder;
        r4 = "moveToState: Fragment state for ";
        r3.<init>(r4);
        r3 = r3.append(r15);
        r4 = " not updated inline; expected state ";
        r3 = r3.append(r4);
        r0 = r16;
        r3 = r3.append(r0);
        r4 = " found ";
        r3 = r3.append(r4);
        r4 = r15.mState;
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Log.w(r2, r3);
        r0 = r16;
        r15.mState = r0;
        goto L_0x003b;
    L_0x008e:
        r2 = r15.mSavedFragmentState;
        if (r2 == 0) goto L_0x00db;
    L_0x0092:
        r2 = r15.mSavedFragmentState;
        r3 = r14.mHost;
        r3 = r3.mContext;
        r3 = r3.getClassLoader();
        r2.setClassLoader(r3);
        r2 = r15.mSavedFragmentState;
        r3 = "android:view_state";
        r2 = r2.getSparseParcelableArray(r3);
        r15.mSavedViewState = r2;
        r2 = r15.mSavedFragmentState;
        r3 = "android:target_state";
        r2 = r14.getFragment(r2, r3);
        r15.mTarget = r2;
        r2 = r15.mTarget;
        if (r2 == 0) goto L_0x00c2;
    L_0x00b7:
        r2 = r15.mSavedFragmentState;
        r3 = "android:target_req_state";
        r4 = 0;
        r2 = r2.getInt(r3, r4);
        r15.mTargetRequestCode = r2;
    L_0x00c2:
        r2 = r15.mSavedFragmentState;
        r3 = "android:user_visible_hint";
        r4 = 1;
        r2 = r2.getBoolean(r3, r4);
        r15.mUserVisibleHint = r2;
        r2 = r15.mUserVisibleHint;
        if (r2 != 0) goto L_0x00db;
    L_0x00d1:
        r2 = 1;
        r15.mDeferStart = r2;
        r2 = 3;
        r0 = r16;
        if (r0 <= r2) goto L_0x00db;
    L_0x00d9:
        r16 = 3;
    L_0x00db:
        r2 = r14.mHost;
        r15.mHost = r2;
        r2 = r14.mParent;
        r15.mParentFragment = r2;
        r2 = r14.mParent;
        if (r2 == 0) goto L_0x011e;
    L_0x00e7:
        r2 = r14.mParent;
        r2 = r2.mChildFragmentManager;
    L_0x00eb:
        r15.mFragmentManager = r2;
        r2 = r14.mHost;
        r2 = r2.mContext;
        r3 = 0;
        r14.dispatchOnFragmentPreAttached(r15, r2, r3);
        r2 = 0;
        r15.mCalled = r2;
        r2 = r14.mHost;
        r2 = r2.mContext;
        r15.onAttach(r2);
        r2 = r15.mCalled;
        if (r2 != 0) goto L_0x0123;
    L_0x0103:
        r2 = new android.support.v4.app.SuperNotCalledException;
        r3 = new java.lang.StringBuilder;
        r4 = "Fragment ";
        r3.<init>(r4);
        r3 = r3.append(r15);
        r4 = " did not call through to super.onAttach()";
        r3 = r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x011e:
        r2 = r14.mHost;
        r2 = r2.mFragmentManager;
        goto L_0x00eb;
    L_0x0123:
        r2 = r15.mParentFragment;
        if (r2 != 0) goto L_0x0295;
    L_0x0127:
        r2 = r14.mHost;
        r2.onAttachFragment(r15);
    L_0x012c:
        r2 = r14.mHost;
        r2 = r2.mContext;
        r3 = 0;
        r14.dispatchOnFragmentAttached(r15, r2, r3);
        r2 = r15.mRetaining;
        if (r2 != 0) goto L_0x029c;
    L_0x0138:
        r2 = r15.mSavedFragmentState;
        r15.performCreate(r2);
        r2 = r15.mSavedFragmentState;
        r3 = 0;
        r14.dispatchOnFragmentCreated(r15, r2, r3);
    L_0x0143:
        r2 = 0;
        r15.mRetaining = r2;
        r2 = r15.mFromLayout;
        if (r2 == 0) goto L_0x0187;
    L_0x014a:
        r2 = r15.mSavedFragmentState;
        r2 = r15.getLayoutInflater(r2);
        r3 = 0;
        r4 = r15.mSavedFragmentState;
        r2 = r15.performCreateView(r2, r3, r4);
        r15.mView = r2;
        r2 = r15.mView;
        if (r2 == 0) goto L_0x02b0;
    L_0x015d:
        r2 = r15.mView;
        r15.mInnerView = r2;
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 11;
        if (r2 < r3) goto L_0x02a6;
    L_0x0167:
        r2 = r15.mView;
        r3 = 0;
        android.support.v4.view.ViewCompat.setSaveFromParentEnabled(r2, r3);
    L_0x016d:
        r2 = r15.mHidden;
        if (r2 == 0) goto L_0x0178;
    L_0x0171:
        r2 = r15.mView;
        r3 = 8;
        r2.setVisibility(r3);
    L_0x0178:
        r2 = r15.mView;
        r3 = r15.mSavedFragmentState;
        r15.onViewCreated(r2, r3);
        r2 = r15.mView;
        r3 = r15.mSavedFragmentState;
        r4 = 0;
        r14.dispatchOnFragmentViewCreated(r15, r2, r3, r4);
    L_0x0187:
        r2 = 1;
        r0 = r16;
        if (r0 <= r2) goto L_0x026d;
    L_0x018c:
        r2 = r15.mFromLayout;
        if (r2 != 0) goto L_0x0256;
    L_0x0190:
        r9 = 0;
        r2 = r15.mContainerId;
        if (r2 == 0) goto L_0x0202;
    L_0x0195:
        r2 = r15.mContainerId;
        r3 = -1;
        if (r2 != r3) goto L_0x01b7;
    L_0x019a:
        r2 = new java.lang.IllegalArgumentException;
        r3 = new java.lang.StringBuilder;
        r4 = "Cannot create fragment ";
        r3.<init>(r4);
        r3 = r3.append(r15);
        r4 = " for a container view with no id";
        r3 = r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3);
        r14.throwException(r2);
    L_0x01b7:
        r2 = r14.mContainer;
        r3 = r15.mContainerId;
        r9 = r2.onFindViewById(r3);
        r9 = (android.view.ViewGroup) r9;
        if (r9 != 0) goto L_0x0202;
    L_0x01c3:
        r2 = r15.mRestored;
        if (r2 != 0) goto L_0x0202;
    L_0x01c7:
        r2 = r15.getResources();	 Catch:{ NotFoundException -> 0x02b5 }
        r3 = r15.mContainerId;	 Catch:{ NotFoundException -> 0x02b5 }
        r11 = r2.getResourceName(r3);	 Catch:{ NotFoundException -> 0x02b5 }
    L_0x01d1:
        r2 = new java.lang.IllegalArgumentException;
        r3 = new java.lang.StringBuilder;
        r4 = "No view found for id 0x";
        r3.<init>(r4);
        r4 = r15.mContainerId;
        r4 = java.lang.Integer.toHexString(r4);
        r3 = r3.append(r4);
        r4 = " (";
        r3 = r3.append(r4);
        r3 = r3.append(r11);
        r4 = ") for fragment ";
        r3 = r3.append(r4);
        r3 = r3.append(r15);
        r3 = r3.toString();
        r2.<init>(r3);
        r14.throwException(r2);
    L_0x0202:
        r15.mContainer = r9;
        r2 = r15.mSavedFragmentState;
        r2 = r15.getLayoutInflater(r2);
        r3 = r15.mSavedFragmentState;
        r2 = r15.performCreateView(r2, r9, r3);
        r15.mView = r2;
        r2 = r15.mView;
        if (r2 == 0) goto L_0x02c6;
    L_0x0216:
        r2 = r15.mView;
        r15.mInnerView = r2;
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 11;
        if (r2 < r3) goto L_0x02ba;
    L_0x0220:
        r2 = r15.mView;
        r3 = 0;
        android.support.v4.view.ViewCompat.setSaveFromParentEnabled(r2, r3);
    L_0x0226:
        if (r9 == 0) goto L_0x022d;
    L_0x0228:
        r2 = r15.mView;
        r9.addView(r2);
    L_0x022d:
        r2 = r15.mHidden;
        if (r2 == 0) goto L_0x0238;
    L_0x0231:
        r2 = r15.mView;
        r3 = 8;
        r2.setVisibility(r3);
    L_0x0238:
        r2 = r15.mView;
        r3 = r15.mSavedFragmentState;
        r15.onViewCreated(r2, r3);
        r2 = r15.mView;
        r3 = r15.mSavedFragmentState;
        r4 = 0;
        r14.dispatchOnFragmentViewCreated(r15, r2, r3, r4);
        r2 = r15.mView;
        r2 = r2.getVisibility();
        if (r2 != 0) goto L_0x02c4;
    L_0x024f:
        r2 = r15.mContainer;
        if (r2 == 0) goto L_0x02c4;
    L_0x0253:
        r2 = 1;
    L_0x0254:
        r15.mIsNewlyAdded = r2;
    L_0x0256:
        r2 = r15.mSavedFragmentState;
        r15.performActivityCreated(r2);
        r2 = r15.mSavedFragmentState;
        r3 = 0;
        r14.dispatchOnFragmentActivityCreated(r15, r2, r3);
        r2 = r15.mView;
        if (r2 == 0) goto L_0x026a;
    L_0x0265:
        r2 = r15.mSavedFragmentState;
        r15.restoreViewState(r2);
    L_0x026a:
        r2 = 0;
        r15.mSavedFragmentState = r2;
    L_0x026d:
        r2 = 2;
        r0 = r16;
        if (r0 <= r2) goto L_0x0275;
    L_0x0272:
        r2 = 3;
        r15.mState = r2;
    L_0x0275:
        r2 = 3;
        r0 = r16;
        if (r0 <= r2) goto L_0x0281;
    L_0x027a:
        r15.performStart();
        r2 = 0;
        r14.dispatchOnFragmentStarted(r15, r2);
    L_0x0281:
        r2 = 4;
        r0 = r16;
        if (r0 <= r2) goto L_0x0057;
    L_0x0286:
        r15.performResume();
        r2 = 0;
        r14.dispatchOnFragmentResumed(r15, r2);
        r2 = 0;
        r15.mSavedFragmentState = r2;
        r2 = 0;
        r15.mSavedViewState = r2;
        goto L_0x0057;
    L_0x0295:
        r2 = r15.mParentFragment;
        r2.onAttachFragment(r15);
        goto L_0x012c;
    L_0x029c:
        r2 = r15.mSavedFragmentState;
        r15.restoreChildFragmentState(r2);
        r2 = 1;
        r15.mState = r2;
        goto L_0x0143;
    L_0x02a6:
        r2 = r15.mView;
        r2 = android.support.v4.app.NoSaveStateFrameLayout.wrap(r2);
        r15.mView = r2;
        goto L_0x016d;
    L_0x02b0:
        r2 = 0;
        r15.mInnerView = r2;
        goto L_0x0187;
    L_0x02b5:
        r2 = move-exception;
        r11 = "unknown";
        goto L_0x01d1;
    L_0x02ba:
        r2 = r15.mView;
        r2 = android.support.v4.app.NoSaveStateFrameLayout.wrap(r2);
        r15.mView = r2;
        goto L_0x0226;
    L_0x02c4:
        r2 = 0;
        goto L_0x0254;
    L_0x02c6:
        r2 = 0;
        r15.mInnerView = r2;
        goto L_0x0256;
    L_0x02ca:
        r2 = r15.mState;
        r0 = r16;
        if (r2 <= r0) goto L_0x0057;
    L_0x02d0:
        r2 = r15.mState;
        switch(r2) {
            case 1: goto L_0x02d7;
            case 2: goto L_0x031b;
            case 3: goto L_0x0313;
            case 4: goto L_0x0307;
            case 5: goto L_0x02fb;
            default: goto L_0x02d5;
        };
    L_0x02d5:
        goto L_0x0057;
    L_0x02d7:
        if (r16 > 0) goto L_0x0057;
    L_0x02d9:
        r2 = r14.mDestroyed;
        if (r2 == 0) goto L_0x02ee;
    L_0x02dd:
        r2 = r15.getAnimatingAway();
        if (r2 == 0) goto L_0x02ee;
    L_0x02e3:
        r12 = r15.getAnimatingAway();
        r2 = 0;
        r15.setAnimatingAway(r2);
        r12.clearAnimation();
    L_0x02ee:
        r2 = r15.getAnimatingAway();
        if (r2 == 0) goto L_0x0392;
    L_0x02f4:
        r15.setStateAfterAnimating(r16);
        r16 = 1;
        goto L_0x0057;
    L_0x02fb:
        r2 = 5;
        r0 = r16;
        if (r0 >= r2) goto L_0x0307;
    L_0x0300:
        r15.performPause();
        r2 = 0;
        r14.dispatchOnFragmentPaused(r15, r2);
    L_0x0307:
        r2 = 4;
        r0 = r16;
        if (r0 >= r2) goto L_0x0313;
    L_0x030c:
        r15.performStop();
        r2 = 0;
        r14.dispatchOnFragmentStopped(r15, r2);
    L_0x0313:
        r2 = 3;
        r0 = r16;
        if (r0 >= r2) goto L_0x031b;
    L_0x0318:
        r15.performReallyStop();
    L_0x031b:
        r2 = 2;
        r0 = r16;
        if (r0 >= r2) goto L_0x02d7;
    L_0x0320:
        r2 = r15.mView;
        if (r2 == 0) goto L_0x0333;
    L_0x0324:
        r2 = r14.mHost;
        r2 = r2.onShouldSaveFragmentState$6585081f();
        if (r2 == 0) goto L_0x0333;
    L_0x032c:
        r2 = r15.mSavedViewState;
        if (r2 != 0) goto L_0x0333;
    L_0x0330:
        r14.saveFragmentViewState(r15);
    L_0x0333:
        r15.performDestroyView();
        r2 = 0;
        r14.dispatchOnFragmentViewDestroyed(r15, r2);
        r2 = r15.mView;
        if (r2 == 0) goto L_0x0387;
    L_0x033e:
        r2 = r15.mContainer;
        if (r2 == 0) goto L_0x0387;
    L_0x0342:
        r8 = 0;
        r2 = r14.mCurState;
        if (r2 <= 0) goto L_0x0363;
    L_0x0347:
        r2 = r14.mDestroyed;
        if (r2 != 0) goto L_0x0363;
    L_0x034b:
        r2 = r15.mView;
        r2 = r2.getVisibility();
        if (r2 != 0) goto L_0x0363;
    L_0x0353:
        r2 = r15.mPostponedAlpha;
        r3 = 0;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 < 0) goto L_0x0363;
    L_0x035a:
        r2 = 0;
        r0 = r17;
        r1 = r18;
        r8 = r14.loadAnimation(r15, r0, r2, r1);
    L_0x0363:
        r2 = 0;
        r15.mPostponedAlpha = r2;
        if (r8 == 0) goto L_0x0380;
    L_0x0368:
        r10 = r15;
        r2 = r15.mView;
        r15.setAnimatingAway(r2);
        r15.setStateAfterAnimating(r16);
        r13 = r15.mView;
        r2 = new android.support.v4.app.FragmentManagerImpl$2;
        r2.<init>(r13, r8, r10);
        r8.setAnimationListener(r2);
        r2 = r15.mView;
        r2.startAnimation(r8);
    L_0x0380:
        r2 = r15.mContainer;
        r3 = r15.mView;
        r2.removeView(r3);
    L_0x0387:
        r2 = 0;
        r15.mContainer = r2;
        r2 = 0;
        r15.mView = r2;
        r2 = 0;
        r15.mInnerView = r2;
        goto L_0x02d7;
    L_0x0392:
        r2 = r15.mRetaining;
        if (r2 != 0) goto L_0x03d8;
    L_0x0396:
        r15.performDestroy();
        r2 = 0;
        r14.dispatchOnFragmentDestroyed(r15, r2);
    L_0x039d:
        r15.performDetach();
        r2 = 0;
        r14.dispatchOnFragmentDetached(r15, r2);
        if (r19 != 0) goto L_0x0057;
    L_0x03a6:
        r2 = r15.mRetaining;
        if (r2 != 0) goto L_0x03dc;
    L_0x03aa:
        r2 = r15.mIndex;
        if (r2 < 0) goto L_0x0057;
    L_0x03ae:
        r2 = r14.mActive;
        r3 = r15.mIndex;
        r4 = 0;
        r2.set(r3, r4);
        r2 = r14.mAvailIndices;
        if (r2 != 0) goto L_0x03c1;
    L_0x03ba:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r14.mAvailIndices = r2;
    L_0x03c1:
        r2 = r14.mAvailIndices;
        r3 = r15.mIndex;
        r3 = java.lang.Integer.valueOf(r3);
        r2.add(r3);
        r2 = r14.mHost;
        r3 = r15.mWho;
        r2.inactivateFragment(r3);
        r15.initState();
        goto L_0x0057;
    L_0x03d8:
        r2 = 0;
        r15.mState = r2;
        goto L_0x039d;
    L_0x03dc:
        r2 = 0;
        r15.mHost = r2;
        r2 = 0;
        r15.mParentFragment = r2;
        r2 = 0;
        r15.mFragmentManager = r2;
        goto L_0x0057;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentManagerImpl.moveToState(android.support.v4.app.Fragment, int, int, int, boolean):void");
    }

    private void moveToState(Fragment f) {
        moveToState(f, this.mCurState, 0, 0, false);
    }

    final void moveFragmentToExpectedState(Fragment f) {
        Fragment underFragment = null;
        if (f != null) {
            int indexOf;
            int nextState = this.mCurState;
            if (f.mRemoving) {
                if (f.isInBackStack()) {
                    nextState = Math.min(nextState, 1);
                } else {
                    nextState = Math.min(nextState, 0);
                }
            }
            moveToState(f, nextState, f.getNextTransition(), f.getNextTransitionStyle(), false);
            if (f.mView != null) {
                ViewGroup viewGroup = f.mContainer;
                View view = f.mView;
                if (viewGroup != null && view != null) {
                    for (indexOf = this.mAdded.indexOf(f) - 1; indexOf >= 0; indexOf--) {
                        Fragment fragment = (Fragment) this.mAdded.get(indexOf);
                        if (fragment.mContainer == viewGroup && fragment.mView != null) {
                            underFragment = fragment;
                            break;
                        }
                    }
                }
                if (underFragment != null) {
                    View underView = underFragment.mView;
                    ViewGroup container = f.mContainer;
                    int underIndex = container.indexOfChild(underView);
                    int viewIndex = container.indexOfChild(f.mView);
                    if (viewIndex < underIndex) {
                        container.removeViewAt(viewIndex);
                        container.addView(f.mView, underIndex);
                    }
                }
                if (f.mIsNewlyAdded && f.mContainer != null) {
                    if (VERSION.SDK_INT < 11) {
                        f.mView.setVisibility(0);
                    } else if (f.mPostponedAlpha > 0.0f) {
                        f.mView.setAlpha(f.mPostponedAlpha);
                    }
                    f.mPostponedAlpha = 0.0f;
                    f.mIsNewlyAdded = false;
                    Animation anim = loadAnimation(f, f.getNextTransition(), true, f.getNextTransitionStyle());
                    if (anim != null) {
                        setHWLayerAnimListenerIfAlpha(f.mView, anim);
                        f.mView.startAnimation(anim);
                    }
                }
            }
            if (f.mHiddenChanged) {
                if (f.mView != null) {
                    boolean z;
                    int i;
                    indexOf = f.getNextTransition();
                    if (f.mHidden) {
                        z = false;
                    } else {
                        z = true;
                    }
                    Animation loadAnimation = loadAnimation(f, indexOf, z, f.getNextTransitionStyle());
                    if (loadAnimation != null) {
                        setHWLayerAnimListenerIfAlpha(f.mView, loadAnimation);
                        f.mView.startAnimation(loadAnimation);
                        setHWLayerAnimListenerIfAlpha(f.mView, loadAnimation);
                        loadAnimation.start();
                    }
                    if (!f.mHidden || f.isHideReplaced()) {
                        i = 0;
                    } else {
                        i = 8;
                    }
                    f.mView.setVisibility(i);
                    if (f.isHideReplaced()) {
                        f.setHideReplaced(false);
                    }
                }
                if (f.mAdded && f.mHasMenu && f.mMenuVisible) {
                    this.mNeedMenuInvalidate = true;
                }
                f.mHiddenChanged = false;
                f.onHiddenChanged(f.mHidden);
            }
        }
    }

    final void moveToState(int newState, boolean always) {
        if (this.mHost == null && newState != 0) {
            throw new IllegalStateException("No activity");
        } else if (always || newState != this.mCurState) {
            this.mCurState = newState;
            if (this.mActive != null) {
                int i;
                Fragment f;
                boolean loadersRunning = false;
                if (this.mAdded != null) {
                    int numAdded = this.mAdded.size();
                    for (i = 0; i < numAdded; i++) {
                        f = (Fragment) this.mAdded.get(i);
                        moveFragmentToExpectedState(f);
                        if (f.mLoaderManager != null) {
                            loadersRunning |= f.mLoaderManager.hasRunningLoaders();
                        }
                    }
                }
                int numActive = this.mActive.size();
                for (i = 0; i < numActive; i++) {
                    f = (Fragment) this.mActive.get(i);
                    if (f != null && ((f.mRemoving || f.mDetached) && !f.mIsNewlyAdded)) {
                        moveFragmentToExpectedState(f);
                        if (f.mLoaderManager != null) {
                            loadersRunning |= f.mLoaderManager.hasRunningLoaders();
                        }
                    }
                }
                if (!loadersRunning) {
                    startPendingDeferredFragments();
                }
                if (this.mNeedMenuInvalidate && this.mHost != null && this.mCurState == 5) {
                    this.mHost.onSupportInvalidateOptionsMenu();
                    this.mNeedMenuInvalidate = false;
                }
            }
        }
    }

    final void startPendingDeferredFragments() {
        if (this.mActive != null) {
            for (int i = 0; i < this.mActive.size(); i++) {
                Fragment f = (Fragment) this.mActive.get(i);
                if (f != null) {
                    performPendingDeferredStart(f);
                }
            }
        }
    }

    final void makeActive(Fragment f) {
        if (f.mIndex < 0) {
            if (this.mAvailIndices == null || this.mAvailIndices.size() <= 0) {
                if (this.mActive == null) {
                    this.mActive = new ArrayList();
                }
                f.setIndex(this.mActive.size(), this.mParent);
                this.mActive.add(f);
                return;
            }
            f.setIndex(((Integer) this.mAvailIndices.remove(this.mAvailIndices.size() - 1)).intValue(), this.mParent);
            this.mActive.set(f.mIndex, f);
        }
    }

    public final void addFragment(Fragment fragment, boolean moveToStateNow) {
        if (this.mAdded == null) {
            this.mAdded = new ArrayList();
        }
        makeActive(fragment);
        if (!fragment.mDetached) {
            if (this.mAdded.contains(fragment)) {
                throw new IllegalStateException("Fragment already added: " + fragment);
            }
            this.mAdded.add(fragment);
            fragment.mAdded = true;
            fragment.mRemoving = false;
            if (fragment.mView == null) {
                fragment.mHiddenChanged = false;
            }
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            if (moveToStateNow) {
                moveToState(fragment);
            }
        }
    }

    public final void removeFragment(Fragment fragment) {
        boolean inactive;
        if (fragment.isInBackStack()) {
            inactive = false;
        } else {
            inactive = true;
        }
        if (!fragment.mDetached || inactive) {
            if (this.mAdded != null) {
                this.mAdded.remove(fragment);
            }
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.mAdded = false;
            fragment.mRemoving = true;
        }
    }

    public static void hideFragment(Fragment fragment) {
        boolean z = true;
        if (!fragment.mHidden) {
            fragment.mHidden = true;
            if (fragment.mHiddenChanged) {
                z = false;
            }
            fragment.mHiddenChanged = z;
        }
    }

    public static void showFragment(Fragment fragment) {
        boolean z = false;
        if (fragment.mHidden) {
            fragment.mHidden = false;
            if (!fragment.mHiddenChanged) {
                z = true;
            }
            fragment.mHiddenChanged = z;
        }
    }

    public final void detachFragment(Fragment fragment) {
        if (!fragment.mDetached) {
            fragment.mDetached = true;
            if (fragment.mAdded) {
                if (this.mAdded != null) {
                    this.mAdded.remove(fragment);
                }
                if (fragment.mHasMenu && fragment.mMenuVisible) {
                    this.mNeedMenuInvalidate = true;
                }
                fragment.mAdded = false;
            }
        }
    }

    public final void attachFragment(Fragment fragment) {
        if (fragment.mDetached) {
            fragment.mDetached = false;
            if (!fragment.mAdded) {
                if (this.mAdded == null) {
                    this.mAdded = new ArrayList();
                }
                if (this.mAdded.contains(fragment)) {
                    throw new IllegalStateException("Fragment already added: " + fragment);
                }
                this.mAdded.add(fragment);
                fragment.mAdded = true;
                if (fragment.mHasMenu && fragment.mMenuVisible) {
                    this.mNeedMenuInvalidate = true;
                }
            }
        }
    }

    public final Fragment findFragmentById(int id) {
        int i;
        Fragment f;
        if (this.mAdded != null) {
            for (i = this.mAdded.size() - 1; i >= 0; i--) {
                f = (Fragment) this.mAdded.get(i);
                if (f != null && f.mFragmentId == id) {
                    return f;
                }
            }
        }
        if (this.mActive != null) {
            for (i = this.mActive.size() - 1; i >= 0; i--) {
                f = (Fragment) this.mActive.get(i);
                if (f != null && f.mFragmentId == id) {
                    return f;
                }
            }
        }
        return null;
    }

    public final Fragment findFragmentByTag(String tag) {
        int i;
        Fragment f;
        if (!(this.mAdded == null || tag == null)) {
            for (i = this.mAdded.size() - 1; i >= 0; i--) {
                f = (Fragment) this.mAdded.get(i);
                if (f != null && tag.equals(f.mTag)) {
                    return f;
                }
            }
        }
        if (!(this.mActive == null || tag == null)) {
            for (i = this.mActive.size() - 1; i >= 0; i--) {
                f = (Fragment) this.mActive.get(i);
                if (f != null && tag.equals(f.mTag)) {
                    return f;
                }
            }
        }
        return null;
    }

    public final Fragment findFragmentByWho(String who) {
        if (!(this.mActive == null || who == null)) {
            for (int i = this.mActive.size() - 1; i >= 0; i--) {
                Fragment f = (Fragment) this.mActive.get(i);
                if (f != null) {
                    f = f.findFragmentByWho(who);
                    if (f != null) {
                        return f;
                    }
                }
            }
        }
        return null;
    }

    private void checkStateLoss() {
        if (this.mStateSaved) {
            throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
        } else if (this.mNoTransactionsBecause != null) {
            throw new IllegalStateException("Can not perform this action inside of " + this.mNoTransactionsBecause);
        }
    }

    public final void enqueueAction(OpGenerator action, boolean allowStateLoss) {
        if (!allowStateLoss) {
            checkStateLoss();
        }
        synchronized (this) {
            if (this.mDestroyed || this.mHost == null) {
                throw new IllegalStateException("Activity has been destroyed");
            }
            if (this.mPendingActions == null) {
                this.mPendingActions = new ArrayList();
            }
            this.mPendingActions.add(action);
            scheduleCommit();
        }
    }

    private void scheduleCommit() {
        boolean pendingReady = true;
        synchronized (this) {
            boolean postponeReady;
            if (this.mPostponedTransactions == null || this.mPostponedTransactions.isEmpty()) {
                postponeReady = false;
            } else {
                postponeReady = true;
            }
            if (this.mPendingActions == null || this.mPendingActions.size() != 1) {
                pendingReady = false;
            }
            if (postponeReady || pendingReady) {
                this.mHost.getHandler().removeCallbacks(this.mExecCommit);
                this.mHost.getHandler().post(this.mExecCommit);
            }
        }
    }

    public final int allocBackStackIndex(BackStackRecord bse) {
        synchronized (this) {
            if (this.mAvailBackStackIndices == null || this.mAvailBackStackIndices.size() <= 0) {
                if (this.mBackStackIndices == null) {
                    this.mBackStackIndices = new ArrayList();
                }
                int index = this.mBackStackIndices.size();
                this.mBackStackIndices.add(bse);
                return index;
            }
            index = ((Integer) this.mAvailBackStackIndices.remove(this.mAvailBackStackIndices.size() - 1)).intValue();
            this.mBackStackIndices.set(index, bse);
            return index;
        }
    }

    private void setBackStackIndex(int index, BackStackRecord bse) {
        synchronized (this) {
            if (this.mBackStackIndices == null) {
                this.mBackStackIndices = new ArrayList();
            }
            int N = this.mBackStackIndices.size();
            if (index < N) {
                this.mBackStackIndices.set(index, bse);
            } else {
                while (N < index) {
                    this.mBackStackIndices.add(null);
                    if (this.mAvailBackStackIndices == null) {
                        this.mAvailBackStackIndices = new ArrayList();
                    }
                    this.mAvailBackStackIndices.add(Integer.valueOf(N));
                    N++;
                }
                this.mBackStackIndices.add(bse);
            }
        }
    }

    private void ensureExecReady(boolean allowStateLoss) {
        if (this.mExecutingActions) {
            throw new IllegalStateException("FragmentManager is already executing transactions");
        } else if (Looper.myLooper() != this.mHost.getHandler().getLooper()) {
            throw new IllegalStateException("Must be called from main thread of fragment host");
        } else {
            if (!allowStateLoss) {
                checkStateLoss();
            }
            if (this.mTmpRecords == null) {
                this.mTmpRecords = new ArrayList();
                this.mTmpIsPop = new ArrayList();
            }
            this.mExecutingActions = true;
            try {
                executePostponedTransaction(null, null);
            } finally {
                this.mExecutingActions = false;
            }
        }
    }

    public final void execSingleAction(OpGenerator action, boolean allowStateLoss) {
        ensureExecReady(true);
        if (action.generateOps(this.mTmpRecords, this.mTmpIsPop)) {
            this.mExecutingActions = true;
            try {
                optimizeAndExecuteOps(this.mTmpRecords, this.mTmpIsPop);
            } finally {
                cleanupExec();
            }
        }
        doPendingDeferredStart();
    }

    private void cleanupExec() {
        this.mExecutingActions = false;
        this.mTmpIsPop.clear();
        this.mTmpRecords.clear();
    }

    private void executePostponedTransaction(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop) {
        int numPostponed = this.mPostponedTransactions == null ? 0 : this.mPostponedTransactions.size();
        int i = 0;
        while (i < numPostponed) {
            int index;
            StartEnterTransitionListener listener = (StartEnterTransitionListener) this.mPostponedTransactions.get(i);
            if (!(records == null || listener.mIsBack)) {
                index = records.indexOf(listener.mRecord);
                if (index != -1 && ((Boolean) isRecordPop.get(index)).booleanValue()) {
                    listener.cancelTransaction();
                    i++;
                }
            }
            if (listener.isReady() || (records != null && listener.mRecord.interactsWith(records, 0, records.size()))) {
                this.mPostponedTransactions.remove(i);
                i--;
                numPostponed--;
                if (!(records == null || listener.mIsBack)) {
                    index = records.indexOf(listener.mRecord);
                    if (index != -1 && ((Boolean) isRecordPop.get(index)).booleanValue()) {
                        listener.cancelTransaction();
                    }
                }
                listener.completeTransaction();
            }
            i++;
        }
    }

    private void optimizeAndExecuteOps(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop) {
        if (records != null && !records.isEmpty()) {
            if (isRecordPop == null || records.size() != isRecordPop.size()) {
                throw new IllegalStateException("Internal error with the back stack records");
            }
            executePostponedTransaction(records, isRecordPop);
            int numRecords = records.size();
            int startIndex = 0;
            int recordNum = 0;
            while (recordNum < numRecords) {
                if (!((BackStackRecord) records.get(recordNum)).mAllowOptimization) {
                    if (startIndex != recordNum) {
                        executeOpsTogether(records, isRecordPop, startIndex, recordNum);
                    }
                    int optimizeEnd = recordNum + 1;
                    if (((Boolean) isRecordPop.get(recordNum)).booleanValue()) {
                        while (optimizeEnd < numRecords && ((Boolean) isRecordPop.get(optimizeEnd)).booleanValue() && !((BackStackRecord) records.get(optimizeEnd)).mAllowOptimization) {
                            optimizeEnd++;
                        }
                    }
                    executeOpsTogether(records, isRecordPop, recordNum, optimizeEnd);
                    startIndex = optimizeEnd;
                    recordNum = optimizeEnd - 1;
                }
                recordNum++;
            }
            if (startIndex != numRecords) {
                executeOpsTogether(records, isRecordPop, startIndex, numRecords);
            }
        }
    }

    private void executeOpsTogether(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, int startIndex, int endIndex) {
        int recordNum;
        boolean allowOptimization = ((BackStackRecord) records.get(startIndex)).mAllowOptimization;
        boolean addToBackStack = false;
        if (this.mTmpAddedFragments == null) {
            this.mTmpAddedFragments = new ArrayList();
        } else {
            this.mTmpAddedFragments.clear();
        }
        if (this.mAdded != null) {
            this.mTmpAddedFragments.addAll(this.mAdded);
        }
        for (recordNum = startIndex; recordNum < endIndex; recordNum++) {
            BackStackRecord record = (BackStackRecord) records.get(recordNum);
            if (((Boolean) isRecordPop.get(recordNum)).booleanValue()) {
                record.trackAddedFragmentsInPop(this.mTmpAddedFragments);
            } else {
                record.expandReplaceOps(this.mTmpAddedFragments);
            }
            if (addToBackStack || record.mAddToBackStack) {
                addToBackStack = true;
            } else {
                addToBackStack = false;
            }
        }
        this.mTmpAddedFragments.clear();
        if (!allowOptimization) {
            FragmentTransition.startTransitions(this, records, isRecordPop, startIndex, endIndex, false);
        }
        executeOps(records, isRecordPop, startIndex, endIndex);
        int postponeIndex = endIndex;
        if (allowOptimization) {
            ArraySet<Fragment> addedFragments = new ArraySet();
            addAddedFragments(addedFragments);
            postponeIndex = postponePostponableTransactions(records, isRecordPop, startIndex, endIndex, addedFragments);
            makeRemovedFragmentsInvisible(addedFragments);
        }
        if (postponeIndex != startIndex && allowOptimization) {
            FragmentTransition.startTransitions(this, records, isRecordPop, startIndex, postponeIndex, true);
            moveToState(this.mCurState, true);
        }
        for (recordNum = startIndex; recordNum < endIndex; recordNum++) {
            record = (BackStackRecord) records.get(recordNum);
            if (((Boolean) isRecordPop.get(recordNum)).booleanValue() && record.mIndex >= 0) {
                int i = record.mIndex;
                synchronized (this) {
                    this.mBackStackIndices.set(i, null);
                    if (this.mAvailBackStackIndices == null) {
                        this.mAvailBackStackIndices = new ArrayList();
                    }
                    this.mAvailBackStackIndices.add(Integer.valueOf(i));
                }
                record.mIndex = -1;
            }
        }
        if (addToBackStack) {
            reportBackStackChanged();
        }
    }

    private static void makeRemovedFragmentsInvisible(ArraySet<Fragment> fragments) {
        int numAdded = fragments.size();
        for (int i = 0; i < numAdded; i++) {
            Fragment fragment = (Fragment) fragments.valueAt(i);
            if (!fragment.mAdded) {
                View view = fragment.getView();
                if (VERSION.SDK_INT < 11) {
                    fragment.getView().setVisibility(4);
                } else {
                    fragment.mPostponedAlpha = view.getAlpha();
                    view.setAlpha(0.0f);
                }
            }
        }
    }

    private int postponePostponableTransactions(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, int startIndex, int endIndex, ArraySet<Fragment> added) {
        int postponeIndex = endIndex;
        int i = endIndex - 1;
        while (i >= startIndex) {
            boolean isPostponed;
            BackStackRecord record = (BackStackRecord) records.get(i);
            boolean isPop = ((Boolean) isRecordPop.get(i)).booleanValue();
            if (!record.isPostponed() || record.interactsWith(records, i + 1, endIndex)) {
                isPostponed = false;
            } else {
                isPostponed = true;
            }
            if (isPostponed) {
                if (this.mPostponedTransactions == null) {
                    this.mPostponedTransactions = new ArrayList();
                }
                StartEnterTransitionListener listener = new StartEnterTransitionListener(record, isPop);
                this.mPostponedTransactions.add(listener);
                record.setOnStartPostponedListener(listener);
                if (isPop) {
                    record.executeOps();
                } else {
                    record.executePopOps(false);
                }
                postponeIndex--;
                if (i != postponeIndex) {
                    records.remove(i);
                    records.add(postponeIndex, record);
                }
                addAddedFragments(added);
            }
            i--;
        }
        return postponeIndex;
    }

    private static void executeOps(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, int startIndex, int endIndex) {
        int i = startIndex;
        while (i < endIndex) {
            BackStackRecord record = (BackStackRecord) records.get(i);
            if (((Boolean) isRecordPop.get(i)).booleanValue()) {
                record.bumpBackStackNesting(-1);
                record.executePopOps(i == endIndex + -1);
            } else {
                record.bumpBackStackNesting(1);
                record.executeOps();
            }
            i++;
        }
    }

    private void addAddedFragments(ArraySet<Fragment> added) {
        if (this.mCurState > 0) {
            int state = Math.min(this.mCurState, 4);
            int numAdded = this.mAdded == null ? 0 : this.mAdded.size();
            for (int i = 0; i < numAdded; i++) {
                Fragment fragment = (Fragment) this.mAdded.get(i);
                if (fragment.mState < state) {
                    moveToState(fragment, state, fragment.getNextAnim(), fragment.getNextTransition(), false);
                    if (!(fragment.mView == null || fragment.mHidden || !fragment.mIsNewlyAdded)) {
                        added.add(fragment);
                    }
                }
            }
        }
    }

    private void forcePostponedTransactions() {
        if (this.mPostponedTransactions != null) {
            while (!this.mPostponedTransactions.isEmpty()) {
                ((StartEnterTransitionListener) this.mPostponedTransactions.remove(0)).completeTransaction();
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean generateOpsForPendingActions(java.util.ArrayList<android.support.v4.app.BackStackRecord> r6, java.util.ArrayList<java.lang.Boolean> r7) {
        /*
        r5 = this;
        r3 = 0;
        monitor-enter(r5);
        r2 = r5.mPendingActions;	 Catch:{ all -> 0x003d }
        if (r2 == 0) goto L_0x000e;
    L_0x0006:
        r2 = r5.mPendingActions;	 Catch:{ all -> 0x003d }
        r2 = r2.size();	 Catch:{ all -> 0x003d }
        if (r2 != 0) goto L_0x0011;
    L_0x000e:
        monitor-exit(r5);	 Catch:{ all -> 0x003d }
        r2 = r3;
    L_0x0010:
        return r2;
    L_0x0011:
        r2 = r5.mPendingActions;	 Catch:{ all -> 0x003d }
        r1 = r2.size();	 Catch:{ all -> 0x003d }
        r0 = 0;
    L_0x0018:
        if (r0 >= r1) goto L_0x0028;
    L_0x001a:
        r2 = r5.mPendingActions;	 Catch:{ all -> 0x003d }
        r2 = r2.get(r0);	 Catch:{ all -> 0x003d }
        r2 = (android.support.v4.app.FragmentManagerImpl.OpGenerator) r2;	 Catch:{ all -> 0x003d }
        r2.generateOps(r6, r7);	 Catch:{ all -> 0x003d }
        r0 = r0 + 1;
        goto L_0x0018;
    L_0x0028:
        r2 = r5.mPendingActions;	 Catch:{ all -> 0x003d }
        r2.clear();	 Catch:{ all -> 0x003d }
        r2 = r5.mHost;	 Catch:{ all -> 0x003d }
        r2 = r2.getHandler();	 Catch:{ all -> 0x003d }
        r4 = r5.mExecCommit;	 Catch:{ all -> 0x003d }
        r2.removeCallbacks(r4);	 Catch:{ all -> 0x003d }
        monitor-exit(r5);	 Catch:{ all -> 0x003d }
        if (r1 <= 0) goto L_0x0040;
    L_0x003b:
        r2 = 1;
        goto L_0x0010;
    L_0x003d:
        r2 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x003d }
        throw r2;
    L_0x0040:
        r2 = r3;
        goto L_0x0010;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentManagerImpl.generateOpsForPendingActions(java.util.ArrayList, java.util.ArrayList):boolean");
    }

    private void doPendingDeferredStart() {
        if (this.mHavePendingDeferredStart) {
            boolean loadersRunning = false;
            for (int i = 0; i < this.mActive.size(); i++) {
                Fragment f = (Fragment) this.mActive.get(i);
                if (!(f == null || f.mLoaderManager == null)) {
                    loadersRunning |= f.mLoaderManager.hasRunningLoaders();
                }
            }
            if (!loadersRunning) {
                this.mHavePendingDeferredStart = false;
                startPendingDeferredFragments();
            }
        }
    }

    final void reportBackStackChanged() {
        if (this.mBackStackChangeListeners != null) {
            for (int i = 0; i < this.mBackStackChangeListeners.size(); i++) {
                ((OnBackStackChangedListener) this.mBackStackChangeListeners.get(i)).onBackStackChanged();
            }
        }
    }

    final boolean popBackStackState(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, String name, int id, int flags) {
        if (this.mBackStack == null) {
            return false;
        }
        if (name == null && id < 0 && (flags & 1) == 0) {
            int last = this.mBackStack.size() - 1;
            if (last < 0) {
                return false;
            }
            records.add(this.mBackStack.remove(last));
            isRecordPop.add(Boolean.valueOf(true));
        } else {
            int index = -1;
            if (name != null || id >= 0) {
                BackStackRecord bss;
                index = this.mBackStack.size() - 1;
                while (index >= 0) {
                    bss = (BackStackRecord) this.mBackStack.get(index);
                    if ((name != null && name.equals(bss.mName)) || (id >= 0 && id == bss.mIndex)) {
                        break;
                    }
                    index--;
                }
                if (index < 0) {
                    return false;
                }
                if ((flags & 1) != 0) {
                    index--;
                    while (index >= 0) {
                        bss = (BackStackRecord) this.mBackStack.get(index);
                        if ((name == null || !name.equals(bss.mName)) && (id < 0 || id != bss.mIndex)) {
                            break;
                        }
                        index--;
                    }
                }
            }
            if (index == this.mBackStack.size() - 1) {
                return false;
            }
            for (int i = this.mBackStack.size() - 1; i > index; i--) {
                records.add(this.mBackStack.remove(i));
                isRecordPop.add(Boolean.valueOf(true));
            }
        }
        return true;
    }

    final FragmentManagerNonConfig retainNonConfig() {
        ArrayList<Fragment> fragments = null;
        ArrayList<FragmentManagerNonConfig> childFragments = null;
        if (this.mActive != null) {
            for (int i = 0; i < this.mActive.size(); i++) {
                Fragment f = (Fragment) this.mActive.get(i);
                if (f != null) {
                    if (f.mRetainInstance) {
                        if (fragments == null) {
                            fragments = new ArrayList();
                        }
                        fragments.add(f);
                        f.mRetaining = true;
                        f.mTargetIndex = f.mTarget != null ? f.mTarget.mIndex : -1;
                    }
                    boolean addedChild = false;
                    if (f.mChildFragmentManager != null) {
                        FragmentManagerNonConfig child = f.mChildFragmentManager.retainNonConfig();
                        if (child != null) {
                            if (childFragments == null) {
                                childFragments = new ArrayList();
                                for (int j = 0; j < i; j++) {
                                    childFragments.add(null);
                                }
                            }
                            childFragments.add(child);
                            addedChild = true;
                        }
                    }
                    if (!(childFragments == null || addedChild)) {
                        childFragments.add(null);
                    }
                }
            }
        }
        if (fragments == null && childFragments == null) {
            return null;
        }
        return new FragmentManagerNonConfig(fragments, childFragments);
    }

    private void saveFragmentViewState(Fragment f) {
        if (f.mInnerView != null) {
            if (this.mStateArray == null) {
                this.mStateArray = new SparseArray();
            } else {
                this.mStateArray.clear();
            }
            f.mInnerView.saveHierarchyState(this.mStateArray);
            if (this.mStateArray.size() > 0) {
                f.mSavedViewState = this.mStateArray;
                this.mStateArray = null;
            }
        }
    }

    final Parcelable saveAllState() {
        int i;
        forcePostponedTransactions();
        if (this.mActive == null) {
            i = 0;
        } else {
            i = this.mActive.size();
        }
        for (int i2 = 0; i2 < i; i2++) {
            Fragment fragment = (Fragment) this.mActive.get(i2);
            if (!(fragment == null || fragment.getAnimatingAway() == null)) {
                int stateAfterAnimating = fragment.getStateAfterAnimating();
                View animatingAway = fragment.getAnimatingAway();
                fragment.setAnimatingAway(null);
                Animation animation = animatingAway.getAnimation();
                if (animation != null) {
                    animation.cancel();
                }
                moveToState(fragment, stateAfterAnimating, 0, 0, false);
            }
        }
        execPendingActions();
        if (HONEYCOMB) {
            this.mStateSaved = true;
        }
        if (this.mActive == null || this.mActive.size() <= 0) {
            return null;
        }
        int i3;
        int N = this.mActive.size();
        FragmentState[] active = new FragmentState[N];
        boolean haveFragments = false;
        for (i3 = 0; i3 < N; i3++) {
            Fragment f = (Fragment) this.mActive.get(i3);
            if (f != null) {
                if (f.mIndex < 0) {
                    throwException(new IllegalStateException("Failure saving state: active " + f + " has cleared index: " + f.mIndex));
                }
                haveFragments = true;
                FragmentState fs = new FragmentState(f);
                active[i3] = fs;
                if (f.mState <= 0 || fs.mSavedFragmentState != null) {
                    fs.mSavedFragmentState = f.mSavedFragmentState;
                } else {
                    Bundle bundle = null;
                    if (this.mStateBundle == null) {
                        this.mStateBundle = new Bundle();
                    }
                    f.performSaveInstanceState(this.mStateBundle);
                    dispatchOnFragmentSaveInstanceState(f, this.mStateBundle, false);
                    if (!this.mStateBundle.isEmpty()) {
                        bundle = this.mStateBundle;
                        this.mStateBundle = null;
                    }
                    if (f.mView != null) {
                        saveFragmentViewState(f);
                    }
                    if (f.mSavedViewState != null) {
                        if (bundle == null) {
                            bundle = new Bundle();
                        }
                        bundle.putSparseParcelableArray("android:view_state", f.mSavedViewState);
                    }
                    if (!f.mUserVisibleHint) {
                        if (bundle == null) {
                            bundle = new Bundle();
                        }
                        bundle.putBoolean("android:user_visible_hint", f.mUserVisibleHint);
                    }
                    fs.mSavedFragmentState = bundle;
                    if (f.mTarget != null) {
                        if (f.mTarget.mIndex < 0) {
                            throwException(new IllegalStateException("Failure saving state: " + f + " has target not in fragment manager: " + f.mTarget));
                        }
                        if (fs.mSavedFragmentState == null) {
                            fs.mSavedFragmentState = new Bundle();
                        }
                        bundle = fs.mSavedFragmentState;
                        String str = "android:target_state";
                        Fragment fragment2 = f.mTarget;
                        if (fragment2.mIndex < 0) {
                            throwException(new IllegalStateException("Fragment " + fragment2 + " is not currently in the FragmentManager"));
                        }
                        bundle.putInt(str, fragment2.mIndex);
                        if (f.mTargetRequestCode != 0) {
                            fs.mSavedFragmentState.putInt("android:target_req_state", f.mTargetRequestCode);
                        }
                    }
                }
            }
        }
        if (!haveFragments) {
            return null;
        }
        int[] added = null;
        BackStackState[] backStack = null;
        if (this.mAdded != null) {
            N = this.mAdded.size();
            if (N > 0) {
                added = new int[N];
                for (i3 = 0; i3 < N; i3++) {
                    added[i3] = ((Fragment) this.mAdded.get(i3)).mIndex;
                    if (added[i3] < 0) {
                        throwException(new IllegalStateException("Failure saving state: active " + this.mAdded.get(i3) + " has cleared index: " + added[i3]));
                    }
                }
            }
        }
        if (this.mBackStack != null) {
            N = this.mBackStack.size();
            if (N > 0) {
                backStack = new BackStackState[N];
                for (i3 = 0; i3 < N; i3++) {
                    backStack[i3] = new BackStackState((BackStackRecord) this.mBackStack.get(i3));
                }
            }
        }
        Parcelable fms = new FragmentManagerState();
        fms.mActive = active;
        fms.mAdded = added;
        fms.mBackStack = backStack;
        return fms;
    }

    final void restoreAllState(Parcelable state, FragmentManagerNonConfig nonConfig) {
        if (state != null) {
            FragmentManagerState fms = (FragmentManagerState) state;
            if (fms.mActive != null) {
                List<Fragment> nonConfigFragments;
                int count;
                int i;
                Fragment f;
                FragmentState fs;
                List<FragmentManagerNonConfig> childNonConfigs = null;
                if (nonConfig != null) {
                    nonConfigFragments = nonConfig.getFragments();
                    childNonConfigs = nonConfig.getChildNonConfigs();
                    count = nonConfigFragments != null ? nonConfigFragments.size() : 0;
                    for (i = 0; i < count; i++) {
                        f = (Fragment) nonConfigFragments.get(i);
                        fs = fms.mActive[f.mIndex];
                        fs.mInstance = f;
                        f.mSavedViewState = null;
                        f.mBackStackNesting = 0;
                        f.mInLayout = false;
                        f.mAdded = false;
                        f.mTarget = null;
                        if (fs.mSavedFragmentState != null) {
                            fs.mSavedFragmentState.setClassLoader(this.mHost.mContext.getClassLoader());
                            f.mSavedViewState = fs.mSavedFragmentState.getSparseParcelableArray("android:view_state");
                            f.mSavedFragmentState = fs.mSavedFragmentState;
                        }
                    }
                }
                this.mActive = new ArrayList(fms.mActive.length);
                if (this.mAvailIndices != null) {
                    this.mAvailIndices.clear();
                }
                i = 0;
                while (i < fms.mActive.length) {
                    fs = fms.mActive[i];
                    if (fs != null) {
                        FragmentManagerNonConfig childNonConfig = null;
                        if (childNonConfigs != null && i < childNonConfigs.size()) {
                            childNonConfig = (FragmentManagerNonConfig) childNonConfigs.get(i);
                        }
                        FragmentHostCallback fragmentHostCallback = this.mHost;
                        Fragment fragment = this.mParent;
                        if (fs.mInstance == null) {
                            Context context = fragmentHostCallback.mContext;
                            if (fs.mArguments != null) {
                                fs.mArguments.setClassLoader(context.getClassLoader());
                            }
                            fs.mInstance = Fragment.instantiate(context, fs.mClassName, fs.mArguments);
                            if (fs.mSavedFragmentState != null) {
                                fs.mSavedFragmentState.setClassLoader(context.getClassLoader());
                                fs.mInstance.mSavedFragmentState = fs.mSavedFragmentState;
                            }
                            fs.mInstance.setIndex(fs.mIndex, fragment);
                            fs.mInstance.mFromLayout = fs.mFromLayout;
                            fs.mInstance.mRestored = true;
                            fs.mInstance.mFragmentId = fs.mFragmentId;
                            fs.mInstance.mContainerId = fs.mContainerId;
                            fs.mInstance.mTag = fs.mTag;
                            fs.mInstance.mRetainInstance = fs.mRetainInstance;
                            fs.mInstance.mDetached = fs.mDetached;
                            fs.mInstance.mHidden = fs.mHidden;
                            fs.mInstance.mFragmentManager = fragmentHostCallback.mFragmentManager;
                            boolean z = DEBUG;
                        }
                        fs.mInstance.mChildNonConfig = childNonConfig;
                        this.mActive.add(fs.mInstance);
                        fs.mInstance = null;
                    } else {
                        this.mActive.add(null);
                        if (this.mAvailIndices == null) {
                            this.mAvailIndices = new ArrayList();
                        }
                        this.mAvailIndices.add(Integer.valueOf(i));
                    }
                    i++;
                }
                if (nonConfig != null) {
                    nonConfigFragments = nonConfig.getFragments();
                    count = nonConfigFragments != null ? nonConfigFragments.size() : 0;
                    for (i = 0; i < count; i++) {
                        f = (Fragment) nonConfigFragments.get(i);
                        if (f.mTargetIndex >= 0) {
                            if (f.mTargetIndex < this.mActive.size()) {
                                f.mTarget = (Fragment) this.mActive.get(f.mTargetIndex);
                            } else {
                                Log.w("FragmentManager", "Re-attaching retained fragment " + f + " target no longer exists: " + f.mTargetIndex);
                                f.mTarget = null;
                            }
                        }
                    }
                }
                if (fms.mAdded != null) {
                    this.mAdded = new ArrayList(fms.mAdded.length);
                    for (i = 0; i < fms.mAdded.length; i++) {
                        f = (Fragment) this.mActive.get(fms.mAdded[i]);
                        if (f == null) {
                            throwException(new IllegalStateException("No instantiated fragment for index #" + fms.mAdded[i]));
                        }
                        f.mAdded = true;
                        if (this.mAdded.contains(f)) {
                            throw new IllegalStateException("Already added!");
                        }
                        this.mAdded.add(f);
                    }
                } else {
                    this.mAdded = null;
                }
                if (fms.mBackStack != null) {
                    this.mBackStack = new ArrayList(fms.mBackStack.length);
                    for (BackStackState instantiate : fms.mBackStack) {
                        BackStackRecord bse = instantiate.instantiate(this);
                        this.mBackStack.add(bse);
                        if (bse.mIndex >= 0) {
                            setBackStackIndex(bse.mIndex, bse);
                        }
                    }
                    return;
                }
                this.mBackStack = null;
            }
        }
    }

    public final void attachController(FragmentHostCallback host, FragmentContainer container, Fragment parent) {
        if (this.mHost != null) {
            throw new IllegalStateException("Already attached");
        }
        this.mHost = host;
        this.mContainer = container;
        this.mParent = parent;
    }

    public final void dispatchCreate() {
        this.mStateSaved = false;
        this.mExecutingActions = true;
        moveToState(1, false);
        this.mExecutingActions = false;
    }

    public final void dispatchActivityCreated() {
        this.mStateSaved = false;
        this.mExecutingActions = true;
        moveToState(2, false);
        this.mExecutingActions = false;
    }

    public final void dispatchStart() {
        this.mStateSaved = false;
        this.mExecutingActions = true;
        moveToState(4, false);
        this.mExecutingActions = false;
    }

    public final void dispatchResume() {
        this.mStateSaved = false;
        this.mExecutingActions = true;
        moveToState(5, false);
        this.mExecutingActions = false;
    }

    public final void dispatchPause() {
        this.mExecutingActions = true;
        moveToState(4, false);
        this.mExecutingActions = false;
    }

    public final void dispatchStop() {
        this.mStateSaved = true;
        this.mExecutingActions = true;
        moveToState(3, false);
        this.mExecutingActions = false;
    }

    public final void dispatchReallyStop() {
        this.mExecutingActions = true;
        moveToState(2, false);
        this.mExecutingActions = false;
    }

    public final void dispatchDestroy() {
        this.mDestroyed = true;
        execPendingActions();
        this.mExecutingActions = true;
        moveToState(0, false);
        this.mExecutingActions = false;
        this.mHost = null;
        this.mContainer = null;
        this.mParent = null;
    }

    public final void dispatchMultiWindowModeChanged(boolean isInMultiWindowMode) {
        if (this.mAdded != null) {
            for (int i = this.mAdded.size() - 1; i >= 0; i--) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null) {
                    f.performMultiWindowModeChanged(isInMultiWindowMode);
                }
            }
        }
    }

    public final void dispatchPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        if (this.mAdded != null) {
            for (int i = this.mAdded.size() - 1; i >= 0; i--) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null) {
                    f.performPictureInPictureModeChanged(isInPictureInPictureMode);
                }
            }
        }
    }

    public final void dispatchConfigurationChanged(Configuration newConfig) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null) {
                    f.performConfigurationChanged(newConfig);
                }
            }
        }
    }

    public final void dispatchLowMemory() {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null) {
                    f.performLowMemory();
                }
            }
        }
    }

    public final boolean dispatchCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int i;
        Fragment f;
        boolean show = false;
        ArrayList<Fragment> newMenus = null;
        if (this.mAdded != null) {
            for (i = 0; i < this.mAdded.size(); i++) {
                f = (Fragment) this.mAdded.get(i);
                if (f != null && f.performCreateOptionsMenu(menu, inflater)) {
                    show = true;
                    if (newMenus == null) {
                        newMenus = new ArrayList();
                    }
                    newMenus.add(f);
                }
            }
        }
        if (this.mCreatedMenus != null) {
            for (i = 0; i < this.mCreatedMenus.size(); i++) {
                f = (Fragment) this.mCreatedMenus.get(i);
                if (newMenus == null || !newMenus.contains(f)) {
                    f.onDestroyOptionsMenu();
                }
            }
        }
        this.mCreatedMenus = newMenus;
        return show;
    }

    public final boolean dispatchPrepareOptionsMenu(Menu menu) {
        boolean show = false;
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null && f.performPrepareOptionsMenu(menu)) {
                    show = true;
                }
            }
        }
        return show;
    }

    public final boolean dispatchOptionsItemSelected(MenuItem item) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null && f.performOptionsItemSelected(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean dispatchContextItemSelected(MenuItem item) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null && f.performContextItemSelected(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    public final void dispatchOptionsMenuClosed(Menu menu) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment f = (Fragment) this.mAdded.get(i);
                if (f != null) {
                    f.performOptionsMenuClosed(menu);
                }
            }
        }
    }

    private void dispatchOnFragmentPreAttached(Fragment f, Context context, boolean onlyRecursive) {
        while (this.mParent != null) {
            FragmentManager parentManager = this.mParent.getFragmentManager();
            if (parentManager instanceof FragmentManagerImpl) {
                this = (FragmentManagerImpl) parentManager;
            } else {
                return;
            }
        }
    }

    private void dispatchOnFragmentAttached(Fragment f, Context context, boolean onlyRecursive) {
        while (this.mParent != null) {
            FragmentManager parentManager = this.mParent.getFragmentManager();
            if (parentManager instanceof FragmentManagerImpl) {
                this = (FragmentManagerImpl) parentManager;
            } else {
                return;
            }
        }
    }

    private void dispatchOnFragmentCreated(Fragment f, Bundle savedInstanceState, boolean onlyRecursive) {
        while (this.mParent != null) {
            FragmentManager parentManager = this.mParent.getFragmentManager();
            if (parentManager instanceof FragmentManagerImpl) {
                this = (FragmentManagerImpl) parentManager;
            } else {
                return;
            }
        }
    }

    private void dispatchOnFragmentActivityCreated(Fragment f, Bundle savedInstanceState, boolean onlyRecursive) {
        while (this.mParent != null) {
            FragmentManager parentManager = this.mParent.getFragmentManager();
            if (parentManager instanceof FragmentManagerImpl) {
                this = (FragmentManagerImpl) parentManager;
            } else {
                return;
            }
        }
    }

    private void dispatchOnFragmentViewCreated(Fragment f, View v, Bundle savedInstanceState, boolean onlyRecursive) {
        while (this.mParent != null) {
            FragmentManager parentManager = this.mParent.getFragmentManager();
            if (parentManager instanceof FragmentManagerImpl) {
                this = (FragmentManagerImpl) parentManager;
            } else {
                return;
            }
        }
    }

    private void dispatchOnFragmentStarted(Fragment f, boolean onlyRecursive) {
        while (this.mParent != null) {
            FragmentManager parentManager = this.mParent.getFragmentManager();
            if (parentManager instanceof FragmentManagerImpl) {
                this = (FragmentManagerImpl) parentManager;
            } else {
                return;
            }
        }
    }

    private void dispatchOnFragmentResumed(Fragment f, boolean onlyRecursive) {
        while (this.mParent != null) {
            FragmentManager parentManager = this.mParent.getFragmentManager();
            if (parentManager instanceof FragmentManagerImpl) {
                this = (FragmentManagerImpl) parentManager;
            } else {
                return;
            }
        }
    }

    private void dispatchOnFragmentPaused(Fragment f, boolean onlyRecursive) {
        while (this.mParent != null) {
            FragmentManager parentManager = this.mParent.getFragmentManager();
            if (parentManager instanceof FragmentManagerImpl) {
                this = (FragmentManagerImpl) parentManager;
            } else {
                return;
            }
        }
    }

    private void dispatchOnFragmentStopped(Fragment f, boolean onlyRecursive) {
        while (this.mParent != null) {
            FragmentManager parentManager = this.mParent.getFragmentManager();
            if (parentManager instanceof FragmentManagerImpl) {
                this = (FragmentManagerImpl) parentManager;
            } else {
                return;
            }
        }
    }

    private void dispatchOnFragmentSaveInstanceState(Fragment f, Bundle outState, boolean onlyRecursive) {
        while (this.mParent != null) {
            FragmentManager parentManager = this.mParent.getFragmentManager();
            if (parentManager instanceof FragmentManagerImpl) {
                this = (FragmentManagerImpl) parentManager;
            } else {
                return;
            }
        }
    }

    private void dispatchOnFragmentViewDestroyed(Fragment f, boolean onlyRecursive) {
        while (this.mParent != null) {
            FragmentManager parentManager = this.mParent.getFragmentManager();
            if (parentManager instanceof FragmentManagerImpl) {
                this = (FragmentManagerImpl) parentManager;
            } else {
                return;
            }
        }
    }

    private void dispatchOnFragmentDestroyed(Fragment f, boolean onlyRecursive) {
        while (this.mParent != null) {
            FragmentManager parentManager = this.mParent.getFragmentManager();
            if (parentManager instanceof FragmentManagerImpl) {
                this = (FragmentManagerImpl) parentManager;
            } else {
                return;
            }
        }
    }

    private void dispatchOnFragmentDetached(Fragment f, boolean onlyRecursive) {
        while (this.mParent != null) {
            FragmentManager parentManager = this.mParent.getFragmentManager();
            if (parentManager instanceof FragmentManagerImpl) {
                this = (FragmentManagerImpl) parentManager;
            } else {
                return;
            }
        }
    }

    public static int reverseTransit(int transit) {
        switch (transit) {
            case 4097:
                return 8194;
            case 4099:
                return 4099;
            case 8194:
                return 4097;
            default:
                return 0;
        }
    }

    public final View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        if (!"fragment".equals(name)) {
            return null;
        }
        String fname = attrs.getAttributeValue(null, "class");
        TypedArray a = context.obtainStyledAttributes(attrs, FragmentTag.Fragment);
        if (fname == null) {
            fname = a.getString(0);
        }
        int id = a.getResourceId(1, -1);
        String tag = a.getString(2);
        a.recycle();
        if (!Fragment.isSupportFragmentClass(this.mHost.mContext, fname)) {
            return null;
        }
        int containerId;
        if (parent != null) {
            containerId = parent.getId();
        } else {
            containerId = 0;
        }
        if (containerId == -1 && id == -1 && tag == null) {
            throw new IllegalArgumentException(attrs.getPositionDescription() + ": Must specify unique android:id, android:tag, or have a parent with an id for " + fname);
        }
        Fragment fragment;
        if (id != -1) {
            fragment = findFragmentById(id);
        } else {
            fragment = null;
        }
        if (fragment == null && tag != null) {
            fragment = findFragmentByTag(tag);
        }
        if (fragment == null && containerId != -1) {
            fragment = findFragmentById(containerId);
        }
        if (fragment == null) {
            int i;
            fragment = Fragment.instantiate(context, fname);
            fragment.mFromLayout = true;
            if (id != 0) {
                i = id;
            } else {
                i = containerId;
            }
            fragment.mFragmentId = i;
            fragment.mContainerId = containerId;
            fragment.mTag = tag;
            fragment.mInLayout = true;
            fragment.mFragmentManager = this;
            fragment.mHost = this.mHost;
            fragment.onInflate(this.mHost.mContext, attrs, fragment.mSavedFragmentState);
            addFragment(fragment, true);
        } else if (fragment.mInLayout) {
            throw new IllegalArgumentException(attrs.getPositionDescription() + ": Duplicate id 0x" + Integer.toHexString(id) + ", tag " + tag + ", or parent id 0x" + Integer.toHexString(containerId) + " with another fragment for " + fname);
        } else {
            fragment.mInLayout = true;
            fragment.mHost = this.mHost;
            if (!fragment.mRetaining) {
                fragment.onInflate(this.mHost.mContext, attrs, fragment.mSavedFragmentState);
            }
        }
        if (this.mCurState > 0 || !fragment.mFromLayout) {
            moveToState(fragment);
        } else {
            moveToState(fragment, 1, 0, 0, false);
        }
        if (fragment.mView == null) {
            throw new IllegalStateException("Fragment " + fname + " did not create a view.");
        }
        if (id != 0) {
            fragment.mView.setId(id);
        }
        if (fragment.mView.getTag() == null) {
            fragment.mView.setTag(tag);
        }
        return fragment.mView;
    }
}
