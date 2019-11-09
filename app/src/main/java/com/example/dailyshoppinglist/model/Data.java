package com.example.dailyshoppinglist.model;

public class Data {

    String mType ;
    int mAmount ;
    String mNote ;
    String date ;
    String id ;

    public Data() {
    }

    public Data(String mType, int mAmount, String mNote, String date, String id) {
        this.mType = mType;
        this.mAmount = mAmount;
        this.mNote = mNote;
        this.date = date;
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public int getmAmount() {
        return mAmount;
    }

    public void setmAmount(int mAmount) {
        this.mAmount = mAmount;
    }

    public String getmNote() {
        return mNote;
    }

    public void setmNote(String mNote) {
        this.mNote = mNote;
    }
}
