package com.instabug.library;

import java.util.HashMap;

public class IBGCustomTextPlaceHolder {
    private HashMap<Key, String> placeHoldersMap = new HashMap();

    public enum Key {
        SHAKE_HINT,
        SWIPE_HINT,
        INVALID_EMAIL_MESSAGE,
        INVALID_COMMENT_MESSAGE,
        INVOCATION_HEADER,
        REPORT_BUG,
        REPORT_FEEDBACK,
        EMAIL_FIELD_HINT,
        COMMENT_FIELD_HINT_FOR_BUG_REPORT,
        COMMENT_FIELD_HINT_FOR_FEEDBACK,
        ADD_VOICE_MESSAGE,
        ADD_IMAGE_FROM_GALLERY,
        ADD_EXTRA_SCREENSHOT,
        CONVERSATIONS_LIST_TITLE,
        AUDIO_RECORDING_PERMISSION_DENIED,
        CONVERSATION_TEXT_FIELD_HINT,
        BUG_REPORT_HEADER,
        FEEDBACK_REPORT_HEADER,
        VOICE_MESSAGE_PRESS_AND_HOLD_TO_RECORD,
        VOICE_MESSAGE_RELEASE_TO_ATTACH,
        REPORT_SUCCESSFULLY_SENT
    }

    public void set(Key key, String str) {
        this.placeHoldersMap.put(key, str);
    }

    public String get(Key key) {
        if (this.placeHoldersMap.containsKey(key)) {
            return (String) this.placeHoldersMap.get(key);
        }
        return null;
    }
}
