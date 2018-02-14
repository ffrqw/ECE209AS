package android.support.v7.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.SeekBar;
import com.rachio.iro.R;

public final class AppCompatSeekBar extends SeekBar {
    private AppCompatSeekBarHelper mAppCompatSeekBarHelper;

    public AppCompatSeekBar(Context context) {
        this(context, null);
    }

    public AppCompatSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.seekBarStyle);
    }

    public AppCompatSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mAppCompatSeekBarHelper = new AppCompatSeekBarHelper(this);
        this.mAppCompatSeekBarHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    protected final void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mAppCompatSeekBarHelper.drawTickMarks(canvas);
    }

    protected final void drawableStateChanged() {
        super.drawableStateChanged();
        this.mAppCompatSeekBarHelper.drawableStateChanged();
    }

    @TargetApi(11)
    public final void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.mAppCompatSeekBarHelper.jumpDrawablesToCurrentState();
    }
}
