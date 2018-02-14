package com.rachio.iro.gen2;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.device.ShallowDevice;

public class FoundIroView extends FrameLayout {
    private final ImageView deviceIcon;
    private final TextView name;

    public FoundIroView(Context context) {
        this(context, null);
    }

    private FoundIroView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    private FoundIroView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        inflate(context, R.layout.view_foundiro, this);
        this.deviceIcon = (ImageView) findViewById(R.id.foundiro_deviceicon);
        this.name = (TextView) findViewById(16908308);
    }

    public final void set(ShallowDevice device) {
        set(device.getDeviceLevel(), device.name);
    }

    public final void set(int level, String name) {
        this.deviceIcon.setImageLevel(level);
        this.name.setText(name);
    }
}
