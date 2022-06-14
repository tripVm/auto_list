package com.example.auto_list.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Auto implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    private String name;

    private String modification;

    private String power;

    private String weight;

    private String photo;

    public Auto() { }

    public Auto(Parcel in) {
        id = in.readInt();
        name = in.readString();
        modification = in.readString();
        power = in.readString();
        weight = in.readString();
        photo = in.readString();
    }

    public Auto(String modification, String power, String weight) {
        this.modification = modification;
        this.power = power;
        this.weight = weight;
    }

    public static final Creator<Auto> CREATOR = new Creator<Auto>() {
        @Override
        public Auto createFromParcel(Parcel in) {
            return new Auto(in);
        }

        @Override
        public Auto[] newArray(int size) {
            return new Auto[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModification() {
        return modification;
    }

    public void setModification(String modification) {
        this.modification = modification;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(modification);
        dest.writeString(power);
        dest.writeString(weight);
        dest.writeString(photo);
    }
}
