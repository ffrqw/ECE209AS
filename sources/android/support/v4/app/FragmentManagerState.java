package android.support.v4.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/* compiled from: FragmentManager */
final class FragmentManagerState implements Parcelable {
    public static final Creator<FragmentManagerState> CREATOR = new Creator<FragmentManagerState>() {
        public final /* bridge */ /* synthetic */ Object[] newArray(int i) {
            return new FragmentManagerState[i];
        }

        public final /* bridge */ /* synthetic */ Object createFromParcel(Parcel parcel) {
            return new FragmentManagerState(parcel);
        }
    };
    FragmentState[] mActive;
    int[] mAdded;
    BackStackState[] mBackStack;

    public FragmentManagerState(Parcel in) {
        this.mActive = (FragmentState[]) in.createTypedArray(FragmentState.CREATOR);
        this.mAdded = in.createIntArray();
        this.mBackStack = (BackStackState[]) in.createTypedArray(BackStackState.CREATOR);
    }

    public final int describeContents() {
        return 0;
    }

    public final void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(this.mActive, flags);
        dest.writeIntArray(this.mAdded);
        dest.writeTypedArray(this.mBackStack, flags);
    }
}
