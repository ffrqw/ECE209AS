package android.support.v4.app;

import android.os.Build.VERSION;
import android.support.v4.app.FragmentManager.BackStackEntry;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

final class BackStackRecord extends FragmentTransaction implements BackStackEntry, OpGenerator {
    static final boolean SUPPORTS_TRANSITIONS = (VERSION.SDK_INT >= 21);
    boolean mAddToBackStack;
    boolean mAllowAddToBackStack = true;
    boolean mAllowOptimization = false;
    int mBreadCrumbShortTitleRes;
    CharSequence mBreadCrumbShortTitleText;
    int mBreadCrumbTitleRes;
    CharSequence mBreadCrumbTitleText;
    boolean mCommitted;
    int mEnterAnim;
    int mExitAnim;
    int mIndex = -1;
    final FragmentManagerImpl mManager;
    String mName;
    ArrayList<Op> mOps = new ArrayList();
    int mPopEnterAnim;
    int mPopExitAnim;
    ArrayList<String> mSharedElementSourceNames;
    ArrayList<String> mSharedElementTargetNames;
    int mTransition;
    int mTransitionStyle;

    static final class Op {
        int cmd;
        int enterAnim;
        int exitAnim;
        Fragment fragment;
        int popEnterAnim;
        int popExitAnim;

        Op() {
        }
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("BackStackEntry{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        if (this.mIndex >= 0) {
            sb.append(" #");
            sb.append(this.mIndex);
        }
        if (this.mName != null) {
            sb.append(" ");
            sb.append(this.mName);
        }
        sb.append("}");
        return sb.toString();
    }

    public BackStackRecord(FragmentManagerImpl manager) {
        this.mManager = manager;
    }

    final void addOp(Op op) {
        this.mOps.add(op);
        op.enterAnim = this.mEnterAnim;
        op.exitAnim = this.mExitAnim;
        op.popEnterAnim = this.mPopEnterAnim;
        op.popExitAnim = this.mPopExitAnim;
    }

    public final FragmentTransaction add(Fragment fragment, String tag) {
        doAddOp(0, fragment, tag, 1);
        return this;
    }

    public final FragmentTransaction add(int containerViewId, Fragment fragment) {
        doAddOp(containerViewId, fragment, null, 1);
        return this;
    }

    public final FragmentTransaction add(int containerViewId, Fragment fragment, String tag) {
        doAddOp(containerViewId, fragment, tag, 1);
        return this;
    }

    private void doAddOp(int containerViewId, Fragment fragment, String tag, int opcmd) {
        Class fragmentClass = fragment.getClass();
        int modifiers = fragmentClass.getModifiers();
        if (fragmentClass.isAnonymousClass() || !Modifier.isPublic(modifiers) || (fragmentClass.isMemberClass() && !Modifier.isStatic(modifiers))) {
            throw new IllegalStateException("Fragment " + fragmentClass.getCanonicalName() + " must be a public static class to be  properly recreated from instance state.");
        }
        fragment.mFragmentManager = this.mManager;
        if (tag != null) {
            if (fragment.mTag == null || tag.equals(fragment.mTag)) {
                fragment.mTag = tag;
            } else {
                throw new IllegalStateException("Can't change tag of fragment " + fragment + ": was " + fragment.mTag + " now " + tag);
            }
        }
        if (containerViewId != 0) {
            if (containerViewId == -1) {
                throw new IllegalArgumentException("Can't add fragment " + fragment + " with tag " + tag + " to container view with no id");
            } else if (fragment.mFragmentId == 0 || fragment.mFragmentId == containerViewId) {
                fragment.mFragmentId = containerViewId;
                fragment.mContainerId = containerViewId;
            } else {
                throw new IllegalStateException("Can't change container ID of fragment " + fragment + ": was " + fragment.mFragmentId + " now " + containerViewId);
            }
        }
        Op op = new Op();
        op.cmd = opcmd;
        op.fragment = fragment;
        addOp(op);
    }

    public final FragmentTransaction replace(int containerViewId, Fragment fragment) {
        return replace(containerViewId, fragment, null);
    }

    public final FragmentTransaction replace(int containerViewId, Fragment fragment, String tag) {
        if (containerViewId == 0) {
            throw new IllegalArgumentException("Must use non-zero containerViewId");
        }
        doAddOp(containerViewId, fragment, tag, 2);
        return this;
    }

