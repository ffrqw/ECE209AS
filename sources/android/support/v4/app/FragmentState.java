package android.support.v4.app;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/* compiled from: Fragment */
final class FragmentState implements Parcelable {
    public static final Creator<FragmentState> CREATOR = new Creator<FragmentState>() {
        public final /* bridge */ /* synthetic */ Object[] newArray(int i) {
            return new FragmentState[i];
        }

        public final /* bridge */ /* synthetic */ Object createFromParcel(Parcel parcel) {
            return new FragmentState(parcel);
        }
    };
    final Bundle mArguments;
    final String mClassName;
    final int mContainerId;
    final boolean mDetached;
    final int mFragmentId;
    final boolean mFromLayout;
    final boolean mHidden;
    final int mIndex;
    Fragment mInstance;
    final boolean mRetainInstance;
    Bundle mSavedFragmentState;
    final String mTag;

    public FragmentState(Fragment frag) {
        this.mClassName = frag.getClass().getName();
        this.mIndex = frag.mIndex;
        this.mFromLayout = frag.mFromLayout;
        this.mFragmentId = frag.mFragmentId;
        this.mContainerId = frag.mContainerId;
        this.mTag = frag.mTag;
        this.mRetainInstance = frag.mRetainInstance;
        this.mDetached = frag.mDetached;
        this.mArguments = frag.mArguments;
        this.mHidden = frag.mHidden;
    }

    public FragmentState(Parcel in) {
        boolean z;
        boolean z2 = true;
        this.mClassName = in.readString();
        this.mIndex = in.readInt();
        this.mFromLayout = in.readInt() != 0;
        this.mFragmentId = in.readInt();
        this.mContainerId = in.readInt();
        this.mTag = in.readString();
        if (in.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.mRetainInstance = z;
        if (in.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.mDetached = z;
        this.mArguments = in.readBundle();
        if (in.readInt() == 0) {
            z2 = false;
        }
        this.mHidden = z2;
        this.mSavedFragmentState = in.readBundle();
    }

    public final int describeContents() {
        return 0;
    }

    public final void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        dest.writeString(this.mClassName);
        dest.writeInt(this.mIndex);
        dest.writeInt(this.mFromLayout ? 1 : 0);
        dest.writeInt(this.mFragmentId);
        dest.writeInt(this.mContainerId);
        dest.writeString(this.mTag);
        if (this.mRetainInstance) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (this.mDetached) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        dest.writeBundle(this.mArguments);
        if (!this.mHidden) {
            i2 = 0;
        }
        dest.writeInt(i2);
        dest.writeBundle(this.mSavedFragmentState);
    }
}
