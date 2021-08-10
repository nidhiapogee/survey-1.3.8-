package com.apogee.surveydemo.model;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Operation {

    int id;
    String name;
    String issupechild;

    public Operation(int id, String name, String issupechild) {
        this.id = id;
        this.name = name;
        this.issupechild = issupechild;
    }


    public Operation() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getissupechild() {
        return issupechild;
    }


    @Override
    public int hashCode() {
        if(name!=null)
            return name.hashCode();
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Operation operation = (Operation) obj;

        if(name!=null)
        {
            if (this.name.equals(operation.name)) {
                return true;
            }

        }

        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }


    String devicename;
    int deviceid;
    String blename;
    int bleid;
    String dgpsname;
    int dgpsid;
    String device_address;

    public String getDevice_address() {
        return device_address;
    }

    public void setDevice_address(String device_address) {
        this.device_address = device_address;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public int getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(int deviceid) {
        this.deviceid = deviceid;
    }

    public String getBlename() {
        return blename;
    }

    public void setBlename(String blename) {
        this.blename = blename;
    }

    public int getBleid() {
        return bleid;
    }

    public void setBleid(int bleid) {
        this.bleid = bleid;
    }

    public String getDgpsname() {
        return dgpsname;
    }

    public void setDgpsname(String dgpsname) {
        this.dgpsname = dgpsname;
    }

    public int getDgpsid() {
        return dgpsid;
    }

    public void setDgpsid(int dgpsid) {
        this.dgpsid = dgpsid;
    }
}
