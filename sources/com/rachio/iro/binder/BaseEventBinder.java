package com.rachio.iro.binder;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.rachio.iro.R;
import com.rachio.iro.binder.viewholder.CardHeaderFooterViewHolder;
import com.rachio.iro.model.Event;
import com.rachio.iro.ui.view.EventCardItem;
import com.rachio.iro.ui.view.ProgressView;
import java.util.List;

public abstract class BaseEventBinder extends BaseModelViewBinder<List<Event>> {

    public interface MoreHistoryListener {
        void onMoreDeviceUpdates(String str);
    }

    public static class ViewHolder extends ModelObjectViewHolder {
        public LinearLayout cardContent = ((LinearLayout) findView(R.id.card_content, false));
        public CardHeaderFooterViewHolder headerFooterHolder;
        public ProgressView progressView;

        public ViewHolder(View v) {
            super(v);
            this.headerFooterHolder = new CardHeaderFooterViewHolder(v);
            this.progressView = (ProgressView) findView(R.id.progress_view, false);
        }
    }

    protected abstract String getTitle();

    protected abstract String getTopic();

    public final /* bridge */ /* synthetic */ void onBind(ModelObjectViewHolder modelObjectViewHolder, Object obj) {
        List list = (List) obj;
        ViewHolder viewHolder = (ViewHolder) modelObjectViewHolder;
        viewHolder.cardContent.removeAllViews();
        int min = Math.min(list.size(), 3);
        for (int i = 0; i < min; i++) {
            boolean z;
            View eventCardItem = new EventCardItem(viewHolder.cardContent.getContext());
            Event event = (Event) list.get(i);
            if (i + 1 == min) {
                z = true;
            } else {
                z = false;
            }
            eventCardItem.setEvent(event, z);
            viewHolder.cardContent.addView(eventCardItem);
        }
    }

    public final ModelObjectViewHolder createViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected final void setContentShown(ModelObjectViewHolder holder, boolean isShown) {
        int color;
        int i = 8;
        ViewHolder viewHolder = (ViewHolder) holder;
        if (TextUtils.isEmpty(viewHolder.headerFooterHolder.headerTextLeft.getText())) {
            color = viewHolder.itemView.getResources().getColor(R.color.rachio_event_device_updates);
            viewHolder.headerFooterHolder.headerTextLeft.setText(getTitle());
            viewHolder.headerFooterHolder.headerTextRight.setText("");
            viewHolder.headerFooterHolder.headerBackground.setColor(color);
            viewHolder.headerFooterHolder.footerText.setText("More");
            viewHolder.headerFooterHolder.footerContainer.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (v.getContext() instanceof MoreHistoryListener) {
                        ((MoreHistoryListener) v.getContext()).onMoreDeviceUpdates(BaseEventBinder.this.getTopic());
                    }
                }
            });
        }
        ProgressView progressView = viewHolder.progressView;
        if (isShown) {
            color = 8;
        } else {
            color = 0;
        }
        progressView.setVisibility(color);
        ProgressBar progressBar = viewHolder.headerFooterHolder.headerProgressBar;
        if (!isShown) {
            i = 0;
        }
        progressBar.setVisibility(i);
    }

    public final int getLayoutId() {
        return R.layout.view_card_events;
    }
}
