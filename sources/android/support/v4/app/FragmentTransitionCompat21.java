package android.support.v4.app;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.transition.Transition;
import android.transition.Transition.EpicenterCallback;
import android.transition.Transition.TransitionListener;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@TargetApi(21)
final class FragmentTransitionCompat21 {

    /* renamed from: android.support.v4.app.FragmentTransitionCompat21$2 */
    static class AnonymousClass2 implements TransitionListener {
        final /* synthetic */ ArrayList val$exitingViews;
        final /* synthetic */ View val$fragmentView;

        AnonymousClass2(View view, ArrayList arrayList) {
            this.val$fragmentView = view;
            this.val$exitingViews = arrayList;
        }

        public final void onTransitionStart(Transition transition) {
        }

        public final void onTransitionEnd(Transition transition) {
            transition.removeListener(this);
            this.val$fragmentView.setVisibility(8);
            int numViews = this.val$exitingViews.size();
            for (int i = 0; i < numViews; i++) {
                ((View) this.val$exitingViews.get(i)).setVisibility(0);
            }
        }

        public final void onTransitionCancel(Transition transition) {
        }

        public final void onTransitionPause(Transition transition) {
        }

        public final void onTransitionResume(Transition transition) {
        }
    }

    /* renamed from: android.support.v4.app.FragmentTransitionCompat21$4 */
    static class AnonymousClass4 implements Runnable {
        final /* synthetic */ Map val$nameOverrides;
        final /* synthetic */ ArrayList val$sharedElementsIn;

        AnonymousClass4(ArrayList arrayList, Map map) {
            this.val$sharedElementsIn = arrayList;
            this.val$nameOverrides = map;
        }

        public final void run() {
            int numSharedElements = this.val$sharedElementsIn.size();
            for (int i = 0; i < numSharedElements; i++) {
                View view = (View) this.val$sharedElementsIn.get(i);
                String name = view.getTransitionName();
                if (name != null) {
                    view.setTransitionName(FragmentTransitionCompat21.access$000(this.val$nameOverrides, name));
                }
            }
        }
    }

    /* renamed from: android.support.v4.app.FragmentTransitionCompat21$7 */
    static class AnonymousClass7 implements Runnable {
        final /* synthetic */ Map val$nameOverrides;
        final /* synthetic */ ArrayList val$sharedElementsIn;

        AnonymousClass7(ArrayList arrayList, Map map) {
            this.val$sharedElementsIn = arrayList;
            this.val$nameOverrides = map;
        }

        public final void run() {
            int numSharedElements = this.val$sharedElementsIn.size();
            for (int i = 0; i < numSharedElements; i++) {
                View view = (View) this.val$sharedElementsIn.get(i);
                view.setTransitionName((String) this.val$nameOverrides.get(view.getTransitionName()));
            }
        }
    }

    public static Object cloneTransition(Object transition) {
        if (transition != null) {
            return ((Transition) transition).clone();
        }
        return null;
    }

    public static void setSharedElementTargets(Object transitionObj, View nonExistentView, ArrayList<View> sharedViews) {
        TransitionSet transition = (TransitionSet) transitionObj;
        List<View> views = transition.getTargets();
        views.clear();
        int count = sharedViews.size();
        for (int i = 0; i < count; i++) {
            View view = (View) sharedViews.get(i);
            int size = views.size();
            if (!containedBeforeIndex(views, view, size)) {
                views.add(view);
                for (int i2 = size; i2 < views.size(); i2++) {
                    View view2 = (View) views.get(i2);
                    if (view2 instanceof ViewGroup) {
                        ViewGroup viewGroup = (ViewGroup) view2;
                        int childCount = viewGroup.getChildCount();
                        for (int i3 = 0; i3 < childCount; i3++) {
                            View childAt = viewGroup.getChildAt(i3);
                            if (!containedBeforeIndex(views, childAt, size)) {
                                views.add(childAt);
                            }
                        }
                    }
                }
            }
        }
        views.add(nonExistentView);
        sharedViews.add(nonExistentView);
        addTargets(transition, sharedViews);
    }

