package com.zzzmode.appopsx.common;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class PackageOps implements Parcelable {

  private final String mPackageName;
  private final int mUid;
  private final List<OpEntry> mEntries;

  public PackageOps(String packageName, int uid, List<OpEntry> entries) {
    mPackageName = packageName;
    mUid = uid;
    mEntries = entries;
  }

  public String getPackageName() {
    return mPackageName;
  }

  public int getUid() {
    return mUid;
  }

  public List<OpEntry> getOps() {
    return mEntries;
  }

  @Override
  public String toString() {
    return "PackageOps{" +
        "mPackageName='" + mPackageName + '\'' +
        ", mUid=" + mUid +
        ", mEntries=" + mEntries +
        '}';
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.mPackageName);
    dest.writeInt(this.mUid);
    dest.writeList(this.mEntries);
  }

  protected PackageOps(Parcel in) {
    this.mPackageName = in.readString();
    this.mUid = in.readInt();
    this.mEntries = new ArrayList<OpEntry>();
    in.readList(this.mEntries, OpEntry.class.getClassLoader());
  }

  public static final Parcelable.Creator<PackageOps> CREATOR = new Parcelable.Creator<PackageOps>() {
    @Override
    public PackageOps createFromParcel(Parcel source) {
      return new PackageOps(source);
    }

    @Override
    public PackageOps[] newArray(int size) {
      return new PackageOps[size];
    }
  };
}