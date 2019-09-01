package com.example.monitormethod;

import android.os.Parcel;
import android.os.Parcelable;

public class IntentData implements Parcelable {
    public IntentData(){

    }
    protected IntentData(Parcel in) {
    }

    public static final Creator<IntentData> CREATOR = new Creator<IntentData>() {
        @Override
        public IntentData createFromParcel(Parcel in) {
            return new IntentData(in);
        }

        @Override
        public IntentData[] newArray(int size) {
            return new IntentData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
