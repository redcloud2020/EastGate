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
 * Created by sammy on 3/16/2017.
 */
@Table(name="Destination")
public class Destination extends Model implements Parcelable {
    @Expose
    @Column(name="Destination_Id", unique = true)
    String Destination_Id;
    @Expose
    @Column(name="Destination_ar")
    String Destination_ar;
    @Expose
    @Column(name="Destination_en")
    String Destination_en;
    @Expose
    @Column(name="Ar_short")
    String Ar_short;
    @Expose
    @Column(name="updated_at")
    String updated_at;
    @Expose
    @Column(name="created_at")
    String created_at;
    @Expose
    @Column(name="Display")
    Boolean Display;
    public Destination(){}
    public static Destination getOldDate(){
        return  new Select().from(Destination.class).orderBy("id DESC").executeSingle();
    }
    public static List<Destination> getall(){
       return new Select().from(Destination.class).execute();
    }

    public Destination(String destination_Id, String destination_ar, String destination_en, String ar_short, String updated_at, String created_at, Boolean display) {
        Destination_Id = destination_Id;
        Destination_ar = destination_ar;
        Destination_en = destination_en;
        Ar_short = ar_short;
        this.updated_at = updated_at;
        this.created_at = created_at;
        Display = display;
    }

    public String getDestination_Id() {
        return Destination_Id;
    }

    public void setDestination_Id(String destination_Id) {
        Destination_Id = destination_Id;
    }

    public String getDestination_ar() {
        return Destination_ar;
    }

    public void setDestination_ar(String destination_ar) {
        Destination_ar = destination_ar;
    }

    public String getDestination_en() {
        return Destination_en;
    }

    public void setDestination_en(String destination_en) {
        Destination_en = destination_en;
    }

    public String getAr_short() {
        return Ar_short;
    }

    public void setAr_short(String ar_short) {
        Ar_short = ar_short;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Boolean getDisplay() {
        return Display;
    }

    public void setDisplay(Boolean display) {
        Display = display;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Destination_Id);
        dest.writeString(this.Destination_ar);
        dest.writeString(this.Destination_en);
        dest.writeString(this.Ar_short);
        dest.writeString(this.updated_at);
        dest.writeString(this.created_at);
        dest.writeValue(this.Display);
    }

    protected Destination(Parcel in) {
        this.Destination_Id = in.readString();
        this.Destination_ar = in.readString();
        this.Destination_en = in.readString();
        this.Ar_short = in.readString();
        this.updated_at = in.readString();
        this.created_at = in.readString();
        this.Display = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Destination> CREATOR = new Creator<Destination>() {
        @Override
        public Destination createFromParcel(Parcel source) {
            return new Destination(source);
        }

        @Override
        public Destination[] newArray(int size) {
            return new Destination[size];
        }
    };

    @Override
    public String toString() {
        return "Destination{" +
                "Destination_Id='" + Destination_Id + '\'' +
                ", Destination_ar='" + Destination_ar + '\'' +
                ", Destination_en='" + Destination_en + '\'' +
                ", Ar_short='" + Ar_short + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", created_at='" + created_at + '\'' +
                ", Display=" + Display +
                '}';
    }
}
