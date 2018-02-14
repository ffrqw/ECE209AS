package com.rachio.iro.binder;

import android.view.View;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchWaterUsageCommand.WaterUsageHolder;
import com.rachio.iro.binder.viewholder.CardHeaderFooterViewHolder;
import com.rachio.iro.ui.widget.MeterWidget;
import com.rachio.iro.utils.StringUtils;
import com.rachio.iro.utils.UnitUtils;
import java.util.Calendar;
import java.util.Locale;

public class WaterUseBinder implements ModelViewBinder<WaterUsageHolder> {

    public static class ViewHolder extends ModelObjectViewHolder {
        MeterWidget gallonsSavedMeter = ((MeterWidget) findView(R.id.gallons_saved_meter, false));
        TextView gallonsSavedText = ((TextView) findView(R.id.gallons_saved_text, false));
        MeterWidget gallonsUsedMeter = ((MeterWidget) findView(R.id.gallons_used_meter, false));
        TextView gallonsUsedText = ((TextView) findView(R.id.gallons_used_text, false));
        CardHeaderFooterViewHolder headerFooterHolder;

        public ViewHolder(View v) {
            super(v);
            this.headerFooterHolder = new CardHeaderFooterViewHolder(v);
        }
    }

    public final /* bridge */ /* synthetic */ void bind(ModelObjectViewHolder modelObjectViewHolder, Object obj) {
        WaterUsageHolder waterUsageHolder = (WaterUsageHolder) obj;
        ViewHolder viewHolder = (ViewHolder) modelObjectViewHolder;
        viewHolder.headerFooterHolder.headerBackground.setColor(viewHolder.itemView.getResources().getColor(R.color.rachio_blue));
        viewHolder.headerFooterHolder.headerTextLeft.setText("Water Use");
        Calendar instance = Calendar.getInstance();
        viewHolder.headerFooterHolder.headerTextRight.setText(String.format("%s %d", new Object[]{instance.getDisplayName(2, 2, Locale.US), Integer.valueOf(instance.get(1))}));
        if (waterUsageHolder != null) {
            float f = (float) waterUsageHolder.gallonsUsed;
            float f2 = (float) waterUsageHolder.gallonsSaved;
            float max = (float) Math.max(Math.max(waterUsageHolder.gallonsUsed, waterUsageHolder.gallonsUsedLast), Math.max(waterUsageHolder.gallonsSaved, waterUsageHolder.gallonsSavedLast));
            String nameOfWaterUnits = UnitUtils.getNameOfWaterUnits(waterUsageHolder.displayUnit);
            max = (float) Math.round((float) UnitUtils.convertGallonsToUserUnits(waterUsageHolder.displayUnit, (double) max));
            f = (float) Math.round((float) UnitUtils.convertGallonsToUserUnits(waterUsageHolder.displayUnit, (double) f));
            f2 = (float) Math.round((float) UnitUtils.convertGallonsToUserUnits(waterUsageHolder.displayUnit, (double) f2));
            viewHolder.gallonsUsedText.setText(String.format("%s %s Used", new Object[]{StringUtils.readableNumber((int) f), nameOfWaterUnits}));
            viewHolder.gallonsUsedMeter.setMaxValue(max);
            viewHolder.gallonsUsedMeter.setCurrentValue(f);
            viewHolder.gallonsSavedText.setText(String.format("%s %s Saved", new Object[]{StringUtils.readableNumber((int) f2), nameOfWaterUnits}));
            viewHolder.gallonsSavedMeter.setMaxValue(max);
            viewHolder.gallonsSavedMeter.setCurrentValue(f2);
        }
    }

    public final int getLayoutId() {
        return R.layout.view_card_device_water_use;
    }

    public final ModelObjectViewHolder createViewHolder(View v) {
        return new ViewHolder(v);
    }
}
