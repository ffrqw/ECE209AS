package com.rachio.iro.binder;

import android.content.Context;
import android.content.ContextWrapper;
import com.rachio.iro.IroApplication;
import com.rachio.iro.ui.activity.BaseActivity;

public abstract class BaseModelViewBinder<T> implements ModelViewBinder<T> {
    protected abstract void onBind(ModelObjectViewHolder modelObjectViewHolder, T t);

    protected void setContentShown(ModelObjectViewHolder holder, boolean isShown) {
    }

    public final void bind(ModelObjectViewHolder holder, T item) {
        IroApplication.get(holder.itemView.getContext().getApplicationContext()).component();
        if (item == null) {
            setContentShown(holder, false);
            return;
        }
        onBind(holder, item);
        setContentShown(holder, true);
    }

    public static BaseActivity findActivity(Context context) {
        while (!(context instanceof BaseActivity)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return (BaseActivity) context;
    }
}
