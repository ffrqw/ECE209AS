package com.squareup.picasso;

import android.graphics.Bitmap;

public interface Cache {
    public static final Cache NONE = new Cache() {
        public final Bitmap get(String key) {
            return null;
        }

        public final void set(String key, Bitmap bitmap) {
        }

        public final int size() {
            return 0;
        }

        public final int maxSize() {
            return 0;
        }
    };

    Bitmap get(String str);

    int maxSize();

    void set(String str, Bitmap bitmap);

    int size();
}
