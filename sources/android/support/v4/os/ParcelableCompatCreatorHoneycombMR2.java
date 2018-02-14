package android.support.v4.os;

import android.annotation.TargetApi;
import android.os.Parcel;
import android.os.Parcelable.ClassLoaderCreator;

@TargetApi(13)
/* compiled from: ParcelableCompatHoneycombMR2 */
final class ParcelableCompatCreatorHoneycombMR2<T> implements ClassLoaderCreator<T> {
    private final ParcelableCompatCreatorCallbacks<T> mCallbacks;

    public ParcelableCompatCreatorHoneycombMR2(ParcelableCompatCreatorCallbacks<T> callbacks) {
        this.mCallbacks = callbacks;
    }

    public final T createFromParcel(Parcel in) {
        return this.mCallbacks.createFromParcel(in, null);
    }

    public final T createFromParcel(Parcel in, ClassLoader loader) {
        return this.mCallbacks.createFromParcel(in, loader);
    }

    public final T[] newArray(int size) {
        return this.mCallbacks.newArray(size);
    }
}