    public final FragmentTransaction remove(Fragment fragment) {
        Op op = new Op();
        op.cmd = 3;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    public final FragmentTransaction detach(Fragment fragment) {
        Op op = new Op();
        op.cmd = 6;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    public final FragmentTransaction attach(Fragment fragment) {
        Op op = new Op();
        op.cmd = 7;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    public final FragmentTransaction setCustomAnimations(int enter, int exit) {
        return setCustomAnimations(enter, exit, 0, 0);
    }

    public final FragmentTransaction setCustomAnimations(int enter, int exit, int popEnter, int popExit) {
        this.mEnterAnim = enter;
        this.mExitAnim = exit;
        this.mPopEnterAnim = popEnter;
        this.mPopExitAnim = popExit;
        return this;
    }

    public final FragmentTransaction setTransition(int transition) {
        this.mTransition = transition;
        return this;
    }

    public final FragmentTransaction addToBackStack(String name) {
        if (this.mAllowAddToBackStack) {
            this.mAddToBackStack = true;
            this.mName = name;
            return this;
        }
        throw new IllegalStateException("This FragmentTransaction is not allowed to be added to the back stack.");
    }

    final void bumpBackStackNesting(int amt) {
        if (this.mAddToBackStack) {
            boolean z = FragmentManagerImpl.DEBUG;
            int numOps = this.mOps.size();
            for (int opNum = 0; opNum < numOps; opNum++) {
                Op op = (Op) this.mOps.get(opNum);
                if (op.fragment != null) {
                    Fragment fragment = op.fragment;
                    fragment.mBackStackNesting += amt;
                    z = FragmentManagerImpl.DEBUG;
                }
            }
        }
    }

    public final int commit() {
        return commitInternal(false);
    }

    public final int commitAllowingStateLoss() {
        return commitInternal(true);
    }

    private int commitInternal(boolean allowStateLoss) {
        if (this.mCommitted) {
            throw new IllegalStateException("commit already called");
        }
        boolean z = FragmentManagerImpl.DEBUG;
        this.mCommitted = true;
        if (this.mAddToBackStack) {
            this.mIndex = this.mManager.allocBackStackIndex(this);
        } else {
            this.mIndex = -1;
        }
        this.mManager.enqueueAction(this, allowStateLoss);
        return this.mIndex;
    }

    public final boolean generateOps(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop) {
        boolean z = FragmentManagerImpl.DEBUG;
        records.add(this);
        isRecordPop.add(Boolean.valueOf(false));
        if (this.mAddToBackStack) {
            FragmentManagerImpl fragmentManagerImpl = this.mManager;
            if (fragmentManagerImpl.mBackStack == null) {
                fragmentManagerImpl.mBackStack = new ArrayList();
            }
            fragmentManagerImpl.mBackStack.add(this);
            fragmentManagerImpl.reportBackStackChanged();
        }
        return true;
    }

    final boolean interactsWith(int containerId) {
        int numOps = this.mOps.size();
        for (int opNum = 0; opNum < numOps; opNum++) {
            if (((Op) this.mOps.get(opNum)).fragment.mContainerId == containerId) {
                return true;
            }
        }
        return false;
    }

    final boolean interactsWith(ArrayList<BackStackRecord> records, int startIndex, int endIndex) {
        if (endIndex == startIndex) {
            return false;
        }
        int numOps = this.mOps.size();
        int lastContainer = -1;
        for (int opNum = 0; opNum < numOps; opNum++) {
            int container = ((Op) this.mOps.get(opNum)).fragment.mContainerId;
            if (!(container == 0 || container == lastContainer)) {
                lastContainer = container;
                for (int i = startIndex; i < endIndex; i++) {
                    BackStackRecord record = (BackStackRecord) records.get(i);
                    int numThoseOps = record.mOps.size();
                    for (int thoseOpIndex = 0; thoseOpIndex < numThoseOps; thoseOpIndex++) {
                        if (((Op) record.mOps.get(thoseOpIndex)).fragment.mContainerId == container) {
                            return true;
                        }
                    }
                }
                continue;
            }
        }
        return false;
    }

    final void executeOps() {
        int numOps = this.mOps.size();
        for (int opNum = 0; opNum < numOps; opNum++) {
            Op op = (Op) this.mOps.get(opNum);
            Fragment f = op.fragment;
            f.setNextTransition(this.mTransition, this.mTransitionStyle);
            switch (op.cmd) {
                case 1:
                    f.setNextAnim(op.enterAnim);
                    this.mManager.addFragment(f, false);
                    break;
                case 3:
                    f.setNextAnim(op.exitAnim);
                    this.mManager.removeFragment(f);
                    break;
                case 4:
                    f.setNextAnim(op.exitAnim);
                    FragmentManagerImpl.hideFragment(f);
                    break;
                case 5:
                    f.setNextAnim(op.enterAnim);
                    FragmentManagerImpl.showFragment(f);
                    break;
                case 6:
                    f.setNextAnim(op.exitAnim);
                    this.mManager.detachFragment(f);
                    break;
                case 7:
                    f.setNextAnim(op.enterAnim);
                    this.mManager.attachFragment(f);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown cmd: " + op.cmd);
            }
            if (!(this.mAllowOptimization || op.cmd == 1)) {
                this.mManager.moveFragmentToExpectedState(f);
            }
        }
        if (!this.mAllowOptimization) {
            this.mManager.moveToState(this.mManager.mCurState, true);
        }
    }

    final void executePopOps(boolean moveToState) {
        for (int opNum = this.mOps.size() - 1; opNum >= 0; opNum--) {
            Op op = (Op) this.mOps.get(opNum);
            Fragment f = op.fragment;
            f.setNextTransition(FragmentManagerImpl.reverseTransit(this.mTransition), this.mTransitionStyle);
            switch (op.cmd) {
                case 1:
                    f.setNextAnim(op.popExitAnim);
                    this.mManager.removeFragment(f);
                    break;
                case 3:
                    f.setNextAnim(op.popEnterAnim);
                    this.mManager.addFragment(f, false);
                    break;
                case 4:
                    f.setNextAnim(op.popEnterAnim);
                    FragmentManagerImpl.showFragment(f);
                    break;
                case 5:
                    f.setNextAnim(op.popExitAnim);
                    FragmentManagerImpl.hideFragment(f);
                    break;
                case 6:
                    f.setNextAnim(op.popEnterAnim);
                    this.mManager.attachFragment(f);
                    break;
                case 7:
                    f.setNextAnim(op.popExitAnim);
                    this.mManager.detachFragment(f);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown cmd: " + op.cmd);
            }
            if (!(this.mAllowOptimization || op.cmd == 3)) {
                this.mManager.moveFragmentToExpectedState(f);
            }
        }
        if (!this.mAllowOptimization && moveToState) {
            this.mManager.moveToState(this.mManager.mCurState, true);
        }
    }

    final void expandReplaceOps(ArrayList<Fragment> added) {
        int opNum = 0;
        while (opNum < this.mOps.size()) {
            Op op = (Op) this.mOps.get(opNum);
            switch (op.cmd) {
                case 1:
                case 7:
                    added.add(op.fragment);
                    break;
                case 2:
                    Fragment f = op.fragment;
                    int containerId = f.mContainerId;
                    boolean alreadyAdded = false;
                    for (int i = added.size() - 1; i >= 0; i--) {
                        Fragment old = (Fragment) added.get(i);
                        if (old.mContainerId == containerId) {
                            if (old == f) {
                                alreadyAdded = true;
                            } else {
                                Op removeOp = new Op();
                                removeOp.cmd = 3;
                                removeOp.fragment = old;
                                removeOp.enterAnim = op.enterAnim;
                                removeOp.popEnterAnim = op.popEnterAnim;
                                removeOp.exitAnim = op.exitAnim;
                                removeOp.popExitAnim = op.popExitAnim;
                                this.mOps.add(opNum, removeOp);
                                added.remove(old);
                                opNum++;
                            }
                        }
                    }
                    if (!alreadyAdded) {
                        op.cmd = 1;
                        added.add(f);
                        break;
                    }
                    this.mOps.remove(opNum);
                    opNum--;
                    break;
                case 3:
                case 6:
                    added.remove(op.fragment);
                    break;
                default:
                    break;
            }
            opNum++;
        }
    }

    final void trackAddedFragmentsInPop(ArrayList<Fragment> added) {
        for (int opNum = 0; opNum < this.mOps.size(); opNum++) {
            Op op = (Op) this.mOps.get(opNum);
            switch (op.cmd) {
                case 1:
                case 7:
                    added.remove(op.fragment);
                    break;
                case 3:
                case 6:
                    added.add(op.fragment);
                    break;
                default:
                    break;
            }
        }
    }

    final boolean isPostponed() {
        for (int opNum = 0; opNum < this.mOps.size(); opNum++) {
            if (isFragmentPostponed((Op) this.mOps.get(opNum))) {
                return true;
            }
        }
        return false;
    }

    final void setOnStartPostponedListener(OnStartEnterTransitionListener listener) {
        for (int opNum = 0; opNum < this.mOps.size(); opNum++) {
            Op op = (Op) this.mOps.get(opNum);
            if (isFragmentPostponed(op)) {
                op.fragment.setOnStartEnterTransitionListener(listener);
            }
        }
    }

    private static boolean isFragmentPostponed(Op op) {
        Fragment fragment = op.fragment;
        return (!fragment.mAdded || fragment.mView == null || fragment.mDetached || fragment.mHidden || !fragment.isPostponed()) ? false : true;
    }

    public final String getName() {
        return this.mName;
    }

    public final void dump$ec96877(String prefix, PrintWriter writer) {
        writer.print(prefix);
        writer.print("mName=");
        writer.print(this.mName);
        writer.print(" mIndex=");
        writer.print(this.mIndex);
        writer.print(" mCommitted=");
        writer.println(this.mCommitted);
        if (this.mTransition != 0) {
            writer.print(prefix);
            writer.print("mTransition=#");
            writer.print(Integer.toHexString(this.mTransition));
            writer.print(" mTransitionStyle=#");
            writer.println(Integer.toHexString(this.mTransitionStyle));
        }
        if (!(this.mEnterAnim == 0 && this.mExitAnim == 0)) {
            writer.print(prefix);
            writer.print("mEnterAnim=#");
            writer.print(Integer.toHexString(this.mEnterAnim));
            writer.print(" mExitAnim=#");
            writer.println(Integer.toHexString(this.mExitAnim));
        }
        if (!(this.mPopEnterAnim == 0 && this.mPopExitAnim == 0)) {
            writer.print(prefix);
            writer.print("mPopEnterAnim=#");
            writer.print(Integer.toHexString(this.mPopEnterAnim));
            writer.print(" mPopExitAnim=#");
            writer.println(Integer.toHexString(this.mPopExitAnim));
        }
        if (!(this.mBreadCrumbTitleRes == 0 && this.mBreadCrumbTitleText == null)) {
            writer.print(prefix);
            writer.print("mBreadCrumbTitleRes=#");
            writer.print(Integer.toHexString(this.mBreadCrumbTitleRes));
            writer.print(" mBreadCrumbTitleText=");
            writer.println(this.mBreadCrumbTitleText);
        }
        if (!(this.mBreadCrumbShortTitleRes == 0 && this.mBreadCrumbShortTitleText == null)) {
            writer.print(prefix);
            writer.print("mBreadCrumbShortTitleRes=#");
            writer.print(Integer.toHexString(this.mBreadCrumbShortTitleRes));
            writer.print(" mBreadCrumbShortTitleText=");
            writer.println(this.mBreadCrumbShortTitleText);
        }
        if (!this.mOps.isEmpty()) {
            writer.print(prefix);
            writer.println("Operations:");
            new StringBuilder().append(prefix).append("    ");
            int size = this.mOps.size();
            for (int i = 0; i < size; i++) {
                String str;
                Op op = (Op) this.mOps.get(i);
                switch (op.cmd) {
                    case 0:
                        str = "NULL";
                        break;
                    case 1:
                        str = "ADD";
                        break;
                    case 2:
                        str = "REPLACE";
                        break;
                    case 3:
                        str = "REMOVE";
                        break;
                    case 4:
                        str = "HIDE";
                        break;
                    case 5:
                        str = "SHOW";
                        break;
                    case 6:
                        str = "DETACH";
                        break;
                    case 7:
                        str = "ATTACH";
                        break;
                    default:
                        str = "cmd=" + op.cmd;
                        break;
                }
                writer.print(prefix);
                writer.print("  Op #");
                writer.print(i);
                writer.print(": ");
                writer.print(str);
                writer.print(" ");
                writer.println(op.fragment);
                if (!(op.enterAnim == 0 && op.exitAnim == 0)) {
                    writer.print(prefix);
                    writer.print("enterAnim=#");
                    writer.print(Integer.toHexString(op.enterAnim));
                    writer.print(" exitAnim=#");
                    writer.println(Integer.toHexString(op.exitAnim));
                }
                if (op.popEnterAnim != 0 || op.popExitAnim != 0) {
                    writer.print(prefix);
                    writer.print("popEnterAnim=#");
                    writer.print(Integer.toHexString(op.popEnterAnim));
                    writer.print(" popExitAnim=#");
                    writer.println(Integer.toHexString(op.popExitAnim));
                }
            }
        }
    }

    public final void commitNowAllowingStateLoss() {
        if (this.mAddToBackStack) {
            throw new IllegalStateException("This transaction is already being added to the back stack");
        }
        this.mAllowAddToBackStack = false;
        this.mManager.execSingleAction(this, true);
    }
}
