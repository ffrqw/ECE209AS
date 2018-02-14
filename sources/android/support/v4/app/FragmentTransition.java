package android.support.v4.app;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.v4.app.FragmentTransitionCompat21.AnonymousClass7;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;

final class FragmentTransition {
    private static final int[] INVERSE_OPS = new int[]{0, 3, 0, 1, 5, 4, 7, 6};

    static class FragmentContainerTransition {
        public Fragment firstOut;
        public boolean firstOutIsPop;
        public BackStackRecord firstOutTransaction;
        public Fragment lastIn;
        public boolean lastInIsPop;
        public BackStackRecord lastInTransaction;

        FragmentContainerTransition() {
        }
    }

    static void startTransitions(FragmentManagerImpl fragmentManager, ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, int startIndex, int endIndex, boolean isOptimized) {
        if (fragmentManager.mCurState > 0 && VERSION.SDK_INT >= 21) {
            int i;
            SparseArray<FragmentContainerTransition> transitioningFragments = new SparseArray();
            for (i = startIndex; i < endIndex; i++) {
                BackStackRecord record = (BackStackRecord) records.get(i);
                if (((Boolean) isRecordPop.get(i)).booleanValue()) {
                    calculatePopFragments(record, transitioningFragments, isOptimized);
                } else {
                    calculateFragments(record, transitioningFragments, isOptimized);
                }
            }
            if (transitioningFragments.size() != 0) {
                View view = new View(fragmentManager.mHost.mContext);
                int numContainers = transitioningFragments.size();
                for (i = 0; i < numContainers; i++) {
                    int containerId = transitioningFragments.keyAt(i);
                    ArrayMap<String, String> nameOverrides = calculateNameOverrides(containerId, records, isRecordPop, startIndex, endIndex);
                    FragmentContainerTransition containerTransition = (FragmentContainerTransition) transitioningFragments.valueAt(i);
                    boolean z;
                    ArrayList arrayList;
                    Object exitTransition;
                    Object obj;
                    View inEpicenterView;
                    final ArrayList configureEnteringExitingViews;
                    if (isOptimized) {
                        View view2;
                        if (fragmentManager.mContainer.onHasView()) {
                            view2 = (ViewGroup) fragmentManager.mContainer.onFindViewById(containerId);
                        } else {
                            view2 = null;
                        }
                        if (view2 != null) {
                            Object obj2;
                            Fragment fragment = containerTransition.lastIn;
                            Fragment fragment2 = containerTransition.firstOut;
                            boolean z2 = containerTransition.lastInIsPop;
                            z = containerTransition.firstOutIsPop;
                            arrayList = new ArrayList();
                            ArrayList arrayList2 = new ArrayList();
                            Object enterTransition = getEnterTransition(fragment, z2);
                            exitTransition = getExitTransition(fragment2, z);
                            final Fragment fragment3 = containerTransition.lastIn;
                            final Fragment fragment4 = containerTransition.firstOut;
                            if (fragment3 != null) {
                                fragment3.getView().setVisibility(0);
                            }
                            if (fragment3 == null || fragment4 == null) {
                                obj2 = null;
                            } else {
                                Object obj3;
                                final boolean z3 = containerTransition.lastInIsPop;
                                if (nameOverrides.isEmpty()) {
                                    obj = null;
                                } else {
                                    obj = getSharedElementTransition(fragment3, fragment4, z3);
                                }
                                ArrayMap captureOutSharedElements = captureOutSharedElements(nameOverrides, obj, containerTransition);
                                final ArrayMap captureInSharedElements = captureInSharedElements(nameOverrides, obj, containerTransition);
                                if (nameOverrides.isEmpty()) {
                                    obj3 = null;
                                    if (captureOutSharedElements != null) {
                                        captureOutSharedElements.clear();
                                    }
                                    if (captureInSharedElements != null) {
                                        captureInSharedElements.clear();
                                    }
                                } else {
                                    addSharedElementsWithMatchingNames(arrayList2, captureOutSharedElements, nameOverrides.keySet());
                                    addSharedElementsWithMatchingNames(arrayList, captureInSharedElements, nameOverrides.values());
                                    obj3 = obj;
                                }
                                if (enterTransition == null && exitTransition == null && obj3 == null) {
                                    obj2 = null;
                                } else {
                                    Rect rect;
                                    callSharedElementStartEnd(fragment3, fragment4, z3, captureOutSharedElements, true);
                                    if (obj3 != null) {
                                        arrayList.add(view);
                                        FragmentTransitionCompat21.setSharedElementTargets(obj3, view, arrayList2);
                                        setOutEpicenter(obj3, exitTransition, captureOutSharedElements, containerTransition.firstOutIsPop, containerTransition.firstOutTransaction);
                                        rect = new Rect();
                                        inEpicenterView = getInEpicenterView(captureInSharedElements, containerTransition, enterTransition, z3);
                                        if (inEpicenterView != null) {
                                            FragmentTransitionCompat21.setEpicenter(enterTransition, rect);
                                        }
                                    } else {
                                        rect = null;
                                        inEpicenterView = null;
                                    }
                                    OneShotPreDrawListener.add(view2, new Runnable() {
                                        public final void run() {
                                            FragmentTransition.callSharedElementStartEnd(fragment3, fragment4, z3, captureInSharedElements, false);
                                            if (inEpicenterView != null) {
                                                FragmentTransitionCompat21.getBoundsOnScreen(inEpicenterView, rect);
                                            }
                                        }
                                    });
                                    obj2 = obj3;
                                }
                            }
                            if (enterTransition != null || obj2 != null || exitTransition != null) {
                                configureEnteringExitingViews = configureEnteringExitingViews(exitTransition, fragment2, arrayList2, view);
                                ArrayList configureEnteringExitingViews2 = configureEnteringExitingViews(enterTransition, fragment, arrayList, view);
                                setViewVisibility(configureEnteringExitingViews2, 4);
                                obj = mergeTransitions(enterTransition, exitTransition, obj2, fragment, z2);
                                if (obj != null) {
                                    if (fragment2 != null && exitTransition != null && fragment2.mAdded && fragment2.mHidden && fragment2.mHiddenChanged) {
                                        fragment2.setHideReplaced(true);
                                        ((Transition) exitTransition).addListener(new android.support.v4.app.FragmentTransitionCompat21.AnonymousClass2(fragment2.getView(), configureEnteringExitingViews));
                                        OneShotPreDrawListener.add(fragment2.mContainer, new Runnable() {
                                            public final void run() {
                                                FragmentTransition.setViewVisibility(configureEnteringExitingViews, 4);
                                            }
                                        });
                                    }
                                    ArrayList prepareSetNameOverridesOptimized = FragmentTransitionCompat21.prepareSetNameOverridesOptimized(arrayList);
                                    FragmentTransitionCompat21.scheduleRemoveTargets(obj, enterTransition, configureEnteringExitingViews2, exitTransition, configureEnteringExitingViews, obj2, arrayList);
                                    FragmentTransitionCompat21.beginDelayedTransition(view2, obj);
                                    FragmentTransitionCompat21.setNameOverridesOptimized(view2, arrayList2, arrayList, prepareSetNameOverridesOptimized, nameOverrides);
                                    setViewVisibility(configureEnteringExitingViews2, 0);
                                    FragmentTransitionCompat21.swapSharedElementTargets(obj2, arrayList2, arrayList);
                                }
                            }
                        }
                    } else {
                        View view3;
                        if (fragmentManager.mContainer.onHasView()) {
                            view3 = (ViewGroup) fragmentManager.mContainer.onFindViewById(containerId);
                        } else {
                            view3 = null;
                        }
                        if (view3 != null) {
                            Object obj4;
                            Fragment fragment5 = containerTransition.lastIn;
                            Fragment fragment6 = containerTransition.firstOut;
                            z = containerTransition.lastInIsPop;
                            boolean z4 = containerTransition.firstOutIsPop;
                            final Object enterTransition2 = getEnterTransition(fragment5, z);
                            Object exitTransition2 = getExitTransition(fragment6, z4);
                            final ArrayList arrayList3 = new ArrayList();
                            configureEnteringExitingViews = new ArrayList();
                            final Fragment fragment7 = containerTransition.lastIn;
                            final Fragment fragment8 = containerTransition.firstOut;
                            if (fragment7 == null || fragment8 == null) {
                                obj4 = null;
                            } else {
                                Object obj5;
                                final boolean z5 = containerTransition.lastInIsPop;
                                if (nameOverrides.isEmpty()) {
                                    obj = null;
                                } else {
                                    obj = getSharedElementTransition(fragment7, fragment8, z5);
                                }
                                ArrayMap captureOutSharedElements2 = captureOutSharedElements(nameOverrides, obj, containerTransition);
                                if (nameOverrides.isEmpty()) {
                                    obj5 = null;
                                } else {
                                    arrayList3.addAll(captureOutSharedElements2.values());
                                    obj5 = obj;
                                }
                                if (enterTransition2 == null && exitTransition2 == null && obj5 == null) {
                                    obj4 = null;
                                } else {
                                    Rect rect2;
                                    callSharedElementStartEnd(fragment7, fragment8, z5, captureOutSharedElements2, true);
                                    if (obj5 != null) {
                                        rect2 = new Rect();
                                        FragmentTransitionCompat21.setSharedElementTargets(obj5, view, arrayList3);
                                        setOutEpicenter(obj5, exitTransition2, captureOutSharedElements2, containerTransition.firstOutIsPop, containerTransition.firstOutTransaction);
                                        if (enterTransition2 != null) {
                                            FragmentTransitionCompat21.setEpicenter(enterTransition2, rect2);
                                        }
                                    } else {
                                        rect2 = null;
                                    }
                                    final ArrayMap<String, String> arrayMap = nameOverrides;
                                    final FragmentContainerTransition fragmentContainerTransition = containerTransition;
                                    inEpicenterView = view;
                                    OneShotPreDrawListener.add(view3, new Runnable() {
                                        public final void run() {
                                            ArrayMap<String, View> inSharedElements = FragmentTransition.captureInSharedElements(arrayMap, obj5, fragmentContainerTransition);
                                            if (inSharedElements != null) {
                                                configureEnteringExitingViews.addAll(inSharedElements.values());
                                                configureEnteringExitingViews.add(inEpicenterView);
                                            }
                                            FragmentTransition.callSharedElementStartEnd(fragment7, fragment8, z5, inSharedElements, false);
                                            if (obj5 != null) {
                                                FragmentTransitionCompat21.swapSharedElementTargets(obj5, arrayList3, configureEnteringExitingViews);
                                                View inEpicenterView = FragmentTransition.getInEpicenterView(inSharedElements, fragmentContainerTransition, enterTransition2, z5);
                                                if (inEpicenterView != null) {
                                                    FragmentTransitionCompat21.getBoundsOnScreen(inEpicenterView, rect2);
                                                }
                                            }
                                        }
                                    });
                                    obj4 = obj5;
                                }
                            }
                            if (enterTransition2 != null || obj4 != null || exitTransition2 != null) {
                                arrayList = configureEnteringExitingViews(exitTransition2, fragment6, arrayList3, view);
                                if (arrayList == null || arrayList.isEmpty()) {
                                    exitTransition2 = null;
                                }
                                if (enterTransition2 != null) {
                                    ((Transition) enterTransition2).addTarget(view);
                                }
                                Object mergeTransitions = mergeTransitions(enterTransition2, exitTransition2, obj4, fragment5, containerTransition.lastInIsPop);
                                if (mergeTransitions != null) {
                                    ArrayList arrayList4 = new ArrayList();
                                    FragmentTransitionCompat21.scheduleRemoveTargets(mergeTransitions, enterTransition2, arrayList4, exitTransition2, arrayList, obj4, configureEnteringExitingViews);
                                    final Object obj6 = enterTransition2;
                                    final View view4 = view;
                                    final Fragment fragment9 = fragment5;
                                    final ArrayList arrayList5 = arrayList4;
                                    final ArrayList arrayList6 = arrayList;
                                    exitTransition = exitTransition2;
                                    OneShotPreDrawListener.add(view3, new Runnable() {
                                        public final void run() {
                                            if (obj6 != null) {
                                                Object obj = obj6;
                                                View view = view4;
                                                if (obj != null) {
                                                    ((Transition) obj).removeTarget(view);
                                                }
                                                arrayList5.addAll(FragmentTransition.configureEnteringExitingViews(obj6, fragment9, configureEnteringExitingViews, view4));
                                            }
                                            if (arrayList6 != null) {
                                                if (exitTransition != null) {
                                                    ArrayList<View> tempExiting = new ArrayList();
                                                    tempExiting.add(view4);
                                                    FragmentTransitionCompat21.replaceTargets(exitTransition, arrayList6, tempExiting);
                                                }
                                                arrayList6.clear();
                                                arrayList6.add(view4);
                                            }
                                        }
                                    });
                                    OneShotPreDrawListener.add(view3, new android.support.v4.app.FragmentTransitionCompat21.AnonymousClass4(configureEnteringExitingViews, nameOverrides));
                                    FragmentTransitionCompat21.beginDelayedTransition(view3, mergeTransitions);
                                    OneShotPreDrawListener.add(view3, new AnonymousClass7(configureEnteringExitingViews, nameOverrides));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static ArrayMap<String, String> calculateNameOverrides(int containerId, ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, int startIndex, int endIndex) {
        ArrayMap<String, String> nameOverrides = new ArrayMap();
        for (int recordNum = endIndex - 1; recordNum >= startIndex; recordNum--) {
            BackStackRecord record = (BackStackRecord) records.get(recordNum);
            if (record.interactsWith(containerId)) {
                boolean isPop = ((Boolean) isRecordPop.get(recordNum)).booleanValue();
                if (record.mSharedElementSourceNames != null) {
                    ArrayList<String> targets;
                    ArrayList<String> sources;
                    int numSharedElements = record.mSharedElementSourceNames.size();
                    if (isPop) {
                        targets = record.mSharedElementSourceNames;
                        sources = record.mSharedElementTargetNames;
                    } else {
                        sources = record.mSharedElementSourceNames;
                        targets = record.mSharedElementTargetNames;
                    }
                    for (int i = 0; i < numSharedElements; i++) {
                        String sourceName = (String) sources.get(i);
                        String targetName = (String) targets.get(i);
                        String previousTarget = (String) nameOverrides.remove(targetName);
                        if (previousTarget != null) {
                            nameOverrides.put(sourceName, previousTarget);
                        } else {
                            nameOverrides.put(sourceName, targetName);
                        }
                    }
                }
            }
        }
        return nameOverrides;
    }

    private static Object getSharedElementTransition(Fragment inFragment, Fragment outFragment, boolean isPop) {
        if (inFragment == null || outFragment == null) {
            return null;
        }
        Object sharedElementReturnTransition;
        if (isPop) {
            sharedElementReturnTransition = outFragment.getSharedElementReturnTransition();
        } else {
            sharedElementReturnTransition = inFragment.getSharedElementEnterTransition();
        }
        Object transition = FragmentTransitionCompat21.cloneTransition(sharedElementReturnTransition);
        if (transition == null) {
            return null;
        }
        sharedElementReturnTransition = new TransitionSet();
        sharedElementReturnTransition.addTransition((Transition) transition);
        return sharedElementReturnTransition;
    }

    private static Object getEnterTransition(Fragment inFragment, boolean isPop) {
        if (inFragment == null) {
            return null;
        }
        Object reenterTransition;
        if (isPop) {
            reenterTransition = inFragment.getReenterTransition();
        } else {
            reenterTransition = inFragment.getEnterTransition();
        }
        return FragmentTransitionCompat21.cloneTransition(reenterTransition);
    }

    private static Object getExitTransition(Fragment outFragment, boolean isPop) {
        if (outFragment == null) {
            return null;
        }
        Object returnTransition;
        if (isPop) {
            returnTransition = outFragment.getReturnTransition();
        } else {
            returnTransition = outFragment.getExitTransition();
        }
        return FragmentTransitionCompat21.cloneTransition(returnTransition);
    }

    private static void addSharedElementsWithMatchingNames(ArrayList<View> views, ArrayMap<String, View> sharedElements, Collection<String> nameOverridesSet) {
        for (int i = sharedElements.size() - 1; i >= 0; i--) {
            View view = (View) sharedElements.valueAt(i);
            if (nameOverridesSet.contains(ViewCompat.getTransitionName(view))) {
                views.add(view);
            }
        }
    }

    private static ArrayMap<String, View> captureOutSharedElements(ArrayMap<String, String> nameOverrides, Object sharedElementTransition, FragmentContainerTransition fragments) {
        if (nameOverrides.isEmpty() || sharedElementTransition == null) {
            nameOverrides.clear();
            return null;
        }
        SharedElementCallback sharedElementCallback;
        ArrayList<String> names;
        Fragment outFragment = fragments.firstOut;
        ArrayMap<String, View> outSharedElements = new ArrayMap();
        FragmentTransitionCompat21.findNamedViews(outSharedElements, outFragment.getView());
        BackStackRecord outTransaction = fragments.firstOutTransaction;
        if (fragments.firstOutIsPop) {
            sharedElementCallback = outFragment.getEnterTransitionCallback();
            names = outTransaction.mSharedElementTargetNames;
        } else {
            sharedElementCallback = outFragment.getExitTransitionCallback();
            names = outTransaction.mSharedElementSourceNames;
        }
        outSharedElements.retainAll(names);
        if (sharedElementCallback != null) {
            for (int i = names.size() - 1; i >= 0; i--) {
                String name = (String) names.get(i);
                View view = (View) outSharedElements.get(name);
                if (view == null) {
                    nameOverrides.remove(name);
                } else if (!name.equals(ViewCompat.getTransitionName(view))) {
                    nameOverrides.put(ViewCompat.getTransitionName(view), (String) nameOverrides.remove(name));
                }
            }
            return outSharedElements;
        }
        nameOverrides.retainAll(outSharedElements.keySet());
        return outSharedElements;
    }

    private static ArrayMap<String, View> captureInSharedElements(ArrayMap<String, String> nameOverrides, Object sharedElementTransition, FragmentContainerTransition fragments) {
        Fragment inFragment = fragments.lastIn;
        View fragmentView = inFragment.getView();
        if (nameOverrides.isEmpty() || sharedElementTransition == null || fragmentView == null) {
            nameOverrides.clear();
            return null;
        }
        SharedElementCallback sharedElementCallback;
        ArrayList<String> names;
        ArrayMap<String, View> inSharedElements = new ArrayMap();
        FragmentTransitionCompat21.findNamedViews(inSharedElements, fragmentView);
        BackStackRecord inTransaction = fragments.lastInTransaction;
        if (fragments.lastInIsPop) {
            sharedElementCallback = inFragment.getExitTransitionCallback();
            names = inTransaction.mSharedElementSourceNames;
        } else {
            sharedElementCallback = inFragment.getEnterTransitionCallback();
            names = inTransaction.mSharedElementTargetNames;
        }
        if (names != null) {
            inSharedElements.retainAll(names);
        }
        if (sharedElementCallback != null) {
            for (int i = names.size() - 1; i >= 0; i--) {
                String name = (String) names.get(i);
                View view = (View) inSharedElements.get(name);
                String key;
                if (view == null) {
                    key = findKeyForValue(nameOverrides, name);
                    if (key != null) {
                        nameOverrides.remove(key);
                    }
                } else if (!name.equals(ViewCompat.getTransitionName(view))) {
                    key = findKeyForValue(nameOverrides, name);
                    if (key != null) {
                        nameOverrides.put(key, ViewCompat.getTransitionName(view));
                    }
                }
            }
            return inSharedElements;
        }
        for (int size = nameOverrides.size() - 1; size >= 0; size--) {
            if (!inSharedElements.containsKey((String) nameOverrides.valueAt(size))) {
                nameOverrides.removeAt(size);
            }
        }
        return inSharedElements;
    }

    private static String findKeyForValue(ArrayMap<String, String> map, String value) {
        int numElements = map.size();
        for (int i = 0; i < numElements; i++) {
            if (value.equals(map.valueAt(i))) {
                return (String) map.keyAt(i);
            }
        }
        return null;
    }

    private static View getInEpicenterView(ArrayMap<String, View> inSharedElements, FragmentContainerTransition fragments, Object enterTransition, boolean inIsPop) {
        BackStackRecord inTransaction = fragments.lastInTransaction;
        if (enterTransition == null || inSharedElements == null || inTransaction.mSharedElementSourceNames == null || inTransaction.mSharedElementSourceNames.isEmpty()) {
            return null;
        }
        String targetName;
        if (inIsPop) {
            targetName = (String) inTransaction.mSharedElementSourceNames.get(0);
        } else {
            targetName = (String) inTransaction.mSharedElementTargetNames.get(0);
        }
        return (View) inSharedElements.get(targetName);
    }

    private static void setOutEpicenter(Object sharedElementTransition, Object exitTransition, ArrayMap<String, View> outSharedElements, boolean outIsPop, BackStackRecord outTransaction) {
        if (outTransaction.mSharedElementSourceNames != null && !outTransaction.mSharedElementSourceNames.isEmpty()) {
            String sourceName;
            if (outIsPop) {
                sourceName = (String) outTransaction.mSharedElementTargetNames.get(0);
            } else {
                sourceName = (String) outTransaction.mSharedElementSourceNames.get(0);
            }
            View outEpicenterView = (View) outSharedElements.get(sourceName);
            FragmentTransitionCompat21.setEpicenter(sharedElementTransition, outEpicenterView);
            if (exitTransition != null) {
                FragmentTransitionCompat21.setEpicenter(exitTransition, outEpicenterView);
            }
        }
    }

    private static void callSharedElementStartEnd(Fragment inFragment, Fragment outFragment, boolean isPop, ArrayMap<String, View> sharedElements, boolean isStart) {
        SharedElementCallback sharedElementCallback;
        if (isPop) {
            sharedElementCallback = outFragment.getEnterTransitionCallback();
        } else {
            sharedElementCallback = inFragment.getEnterTransitionCallback();
        }
        if (sharedElementCallback != null) {
            ArrayList<View> views = new ArrayList();
            ArrayList<String> names = new ArrayList();
            int count = sharedElements == null ? 0 : sharedElements.size();
            for (int i = 0; i < count; i++) {
                names.add(sharedElements.keyAt(i));
                views.add(sharedElements.valueAt(i));
            }
        }
    }

    private static ArrayList<View> configureEnteringExitingViews(Object transition, Fragment fragment, ArrayList<View> sharedElements, View nonExistentView) {
        ArrayList<View> viewList = null;
        if (transition != null) {
            viewList = new ArrayList();
            View root = fragment.getView();
            if (root != null) {
                FragmentTransitionCompat21.captureTransitioningViews(viewList, root);
            }
            if (sharedElements != null) {
                viewList.removeAll(sharedElements);
            }
            if (!viewList.isEmpty()) {
                viewList.add(nonExistentView);
                FragmentTransitionCompat21.addTargets(transition, viewList);
            }
        }
        return viewList;
    }

    private static void setViewVisibility(ArrayList<View> views, int visibility) {
        if (views != null) {
            for (int i = views.size() - 1; i >= 0; i--) {
                ((View) views.get(i)).setVisibility(visibility);
            }
        }
    }

    private static Object mergeTransitions(Object enterTransition, Object exitTransition, Object sharedElementTransition, Fragment inFragment, boolean isPop) {
        boolean overlap = true;
        if (!(enterTransition == null || exitTransition == null || inFragment == null)) {
            overlap = isPop ? inFragment.getAllowReturnTransitionOverlap() : inFragment.getAllowEnterTransitionOverlap();
        }
        Object transition;
        if (overlap) {
            transition = new TransitionSet();
            if (exitTransition != null) {
                transition.addTransition((Transition) exitTransition);
            }
            if (enterTransition != null) {
                transition.addTransition((Transition) enterTransition);
            }
            if (sharedElementTransition == null) {
                return transition;
            }
            transition.addTransition((Transition) sharedElementTransition);
            return transition;
        }
        Transition transition2 = null;
        Transition transition3 = (Transition) exitTransition;
        Transition transition4 = (Transition) enterTransition;
        Transition transition5 = (Transition) sharedElementTransition;
        if (transition3 != null && transition4 != null) {
            transition2 = new TransitionSet().addTransition(transition3).addTransition(transition4).setOrdering(1);
        } else if (transition3 != null) {
            transition2 = transition3;
        } else if (transition4 != null) {
            transition2 = transition4;
        }
        if (transition5 == null) {
            return transition2;
        }
        transition = new TransitionSet();
        if (transition2 != null) {
            transition.addTransition(transition2);
        }
        transition.addTransition(transition5);
        return transition;
    }

    private static void calculateFragments(BackStackRecord transaction, SparseArray<FragmentContainerTransition> transitioningFragments, boolean isOptimized) {
        int numOps = transaction.mOps.size();
        for (int opNum = 0; opNum < numOps; opNum++) {
            addToFirstInLastOut(transaction, (Op) transaction.mOps.get(opNum), transitioningFragments, false, isOptimized);
        }
    }

    private static void calculatePopFragments(BackStackRecord transaction, SparseArray<FragmentContainerTransition> transitioningFragments, boolean isOptimized) {
        if (transaction.mManager.mContainer.onHasView()) {
            for (int opNum = transaction.mOps.size() - 1; opNum >= 0; opNum--) {
                addToFirstInLastOut(transaction, (Op) transaction.mOps.get(opNum), transitioningFragments, true, isOptimized);
            }
        }
    }

    private static void addToFirstInLastOut(BackStackRecord transaction, Op op, SparseArray<FragmentContainerTransition> transitioningFragments, boolean isPop, boolean isOptimizedTransaction) {
        Fragment fragment = op.fragment;
        int containerId = fragment.mContainerId;
        if (containerId != 0) {
            boolean setLastIn = false;
            boolean wasRemoved = false;
            boolean setFirstOut = false;
            boolean wasAdded = false;
            switch (isPop ? INVERSE_OPS[op.cmd] : op.cmd) {
                case 1:
                case 7:
                    setLastIn = isOptimizedTransaction ? fragment.mIsNewlyAdded : (fragment.mAdded || fragment.mHidden) ? false : true;
                    wasAdded = true;
                    break;
                case 3:
                case 6:
                    setFirstOut = isOptimizedTransaction ? !fragment.mAdded && fragment.mView != null && fragment.mView.getVisibility() == 0 && fragment.mPostponedAlpha >= 0.0f : fragment.mAdded && !fragment.mHidden;
                    wasRemoved = true;
                    break;
                case 4:
                    setFirstOut = isOptimizedTransaction ? fragment.mHiddenChanged && fragment.mAdded && fragment.mHidden : fragment.mAdded && !fragment.mHidden;
                    wasRemoved = true;
                    break;
                case 5:
                    setLastIn = isOptimizedTransaction ? fragment.mHiddenChanged && !fragment.mHidden && fragment.mAdded : fragment.mHidden;
                    wasAdded = true;
                    break;
            }
            FragmentContainerTransition containerTransition = (FragmentContainerTransition) transitioningFragments.get(containerId);
            if (setLastIn) {
                containerTransition = ensureContainer(containerTransition, transitioningFragments, containerId);
                containerTransition.lastIn = fragment;
                containerTransition.lastInIsPop = isPop;
                containerTransition.lastInTransaction = transaction;
            }
            if (!isOptimizedTransaction && wasAdded) {
                if (containerTransition != null && containerTransition.firstOut == fragment) {
                    containerTransition.firstOut = null;
                }
                FragmentManagerImpl manager = transaction.mManager;
                if (fragment.mState <= 0 && manager.mCurState > 0 && !transaction.mAllowOptimization) {
                    manager.makeActive(fragment);
                    manager.moveToState(fragment, 1, 0, 0, false);
                }
            }
            if (setFirstOut && (containerTransition == null || containerTransition.firstOut == null)) {
                containerTransition = ensureContainer(containerTransition, transitioningFragments, containerId);
                containerTransition.firstOut = fragment;
                containerTransition.firstOutIsPop = isPop;
                containerTransition.firstOutTransaction = transaction;
            }
            if (!isOptimizedTransaction && wasRemoved && containerTransition != null && containerTransition.lastIn == fragment) {
                containerTransition.lastIn = null;
            }
        }
    }

    private static FragmentContainerTransition ensureContainer(FragmentContainerTransition containerTransition, SparseArray<FragmentContainerTransition> transitioningFragments, int containerId) {
        if (containerTransition != null) {
            return containerTransition;
        }
        containerTransition = new FragmentContainerTransition();
        transitioningFragments.put(containerId, containerTransition);
        return containerTransition;
    }
}
