package com.creadigol.drivehere.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ravi on 20-06-2017.
 */

public class ScanHistoryItem {

    String address;
    @SerializedName("created_date")
    String date;
    String latitude, longitude;
    @SerializedName("lotcode")
    String lotCode;

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLotCode() {
        return lotCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

}
