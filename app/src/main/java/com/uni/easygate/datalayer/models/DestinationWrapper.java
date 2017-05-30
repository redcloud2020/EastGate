package com.uni.easygate.datalayer.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by sammy on 4/7/2017.
 */

public class DestinationWrapper implements Parcelable {
    private ArrayList<Destination> DeletedData;
    private ArrayList<Destination> NewUpdatedData;

    public DestinationWrapper(ArrayList<Destination> deletedData, ArrayList<Destination> newUpdatedData) {
        DeletedData = deletedData;
        NewUpdatedData = newUpdatedData;
    }

    public ArrayList<Destination> getDeletedData() {
        return DeletedData;
    }

    public void setDeletedData(ArrayList<Destination> deletedData) {
        DeletedData = deletedData;
    }

    public ArrayList<Destination> getNewUpdatedData() {
        return NewUpdatedData;
    }

    public void setNewUpdatedData(ArrayList<Destination> newUpdatedData) {
        NewUpdatedData = newUpdatedData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.DeletedData);
        dest.writeTypedList(this.NewUpdatedData);
    }

    protected DestinationWrapper(Parcel in) {
        this.DeletedData = in.createTypedArrayList(Destination.CREATOR);
        this.NewUpdatedData = in.createTypedArrayList(Destination.CREATOR);
    }

    public static final Parcelable.Creator<DestinationWrapper> CREATOR = new Parcelable.Creator<DestinationWrapper>() {
        @Override
        public DestinationWrapper createFromParcel(Parcel source) {
            return new DestinationWrapper(source);
        }

        @Override
        public DestinationWrapper[] newArray(int size) {
            return new DestinationWrapper[size];
        }
    };

    @Override
    public String toString() {
        return "DestinationWrapper{" +
                "DeletedData=" + DeletedData +
                ", NewUpdatedData=" + NewUpdatedData +
                '}';
    }
}
