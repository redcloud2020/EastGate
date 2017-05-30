package com.uni.easygate.datalayer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by sammy on 4/14/2017.
 */
@Table(name = "Activity")
public class Activity extends Model implements Parcelable {
    @Expose
    @Column(name = "Activity_Id")
    String Activity_Id;
    @Expose
    @Column(name = "User_Id")
    String User_Id;
    @Expose
    @Column(name = "Activity_type_Id")
    String Activity_type_Id;
    @Expose
    @Column(name = "Timestamp")
    String Timestamp;

    public Activity(){}

    public Activity(String activity_Id, String user_Id, String activity_type_Id, String timestamp) {
        Activity_Id = activity_Id;
        User_Id = user_Id;
        Activity_type_Id = activity_type_Id;
        Timestamp = timestamp;
    }
    public static List<Activity> getAll(){
        return new Select().from(Activity.class).execute();
    }
    public String getActivity_Id() {
        return Activity_Id;
    }

    public void setActivity_Id(String activity_Id) {
        Activity_Id = activity_Id;
    }

    public String getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(String user_Id) {
        User_Id = user_Id;
    }

    public String getActivity_type_Id() {
        return Activity_type_Id;
    }

    public void setActivity_type_Id(String activity_type_Id) {
        Activity_type_Id = activity_type_Id;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Activity_Id);
        dest.writeString(this.User_Id);
        dest.writeString(this.Activity_type_Id);
        dest.writeString(this.Timestamp);
    }

    protected Activity(Parcel in) {
        this.Activity_Id = in.readString();
        this.User_Id = in.readString();
        this.Activity_type_Id = in.readString();
        this.Timestamp = in.readString();
    }

    public static final Parcelable.Creator<Activity> CREATOR = new Parcelable.Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel source) {
            return new Activity(source);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

    @Override
    public String toString() {
        return "Activity{" +
                "Activity_Id='" + Activity_Id + '\'' +
                ", User_Id='" + User_Id + '\'' +
                ", Activity_type_Id='" + Activity_type_Id + '\'' +
                ", Timestamp='" + Timestamp + '\'' +
                '}';
    }
}
