package android.support.v4.os;

import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class ParcelableCompat {

    static class CompatCreator<T> implements Creator<T> {
        final ParcelableCompatCreatorCallbacks<T> mCallbacks;

        public CompatCreator(ParcelableCompatCreatorCallbacks<T> callbacks) {
            this.mCallbacks = callbacks;
        }

        public final T createFromParcel(Parcel source) {
            return this.mCallbacks.createFromParcel(source, null);
        }

        public final T[] newArray(int size) {
            return this.mCallbacks.newArray(size);
        }
    }

    public static <T> Creator<T> newCreator(ParcelableCompatCreatorCallbacks<T> callbacks) {
        return VERSION.SDK_INT >= 13 ? new ParcelableCompatCreatorHoneycombMR2(callbacks) : new CompatCreator(callbacks);
    }
}
