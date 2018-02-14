package com.rachio.iro.gen2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

public class SerialDigitEditText extends EditText {

    private class BackspaceFixupInputConnection extends InputConnectionWrapper {
        public BackspaceFixupInputConnection(InputConnection target, boolean mutable) {
            super(target, true);
        }

        public boolean sendKeyEvent(KeyEvent event) {
            if (SerialDigitEditText.this.getEditableText().length() == 0 && event.getAction() == 0 && event.getKeyCode() == 67) {
                SerialDigitEditText.this.focusSearch(1).requestFocus();
            }
            return super.sendKeyEvent(event);
        }

        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            if (beforeLength != 1 || afterLength != 0) {
                return super.deleteSurroundingText(beforeLength, afterLength);
            }
            if (sendKeyEvent(new KeyEvent(0, 67)) && sendKeyEvent(new KeyEvent(1, 67))) {
                return true;
            }
            return false;
        }
    }

    public SerialDigitEditText(Context context) {
        super(context);
    }

    public SerialDigitEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SerialDigitEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new BackspaceFixupInputConnection(super.onCreateInputConnection(outAttrs), true);
    }
}
