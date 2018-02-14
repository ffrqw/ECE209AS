package com.rachio.iro.gen2.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.rachio.iro.R;
import com.rachio.iro.gen2.BaseProvisioningFragment;
import com.rachio.iro.utils.StringUtils;

public class SerialNumberCaptureFragment extends BaseProvisioningFragment {
    private Button cont;
    private boolean finished = false;
    private EditText[] serial;

    private class DigitTextWatcher implements TextWatcher {
        private final boolean last;
        private final EditText thisDigit;

        public DigitTextWatcher(EditText thisDigit, boolean last) {
            this.thisDigit = thisDigit;
            this.last = last;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && !this.last) {
                this.thisDigit.focusSearch(2).requestFocus();
            }
            SerialNumberCaptureFragment.this.validateSerial();
        }
    }

    public static SerialNumberCaptureFragment newInstance(String prefix, String currentSerial) {
        SerialNumberCaptureFragment fragment = new SerialNumberCaptureFragment();
        Bundle args = new Bundle();
        args.putString("prefix", prefix);
        args.putString("currentserial", currentSerial);
        fragment.setArguments(args);
        return fragment;
    }

    private void validateSerial() {
        if (!this.finished) {
            boolean validSerial = true;
            for (EditText et : this.serial) {
                if (et.length() != 1) {
                    validSerial = false;
                    break;
                }
            }
            this.cont.setEnabled(validSerial);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int i;
        View view = inflater.inflate(R.layout.fragment_gen2_serialnumber, container, false);
        final Spinner serialPrefix = (Spinner) view.findViewById(R.id.serialnumber_prefix);
        int[] serialNumberEditTexts = new int[]{R.id.serialnumber_serial_0, R.id.serialnumber_serial_1, R.id.serialnumber_serial_2, R.id.serialnumber_serial_3, R.id.serialnumber_serial_4, R.id.serialnumber_serial_5, R.id.serialnumber_serial_6};
        this.serial = new EditText[7];
        for (i = 0; i < 7; i++) {
            this.serial[i] = (EditText) view.findViewById(serialNumberEditTexts[i]);
        }
        this.cont = (Button) view.findViewById(R.id.serialnumber_continue);
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), R.layout.view_gen2provspinneritem, getResources().getStringArray(R.array.gen2serialnumberprefixes_prod));
        adapter.setDropDownViewResource(17367050);
        serialPrefix.setAdapter(adapter);
        if (savedInstanceState == null) {
            String prefix = getArguments().getString("prefix");
            if (prefix != null) {
                for (i = 0; i < adapter.getCount(); i++) {
                    if (StringUtils.equals((CharSequence) adapter.getItem(i), prefix)) {
                        serialPrefix.setSelection(i);
                        serialPrefix.setEnabled(false);
                        break;
                    }
                }
            }
            String currentSerial = getArguments().getString("currentserial");
            if (currentSerial != null) {
                if (prefix == null) {
                    prefix = currentSerial.substring(0, 2);
                    for (i = 0; i < adapter.getCount(); i++) {
                        if (StringUtils.equals((String) adapter.getItem(i), prefix)) {
                            serialPrefix.setSelection(i);
                        }
                    }
                }
                String digits = currentSerial.substring(2, currentSerial.length());
                for (i = 0; i < digits.length(); i++) {
                    this.serial[i].setText(String.format("%c", new Object[]{Character.valueOf(digits.charAt(i))}));
                }
            }
            validateSerial();
        }
        for (i = 0; i < 7; i++) {
            this.serial[i].addTextChangedListener(new DigitTextWatcher(this.serial[i], i + 1 == this.serial.length));
        }
        this.cont.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SerialNumberCaptureFragment.this.finished = true;
                SerialNumberCaptureFragment.this.cont.setEnabled(false);
                StringBuffer sb = new StringBuffer();
                sb.append((String) serialPrefix.getSelectedItem());
                for (EditText et : SerialNumberCaptureFragment.this.serial) {
                    sb.append(et.getEditableText().toString());
                }
                SerialNumberCaptureFragment.this.onSerialNumberCaptured(sb.toString());
            }
        });
        wireUpHelpAndExit(view);
        return view;
    }
}