    private static boolean containedBeforeIndex(List<View> views, View view, int maxIndex) {
        for (int i = 0; i < maxIndex; i++) {
            if (views.get(i) == view) {
                return true;
            }
        }
        return false;
    }

    public static void setEpicenter(Object transitionObj, View view) {
        if (view != null) {
            Transition transition = (Transition) transitionObj;
            final Rect epicenter = new Rect();
            getBoundsOnScreen(view, epicenter);
            transition.setEpicenterCallback(new EpicenterCallback() {
                public final Rect onGetEpicenter(Transition transition) {
                    return epicenter;
                }
            });
        }
    }

    public static void getBoundsOnScreen(View view, Rect epicenter) {
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        epicenter.set(loc[0], loc[1], loc[0] + view.getWidth(), loc[1] + view.getHeight());
    }

    public static void addTargets(Object transitionObj, ArrayList<View> views) {
        Transition transition = (Transition) transitionObj;
        if (transition != null) {
            int i;
            if (transition instanceof TransitionSet) {
                TransitionSet set = (TransitionSet) transition;
                int numTransitions = set.getTransitionCount();
                for (i = 0; i < numTransitions; i++) {
                    addTargets(set.getTransitionAt(i), views);
                }
            } else if (!hasSimpleTarget(transition) && isNullOrEmpty(transition.getTargets())) {
                int numViews = views.size();
                for (i = 0; i < numViews; i++) {
                    transition.addTarget((View) views.get(i));
                }
            }
        }
    }

    private static boolean hasSimpleTarget(Transition transition) {
        return (isNullOrEmpty(transition.getTargetIds()) && isNullOrEmpty(transition.getTargetNames()) && isNullOrEmpty(transition.getTargetTypes())) ? false : true;
    }

    private static boolean isNullOrEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static void beginDelayedTransition(ViewGroup sceneRoot, Object transition) {
        TransitionManager.beginDelayedTransition(sceneRoot, (Transition) transition);
    }

    public static ArrayList<String> prepareSetNameOverridesOptimized(ArrayList<View> sharedElementsIn) {
        ArrayList<String> names = new ArrayList();
        int numSharedElements = sharedElementsIn.size();
        for (int i = 0; i < numSharedElements; i++) {
            View view = (View) sharedElementsIn.get(i);
            names.add(view.getTransitionName());
            view.setTransitionName(null);
        }
        return names;
    }

    public static void setNameOverridesOptimized(View sceneRoot, ArrayList<View> sharedElementsOut, ArrayList<View> sharedElementsIn, ArrayList<String> inNames, Map<String, String> nameOverrides) {
        final int numSharedElements = sharedElementsIn.size();
        final ArrayList<String> outNames = new ArrayList();
        for (int i = 0; i < numSharedElements; i++) {
            View view = (View) sharedElementsOut.get(i);
            String name = view.getTransitionName();
            outNames.add(name);
            if (name != null) {
                view.setTransitionName(null);
                String inName = (String) nameOverrides.get(name);
                for (int j = 0; j < numSharedElements; j++) {
                    if (inName.equals(inNames.get(j))) {
                        ((View) sharedElementsIn.get(j)).setTransitionName(name);
                        break;
                    }
                }
            }
        }
        final ArrayList<View> arrayList = sharedElementsIn;
        final ArrayList<String> arrayList2 = inNames;
        final ArrayList<View> arrayList3 = sharedElementsOut;
        OneShotPreDrawListener.add(sceneRoot, new Runnable() {
            public final void run() {
                for (int i = 0; i < numSharedElements; i++) {
                    ((View) arrayList.get(i)).setTransitionName((String) arrayList2.get(i));
                    ((View) arrayList3.get(i)).setTransitionName((String) outNames.get(i));
                }
            }
        });
    }

