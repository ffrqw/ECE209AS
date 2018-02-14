package com.rachio.iro.ui.fragment.zoneproperties;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.rachio.iro.R;
import com.rachio.iro.ui.fragment.BaseFragment;

public class ZoneNameFragment extends BaseFragment {
    private EditText descriptionEditText;
    private String oldName = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_zonename, null);
        this.descriptionEditText = (EditText) v.findViewById(R.id.describe_edit_text);
        this.descriptionEditText.setText(this.oldName);
        this.oldName = null;
        return v;
    }

    public final String getName() {
        if (this.descriptionEditText == null) {
            return this.oldName;
        }
        return this.descriptionEditText.getEditableText().toString();
    }

    public final void setName(String name) {
        if (this.descriptionEditText == null) {
            this.oldName = name;
        } else {
            this.descriptionEditText.setText(name);
        }
    }
}
