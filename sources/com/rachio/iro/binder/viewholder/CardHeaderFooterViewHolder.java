package com.rachio.iro.binder.viewholder;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.binder.ModelObjectViewHolder;

public class CardHeaderFooterViewHolder extends ModelObjectViewHolder {
    public ViewGroup footerContainer = ((ViewGroup) findView(R.id.footer_content, true));
    public TextView footerText = ((TextView) findView(R.id.footer_text, true));
    public GradientDrawable headerBackground;
    public ProgressBar headerProgressBar = ((ProgressBar) findView(R.id.header_progress_bar, true));
    public TextView headerTextLeft = ((TextView) findView(R.id.header_text_left, true));
    public TextView headerTextRight = ((TextView) findView(R.id.header_text_right, true));

    public CardHeaderFooterViewHolder(View itemView) {
        super(itemView);
        ViewGroup header = (ViewGroup) findView(R.id.header_content, true);
        if (header != null) {
            this.headerBackground = (GradientDrawable) header.getBackground();
        }
    }
}
