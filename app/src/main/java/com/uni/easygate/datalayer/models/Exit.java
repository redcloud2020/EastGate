package com.uni.easygate.datalayer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by sammy on 4/10/2017.
 */
@Table(name = "Exit")
public class Exit extends Model implements Parcelable {
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
    @Column(name = "Controller_user_Id")
    String Controller_user_Id;
    @Expose
    @Column(name = "Destination")
    String Destination;
    @Expose
    @Column(name = "Truck_Id")
    String Truck_Id;
    @Expose
    @Column(name = "Comment_Id")
    String Comment_Id;
    @Expose
    @Column(name = "Payment_voucher_number")
    String Payment_voucher_number;
    @Expose
    @Column(name = "EntryExit_voucher_number")
    String EntryExit_voucher_number;
    @Expose
    @Column(name = "Entrance_Id",  unique = true)
    String Entrance_Id;
    @Expose
    @Column(name = "Truck_volume")
    String Truck_volume;
    @Expose
    @Column(name = "Data_edited")
    String Data_edited;
    @Expose
    @Column(name = "Time_user")
    String Time_user;
    public Exit(){}



    public static List<Exit> getAll(){
        return new Select().from(Exit.class).execute();
    }
    public static void deleteById(String event_Id){
        new Delete().from(Exit.class).where("Event_Id = '"+event_Id+"'").executeSingle();
    }

    public Exit(String event_Id, String timestamp, String driver_user_Id, String controller_user_Id, String destination, String truck_Id, String comment_Id, String payment_voucher_number, String entryExit_voucher_number, String entrance_Id, String truck_volume, String data_edited, String time_user) {
        Event_Id = event_Id;
        Timestamp = timestamp;
        Driver_user_Id = driver_user_Id;
        Controller_user_Id = controller_user_Id;
        Destination = destination;
        Truck_Id = truck_Id;
        Comment_Id = comment_Id;
        Payment_voucher_number = payment_voucher_number;
        EntryExit_voucher_number = entryExit_voucher_number;
        Entrance_Id = entrance_Id;
        Truck_volume = truck_volume;
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

    public String getController_user_Id() {
        return Controller_user_Id;
    }

    public void setController_user_Id(String controller_user_Id) {
        Controller_user_Id = controller_user_Id;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public String getTruck_Id() {
        return Truck_Id;
    }

    public void setTruck_Id(String truck_Id) {
        Truck_Id = truck_Id;
    }

    public String getComment_Id() {
        return Comment_Id;
    }

    public void setComment_Id(String comment_Id) {
        Comment_Id = comment_Id;
    }

    public String getPayment_voucher_number() {
        return Payment_voucher_number;
    }

    public void setPayment_voucher_number(String payment_voucher_number) {
        Payment_voucher_number = payment_voucher_number;
    }

    public String getEntryExit_voucher_number() {
        return EntryExit_voucher_number;
    }

    public void setEntryExit_voucher_number(String entryExit_voucher_number) {
        EntryExit_voucher_number = entryExit_voucher_number;
    }

    public String getEntrance_Id() {
        return Entrance_Id;
    }

    public void setEntrance_Id(String entrance_Id) {
        Entrance_Id = entrance_Id;
    }

    public String getTruck_volume() {
        return Truck_volume;
    }

    public void setTruck_volume(String truck_volume) {
        Truck_volume = truck_volume;
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
        dest.writeString(this.Controller_user_Id);
        dest.writeString(this.Destination);
        dest.writeString(this.Truck_Id);
        dest.writeString(this.Comment_Id);
        dest.writeString(this.Payment_voucher_number);
        dest.writeString(this.EntryExit_voucher_number);
        dest.writeString(this.Entrance_Id);
        dest.writeString(this.Truck_volume);
        dest.writeString(this.Data_edited);
        dest.writeString(this.Time_user);
    }

    protected Exit(Parcel in) {
        this.Event_Id = in.readString();
        this.Timestamp = in.readString();
        this.Driver_user_Id = in.readString();
        this.Controller_user_Id = in.readString();
        this.Destination = in.readString();
        this.Truck_Id = in.readString();
        this.Comment_Id = in.readString();
        this.Payment_voucher_number = in.readString();
        this.EntryExit_voucher_number = in.readString();
        this.Entrance_Id = in.readString();
        this.Truck_volume = in.readString();
        this.Data_edited = in.readString();
        this.Time_user = in.readString();
    }

    public static final Creator<Exit> CREATOR = new Creator<Exit>() {
        @Override
        public Exit createFromParcel(Parcel source) {
            return new Exit(source);
        }

        @Override
        public Exit[] newArray(int size) {
            return new Exit[size];
        }
    };

    @Override
    public String toString() {
        return "Exit{" +
                "Event_Id='" + Event_Id + '\'' +
                ", Timestamp='" + Timestamp + '\'' +
                ", Driver_user_Id='" + Driver_user_Id + '\'' +
                ", Controller_user_Id='" + Controller_user_Id + '\'' +
                ", Destination='" + Destination + '\'' +
                ", Truck_Id='" + Truck_Id + '\'' +
                ", Comment_Id='" + Comment_Id + '\'' +
                ", Payment_voucher_number='" + Payment_voucher_number + '\'' +
                ", EntryExit_voucher_number='" + EntryExit_voucher_number + '\'' +
                ", Entrance_Id='" + Entrance_Id + '\'' +
                ", Truck_volume='" + Truck_volume + '\'' +
                ", Data_edited='" + Data_edited + '\'' +
                ", Time_user='" + Time_user + '\'' +
                '}';
    }
}
