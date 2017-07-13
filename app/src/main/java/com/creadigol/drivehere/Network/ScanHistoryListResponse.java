package com.creadigol.drivehere.Network;

import com.creadigol.drivehere.Model.BaseObject;
import com.creadigol.drivehere.Model.Car;
import com.creadigol.drivehere.Model.ScanHistoryItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class ScanHistoryListResponse extends BaseObject {

    int count;

    @SerializedName("scanHistory")
    List<ScanHistoryItem> scanHistory;

    public List<ScanHistoryItem> getScanList() {
        return scanHistory;
    }

    public int getCount() {
        return count;
    }

    public static ScanHistoryListResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        ScanHistoryListResponse scanListResponse = gson.fromJson(response, ScanHistoryListResponse.class);
        return scanListResponse;
    }
}
