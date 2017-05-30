package com.uni.easygate.datalayer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sammy on 2/28/2017.
 */

/**
 * Created by sammy on 2/28/2017.
 */
@Table(name = "User")
public class User extends Model implements Parcelable {
    @SerializedName("Id")
    @Column(name = "User_Id", unique = true)
    String User_Id;
    @SerializedName("First_name_ar")
    @Column(name = "First_name_ar")
    String First_name_ar;
    @SerializedName("Last_name_ar")
    @Column(name = "Last_name_ar")
    String Last_name_ar;
    @SerializedName("First_name_en")
    @Column(name = "First_name_en")
    String First_name_en;
    @SerializedName("Last_name_en")
    @Column(name = "Last_name_en")
    String Last_name_en;
    @SerializedName("Phone_number")
    @Column(name = "Phone_number")
    String Phone_number;
    @SerializedName("Username")
    @Column(name = "Username")
    String Username;
    @SerializedName("Password")
    @Column(name = "Password")
    String Password;
    @SerializedName("Employer_Id")
    @Column(name = "Employer_Id")
    String Employer_Id;
    @SerializedName("Rating")
    @Column(name = "Rating")
    String Rating;
    @SerializedName("created_at")
    @Column(name = "created_at")
    String created_at;
    @SerializedName("updated_at")
    @Column(name = "updated_at")
    String updated_at;
    @SerializedName("isDeleted")
    @Column(name = "isDeleted")
    String isDeleted;
    @SerializedName("Role_Id")
    @Column(name = "Role_Id")
    String RoleId;
    @SerializedName("east_gate_app")
    @Column(name = "east_gate_app")
    String east_gate_app;
    public static User getOldDate(){
        return  new Select().from(User.class).orderBy("id DESC").executeSingle();
    }
    public static User login(String username, String password){
        return  new Select().from(User.class).where("Username = '"+username+"' AND Password= '"+password+"'").executeSingle();
    }
    public static User getUserById(String driverId){
        return  new Select().from(User.class).where("User_Id = '"+driverId+"'").executeSingle();
    }
    public static User getUserByName(String driverId){
        return  new Select().from(User.class).where("Username = '"+driverId+"'").executeSingle();
    }
    public static List<User> getAll(){
        return new Select().from(User.class).execute();
    }
    public User(){}

    public User(String user_Id, String first_name_ar, String last_name_ar, String first_name_en, String last_name_en, String phone_number, String username, String password, String employer_Id, String rating, String created_at, String updated_at, String isDeleted, String roleId, String east_gate_app) {
        User_Id = user_Id;
        First_name_ar = first_name_ar;
        Last_name_ar = last_name_ar;
        First_name_en = first_name_en;
        Last_name_en = last_name_en;
        Phone_number = phone_number;
        Username = username;
        Password = password;
        Employer_Id = employer_Id;
        Rating = rating;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.isDeleted = isDeleted;
        RoleId = roleId;
        this.east_gate_app = east_gate_app;
    }

    public String getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(String user_Id) {
        User_Id = user_Id;
    }

    public String getFirst_name_ar() {
        return First_name_ar;
    }

    public void setFirst_name_ar(String first_name_ar) {
        First_name_ar = first_name_ar;
    }

    public String getLast_name_ar() {
        return Last_name_ar;
    }

    public void setLast_name_ar(String last_name_ar) {
        Last_name_ar = last_name_ar;
    }

    public String getFirst_name_en() {
        return First_name_en;
    }

    public void setFirst_name_en(String first_name_en) {
        First_name_en = first_name_en;
    }

    public String getLast_name_en() {
        return Last_name_en;
    }

    public void setLast_name_en(String last_name_en) {
        Last_name_en = last_name_en;
    }

    public String getPhone_number() {
        return Phone_number;
    }

    public void setPhone_number(String phone_number) {
        Phone_number = phone_number;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmployer_Id() {
        return Employer_Id;
    }

    public void setEmployer_Id(String employer_Id) {
        Employer_Id = employer_Id;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getRoleId() {
        return RoleId;
    }

    public void setRoleId(String roleId) {
        RoleId = roleId;
    }

    public String getEast_gate_app() {
        return east_gate_app;
    }

    public void setEast_gate_app(String east_gate_app) {
        this.east_gate_app = east_gate_app;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.User_Id);
        dest.writeString(this.First_name_ar);
        dest.writeString(this.Last_name_ar);
        dest.writeString(this.First_name_en);
        dest.writeString(this.Last_name_en);
        dest.writeString(this.Phone_number);
        dest.writeString(this.Username);
        dest.writeString(this.Password);
        dest.writeString(this.Employer_Id);
        dest.writeString(this.Rating);
        dest.writeString(this.created_at);
        dest.writeString(this.updated_at);
        dest.writeString(this.isDeleted);
        dest.writeString(this.RoleId);
        dest.writeString(this.east_gate_app);
    }

    protected User(Parcel in) {
        this.User_Id = in.readString();
        this.First_name_ar = in.readString();
        this.Last_name_ar = in.readString();
        this.First_name_en = in.readString();
        this.Last_name_en = in.readString();
        this.Phone_number = in.readString();
        this.Username = in.readString();
        this.Password = in.readString();
        this.Employer_Id = in.readString();
        this.Rating = in.readString();
        this.created_at = in.readString();
        this.updated_at = in.readString();
        this.isDeleted = in.readString();
        this.RoleId = in.readString();
        this.east_gate_app = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return "User{" +
                "User_Id='" + User_Id + '\'' +
                ", First_name_ar='" + First_name_ar + '\'' +
                ", Last_name_ar='" + Last_name_ar + '\'' +
                ", First_name_en='" + First_name_en + '\'' +
                ", Last_name_en='" + Last_name_en + '\'' +
                ", Phone_number='" + Phone_number + '\'' +
                ", Username='" + Username + '\'' +
                ", Password='" + Password + '\'' +
                ", Employer_Id='" + Employer_Id + '\'' +
                ", Rating='" + Rating + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", isDeleted='" + isDeleted + '\'' +
                ", RoleId='" + RoleId + '\'' +
                ", east_gate_app='" + east_gate_app + '\'' +
                '}';
    }
}

