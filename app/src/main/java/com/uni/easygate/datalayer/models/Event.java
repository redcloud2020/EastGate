package com.uni.easygate.datalayer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.uni.easygate.utilities.Methods;

import java.util.Comparator;
import java.util.List;

/**
 * Created by sammy on 2/28/2017.
 */

@Table(name = "Event")
public class Event extends Model implements Parcelable {
    @Expose
    @Column(name = "Event_Id", unique = true)
    String Event_Id;
    @Expose
    @Column(name = "Timestamp")
    String Timestamp;
    @Expose
    @Column(name = "Driver_user_Id")
    String Driver_user_Id;
    @Expose
    @Column(name = "Truck_Id")
    String Truck_Id;

    @Expose
    @Column(name = "Controller_user_Id")
    String Controller_user_Id;
    @Expose
    @Column(name = "EntryExit_voucher_number")
    String EntryExit_voucher_number;
    @Expose
    @Column(name = "Truck_volume")
    String Truck_volume;
    @Expose
    @Column(name = "Comment_Id")
    String Comment_Id;
    @Expose
    @Column(name = "exitTime")
    String exitTime;
    @Expose
    @Column(name = "exited")
    Boolean exited;
    @Expose
    @Column(name = "truckNumber")
    String truckNumber;
    @Expose
    @Column(name = "driverName")
    String driverName;
    @Expose
    @Column(name = "truckTotalVolume")
    String truckTotalVolume;
    @Expose
    @Column(name = "driverUserNumber")
    String driverUserNumber;
    @Expose
    @Column(name = "Data_edited")
    String Data_edited;
    @Expose
    @Column(name = "Time_user")
    String Time_user;
    public Event() {
    }

    public static Event selectById(String id) {
        return new Select().from(Event.class).where("Tank_Id = '" + id + "'").executeSingle();
    }
    public static void deleteById(String id) {
         new Delete().from(Event.class).where("Event_Id = '" + id + "'").executeSingle();
    }
    public static Event getEventById(String id) {
        return new Select().from(Event.class).where("Event_Id = '" + id + "'").executeSingle();
    }

    public static List<Event> getAll() {
        return new Select().from(Event.class).execute();
    }

    public Event(String event_Id, String timestamp, String driver_user_Id, String truck_Id, String controller_user_Id, String entryExit_voucher_number, String truck_volume, String comment_Id, String exitTime, Boolean exited, String truckNumber, String driverName, String truckTotalVolume, String driverUserNumber, String data_edited, String time_user) {
        Event_Id = event_Id;
        Timestamp = timestamp;
        Driver_user_Id = driver_user_Id;
        Truck_Id = truck_Id;
        Controller_user_Id = controller_user_Id;
        EntryExit_voucher_number = entryExit_voucher_number;
        Truck_volume = truck_volume;
        Comment_Id = comment_Id;
        this.exitTime = exitTime;
        this.exited = exited;
        this.truckNumber = truckNumber;
        this.driverName = driverName;
        this.truckTotalVolume = truckTotalVolume;
        this.driverUserNumber = driverUserNumber;
        Data_edited = data_edited;
        Time_user = time_user;
    }

    public String getEvent_Id() {
        return Event_Id;
    }

    public void setEvent_Id(String event_Id) {
        Event_Id = event_Id;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getDriver_user_Id() {
        return Driver_user_Id;
    }

    public void setDriver_user_Id(String driver_user_Id) {
        Driver_user_Id = driver_user_Id;
    }

    public String getTruck_Id() {
        return Truck_Id;
    }

    public void setTruck_Id(String truck_Id) {
        Truck_Id = truck_Id;
    }

    public String getController_user_Id() {
        return Controller_user_Id;
    }

    public void setController_user_Id(String controller_user_Id) {
        Controller_user_Id = controller_user_Id;
    }

    public String getEntryExit_voucher_number() {
        return EntryExit_voucher_number;
    }

    public void setEntryExit_voucher_number(String entryExit_voucher_number) {
        EntryExit_voucher_number = entryExit_voucher_number;
    }

    public String getTruck_volume() {
        return Truck_volume;
    }

    public void setTruck_volume(String truck_volume) {
        Truck_volume = truck_volume;
    }

    public String getComment_Id() {
        return Comment_Id;
    }

    public void setComment_Id(String comment_Id) {
        Comment_Id = comment_Id;
    }

    public String getExitTime() {
        return exitTime;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

    public Boolean getExited() {
        return exited;
    }

    public void setExited(Boolean exited) {
        this.exited = exited;
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getTruckTotalVolume() {
        return truckTotalVolume;
    }

    public void setTruckTotalVolume(String truckTotalVolume) {
        this.truckTotalVolume = truckTotalVolume;
    }

    public String getDriverUserNumber() {
        return driverUserNumber;
    }

    public void setDriverUserNumber(String driverUserNumber) {
        this.driverUserNumber = driverUserNumber;
    }

    public String getData_edited() {
        return Data_edited;
    }

    public void setData_edited(String data_edited) {
        Data_edited = data_edited;
    }

    public String getTime_user() {
        return Time_user;
    }

    public void setTime_user(String time_user) {
        Time_user = time_user;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Event_Id);
        dest.writeString(this.Timestamp);
        dest.writeString(this.Driver_user_Id);
        dest.writeString(this.Truck_Id);
        dest.writeString(this.Controller_user_Id);
        dest.writeString(this.EntryExit_voucher_number);
        dest.writeString(this.Truck_volume);
        dest.writeString(this.Comment_Id);
        dest.writeString(this.exitTime);
        dest.writeValue(this.exited);
        dest.writeString(this.truckNumber);
        dest.writeString(this.driverName);
        dest.writeString(this.truckTotalVolume);
        dest.writeString(this.driverUserNumber);
        dest.writeString(this.Data_edited);
        dest.writeString(this.Time_user);
    }

    protected Event(Parcel in) {
        this.Event_Id = in.readString();
        this.Timestamp = in.readString();
        this.Driver_user_Id = in.readString();
        this.Truck_Id = in.readString();
        this.Controller_user_Id = in.readString();
        this.EntryExit_voucher_number = in.readString();
        this.Truck_volume = in.readString();
        this.Comment_Id = in.readString();
        this.exitTime = in.readString();
        this.exited = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.truckNumber = in.readString();
        this.driverName = in.readString();
        this.truckTotalVolume = in.readString();
        this.driverUserNumber = in.readString();
        this.Data_edited = in.readString();
        this.Time_user = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public String toString() {
        return "Event{" +
                "Event_Id='" + Event_Id + '\'' +
                ", Timestamp='" + Timestamp + '\'' +
                ", Driver_user_Id='" + Driver_user_Id + '\'' +
                ", Truck_Id='" + Truck_Id + '\'' +
                ", Controller_user_Id='" + Controller_user_Id + '\'' +
                ", EntryExit_voucher_number='" + EntryExit_voucher_number + '\'' +
                ", Truck_volume='" + Truck_volume + '\'' +
                ", Comment_Id='" + Comment_Id + '\'' +
                ", exitTime='" + exitTime + '\'' +
                ", exited=" + exited +
                ", truckNumber='" + truckNumber + '\'' +
                ", driverName='" + driverName + '\'' +
                ", truckTotalVolume='" + truckTotalVolume + '\'' +
                ", driverUserNumber='" + driverUserNumber + '\'' +
                ", Data_edited='" + Data_edited + '\'' +
                ", Time_user='" + Time_user + '\'' +
                '}';
    }
}
