package android.support.v4.view;

import android.os.Build.VERSION;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.LayoutInflater.Factory2;

public final class LayoutInflaterCompat {
    static final LayoutInflaterCompatImpl IMPL;

    interface LayoutInflaterCompatImpl {
        LayoutInflaterFactory getFactory(LayoutInflater layoutInflater);

        void setFactory(LayoutInflater layoutInflater, LayoutInflaterFactory layoutInflaterFactory);
    }

    static class LayoutInflaterCompatImplBase implements LayoutInflaterCompatImpl {
        LayoutInflaterCompatImplBase() {
        }

        public void setFactory(LayoutInflater layoutInflater, LayoutInflaterFactory factory) {
            Factory factoryWrapper;
            if (factory != null) {
                factoryWrapper = new FactoryWrapper(factory);
            } else {
                factoryWrapper = null;
            }
            layoutInflater.setFactory(factoryWrapper);
        }

        public final LayoutInflaterFactory getFactory(LayoutInflater layoutInflater) {
            Factory factory = layoutInflater.getFactory();
            if (factory instanceof FactoryWrapper) {
                return ((FactoryWrapper) factory).mDelegateFactory;
            }
            return null;
        }
    }

    static class LayoutInflaterCompatImplV11 extends LayoutInflaterCompatImplBase {
        LayoutInflaterCompatImplV11() {
        }

        public void setFactory(LayoutInflater layoutInflater, LayoutInflaterFactory factory) {
            Factory2 factoryWrapperHC;
            if (factory != null) {
                factoryWrapperHC = new FactoryWrapperHC(factory);
            } else {
                factoryWrapperHC = null;
            }
            layoutInflater.setFactory2(factoryWrapperHC);
            Factory factory2 = layoutInflater.getFactory();
            if (factory2 instanceof Factory2) {
                LayoutInflaterCompatHC.forceSetFactory2(layoutInflater, (Factory2) factory2);
            } else {
                LayoutInflaterCompatHC.forceSetFactory2(layoutInflater, factoryWrapperHC);
            }
        }
    }

    static class LayoutInflaterCompatImplV21 extends LayoutInflaterCompatImplV11 {
        LayoutInflaterCompatImplV21() {
        }

        public final void setFactory(LayoutInflater layoutInflater, LayoutInflaterFactory factory) {
            Factory2 factoryWrapperHC;
            if (factory != null) {
                factoryWrapperHC = new FactoryWrapperHC(factory);
            } else {
                factoryWrapperHC = null;
            }
            layoutInflater.setFactory2(factoryWrapperHC);
        }
    }

    static {
        int version = VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new LayoutInflaterCompatImplV21();
        } else if (version >= 11) {
            IMPL = new LayoutInflaterCompatImplV11();
        } else {
            IMPL = new LayoutInflaterCompatImplBase();
        }
    }

    public static void setFactory(LayoutInflater inflater, LayoutInflaterFactory factory) {
        IMPL.setFactory(inflater, factory);
    }

    public static LayoutInflaterFactory getFactory(LayoutInflater inflater) {
        return IMPL.getFactory(inflater);
    }
}
