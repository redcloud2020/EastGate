package com.uni.easygate.datalayer.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by sammy on 4/7/2017.
 */

public class EventWrapper implements Parcelable {
    public ArrayList<Event> CampEntrance;
    public ArrayList<Exit> CampExit;

    public EventWrapper(ArrayList<Event> campEntrance, ArrayList<Exit> campExit) {
        CampEntrance = campEntrance;
        CampExit = campExit;
    }

    public ArrayList<Event> getCampEntrance() {
        return CampEntrance;
    }

    public void setCampEntrance(ArrayList<Event> campEntrance) {
        CampEntrance = campEntrance;
    }

    public ArrayList<Exit> getCampExit() {
        return CampExit;
    }

    public void setCampExit(ArrayList<Exit> campExit) {
        CampExit = campExit;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.CampEntrance);
        dest.writeTypedList(this.CampExit);
    }

    protected EventWrapper(Parcel in) {
        this.CampEntrance = in.createTypedArrayList(Event.CREATOR);
        this.CampExit = in.createTypedArrayList(Exit.CREATOR);
    }

    public static final Creator<EventWrapper> CREATOR = new Creator<EventWrapper>() {
        @Override
        public EventWrapper createFromParcel(Parcel source) {
            return new EventWrapper(source);
        }

        @Override
        public EventWrapper[] newArray(int size) {
            return new EventWrapper[size];
        }
    };

    @Override
    public String toString() {
        return "EventWrapper{" +
                "CampEntrance=" + CampEntrance +
                ", CampExit=" + CampExit +
                '}';
    }
}
