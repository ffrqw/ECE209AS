package com.rachio.iro.binder;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

public abstract class ModelObjectViewHolder extends ViewHolder {
    public ModelObjectViewHolder(View itemView) {
        super(itemView);
    }

    public final <T> T findView(int viewId, boolean isOptional) {
        T view = this.itemView.findViewById(viewId);
        if (isOptional || view != null) {
            return view;
        }
        String resourceEntryName = this.itemView.getResources().getResourceEntryName(viewId);
        throw new IllegalArgumentException(String.format("Error while creating a ViewHolder for %s. The view provided did not contain an element with an id of '%s'. Please double check the layout for %s and verify that it has an element with an id of '%s'", new Object[]{getClass().getSimpleName(), resourceEntryName, getClass().getSimpleName(), resourceEntryName}));
    }
}
