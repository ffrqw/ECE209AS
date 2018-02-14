package com.rachio.iro.binder;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchZonesCommand.ZonesMetaHolder;
import com.rachio.iro.binder.viewholder.CardHeaderFooterViewHolder;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.view.MyYardColumnLayout;

public class MyYardBinder extends BaseModelViewBinder<ZonesMetaHolder> {

    public interface MyYardCardListener {
        void onConfigureZones();

        void onToggleShowDisabled();
    }

    public static class ViewHolder extends ModelObjectViewHolder {
        public CardHeaderFooterViewHolder headerHolder;
        public MyYardColumnLayout myYardColumnLayout = ((MyYardColumnLayout) findView(R.id.stamp_collector, false));
        public TextView setupZones;
        public TextView toggleDisabledZones;

        public ViewHolder(View v) {
            super(v);
            this.headerHolder = new CardHeaderFooterViewHolder(v);
            this.setupZones = (TextView) findView(R.id.myyard_setupzones, false);
            this.toggleDisabledZones = (TextView) findView(R.id.myyard_toggledisabledzones, false);
            this.setupZones.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    BaseActivity activity = BaseModelViewBinder.findActivity(v.getContext());
                    if (activity instanceof MyYardCardListener) {
                        ((MyYardCardListener) activity).onConfigureZones();
                    }
                }
            });
            this.toggleDisabledZones.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    BaseActivity activity = BaseModelViewBinder.findActivity(v.getContext());
                    if (activity instanceof MyYardCardListener) {
                        ((MyYardCardListener) activity).onToggleShowDisabled();
                    }
                }
            });
        }
    }

    protected final /* bridge */ /* synthetic */ void onBind(ModelObjectViewHolder modelObjectViewHolder, Object obj) {
        CharSequence charSequence;
        int i;
        boolean z = true;
        ZonesMetaHolder zonesMetaHolder = (ZonesMetaHolder) obj;
        ViewHolder viewHolder = (ViewHolder) modelObjectViewHolder;
        TextView textView = viewHolder.headerHolder.headerTextRight;
        if (zonesMetaHolder.includesDisabled) {
            charSequence = zonesMetaHolder.zones.size() + " Zones";
        } else {
            charSequence = zonesMetaHolder.zones.size() + "/" + zonesMetaHolder.deviceTotalZones + " Zones";
        }
        textView.setText(charSequence);
        viewHolder.myYardColumnLayout.setZones(zonesMetaHolder.zones, zonesMetaHolder.activeZone, zonesMetaHolder.activeZoneDuration);
        textView = viewHolder.toggleDisabledZones;
        if (zonesMetaHolder.allZonesEnabled) {
            i = 4;
        } else {
            i = 0;
        }
        textView.setVisibility(i);
        viewHolder.toggleDisabledZones.setText(zonesMetaHolder.includesDisabled ? "Hide Disabled Zones" : "Show Disabled Zones");
        Drawable drawable = viewHolder.toggleDisabledZones.getCompoundDrawables()[0];
        if (zonesMetaHolder.includesDisabled) {
            i = 1;
        } else {
            i = 0;
        }
        drawable.setLevel(i);
        TextView textView2 = viewHolder.setupZones;
        if (zonesMetaHolder.readOnly) {
            z = false;
        }
        textView2.setEnabled(z);
    }

    public final int getLayoutId() {
        return R.layout.view_card_device_my_yard;
    }

    public final ModelObjectViewHolder createViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected final void setContentShown(ModelObjectViewHolder holder, boolean isShown) {
        ViewHolder viewHolder = (ViewHolder) holder;
        if (TextUtils.isEmpty(viewHolder.headerHolder.headerTextLeft.getText())) {
            int color = viewHolder.itemView.getResources().getColor(R.color.rachio_blue_grey_300);
            viewHolder.headerHolder.headerTextLeft.setText("My Yard");
            viewHolder.headerHolder.headerTextRight.setText("");
            viewHolder.headerHolder.headerBackground.setColor(color);
        }
        viewHolder.headerHolder.headerProgressBar.setVisibility(isShown ? 8 : 0);
    }
}