    public static void captureTransitioningViews(ArrayList<View> transitioningViews, View view) {
        if (view.getVisibility() != 0) {
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            if (viewGroup.isTransitionGroup()) {
                transitioningViews.add(viewGroup);
                return;
            }
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                captureTransitioningViews(transitioningViews, viewGroup.getChildAt(i));
            }
            return;
        }
        transitioningViews.add(view);
    }

    public static void findNamedViews(Map<String, View> namedViews, View view) {
        if (view.getVisibility() == 0) {
            String transitionName = view.getTransitionName();
            if (transitionName != null) {
                namedViews.put(transitionName, view);
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int count = viewGroup.getChildCount();
                for (int i = 0; i < count; i++) {
                    findNamedViews(namedViews, viewGroup.getChildAt(i));
                }
            }
        }
    }

    public static void scheduleRemoveTargets(Object overallTransitionObj, Object enterTransition, ArrayList<View> enteringViews, Object exitTransition, ArrayList<View> exitingViews, Object sharedElementTransition, ArrayList<View> sharedElementsIn) {
        final Object obj = enterTransition;
        final ArrayList<View> arrayList = enteringViews;
        final Object obj2 = exitTransition;
        final ArrayList<View> arrayList2 = exitingViews;
        final Object obj3 = sharedElementTransition;
        final ArrayList<View> arrayList3 = sharedElementsIn;
        ((Transition) overallTransitionObj).addListener(new TransitionListener() {
            public final void onTransitionStart(Transition transition) {
                if (obj != null) {
                    FragmentTransitionCompat21.replaceTargets(obj, arrayList, null);
                }
                if (obj2 != null) {
                    FragmentTransitionCompat21.replaceTargets(obj2, arrayList2, null);
                }
                if (obj3 != null) {
                    FragmentTransitionCompat21.replaceTargets(obj3, arrayList3, null);
                }
            }

            public final void onTransitionEnd(Transition transition) {
            }

            public final void onTransitionCancel(Transition transition) {
            }

            public final void onTransitionPause(Transition transition) {
            }

            public final void onTransitionResume(Transition transition) {
            }
        });
    }

    public static void swapSharedElementTargets(Object sharedElementTransitionObj, ArrayList<View> sharedElementsOut, ArrayList<View> sharedElementsIn) {
        TransitionSet sharedElementTransition = (TransitionSet) sharedElementTransitionObj;
        if (sharedElementTransition != null) {
            sharedElementTransition.getTargets().clear();
            sharedElementTransition.getTargets().addAll(sharedElementsIn);
            replaceTargets(sharedElementTransition, sharedElementsOut, sharedElementsIn);
        }
    }

    public static void replaceTargets(Object transitionObj, ArrayList<View> oldTargets, ArrayList<View> newTargets) {
        Transition transition = (Transition) transitionObj;
        int i;
        if (transition instanceof TransitionSet) {
            TransitionSet set = (TransitionSet) transition;
            int numTransitions = set.getTransitionCount();
            for (i = 0; i < numTransitions; i++) {
                replaceTargets(set.getTransitionAt(i), oldTargets, newTargets);
            }
        } else if (!hasSimpleTarget(transition)) {
            List<View> targets = transition.getTargets();
            if (targets != null && targets.size() == oldTargets.size() && targets.containsAll(oldTargets)) {
                int targetCount = newTargets == null ? 0 : newTargets.size();
                for (i = 0; i < targetCount; i++) {
                    transition.addTarget((View) newTargets.get(i));
                }
                for (i = oldTargets.size() - 1; i >= 0; i--) {
                    transition.removeTarget((View) oldTargets.get(i));
                }
            }
        }
    }

    public static void setEpicenter(Object transitionObj, final Rect epicenter) {
        if (transitionObj != null) {
            ((Transition) transitionObj).setEpicenterCallback(new EpicenterCallback() {
                public final Rect onGetEpicenter(Transition transition) {
                    if (epicenter == null || epicenter.isEmpty()) {
                        return null;
                    }
                    return epicenter;
                }
            });
        }
    }

    static /* synthetic */ String access$000(Map x0, String x1) {
        for (Entry entry : x0.entrySet()) {
            if (x1.equals(entry.getValue())) {
                return (String) entry.getKey();
            }
        }
        return null;
    }
}
