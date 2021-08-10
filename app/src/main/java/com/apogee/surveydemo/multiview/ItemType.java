package com.apogee.surveydemo.multiview;

import java.util.List;
import java.util.Map;

public class ItemType {

    public static final int DROPDOWNTYPE=0;
    public static final int INPUTTYPE=1;
    public static final int INPUTTYPEPROJECT=2;
    public static final int INPUTONLYTEXT=3;

    public int type;
    public String title;
    public String oprtr;
    public String time;
    List<String> stringList;
    public   String timeStamp;

    public Map<String, String> getStringStringMapdrop() {
        return stringStringMapdrop;
    }

    Map<String,String> stringStringMapdrop;

    public ItemType(int type, String title, Map<String, String> stringStringMapdrop) {
        this.type = type;
        this.title = title;
        this.stringStringMapdrop = stringStringMapdrop;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public int getType() {
        return type;
    }

    public ItemType(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public ItemType(int type, String title, List<String> stringList , String oprtr , String time, String timeStamp) {
        this.type = type;
        this.title = title;
        this.oprtr = oprtr;
        this.time = time;
        this.stringList = stringList;
        this.timeStamp = timeStamp;


    }


    class OnDropdownlist{
        public String dropdownvalue;
        public String dropdownvalueId;

        public OnDropdownlist(String dropdownvalue, String dropdownvalueId) {
            this.dropdownvalue = dropdownvalue;
            this.dropdownvalueId = dropdownvalueId;
        }
    }


}
