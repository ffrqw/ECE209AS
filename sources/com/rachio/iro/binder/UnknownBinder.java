package com.rachio.iro.binder;

import android.view.View;
import android.widget.TextView;
import com.rachio.iro.R;

public class UnknownBinder implements ModelViewBinder {

    public static class ViewHolder extends ModelObjectViewHolder {
        public TextView textView = ((TextView) findView(R.id.text, false));

        public ViewHolder(View v) {
            super(v);
        }
    }

    public final int getLayoutId() {
        return R.layout.view_card_device_unknown;
    }

    public final ModelObjectViewHolder createViewHolder(View v) {
        return new ViewHolder(v);
    }

    public final void bind(ModelObjectViewHolder holder, Object item) {
        String str;
        ViewHolder viewHolder = (ViewHolder) holder;
        String str2 = "Processing data%s";
        Object[] objArr = new Object[1];
        if (item == null) {
            str = "...";
        } else {
            str = " for " + item.getClass().getSimpleName() + "...";
        }
        objArr[0] = str;
        viewHolder.textView.setText(String.format(str2, objArr));
    }
}
