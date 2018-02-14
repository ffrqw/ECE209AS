package android.support.v4.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater.Factory;
import android.view.View;

@TargetApi(9)
final class LayoutInflaterCompatBase {

    static class FactoryWrapper implements Factory {
        final LayoutInflaterFactory mDelegateFactory;

        FactoryWrapper(LayoutInflaterFactory delegateFactory) {
            this.mDelegateFactory = delegateFactory;
        }

        public View onCreateView(String name, Context context, AttributeSet attrs) {
            return this.mDelegateFactory.onCreateView(null, name, context, attrs);
        }

        public String toString() {
            return getClass().getName() + "{" + this.mDelegateFactory + "}";
        }
    }
}
