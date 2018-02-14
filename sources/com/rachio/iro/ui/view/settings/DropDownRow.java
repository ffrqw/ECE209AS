package com.rachio.iro.ui.view.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.utils.SpinnerUtils;

public class DropDownRow extends FrameLayout {
    private Spinner spinner;
    private TextView title;

    public DropDownRow(Context context) {
        this(context, null);
    }

    public DropDownRow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_settings_dropdownrow, this);
        this.title = (TextView) findViewById(R.id.title);
        this.spinner = (Spinner) findViewById(R.id.spinner);
        SpinnerUtils.fixChevronSpinner(this.spinner);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DropDownRow, 0, 0);
            try {
                this.title.setText(a.getText(0));
                int entriesResourceId = a.getResourceId(1, -1);
                if (entriesResourceId != -1) {
                    ArrayAdapter<String> adapter = new ArrayAdapter(context, R.layout.dropdownrow, context.getResources().getStringArray(entriesResourceId));
                    adapter.setDropDownViewResource(17367050);
                    this.spinner.setAdapter(adapter);
                }
                a.recycle();
            } catch (Throwable th) {
                a.recycle();
            }
        }
    }

    public final void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.spinner.setOnItemSelectedListener(listener);
    }

    public final int getSelectedItemPosition() {
        return this.spinner.getSelectedItemPosition();
    }

    public final void setSelection(int position) {
        this.spinner.setSelection(1);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.spinner.setEnabled(enabled);
    }
}
