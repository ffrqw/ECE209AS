package com.rachio.iro.binder;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.ShallowDevice.RoughStatus;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.popup.ModalPopup;
import com.rachio.iro.ui.view.SuperDuperRadioGroup;
import com.rachio.iro.utils.DateFormats;
import java.util.Date;

public class DeviceStatusBinder extends BaseModelViewBinder<Device> {

    public interface EditDeviceSettingsListener {
        void onEditDeviceSettings();
    }

    public interface SetRainDelayListener {
        void onRainDelaySet(int i);
    }

    public static class ViewHolder extends ModelObjectViewHolder {
        public final TextView editSettingsTextView = ((TextView) findView(R.id.edit_settings_text, false));
        public final TextView lastRunTextView = ((TextView) findView(R.id.lastrun_text, false));
        public final TextView rainDelayTextView = ((TextView) findView(R.id.raindelay, false));
        public final TextView sharedTextView = ((TextView) findView(R.id.shared_text, false));
        public final ImageView statusBadge = ((ImageView) findView(R.id.status_badge, false));
        public final ImageView statusIcon = ((ImageView) findView(R.id.status_icon, false));
        public final TextView statusTextView = ((TextView) findView(R.id.status_text, false));

        public ViewHolder(View v) {
            super(v);
            this.editSettingsTextView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    BaseActivity activity = BaseModelViewBinder.findActivity(v.getContext());
                    if (activity instanceof EditDeviceSettingsListener) {
                        ((EditDeviceSettingsListener) activity).onEditDeviceSettings();
                    }
                }
            });
        }
    }

    public final /* bridge */ /* synthetic */ void onBind(ModelObjectViewHolder modelObjectViewHolder, Object obj) {
        int i;
        boolean z = true;
        final Device device = (Device) obj;
        ViewHolder viewHolder = (ViewHolder) modelObjectViewHolder;
        boolean isInRainDelay = device.isInRainDelay();
        Date lastRunDate = device.getLastRunDate();
        RoughStatus roughStatus = device.getRoughStatus();
        viewHolder.statusTextView.setText(roughStatus.statusText);
        viewHolder.statusTextView.setTextColor(viewHolder.statusTextView.getResources().getColor(roughStatus.statusTextColour));
        if (lastRunDate != null) {
            viewHolder.lastRunTextView.setText("Last Ran " + DateFormats.formatDayMonthAtTimeWithTodayYesterday(lastRunDate));
        }
        viewHolder.lastRunTextView.setVisibility(lastRunDate != null ? 0 : 8);
        boolean isShared = device.isShared();
        boolean belongsToSomeoneElse = device.belongsToSomeoneElse();
        if (isShared || belongsToSomeoneElse) {
            i = true;
        } else {
            i = 0;
        }
        viewHolder.sharedTextView.setVisibility(i != 0 ? 0 : 4);
        if (isShared) {
            viewHolder.sharedTextView.setText("Shared");
        } else if (belongsToSomeoneElse && device.owner != null) {
            viewHolder.sharedTextView.setText("Shared by " + device.owner.fullName);
        }
        if (isInRainDelay) {
            CharSequence spannableString = new SpannableString("Rain Delay\nUntil " + DateFormats.dayMonthAndtime.format(device.rainDelayExpirationDate));
            spannableString.setSpan(new ForegroundColorSpan(viewHolder.rainDelayTextView.getResources().getColor(RoughStatus.ONLINE.statusTextColour)), 0, 10, 33);
            viewHolder.rainDelayTextView.setText(spannableString);
            viewHolder.rainDelayTextView.getCompoundDrawables()[0].setLevel(1);
        } else {
            viewHolder.rainDelayTextView.setText("No Rain Delay");
            viewHolder.rainDelayTextView.getCompoundDrawables()[0].setLevel(0);
        }
        ImageView imageView = viewHolder.statusIcon;
        if (device.isOffline() || device.schedulePause) {
            z = false;
        }
        imageView.setEnabled(z);
        viewHolder.statusIcon.setImageResource(device.isGen1() ? R.drawable.device_status_gen1 : R.drawable.device_status_gen2);
        viewHolder.statusBadge.setImageLevel(roughStatus.statusBadgeLevel);
        viewHolder.statusBadge.requestLayout();
        viewHolder.rainDelayTextView.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                View vv = LayoutInflater.from(v.getContext()).inflate(R.layout.popup_raindelay, null);
                final ModalPopup pw = new ModalPopup(vv);
                final SuperDuperRadioGroup rg = (SuperDuperRadioGroup) vv.findViewById(R.id.popup_raindelay_choices);
                rg.fixup();
                int selected = device.getRainDelayDays();
                if (selected == 0) {
                    selected = rg.getCheckCount() - 1;
                } else {
                    selected = Math.min(rg.getCheckCount() - 2, selected - 1);
                }
                rg.checkPosition(selected);
                rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        int days = rg.getCheckPosition() + 1;
                        if (days == 8) {
                            days = 0;
                        }
                        Context activity = BaseModelViewBinder.findActivity(v.getContext());
                        if (activity instanceof SetRainDelayListener) {
                            ((SetRainDelayListener) activity).onRainDelaySet(days);
                        }
                        pw.dismiss();
                    }
                });
                pw.setContentView(vv);
                View contentView = pw.getContentView();
                contentView.measure(-2, -2);
                int measuredHeight = contentView.getMeasuredHeight() + pw.getContentView().getResources().getDimensionPixelSize(R.dimen.padding_general);
                int[] iArr = new int[2];
                v.getLocationOnScreen(iArr);
                int height = v.getResources().getDisplayMetrics().heightPixels - (iArr[1] + v.getHeight());
                if (height > measuredHeight) {
                    pw.showAsDropDown(v);
                } else {
                    pw.showAsDropDown(v, 0, height - measuredHeight);
                }
            }
        });
    }

    public final int getLayoutId() {
        return R.layout.view_card_device_connection_status;
    }

    public final ModelObjectViewHolder createViewHolder(View v) {
        return new ViewHolder(v);
    }
}
