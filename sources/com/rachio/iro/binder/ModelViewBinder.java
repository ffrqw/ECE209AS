package com.rachio.iro.binder;

import android.view.View;

public interface ModelViewBinder<T> {
    void bind(ModelObjectViewHolder modelObjectViewHolder, T t);

    ModelObjectViewHolder createViewHolder(View view);

    int getLayoutId();
}
