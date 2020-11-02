package com.andrstudy.lecture.fcmtest.myfriend.network;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("back")
    private String data;
    private String value;
    private String action;
    private String dept;

    public String getDept() {
        return dept;
    }

    public void setDept(String value) {
        this.dept = dept;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setData(String data){
        this.data = data;
    }

    public String getData(){
        return data;
    }
}
